package ru.ekr.swipe_recycler_experimental.swiper

import android.view.View


sealed interface SwipeButton {
    val width: Int
    val height: Int

    fun isMenuOpen(scrollDis: Int): Boolean
    fun isClickOnContentView(contentView: View, clickPoint: Float): Boolean


}
