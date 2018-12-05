package edu.uw.oazeemi.trashcache

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

class RecyclerAdapter(private val parentActivity: LocationActivity,
                      private val values: MutableList<LocationActivity.LocationData>) : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var itemTitle: TextView
        var itemAddress: TextView
        var itemNumber: TextView

        init {
            itemTitle = itemView.findViewById(R.id.title)
            itemAddress = itemView.findViewById(R.id.address)
            itemNumber = itemView.findViewById(R.id.number)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.location_row, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, i: Int) {
        viewHolder.itemTitle.text = values[i].descr
        viewHolder.itemAddress.text = values[i].address + ", " + values[i].city + " - " + values[i].pcode
        viewHolder.itemNumber.text = values[i].phone
    }

    override fun getItemCount(): Int {
        return values.size
    }

}