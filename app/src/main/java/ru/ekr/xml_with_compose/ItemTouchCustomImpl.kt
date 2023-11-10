package ru.ekr.xml_with_compose

import android.graphics.Canvas
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.ItemTouchUIUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.R

internal class ItemTouchCustomImpl : ItemTouchUIUtil {
    override fun onDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        view: View,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        view.translationX = dX
        view.translationY = dY
    }

    override fun onDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        view: View,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
    }

    override fun clearView(view: View) {
        view.translationX = 0f
        view.translationY = 0f
    }

    override fun onSelected(view: View) {}

    companion object {
        val INSTANCE: ItemTouchUIUtil = ItemTouchCustomImpl()
    }
}

