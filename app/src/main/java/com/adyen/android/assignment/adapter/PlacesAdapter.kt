package com.adyen.android.assignment.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.R
import javax.inject.Inject
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.utils.Resource
import kotlinx.android.synthetic.main.item_layout.view.*

class PlacesAdapter @Inject constructor(
) : RecyclerView.Adapter<PlacesAdapter.DataViewHolder>() {

    private var resultList: ArrayList<Result> = ArrayList()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(resultData: Result) {
            itemView.tvPlaceName.text = resultData.name
            itemView.tvPlaceCountryLocation.text = resultData.location.country

         /*   Glide.with(itemView.imgPlaces.context)
                .into(itemView.imgPlaces)*/
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = resultList.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) =
        holder.bind(resultList[position])

    fun addData(resultList: List<Result>) {
        this.resultList.apply {
            clear()
            addAll(resultList)
        }
    }

}