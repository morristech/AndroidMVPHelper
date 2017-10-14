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

package com.ufkoku.mvp.list.delegate

import android.app.Activity
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.ufkoku.mvp.list.viewstate.BasePagingSearchableViewState
import com.ufkoku.mvp.list.adapter.BasePagingAdapter
import com.ufkoku.mvp.list.interfaces.IPagingResponse
import com.ufkoku.mvp.list.interfaces.IPagingSearchablePresenter
import com.ufkoku.mvp.list.interfaces.IPagingSearchableView
import com.ufkoku.mvp.list.util.RecyclerViewOnScrollUpdater
import com.ufkoku.mvp.list.util.StringUtils
import com.ufkoku.mvp_base.presenter.IAsyncPresenter

abstract class BasePagingSearchableDelegate<I, in PR : IPagingResponse<I>, in V : IPagingSearchableView<I, PR>, P : IPagingSearchablePresenter, VS : BasePagingSearchableViewState<I, V>>
    : IPagingSearchableView<I, PR>,
      RecyclerViewOnScrollUpdater.Listener,
      BasePagingAdapter.AdapterListener,
      SearchView.OnQueryTextListener,
      SwipeRefreshLayout.OnRefreshListener {

    protected var activity: Activity? = null

    var presenter: P? = null
    var viewState: VS? = null

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

    //--------------------------------------------------------------------------//

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

    //----Call these methods from class, that delegate is attached to-----------//

    open fun onAttach(activity: Activity) {
        this.activity = activity
    }

    open fun onInitialized(presenter: P, state: VS) {
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

    //--------------------------------------------------------------------------//

    /**
     * @param tvErrorMessage must be child of vError
     * */
    open fun setErrorView(vError: View, tvErrorMessage: TextView, retryButton: View?) {
        this.vError = vError

        retryButton?.setOnClickListener {
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
        val view = searchView
        if (view != null) {
            view.setOnQueryTextListener(null)
            view.setQuery(query, false)
            view.setOnQueryTextListener(this)
        }
    }

    /**
     * Called from presenter when first page loaded
     * */
    override fun onFirstPageLoaded(response: PR) {
        val viewState = this.viewState
        val presenter = this.presenter
        if (viewState != null && presenter != null) {
            presenter.cancelNextPages()
            viewState.items = response.data
            viewState.errorCode = BasePagingSearchableViewState.NO_ERROR_CODE
            viewState.nextPageFailed = false
            viewState.canLoadMore = response.canLoadMore
            setItems(response.data, response.canLoadMore, StringUtils.isNotNullOrEmpty(viewState.query))
            scrollUpdater?.loading = false
        }
    }

    /**
     * Set items to RecyclerView and initiates it with adapter.
     * */
    override fun setItems(items: MutableList<I>, canLoadMore: Boolean, isSearch: Boolean) {
        updateErrorViewVisibility()

        val recyclerView = this.recyclerView
        if (recyclerView != null) {
            @Suppress("UNCHECKED_CAST")
            var adapter: BasePagingAdapter<I, *>? = recyclerView.adapter as BasePagingAdapter<I, *>?
            if (adapter == null) {
                recyclerView.adapter = createPagingAdapter(activity!!.layoutInflater, items)
            } else {
                adapter.items = items
                adapter.additionalItem = BasePagingAdapter.ADDITIONAL_ITEM_NONE
            }
        }

        scrollUpdater?.enabled = canLoadMore

        updateEmptyViewVisibility()
    }

    /**
     * Called from presenter when next page loaded
     * */
    @Suppress("UNCHECKED_CAST")
    override fun onNextPageLoaded(response: PR) {
        val viewState = this.viewState
        if (viewState != null) {
            viewState.canLoadMore = response.canLoadMore
            viewState.nextPageFailed = false
            viewState.errorCode = BasePagingSearchableViewState.NO_ERROR_CODE
            viewState.items?.addAll(response.data)
        }

        (recyclerView?.adapter as BasePagingAdapter<I, *>?)?.addItems(response.data)

        val scrollUpdater = this.scrollUpdater
        if (scrollUpdater != null) {
            scrollUpdater.enabled = response.canLoadMore
            scrollUpdater.loading = false
        }
    }

    /**
     * Called from presenter when first page load failed with error code
     * */
    override fun onFirstPageLoadFailed(code: Int) {
        val viewState = this.viewState
        if (viewState != null) {
            if (viewState.items == null) {
                viewState.errorCode = code
                viewState.nextPageFailed = false
                setFirstPageLoadFailed(code)
            } else {
                val message = getErrorMessage(StringUtils.isNotNullOrEmpty(viewState.query), code)
                Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun setFirstPageLoadFailed(code: Int) {
        updateErrorViewVisibility()
    }

    /**
     * Called from presenter when next page load failed with error code
     * */
    override fun onNextPageLoadFailed(code: Int) {
        val viewState = this.viewState
        if (viewState != null) {
            viewState.errorCode = code
            viewState.nextPageFailed = true
        }

        setNextPageLoadFailed(code)
    }

    override fun setNextPageLoadFailed(code: Int) {
        val scrollUpdater = this.scrollUpdater
        if (scrollUpdater != null) {
            scrollUpdater.enabled = false
            scrollUpdater.loading = false
        }

        (recyclerView?.adapter as BasePagingAdapter<*, *>?)?.additionalItem = BasePagingAdapter.ADDITIONAL_ITEM_LOAD_MANUALLY
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
        val presenter = this.presenter
        if (presenter != null) {
            presenter.cancelAllPageRequests()
            loadFirstPage()
        }
    }

    /**
     * RecyclerViewOnScrollUpdater.Listener callback
     * */
    override fun onListEndReached(): Boolean {
        val viewState = this.viewState
        val presenter = this.presenter
        if (viewState != null && presenter != null) {
            if (viewState.canLoadMore && !presenter.isFirstPageLoading()) {
                recyclerView!!.post { loadNextPage() }
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
        val viewState = this.viewState
        val presenter = this.presenter
        if (viewState != null && presenter != null) {
            if (viewState.query != query) {

                viewState.items = null
                viewState.query = query
                presenter.cancelAllPageRequests()

                recyclerView?.adapter = null

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
        val viewState = this.viewState
        val presenter = this.presenter
        if (viewState != null && presenter != null) {
            if (presenter.isFirstPageLoading()) {
                waitView?.visibility = if (viewState.items == null) View.VISIBLE else View.GONE
                swipeRefreshLayout?.isRefreshing = viewState.items != null
            } else {
                waitView?.visibility = View.GONE
                swipeRefreshLayout?.isRefreshing = false
            }

            val adapter = recyclerView?.adapter as BasePagingAdapter<*, *>?
            if (adapter != null) {
                if (presenter.isNextPageLoading()) {
                    adapter.additionalItem = BasePagingAdapter.ADDITIONAL_ITEM_LOADER
                } else if (adapter.additionalItem == BasePagingAdapter.ADDITIONAL_ITEM_LOADER) {
                    adapter.additionalItem = BasePagingAdapter.ADDITIONAL_ITEM_NONE
                }
            }
        }
    }

    /**
     * Updates empty stub visibility based on items count
     * */
    open fun updateEmptyViewVisibility() {
        val viewState = this.viewState
        if (viewState != null) {
            val items = viewState.items
            showEmptyView(items != null && items.size == 0,
                          StringUtils.isNotNullOrEmpty(viewState.query))
        }
    }

    /**
     * Updates error stub visibility based on view state
     * */
    open fun updateErrorViewVisibility() {
        val viewState = this.viewState
        if (viewState != null) {
            val items = viewState.items
            showErrorView(items == null && viewState.errorCode != BasePagingSearchableViewState.NO_ERROR_CODE && !viewState.nextPageFailed,
                          StringUtils.isNotNullOrEmpty(viewState.query),
                          viewState.errorCode)
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
                tvErrorMessage!!.text = null
            }
        }
    }

}
