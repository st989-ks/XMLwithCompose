package ru.ekr.swipe_recycler_experimental.swipe_layout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import kotlin.math.abs

class SwipeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : BaseSwipeLayout(context, attrs, defStyle, defStyleRes) {

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = when (event.action) {
        MotionEvent.ACTION_DOWN -> interceptActionDown(event)
        MotionEvent.ACTION_MOVE -> interceptActionMove(event)
        MotionEvent.ACTION_UP-> interceptEventActionUP(event)
        else -> super.onInterceptTouchEvent(event)
    }

    private fun interceptActionDown(event: MotionEvent): Boolean {
        lastX = event.x
        downX = event.x
        return super.onTouchEvent(event)
    }

    private fun interceptActionMove(event: MotionEvent): Boolean {
        val disX = (event.x - downX)
        return abs(disX) > scaledTouchSlop
    }

    /** menu view opened and click on content view,
     *  we just close the menu view and intercept the up event */
    private fun interceptEventActionUP(event: MotionEvent): Boolean {
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean = when (event.action) {
        MotionEvent.ACTION_MOVE -> eventActionMove(event)
        MotionEvent.ACTION_UP -> eventActionUP(event)
        MotionEvent.ACTION_CANCEL -> eventActionCancel(event)
        else -> super.onTouchEvent(event)
    }

    private fun eventActionMove(event: MotionEvent): Boolean {
        if (!isSwipeEnable) return super.onTouchEvent(event)
        val disX = lastX - event.x
        if (!dragging && (abs(disX) > scaledTouchSlop)) dragging = true
        if (dragging) {
            positionX -= disX
            lastX = event.x
        }
        return super.onTouchEvent(event)
    }

    private fun eventActionUP(event: MotionEvent): Boolean {
        dragging = false
        judgeActionOpenClose()
        return super.onTouchEvent(event)
    }

    private fun eventActionCancel(event: MotionEvent): Boolean {
        dragging = false
        judgeActionOpenClose()
        return super.onTouchEvent(event)
    }
}