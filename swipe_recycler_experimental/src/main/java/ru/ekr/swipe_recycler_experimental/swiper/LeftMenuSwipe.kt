package ru.ekr.swipe_recycler_experimental.swiper

import android.view.View

class LeftMenuSwipe(view: View? = null) : SwipeButton {
    override val width: Int = view?.width ?: 0
    override val height: Int = view?.height ?: 0

    override fun isMenuOpen(scrollDis: Int): Boolean = scrollDis <= -width

    override fun isClickOnContentView(contentView: View, clickPoint: Float): Boolean {
        return clickPoint > width
    }
}