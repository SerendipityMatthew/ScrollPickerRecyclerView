package com.xuwanjin.scrollpickerview

import android.view.View

interface IPickerViewOperation {
    /**
     *  获取默认的选中的元素, 不包含 null 的元素
     */
    fun getSelectedItemOffset(): Int
    /**
     *  获取列表中,位列于前面的, 元素为 null 的元素的个数
     */
    fun getNullFrontItemCount(): Int

    /**
     *  可见元素的个数
     */
    fun getVisibleItemCount(): Int

    /**
     *  指示被选中的元素的横线的颜色
     */
    fun getLineColor(): Int

    /**
     * 最终被选中的元素
     */
    fun updateView(itemView: View, isSelected: Boolean)
}