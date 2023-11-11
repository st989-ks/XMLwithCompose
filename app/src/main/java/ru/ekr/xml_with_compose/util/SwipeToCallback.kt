package ru.ekr.xml_with_compose.util

import android.graphics.Canvas
import android.util.Log
import androidx.core.view.isGone
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import ru.ekr.xml_with_compose.screen_xml_non_stop.AdapterRecyclerXML

private const val TAG = "SwipeToCallback"

abstract class SwipeToCallback(private val threshold: Float = 0.4f) :
    ItemTouchHelper.SimpleCallback(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START or ItemTouchHelper.END) {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (!canMovePosition(viewHolder)) return ItemTouchHelper.ACTION_STATE_IDLE
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    abstract fun canMovePosition(item: RecyclerView.ViewHolder): Boolean

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false


    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = threshold

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        (viewHolder as? AdapterRecyclerXML.HolderForeXML)?.apply {
            when (direction) {
                ItemTouchHelper.START -> onSwipeEndToStart(this)
                ItemTouchHelper.END -> onSwipeStartToEnd(this)
                else -> Log.e(TAG, "onSwiped: direction does not match")
            }
        }
    }

    open fun onSwipeEndToStart(viewHolder: AdapterRecyclerXML.HolderForeXML) {
        onClearView(viewHolder)
    }

    open fun onSwipeStartToEnd(viewHolder: AdapterRecyclerXML.HolderForeXML) {
        onClearView(viewHolder)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        (viewHolder as? AdapterRecyclerXML.HolderForeXML)?.apply {
            onClearView(this)
        }
    }

    open fun onClearView(viewHolder: AdapterRecyclerXML.HolderForeXML) {
        viewHolder.getBinding().apply {
            columText.translationX = 0f
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        (viewHolder as? AdapterRecyclerXML.HolderForeXML)?.getBinding()?.apply {
            when {
                dX < 0 -> {
                    leftContainer.isGone = true
                    rightContainer.isGone = false
                }

                dX > 0 -> {
                    leftContainer.isGone = false
                    rightContainer.isGone = true
                }
            }
            columText.translationX = dX
        }
    }
}