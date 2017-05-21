/*
 * Copyright 2017 Ufkoku (https://github.com/Ufkoku/AndroidMVPHelper)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ufkoku.mvp.list

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ufkoku.mvp.list.interfaces.IPagingResponse
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter
import com.ufkoku.mvp.list.interfaces.IPagingSearchableView
import com.ufkoku.mvp.list.util.RecyclerViewOnScrollUpdater
import com.ufkoku.mvp.list.util.StringUtils
import com.ufkoku.mvp_base.presenter.IAsyncPresenter

abstract class BasePagingSearchableDelegate<I, in PR : IPagingResponse<I>, in V, P : IPagingSearchablePresenter, VS : BasePagingSearchableViewState<I, V>>
    :
        IPagingSearchableView<I, PR>,
        RecyclerViewOnScrollUpdater.Listener,
        BasePagingAdapter.AdapterListener,
        SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener,
        IAsyncPresenter.ITaskListener

where V : IPagingSearchableView<I, PR>, V : IAsyncPresenter.ITaskListener {

    protected var activity: Activity? = null
    protected var presenter: P? = null
    protected var viewState: VS? = null

    var searchView: SearchView? = null
        set(value) {
            searchView?.setOnQueryTextListener(null)
            if (value != null) {
                if (viewState != null) {
                    value.setQuery(viewState!!.query, false)
                }
                value.setOnQueryTextListener(this)
            }
            field = value
        }

    var swipeRefreshLayout: SwipeRefreshLayout? = null
        set(value) {
            swipeRefreshLayout?.setOnRefreshListener(null)
            value?.setOnRefreshListener(this)
            field = value
        }

    protected var scrollUpdater: RecyclerViewOnScrollUpdater? = null
        set(value) {
            if (scrollUpdater != null) {
                scrollUpdater!!.listener = null
                scrollUpdater!!.unregister()
            }
            if (value != null) {
                value.listener = this
            }
            field = value
        }

    var recyclerView: RecyclerView? = null
        set(value) {
            if (scrollUpdater != null) {
                scrollUpdater!!.listener = null
                scrollUpdater = null
            }
            if (value != null) {
                scrollUpdater = RecyclerViewOnScrollUpdater(value)
                scrollUpdater!!.listener = this
            }
            field = value
        }

    var waitView: View? = null

    protected var vError: View? = null
    protected var tvErrorMessage: TextView? = null

    protected var vEmpty: View? = null
    protected var tvEmptyMessage: TextView? = null

    /****************************************************************************/

    /**
     * Call your presenter to first next page in this method
     * */
    protected abstract fun loadFirstPage()

    /**
     * Call your presenter to load next page in this method
     * */
    protected abstract fun loadNextPage()

    /**
     * @param items list that passed from presenter
     * @return adapter that will be set to RecyclerView
     * */
    protected abstract fun createPagingAdapter(inflater: LayoutInflater, items: MutableList<I>): BasePagingAdapter<I, *>

    /**
     * @param isSearch use it, if you want to display different message for empty content
     * @return string that will be displayed at empty stub view
     * */
    protected abstract fun getEmptyMessage(isSearch: Boolean): String

    /**
     * @param isSearch use it, if you want to display different message for empty content;
     * @param code code of error to get message for;
     * @return String that will be displayed at error stub or search view
     * */
    protected abstract fun getErrorMessage(isSearch: Boolean, code: Int): String

    /*****Call these methods from class, that delegate is attached to************/

    open fun onAttach(activity: Activity) {
        this.activity = activity
    }

    open fun onInitialized(presenter: P, state: VS) {
        this.presenter = presenter
        this.viewState = state

        if (state.errorCode == BasePagingSearchableViewState.NO_ERROR_CODE) {
            if (state.items == null) {
                if (!presenter.isFirstPageLoading()) {
                    loadFirstPage()
                }
            }
        }

        scrollUpdater?.loading = presenter.isNextPageLoading()

        updateProgressVisibility()
    }

    open fun onDestroyView() {
        swipeRefreshLayout = null
        scrollUpdater = null
        recyclerView = null
        vError = null
        tvErrorMessage = null
        waitView = null
        vEmpty = null
        tvEmptyMessage = null
    }

    open fun onDestroy() {
        this.presenter = null
        this.viewState = null
    }

    open fun onDetach() {
        this.activity = null
    }

    /****************************************************************************/

    /**
     * @param tvErrorMessage must be child of vError
     * */
    open fun setErrorView(vError: View, tvErrorMessage: TextView, retryButton: View) {
        this.vError = vError

        retryButton.setOnClickListener {
            onErrorRetryButtonClicked()
        }

        this.tvErrorMessage = tvErrorMessage
    }

    /**
     * @param tvEmptyMessage must be child of vError
     * */
    open fun setEmptyView(vEmpty: View, tvEmptyMessage: TextView) {
        this.vEmpty = vEmpty
        this.tvEmptyMessage = tvEmptyMessage
    }

    /**
     * Set query to search view, without calling OnQueryTextListener
     * */
    override fun setQuery(query: String) {
        if (searchView != null) {
            searchView!!.setOnQueryTextListener(null)
            searchView!!.setQuery(query, false)
            searchView!!.setOnQueryTextListener(this)
        }
    }

    /**
     * Called from presenter when first page loaded
     * */
    override fun onFirstPageLoaded(response: PR) {
        if (viewState != null && presenter != null) {
            viewState!!.items = response.data
            viewState!!.errorCode = BasePagingSearchableViewState.NO_ERROR_CODE
            viewState!!.nextPageFailed = false
            viewState!!.canLoadMore = response.canLoadMore
            setItems(response.data, response.canLoadMore, StringUtils.isNotNullOrEmpty(viewState!!.query))
            scrollUpdater?.loading = false
        }
    }

    /**
     * Set items to RecyclerView and initiates it with adapter.
     * */
    override fun setItems(items: MutableList<I>, canLoadMore: Boolean, isSearch: Boolean) {
        showErrorView(false)

        if (recyclerView != null) {
            var adapter: BasePagingAdapter<I, *>? = recyclerView!!.adapter as BasePagingAdapter<I, *>?
            if (adapter == null) {
                adapter = createPagingAdapter(activity!!.layoutInflater, items)
                val finalAdapter = adapter
                recyclerView!!.post { if (recyclerView != null) recyclerView!!.adapter = finalAdapter as RecyclerView.Adapter<*> }
            } else {
                val finalAdapter = adapter
                recyclerView!!.post { if (recyclerView != null) finalAdapter.items = items }
            }
        }

        scrollUpdater?.enabled = canLoadMore

        showEmptyView(items.size > 0, isSearch)
    }

    /**
     * Called from presenter when next page loaded
     * */
    override fun onNextPageLoaded(response: PR) {
        if (viewState != null) {
            viewState!!.canLoadMore = response.canLoadMore
            viewState!!.nextPageFailed = false
            viewState!!.errorCode = BasePagingSearchableViewState.NO_ERROR_CODE
            viewState!!.items?.addAll(response.data)
        }

        if (recyclerView != null) {
            recyclerView!!.post {
                if (recyclerView != null && recyclerView!!.adapter != null) {
                    val adapter = recyclerView!!.adapter as BasePagingAdapter<I, *>
                    adapter.addItems(response.data)
                }
            }
        }

        if (scrollUpdater != null) {
            scrollUpdater!!.enabled = response.canLoadMore
            scrollUpdater!!.loading = false
        }
    }

    /**
     * Called from presenter when first page load failed with error code
     * */
    override fun onFirstPageLoadFailed(code: Int) {
        if (viewState != null) {
            val message = getErrorMessage(StringUtils.isNotNullOrEmpty(viewState!!.query), code)
            if (viewState!!.items == null) {
                viewState!!.errorCode = code
                viewState!!.nextPageFailed = false
                showErrorView(true, StringUtils.isNotNullOrEmpty(viewState!!.query), code)
            } else {
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Called from presenter when next page load failed with error code
     * */
    override fun onNextPageLoadFailed(code: Int) {
        if (viewState != null) {
            viewState!!.errorCode = code
            viewState!!.nextPageFailed = true
        }

        if (scrollUpdater != null) {
            scrollUpdater!!.enabled = false
            scrollUpdater!!.loading = false
        }
    }

    /**
     * BasePagingAdapter.AdapterListener callback if next page was failed
     * */
    override fun loadNextPageClicked() {
        loadNextPage()
    }

    /**
     * SwipeToRefreshLayout callback
     * */
    override fun onRefresh() {
        if (presenter != null) {
            presenter!!.cancelAllPageRequests()
            loadFirstPage()
        }
    }

    /**
     * RecyclerViewOnScrollUpdater.Listener callback
     * */
    override fun onListEndReached(): Boolean {
        if (viewState != null) {
            if (viewState!!.canLoadMore && !presenter!!.isFirstPageLoading()) {
                loadNextPage()
                return true
            } else {
                return false
            }
        }
        return false
    }

    /**
     * Callback of SearchView OnQueryTextListener
     * */
    override fun onQueryTextChange(query: String): Boolean {
        if (viewState != null && presenter != null) {
            if (viewState!!.query != query) {

                viewState!!.items = null
                viewState!!.query = query
                presenter!!.cancelAllPageRequests()

                recyclerView?.post { recyclerView?.adapter = null }

                loadFirstPage()
            }
        }
        return false
    }

    /**
     * Callback of SearchView OnQueryTextListener
     * */
    override fun onQueryTextSubmit(query: String): Boolean {
        return false
    }

    /**
     * IAsyncPresenter.ITaskListener callback.
     * If you are using inheritance of BaseAsyncPresenter, callback will be called after calling notifyTaskAdded and notifyTaskFinished.
     * Else you should implement calling of callback by your self.
     * */
    override fun onTaskStatusChanged(taskId: Int, status: Int) {
        updateProgressVisibility()
    }

    /**
     * Updates progress visibility based on presenter running tasks
     * */
    open fun updateProgressVisibility() {
        if (presenter != null && viewState != null) {
            if (presenter!!.isFirstPageLoading()) {
                waitView?.visibility = if (viewState!!.items == null) View.VISIBLE else View.GONE
                swipeRefreshLayout?.isRefreshing = viewState!!.items != null
            } else {
                waitView?.visibility = View.GONE
                swipeRefreshLayout?.isRefreshing = false
            }
            recyclerView?.post {
                if (recyclerView != null && recyclerView!!.adapter != null) {
                    val adapter = recyclerView!!.adapter as BasePagingAdapter<*, *>
                    if (presenter!!.isNextPageLoading()) {
                        adapter.additionalItem = BasePagingAdapter.ADDITIONAL_ITEM_LOADER
                    } else if (adapter.additionalItem == BasePagingAdapter.ADDITIONAL_ITEM_LOADER) {
                        adapter.additionalItem = BasePagingAdapter.ADDITIONAL_ITEM_NONE
                    }
                }
            }
        }
    }

    /**
     * Called when Error view retry button clicked
     * */
    protected open fun onErrorRetryButtonClicked() {
        waitView?.visibility = View.VISIBLE
        loadFirstPage()
    }

    /**
     * Called to show or hide empty stub view, if there is no items
     */
    protected open fun showEmptyView(visible: Boolean, isSearch: Boolean = false) {
        if (vEmpty != null) {
            if (visible) {
                val id = getEmptyMessage(isSearch)
                tvEmptyMessage!!.text = id
                vEmpty!!.visibility = View.VISIBLE
            } else {
                vEmpty!!.visibility = View.GONE
            }
        }
    }

    /**
     * Called to show error view, if there is no items at all
     */
    protected open fun showErrorView(visible: Boolean, isSearch: Boolean = false, code: Int = 0) {
        if (vError != null) {
            if (visible) {
                tvErrorMessage!!.text = getErrorMessage(isSearch, code)
                vError!!.visibility = View.VISIBLE
            } else {
                vError!!.visibility = View.GONE
            }
        }
    }

}
