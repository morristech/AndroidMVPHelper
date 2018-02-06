package com.ufkoku.demo_app.ui.view

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout

import com.ufkoku.demo_app.R
import com.ufkoku.demo_app.entity.AwesomeEntity
import com.ufkoku.demo_app.ui.view.adapter.DataAdapter

class DataView : FrameLayout, DataAdapter.AdapterListener {

    private var listener: ViewListener? = null

    private var recyclerView: RecyclerView? = null

    private var vWaitView: View? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onFinishInflate() {
        super.onFinishInflate()

        recyclerView = findViewById<View>(R.id.recycler) as RecyclerView
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        vWaitView = findViewById(R.id.waitView)

        findViewById<View>(R.id.retainable).setOnClickListener { view ->
            if (listener != null) {
                listener!!.onRetainableClicked()
            }
        }
        findViewById<View>(R.id.savable).setOnClickListener { view ->
            if (listener != null) {
                listener!!.onSavableClicked()
            }
        }
    }

    fun setListener(listener: ViewListener) {
        this.listener = listener
    }

    fun populateData(entities: List<AwesomeEntity>) {
        recyclerView!!.post {
            val adapter = DataAdapter(context, entities)
            adapter.listener = this
            recyclerView!!.adapter = adapter
        }
    }

    fun setWaitViewVisible(visible: Boolean) {
        vWaitView!!.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun onItemClicked(entity: AwesomeEntity) {
        listener?.onItemClicked(entity)
    }

    interface ViewListener {

        fun onItemClicked(entity: AwesomeEntity)

        fun onRetainableClicked()

        fun onSavableClicked()

    }

}
