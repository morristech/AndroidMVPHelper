package com.ufkoku.demo_app.ui.activity.static_list

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ufkoku.demo_app.R
import com.ufkoku.demo_app.entity.AwesomeEntity
import com.ufkoku.demo_app.ui.common.presenter.StaticListPresenter
import com.ufkoku.demo_app.ui.common.view_state.StaticListViewState
import com.ufkoku.demo_app.ui.lifecycle_listeners.ActivityLifecycleObserver
import com.ufkoku.demo_app.ui.view.DataView
import com.ufkoku.mvp.BaseMvpActivity
import kotlinx.android.synthetic.main.view_data.*
import java.util.*

class StaticListActivity : BaseMvpActivity<IStaticListActivityWrap, StaticListPresenter<IStaticListActivityWrap>, StaticListViewState>(), IStaticListActivity {

    companion object {
        protected val ARG_RETAIN = "com.ufkoku.demo_app.ui.activity.static_list.StaticListActivity.ARG_RETAIN"
    }

    private val wrap = IStaticListActivityWrap(this)

    private val observer = ActivityLifecycleObserver()

    init {
        subscribe(observer)
    }

    //------------------------------------------------------------------------------------//

    override fun retainPresenter(): Boolean {
        //don't do it in real life, return constant value
        val intent = intent
        return intent != null && intent.getBooleanExtra(ARG_RETAIN, false)
    }

    override fun retainViewState(): Boolean {
        //don't do it in real life, return constant value
        val intent = intent
        return intent != null && intent.getBooleanExtra(ARG_RETAIN, false)
    }

    override fun createView() {
        //set view
        setContentView(R.layout.view_data)
        //init view fields
        dataView!!.setListener(object : DataView.ViewListener {
            override fun onItemClicked(entity: AwesomeEntity) {
                presenter?.processItem(entity)
            }

            override fun onRetainableClicked() {
                startActivity(Builder(true).build(this@StaticListActivity))
            }

            override fun onSavableClicked() {
                startActivity(Builder(false).build(this@StaticListActivity))
            }
        })
        //set title
        this.title = if (retainPresenter()) "Retainable" else "Savable"
    }

    override fun getMvpView(): IStaticListActivityWrap = wrap

    override fun createViewState(): StaticListViewState = StaticListViewState()

    override fun createPresenter(): StaticListPresenter<IStaticListActivityWrap> = StaticListPresenter()

    override fun onInitialized(presenter: StaticListPresenter<IStaticListActivityWrap>, viewState: StaticListViewState) {
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

    //------------------------------------------------------------------------------------//

    override fun onDataLoaded(data: ArrayList<AwesomeEntity>) {
        val state = viewState
        if (state != null) {
            state.entities = data
        }

        populateData(data)
    }

    override fun getViewWidth(): Int = dataView!!.width

    override fun onItemProcessed(result: Int) {
        Toast.makeText(this,
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

        fun build(context: Context): Intent {
            val intent = Intent(context, StaticListActivity::class.java)

            intent.putExtra(ARG_RETAIN, isRetainElements)

            return intent
        }

    }

}