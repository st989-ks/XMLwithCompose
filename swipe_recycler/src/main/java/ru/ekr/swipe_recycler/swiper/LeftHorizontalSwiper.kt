package ru.ekr.swipe_recycler.swiper

import android.view.View
import android.widget.OverScroller
import kotlin.math.abs

/**
 * Created by tubingbing on 16/4/11.
 */
class LeftHorizontalSwiper(menuView: View?) : Swiper(BEGIN_DIRECTION, menuView!!) {
    override fun isMenuOpen(scrollX: Int): Boolean {
        return scrollX <= -menuView.width * direction
    }

    override fun isMenuOpenNotEqual(scrollX: Int): Boolean {
        return scrollX < -menuView.width * direction
    }

    override fun autoOpenMenu(scroller: OverScroller?, scrollX: Int, duration: Int) {
        scroller!!.startScroll(abs(scrollX.toDouble())
            .toInt(), 0, (menuView.width - abs(scrollX.toDouble())).toInt(), 0, duration)
    }

    override fun autoCloseMenu(scroller: OverScroller?, scrollX: Int, duration: Int) {
        scroller!!.startScroll((-abs(scrollX.toDouble())).toInt(), 0, abs(scrollX.toDouble())
            .toInt(), 0, duration)
    }

    override fun checkXY(x: Int, y: Int): Checker? {
        mChecker.x = x
        mChecker.y = y
        mChecker.shouldResetSwiper = false
        if (mChecker.x == 0) {
            mChecker.shouldResetSwiper = true
        }
        if (mChecker.x >= 0) {
            mChecker.x = 0
        }
        if (mChecker.x <= -menuView.width) {
            mChecker.x = -menuView.width
        }
        return mChecker
    }

    override fun isClickOnContentView(contentView: View?, x: Float): Boolean {
        return x > menuView.width
    }
}