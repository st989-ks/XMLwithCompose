package ru.ekr.swipe_recycler_experimental

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

private const val INVALID_POSITION = -1

class SwipeMenuRecyclerViewEXP @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RecyclerView(context, attrs, defStyle) {


    private var menuSwipedView: SwipeHorizontalMenuLayoutEXP? = null
    private var menuTouchedPosition = INVALID_POSITION

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = when {
        event.actionIndex != 0 -> true
        event.action == MotionEvent.ACTION_DOWN -> doInActionDown(event)
        else -> super.onInterceptTouchEvent(event)
    }

    private fun doInActionDown(event: MotionEvent): Boolean {
        var isIntercepted = super.onInterceptTouchEvent(event)
        findChildViewUnder(event.x, event.y)?.let { findChild ->
            val touchingPosition = getChildAdapterPosition(findChild)
            closeTheSwipeMenuAndInterceptTheEvent(
                touchingPosition = touchingPosition,
                onUpdate = { isIntercepted = it })

            updateMenuWhenFindHolder(touchingPosition)

            if (isIntercepted) resetSwipeItems()
        }
        return isIntercepted
    }

    /**The position is determined by the Holder and updated.*/
    private fun updateMenuWhenFindHolder(touchingPosition: Int) =
        findViewHolderForAdapterPosition(touchingPosition)?.let { viewHolder ->
            castInSwipeHorizontalMenuLayout(viewHolder)?.let { itemView ->
                menuSwipedView = itemView
                menuTouchedPosition = touchingPosition
            }
        }


    private fun castInSwipeHorizontalMenuLayout(
        viewHolder: ViewHolder
    ): SwipeHorizontalMenuLayoutEXP? = (viewHolder.itemView as? ViewGroup)?.let {
        getSwipeMenuView(it) as? SwipeHorizontalMenuLayoutEXP
    }


    /** if we intercept the event, just reset*/
    fun resetSwipeItems() {
        menuSwipedView = null
        menuTouchedPosition = INVALID_POSITION
    }

    /**already one swipe menu is opened, so we close it and intercept the event*/
    private fun closeTheSwipeMenuAndInterceptTheEvent(
        touchingPosition: Int,
        onUpdate: (Boolean) -> Unit,
    ) = menuSwipedView?.let { oldSwipedView ->
        if (touchingPosition != menuTouchedPosition && oldSwipedView.isMenuOpen) {
            oldSwipedView.smoothCloseMenu()
            onUpdate.invoke(true)
        }
    }

    private fun getSwipeMenuView(itemView: ViewGroup): View {
        if (itemView is SwipeHorizontalMenuLayoutEXP) return itemView
        val unvisited = mutableListOf<View>()
        unvisited.add(itemView)

        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0) as? ViewGroup ?: continue
            if (child is SwipeHorizontalMenuLayoutEXP) return child
            for (i in 0 until child.childCount) unvisited.add(child.getChildAt(i))
        }
        return itemView
    }
}