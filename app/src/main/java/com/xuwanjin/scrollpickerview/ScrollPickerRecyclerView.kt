package com.xuwanjin.scrollpickerview

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlin.properties.Delegates

class ScrollPickerRecyclerView : RecyclerView {
    companion object {
        const val TAG = "ScrollPicker"
        const val DEFAULT_VISIBILITY_COUNT = 5
        const val DEFAULT_SELECTED_ITEM_OFFSET = 1
        fun dpToPx(dp: Float): Float {
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
        }
    }

    private var mFirstAmend = false

    private var mBgPaint: Paint = Paint()
    private lateinit var mSmoothScrollRunnable: Runnable
    private var mInitialY by Delegates.notNull<Int>()
    private var mFirstLineY: Float = 0f
    private var mSecondLineY: Float = 0f
    private var mItemHeight: Int = 0
    private var mItemWidth: Int = 0

    init {
        initTask()
        initPaint()
        initAttributes()
    }

    private fun initAttributes() {
    }

    @JvmOverloads
    constructor(context: Context)
            : super(context, null) {
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?)
            : super(context, attrs, 0) {
    }

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {
    }


    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(emotionEvent: MotionEvent?): Boolean {
        if (emotionEvent != null) {
            if (emotionEvent.action == MotionEvent.ACTION_UP) {
                processItemOffset()
            }
        }
        return super.onTouchEvent(emotionEvent)
    }

    private fun processItemOffset() {
        mInitialY = getScrollYDistance()
        postDelayed(mSmoothScrollRunnable, 30)
        updateView()
    }

    private fun initTask() {
        Log.d(TAG, "initTask: ")
        mSmoothScrollRunnable = Runnable {
            val newY = getScrollYDistance()
            if (mInitialY != newY) {
                mInitialY = newY
                postDelayed(mSmoothScrollRunnable, 50)
            } else if (mItemHeight > 0) {
                val offset = mInitialY % mItemHeight
                if (offset == 0) {
                    return@Runnable
                }
                if (offset >= mItemHeight / 2) {
                    smoothScrollBy(0, mItemHeight - offset)
                } else {
                    smoothScrollBy(0, -offset)
                }
            }
        }
    }

    override fun onScrolled(dx: Int, dy: Int) {
        super.onScrolled(dx, dy)
        Log.d(TAG, "onScrolled: $childCount")
    }

    private fun updateView() {
        for (i in 0 until childCount) {
            val childView = getChildAt(i)
            val itemViewY = childView.top + mItemHeight / 2
            val contentView = childView.findViewById<TextView>(R.id.scrollPickerContent)
            val pickerOperation: IPickerViewOperation = adapter as IPickerViewOperation
            pickerOperation.let {
                contentView?.let { view ->
                    val isSelected = mFirstLineY < itemViewY && itemViewY < mSecondLineY
                    pickerOperation.updateView(view, isSelected)
                }
            }
        }
    }

    private fun getScrollYDistance(): Int {
        val linearLayoutManager: LinearLayoutManager = this.layoutManager as LinearLayoutManager
        val position = linearLayoutManager.findFirstVisibleItemPosition()
        val firstVisibleChildView = linearLayoutManager.findViewByPosition(position) ?: return 0
        val itemHeight = firstVisibleChildView.height
        return position * itemHeight - firstVisibleChildView.top
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.d(TAG, "onDraw: mItemHeight = $mItemHeight")
        Log.d(TAG, "onDraw: mBgPaint = $mBgPaint")
        if (mItemHeight > 0) {
            val screenX: Int = width
            val startX: Float = screenX / 2 - mItemHeight / 2 - dpToPx(5f)
            val stopX = mItemHeight + startX + dpToPx(5f)
            mBgPaint.let {
                canvas?.drawLine(0f, mFirstLineY, width.toFloat(), mFirstLineY, it)
            }
            Log.d(TAG, "onDraw: $mFirstLineY = $mSecondLineY")
            mBgPaint.let {
                canvas?.drawLine(0f, mSecondLineY, width.toFloat(), mSecondLineY, it)
            }
        }
        if (!mFirstAmend) {
            mFirstAmend = true
            (layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(getItemSelectedOffSet(), 0)
        }
    }

    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        layoutParams.width
        val newWidthSpec = widthSpec
        Log.d(TAG, "onMeasure: newWidthSpec = $newWidthSpec")
        Log.d(TAG, "onMeasure:  layoutParams.width = ${layoutParams.width}")
        val newHeightSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE shr 2, MeasureSpec.AT_MOST)
        super.onMeasure(newWidthSpec, newHeightSpec)
        if (this.childCount > 0) {
            if (mItemHeight == 0) {
                mItemHeight = getChildAt(0).measuredHeight
            }
            if (mItemWidth == 0) {
                mItemWidth = getChildAt(0).measuredWidth
            }
            val visibleCount = getVisibleCount()
            if (mFirstLineY == 0F || mSecondLineY == 0F) {
                mFirstLineY = (mItemHeight * (visibleCount / 2)).toFloat()
                mSecondLineY = (mItemHeight * ((visibleCount / 2) + 1)).toFloat()
            }
        }
        setMeasuredDimension(newWidthSpec, mItemHeight * getVisibleCount())
    }

    /**
     *  默认被选择中的数据,
     */
    private fun getItemSelectedOffSet(): Int {
        val pickerOperation: IPickerViewOperation = adapter as IPickerViewOperation
        var offset: Int = DEFAULT_SELECTED_ITEM_OFFSET
        pickerOperation.let {
            offset = it.getSelectedItemOffset()
        }
        offset += pickerOperation.getNullFrontItemCount()

        return offset
    }

    private fun getVisibleCount(): Int {
        var pickerOperation: IPickerViewOperation = adapter as IPickerViewOperation
        Log.d(TAG, "getVisibleCount: pickerOperation = $pickerOperation")
        var visibleCount = DEFAULT_VISIBILITY_COUNT
        pickerOperation.let {
            visibleCount = it.getVisibleItemCount()
        }
        return visibleCount
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
    }

    private fun initPaint() {
        Log.d(TAG, "initPaint: mBgPaint 111 = $mBgPaint")
        if (mBgPaint == null) {
            mBgPaint = Paint()
        }
        Log.d(TAG, "initPaint: mBgPaint 222 = $mBgPaint")

        mBgPaint.color = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(R.color.colorAccent, context.theme)
        } else {
            resources.getColor(R.color.colorAccent)
        }
        mBgPaint.strokeWidth = dpToPx(1f)
    }

}