package ru.ekr.swipe_recycler_experimental.swiper

import android.view.View
import android.widget.OverScroller


abstract class SwiperEXP(
    val direction: Int,
    val menuView: View
) {

    protected var mChecker: Checker = Checker()
    abstract fun isMenuOpen(scrollDis: Int): Boolean
    abstract fun isMenuOpenNotEqual(scrollDis: Int): Boolean
    abstract fun checkX(x: Int): Checker
    abstract fun isClickOnContentView(contentView: View?, clickPoint: Float): Boolean

    val menuWidth: Int
        get() = menuView.width
    val menuHeight: Int
        get() = menuView.height

    class Checker {
        var x = 0
        var shouldResetSwiper = false
    }

    companion object {
        const val BEGIN_DIRECTION = 1
        const val END_DIRECTION = -1
    }
}