package com.xuwanjin.scrollpickerview

import  android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScrollPickerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var scrollPickerContent: TextView

    init {
        initViewItem(itemView)
    }

    private fun initViewItem(itemView: View) {
        scrollPickerContent = itemView.findViewById(R.id.scrollPickerContent)
    }
}