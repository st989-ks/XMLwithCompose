package ru.ekr.xml_with_compose.util.swipe_recycler

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


class SwipeMenuRecyclerView : RecyclerView {
    protected var mViewConfig: ViewConfiguration? = null
    protected var mOldSwipedView: SwipeHorizontalMenuLayout? = null
    protected var mOldTouchedPosition = INVALID_POSITION
    private var mDownX = 0
    private var mDownY = 0

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!, attrs, defStyle) {
        init()
    }

    protected fun init() {
        mViewConfig = ViewConfiguration.get(context)
    }

    private fun getSwipeMenuView(itemView: ViewGroup): View {
        if (itemView is SwipeHorizontalMenuLayout) return itemView
        val unvisited: MutableList<View> = ArrayList()
        unvisited.add(itemView)
        while (!unvisited.isEmpty()) {
            val child = unvisited.removeAt(0) as? ViewGroup
                ?: // view
                continue
            if (child is SwipeHorizontalMenuLayout) return child
            val group = child
            val childCount = group.childCount
            for (i in 0 until childCount) unvisited.add(group.getChildAt(i))
        }
        return itemView
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var isIntercepted = super.onInterceptTouchEvent(ev)
        // ignore Multi-Touch
        if (ev.actionIndex != 0) return true
        val action = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mDownX = ev.x.toInt()
                mDownY = ev.y.toInt()
                val touchingPosition =
                    getChildAdapterPosition(findChildViewUnder(ev.x.toInt().toFloat(), ev.y.toInt()
                        .toFloat())!!)
                val oldSwipedView = mOldSwipedView
                if (touchingPosition != mOldTouchedPosition && oldSwipedView  != null) {
                    // already one swipe menu is opened, so we close it and intercept the event
                    if (oldSwipedView.isMenuOpen) {
                        oldSwipedView.smoothCloseMenu()
                        isIntercepted = true
                    }
                }
                val vh = findViewHolderForAdapterPosition(touchingPosition)
                if (vh != null) {
                    val itemView = getSwipeMenuView(vh.itemView as ViewGroup)
                    if (itemView is SwipeHorizontalMenuLayout) {
                        mOldSwipedView = itemView as SwipeHorizontalMenuLayout
                        mOldTouchedPosition = touchingPosition
                    }
                }
                // if we intercept the event, just reset
                if (isIntercepted) {
                    mOldSwipedView = null
                    mOldTouchedPosition = INVALID_POSITION
                }
            }
        }
        return isIntercepted
    }

    companion object {
        private const val INVALID_POSITION = -1
    }
}