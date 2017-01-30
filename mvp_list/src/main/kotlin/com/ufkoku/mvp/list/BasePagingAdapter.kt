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

import android.support.annotation.IntDef
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater

import kotlin.annotation.Retention
import kotlin.annotation.AnnotationRetention

abstract class BasePagingAdapter<I, L : BasePagingAdapter.AdapterListener> : RecyclerView.Adapter<RecyclerView.ViewHolder> {

    companion object {
        const val ADDITIONAL_ITEM_NONE = 0
        const val ADDITIONAL_ITEM_LOADER = 1
        const val ADDITIONAL_ITEM_LOAD_MANUALLY = 2

        const val TYPE_ITEM = 0
        const val TYPE_LOADER = 1
        const val TYPE_LOAD_MANUALLY = 2
    }

    @Retention(AnnotationRetention.SOURCE)
    @IntDef(ADDITIONAL_ITEM_NONE.toLong(), ADDITIONAL_ITEM_LOADER.toLong(), ADDITIONAL_ITEM_LOAD_MANUALLY.toLong())
    annotation class AdditionalItemType

    protected val inflater: LayoutInflater

    var items: List<I>
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: L? = null

    @AdditionalItemType
    var additionalItem = ADDITIONAL_ITEM_NONE
        set(newAdditionalItem) {
            if (newAdditionalItem != additionalItem) {
                val oldAdditionalItem = additionalItem
                field = newAdditionalItem
                if (newAdditionalItem == ADDITIONAL_ITEM_NONE) {
                    notifyItemRemoved(itemCount)
                } else if (oldAdditionalItem != ADDITIONAL_ITEM_NONE) {
                    notifyItemChanged(itemCount - 1)
                } else {
                    notifyItemInserted(itemCount - 1)
                }
            }
        }

    constructor(inflater: LayoutInflater, items: List<I>) {
        this.inflater = inflater
        this.items = items
    }

    override fun getItemCount(): Int {
        return items.size + if (additionalItem != ADDITIONAL_ITEM_NONE) 1 else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position == itemCount - 1 && additionalItem != ADDITIONAL_ITEM_NONE) {
            return if (additionalItem == ADDITIONAL_ITEM_LOADER) TYPE_LOADER else TYPE_LOAD_MANUALLY
        } else {
            return TYPE_ITEM
        }
    }

    interface AdapterListener {

        fun loadNextPageClicked()

    }

}