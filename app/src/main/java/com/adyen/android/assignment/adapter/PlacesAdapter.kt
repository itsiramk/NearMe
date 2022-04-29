package com.adyen.android.assignment.adapter

import android.app.PendingIntent.getActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.BuildConfig
import com.adyen.android.assignment.R
import javax.inject.Inject
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.utils.Resource
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_layout.view.*

class PlacesAdapter @Inject constructor(
) : RecyclerView.Adapter<PlacesAdapter.DataViewHolder>() {

    private var resultList: ArrayList<Result> = ArrayList()

    class DataViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(resultData: Result) {
            itemView.tvPlaceName.text = resultData.name
            itemView.tvPlaceCountryLocation.text = resultData.location.formatted_address
            itemView.tvCountry.text =  resultData.location.country
            if(bindingAdapterPosition%2==0){
                itemView.tvCountry.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_bg)
            }else{
                itemView.tvCountry.background = ContextCompat.getDrawable(itemView.context, R.drawable.circle_bg2)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PlacesAdapter.DataViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_layout, parent,
                false
            )
        )

    override fun getItemCount(): Int = resultList.size

    override fun onBindViewHolder(holder: PlacesAdapter.DataViewHolder, position: Int) =
        holder.bind(resultList[position])

    fun addData(resultList: List<Result>) {
        this.resultList.apply {
            clear()
            addAll(resultList)
        }
    }

}