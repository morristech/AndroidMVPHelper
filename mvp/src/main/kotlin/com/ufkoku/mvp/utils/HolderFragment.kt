package com.ufkoku.mvp.utils

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import com.ufkoku.mvp_base.presenter.IAsyncPresenter
import com.ufkoku.mvp_base.presenter.IPresenter
import com.ufkoku.mvp_base.viewstate.IViewState
import java.util.*

class HolderFragment : Fragment() {

    companion object {

        val TAG = "com.ufkoku.mvp.utils.HolderFragment"

        private val KEY_NEXT_ID = "$TAG.keyNextId"

        private val holders: WeakHashMap<FragmentManager, HolderFragment> = WeakHashMap()

        fun getInstance(fragment: Fragment): HolderFragment {
            return getInstance(fragment.activity!!)
        }

        fun getInstance(activity: FragmentActivity): HolderFragment {
            return getInstance(activity.supportFragmentManager, true)!!
        }

        fun getInstanceIfExist(fragment: Fragment): HolderFragment? {
            val activity = fragment.activity
            return if (activity != null) getInstanceIfExist(activity) else null
        }

        fun getInstanceIfExist(activity: FragmentActivity): HolderFragment? {
            return getInstance(activity.supportFragmentManager, false)
        }

        private fun getInstance(fragmentManager: FragmentManager, create: Boolean): HolderFragment? {
            var fragment = holders[fragmentManager]
            if (fragment == null) {
                fragment = fragmentManager.findFragmentByTag(TAG) as HolderFragment?
                if (fragment == null && create) {
                    fragment = HolderFragment()
                    fragmentManager.beginTransaction()
                            .add(fragment, TAG)
                            .commitAllowingStateLoss()
                }
                if (fragment != null) {
                    holders[fragmentManager] = fragment
                }
            }
            return fragment
        }

    }

    private val presenters: MutableMap<Int, IPresenter<*>> = HashMap()

    private val viewStates: MutableMap<Int, IViewState<*>> = HashMap()

    private var nextId = 0

    //------------------------------------------------------------------------------------//

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        holders[fragmentManager] = this
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            nextId = savedInstanceState.getInt(KEY_NEXT_ID)
        }
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(KEY_NEXT_ID, nextId)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        for (presenter in presenters) {
            if (presenter is IAsyncPresenter<*>) {
                presenter.cancel()
            }
        }
        super.onDestroy()
    }

    override fun onDetach() {
        holders.remove(fragmentManager)
        super.onDetach()
    }

    //------------------------------------------------------------------------------------//

    fun addPresenter(value: IPresenter<*>): Int {
        val key = nextId++
        presenters[key] = value
        return key
    }

    fun setPresenter(key: Int, value: IPresenter<*>) {
        presenters[key] = value
    }

    fun getPresenter(key: Int): IPresenter<*>? {
        return presenters[key]
    }

    fun removePresenter(key: Int) {
        presenters.remove(key)
    }

    //------------------------------------------------------------------------------------//

    fun addViewState(value: IViewState<*>): Int {
        val key = nextId++
        viewStates[key] = value
        return key
    }

    fun setViewState(key: Int, value: IViewState<*>) {
        viewStates[key] = value
    }

    fun getViewState(key: Int): IViewState<*>? {
        return viewStates[key]
    }

    fun removeViewState(key: Int) {
        viewStates.remove(key)
    }

}
