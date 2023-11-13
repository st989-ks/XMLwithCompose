package ru.ekr.swipe_recycler.swiper

import android.util.Log
import android.view.View
import android.widget.OverScroller
import kotlin.math.abs

class RightHorizontalSwiper(menuView: View) : Swiper(END_DIRECTION, menuView) {

    override fun isMenuOpen(scrollDis: Int): Boolean {
        return scrollDis >= -menuView.width * direction
    }

    override fun isMenuOpenNotEqual(scrollDis: Int): Boolean {
        return scrollDis > -menuView.width * direction
    }

    override fun autoOpenMenu(scroller: OverScroller, scrollDis: Int, duration: Int) {
        scroller.startScroll(abs(scrollDis), 0, (menuView.width - abs(scrollDis)), 0, duration)
    }

    override fun autoCloseMenu(scroller: OverScroller, scrollDis: Int, duration: Int) {
        scroller.startScroll((-abs(scrollDis)), 0, abs(scrollDis), 0, duration)
    }

    override fun checkXY(x: Int): Checker {
        mChecker.x = x
        mChecker.shouldResetSwiper = false
        if (mChecker.x == 0) {
            mChecker.shouldResetSwiper = true
        }
        if (mChecker.x < 0) {
            mChecker.x = 0
        }
        if (mChecker.x > menuView.width) {
            mChecker.x = menuView.width
        }
        return mChecker
    }

    override fun isClickOnContentView(contentView: View?, clickPoint: Float): Boolean {
        return clickPoint < contentView!!.width - menuView.width
    }
}