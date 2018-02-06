package com.ufkoku.demo_app.ui.view.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.ufkoku.demo_app.R
import com.ufkoku.demo_app.entity.AwesomeEntity

class DataAdapter(context: Context, private val entities: List<AwesomeEntity>) : RecyclerView.Adapter<DataAdapter.AdapterViewHolder>() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    var listener: AdapterListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        return AdapterViewHolder(inflater.inflate(R.layout.list_item, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount(): Int {
        return entities.size
    }

    inner class AdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView

        private var binded: AwesomeEntity? = null

        init {
            textView = itemView as TextView
            textView.setOnClickListener { listener?.onItemClicked(binded!!) }
        }

        fun bind(entity: AwesomeEntity) {
            binded = entity
            textView.text = entity.importantDataField.toString()
        }

    }

    interface AdapterListener {

        fun onItemClicked(entity: AwesomeEntity)

    }

}
