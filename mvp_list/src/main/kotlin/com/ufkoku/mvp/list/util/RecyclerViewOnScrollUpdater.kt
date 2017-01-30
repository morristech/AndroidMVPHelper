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

package com.ufkoku.mvp.list.util

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager

class RecyclerViewOnScrollUpdater(
        private val recyclerView: RecyclerView,
        private val visibleThreshold: Int = 5) : RecyclerView.OnScrollListener() {

    var listener: RecyclerViewOnScrollUpdater.Listener? = null
    var enabled = false
    var loading = false
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0

    init {
        recyclerView.addOnScrollListener(this)
    }

    fun unregister() {
        recyclerView.removeOnScrollListener(this)
    }

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val layoutManager = recyclerView!!.layoutManager ?: return

        visibleItemCount = recyclerView.childCount
        totalItemCount = layoutManager.itemCount
        if (layoutManager is LinearLayoutManager) {
            firstVisibleItem = layoutManager.findFirstVisibleItemPosition()
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val positions = IntArray(layoutManager.spanCount)
            layoutManager.findFirstCompletelyVisibleItemPositions(positions)
            var position = Integer.MAX_VALUE
            for (i in positions.indices) {
                if (positions[i] != RecyclerView.NO_POSITION && positions[i] < position) {
                    position = positions[i]
                }
            }
            if (position == Integer.MAX_VALUE) {
                firstVisibleItem = 0
            } else {
                firstVisibleItem = position
            }
        }

        if (enabled
                && !loading
                && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold
                && listener != null) {
            loading = listener!!.onListEndReached()
        }
    }

    interface Listener {

        /**
         * @return true if loading started
         */
        fun onListEndReached(): Boolean

    }

}
