package com.example.adconnect.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.adconnect.R
import com.example.adconnect.model.Campaign

class CampaignAdapter(private val list: List<Campaign>) : RecyclerView.Adapter<CampaignAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val title: TextView = v.findViewById(R.id.tvCampaignTitle)
        val desc: TextView = v.findViewById(R.id.tvCampaignDesc)
        val img: ImageView = v.findViewById(R.id.ivCampaignImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_campaign, parent, false)
        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]
        holder.title.text = item.title
        holder.desc.text = item.description

        // Use Glide to load image URL into ImageView
        Glide.with(holder.itemView.context).load(item.imageUrl).into(holder.img)
    }

    override fun getItemCount() = list.size
}