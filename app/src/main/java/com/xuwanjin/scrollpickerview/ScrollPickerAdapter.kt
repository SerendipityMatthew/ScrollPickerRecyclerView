package com.xuwanjin.scrollpickerview

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class ScrollPickerAdapter(
        private val context: Context,
) : RecyclerView.Adapter<ScrollPickerViewHolder>(), IPickerViewOperation {
    companion object {
        const val TAG = "ScrollPickerAdapter"
    }

    private var mDefaultSelectedItemOffset: Int = 0
    private var mDataList: ArrayList<String?>? = null
    private var mVisibleItemCount = 3
    private var mOnSelectedItemClickListener: OnSelectedItemClickListener? = null

    private var mLineColor: Int by Delegates.notNull<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScrollPickerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item, parent, false)
        return ScrollPickerViewHolder(view)
    }

    override fun onBindViewHolder(holder: ScrollPickerViewHolder, position: Int) {
        mDataList?.let {
            holder.scrollPickerContent.text = it[position]
        }
    }

    override fun getItemCount(): Int {
        var count = 0
        mDataList?.let { count = it.size }
        return count
    }


    override fun getNullFrontItemCount(): Int {
        var notNullCount = 0
        mDataList?.let {
            for (i in 0 until it.size) {
                if (it[i] != null) {
                    notNullCount++
                } else {
                    break
                }
            }
        }
        return notNullCount
    }

    interface OnSelectedItemClickListener {
        fun onSelectedItemClick(view: View)
    }

    class Builder(context: Context) {
        private var scrollPickerAdapter: ScrollPickerAdapter = ScrollPickerAdapter(context)

        fun setDefaultSelectItemIndex(offset: Int): Builder {
            scrollPickerAdapter.mDefaultSelectedItemOffset = offset
            return this
        }

        fun setAdapterDataList(dataList: ArrayList<String?>): Builder {
            scrollPickerAdapter.mDataList = dataList
            return this
        }

        fun setOnSelectedItemClickListener(listener: OnSelectedItemClickListener): Builder {
            scrollPickerAdapter.mOnSelectedItemClickListener = listener;
            return this
        }

        fun setVisibleItemCount(visibleItemCount: Int): Builder {
            scrollPickerAdapter.mVisibleItemCount = visibleItemCount
            return this
        }

        fun setLineColor(color: Int): Builder {
            scrollPickerAdapter.mLineColor = color
            return this
        }

        fun build(): ScrollPickerAdapter {
            if (scrollPickerAdapter.mDataList == null) {
                throw IllegalStateException("data list should not be null,")
            }
            scrollPickerAdapter.mDataList.let {
                adjustData(scrollPickerAdapter.mDataList!!)
            }
            return scrollPickerAdapter
        }

        private fun adjustData(dataList: ArrayList<String?>) {
            val visibleItemCount = scrollPickerAdapter.mVisibleItemCount
            val defaultSelectedItemOffset = scrollPickerAdapter.mDefaultSelectedItemOffset
            Log.d(TAG, "adjustData: defaultSelectedItemOffset = $defaultSelectedItemOffset")
            for (i in 0 until visibleItemCount / 2) {
                dataList.add(0, null)
            }
            for (i in 0 until visibleItemCount / 2) {
                dataList.add(null)
            }
        }
    }

    override fun getSelectedItemOffset(): Int {
        return mDefaultSelectedItemOffset
    }

    override fun getVisibleItemCount(): Int {
        return mVisibleItemCount
    }

    override fun getLineColor(): Int {
        return mLineColor
    }

    override fun updateView(itemView: View, isSelected: Boolean) {
        Log.d("Matthew", "updateView: isSelected = $isSelected")
        if (isSelected) {
            mOnSelectedItemClickListener?.onSelectedItemClick(itemView)
        }
    }

}