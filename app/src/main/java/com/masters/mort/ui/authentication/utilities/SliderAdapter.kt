package com.masters.mort.ui.authentication.utilities

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.masters.mort.R
import com.masters.mort.databinding.SliderItemBinding
import com.masters.mort.model.slider_item.AnimationSliderItem
import com.smarteist.autoimageslider.SliderViewAdapter


class SliderAdapter(context: Context): SliderViewAdapter<SliderAdapterViewHolder>() {

    private var context:Context
    private var images = mutableListOf<AnimationSliderItem>()

    init {
        this.context = context
    }

    fun addItem(item: AnimationSliderItem){
        this.images.add(item)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return this.images.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterViewHolder {
        val view = LayoutInflater.from(parent?.context ?: this.context)
            .inflate(R.layout.slider_item, parent, false)
        return SliderAdapterViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderAdapterViewHolder?, position: Int) {
        val image = images[position]
        viewHolder?.bind(image)
    }
}

class SliderAdapterViewHolder(itemView: View?) : SliderViewAdapter.ViewHolder(itemView) {
    fun bind(item: AnimationSliderItem){
        val binding = SliderItemBinding.bind(itemView)
        binding.featuredImage.setImageResource(item.image)
    }
}