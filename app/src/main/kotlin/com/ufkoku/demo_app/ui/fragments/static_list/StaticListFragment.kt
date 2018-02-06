package com.ufkoku.demo_app.ui.fragments.static_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.ufkoku.demo_app.R
import com.ufkoku.demo_app.entity.AwesomeEntity
import com.ufkoku.demo_app.ui.common.presenter.StaticListPresenter
import com.ufkoku.demo_app.ui.common.view_state.StaticListViewState
import com.ufkoku.demo_app.ui.fragments.base.IFragmentManager
import com.ufkoku.demo_app.ui.lifecycle_listeners.FragmentLifecycleObserver
import com.ufkoku.demo_app.ui.view.DataView
import com.ufkoku.mvp.BaseMvpFragment
import kotlinx.android.synthetic.main.view_data.*
import java.util.*

class StaticListFragment : BaseMvpFragment<IStaticListFragmentWrap, StaticListPresenter<IStaticListFragmentWrap>, StaticListViewState>(), IStaticListFragment {

    companion object {
        protected val ARG_RETAIN = "com.ufkoku.demo_app.ui.fragments.savable.StaticListFragment.retain"
    }

    private val wrap = IStaticListFragmentWrap(this)

    private val observer = FragmentLifecycleObserver()

    init {
        subscribe(observer)
    }

    //----------------------------------------------------------------------------------------//

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.view_data, container, false)
    }

    override fun retainPresenter(): Boolean {
        //don't do it in real life, return constant
        val args = arguments
        return args != null && args.getBoolean(ARG_RETAIN)
    }

    override fun retainViewState(): Boolean {
        //don't do it in real life, return constant
        val args = arguments
        return args != null && args.getBoolean(ARG_RETAIN)
    }

    override fun getMvpView(): IStaticListFragmentWrap {
        return wrap
    }

    override fun createViewState(): StaticListViewState {
        return StaticListViewState()
    }

    override fun createPresenter(): StaticListPresenter<IStaticListFragmentWrap> {
        return StaticListPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dataView!!.setListener(object : DataView.ViewListener {
            override fun onItemClicked(entity: AwesomeEntity) {
                presenter?.processItem(entity)
            }

            override fun onRetainableClicked() {
                val context = context
                if (context is IFragmentManager) {
                    (context as IFragmentManager).setFragment(Builder(true).build())
                }
            }

            override fun onSavableClicked() {
                val context = context
                if (context is IFragmentManager) {
                    (context as IFragmentManager).setFragment(Builder(false).build())
                }
            }
        })

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onInitialized(presenter: StaticListPresenter<IStaticListFragmentWrap>, viewState: StaticListViewState) {
        //no entities in view state
        if (viewState.entities == null) {
            //load task not running
            if (!presenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA)) {
                //load data from presenter
                presenter.fetchData()
            }
        }
        //invalidate progress visibility anyway
        updateProgressVisibility()
    }

    //----------------------------------------------------------------------------------------//

    override fun onDataLoaded(data: ArrayList<AwesomeEntity>) {
        val state = viewState
        state?.entities = data
        populateData(data)
    }

    override fun getViewWidth(): Int = dataView!!.width

    override fun onItemProcessed(result: Int) {
        Toast.makeText(context,
                       "Item processed, result is $result",
                       Toast.LENGTH_LONG).show()
    }

    override fun populateData(entities: List<AwesomeEntity>) {
        dataView?.populateData(entities)
    }

    override fun onTaskStatusChanged(taskId: Int, status: Int) {
        updateProgressVisibility()
    }

    //---------------------------------------------------------------------------------//

    fun updateProgressVisibility() {
        val presenter = presenter
        if (presenter != null) {
            dataView?.setWaitViewVisible(presenter.isTaskRunning(StaticListPresenter.TASK_FETCH_DATA))
        }
    }

    //---------------------------------------------------------------------------------//

    class Builder() {

        var isRetainElements = false

        constructor(retainElements: Boolean) : this() {
            this.isRetainElements = retainElements
        }

        fun build(): StaticListFragment {
            val fragment = StaticListFragment()

            val args = Bundle()
            args.putBoolean(ARG_RETAIN, isRetainElements)
            fragment.arguments = args

            return fragment
        }

    }

}