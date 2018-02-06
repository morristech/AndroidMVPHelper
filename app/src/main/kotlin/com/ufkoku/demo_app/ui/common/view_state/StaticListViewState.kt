package com.ufkoku.demo_app.ui.common.view_state

import android.os.Bundle
import com.ufkoku.demo_app.entity.AwesomeEntity
import com.ufkoku.mvp.viewstate.autosavable.AutoSavable
import com.ufkoku.mvp.viewstate.autosavable.DontSave
import com.ufkoku.mvp_base.viewstate.IViewState
import java.util.*

@AutoSavable
class StaticListViewState : IViewState<StaticListViewState.StaticListViewStateView> {

    var entities: ArrayList<AwesomeEntity>? = null

    override fun save(out: Bundle) {
        StaticListViewStateSaver.save(this, out)
    }

    override fun restore(inState: Bundle) {
        StaticListViewStateSaver.restore(this, inState)
    }

    override fun apply(view: StaticListViewStateView) {
        if (entities != null) {
            view.populateData(entities!!)
        }
    }

    interface StaticListViewStateView {

        fun populateData(entities: List<AwesomeEntity>)

    }

}
