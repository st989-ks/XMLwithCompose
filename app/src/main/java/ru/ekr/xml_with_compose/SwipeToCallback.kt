package ru.ekr.xml_with_compose

import android.graphics.Canvas
import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

private const val TAG = "SwipeToCallback"

abstract class SwipeToCallback
    : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.START or ItemTouchHelper.END) {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        if (!canMovePosition(viewHolder)) return 0
        return super.getMovementFlags(recyclerView, viewHolder)
    }

    open fun canMovePosition(item: RecyclerView.ViewHolder): Boolean = true

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ) = false

    @Suppress("UNCHECKED_CAST")
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val view = (viewHolder as? AdapterRecyclerXML.HolderForeXML) ?: run {
            Log.e(TAG, "onSwiped: view error casts")
            return
        }
        when (direction) {
            ItemTouchHelper.START -> onSwipeEndToStart(view)
            ItemTouchHelper.END -> onSwipeStartToEnd(view)
            else -> Log.e(TAG, "onSwiped: direction does not match")
        }
    }

    open fun onSwipeStartToEnd(viewHolder: AdapterRecyclerXML.HolderForeXML) {}
    open fun onSwipeEndToStart(viewHolder: AdapterRecyclerXML.HolderForeXML) {}

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder) = 0f

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
        val view = (viewHolder as? AdapterRecyclerXML.HolderForeXML)?.getBinding() ?: run {
            Log.e(TAG, "onChildDraw: view error casts")
            return
        }

        val sizeContainer = view.rightContainer.width.toFloat()

        Log.e(TAG, "onChildDraw: actionState ${dX}")

        val dragMoveOnX = dX.coerceIn(-sizeContainer, sizeContainer)

        view.columText.translationX = dragMoveOnX
    }
}