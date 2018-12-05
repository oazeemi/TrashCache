package edu.uw.oazeemi.trashcache

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class RecyclingAdapter(private val parentActivity: RecycleInformation,
                       private val values: MutableList<RecycleInformation.ItemsData>) : RecyclerView.Adapter<RecyclingAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var recycleTitle: TextView
        var recycleDescr: TextView

        init {
            recycleTitle = itemView.findViewById(R.id.recycleTitle)
            recycleDescr = itemView.findViewById(R.id.recycleDescr)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.recycle_row, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(p0: RecyclingAdapter.ViewHolder, p1: Int) {
        p0.recycleDescr.text = values[p1].descr
        p0.recycleTitle.text = values[p1].name
    }


    override fun getItemCount(): Int {
        return values.size
    }

}