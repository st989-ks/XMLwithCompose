package ru.ekr.swipe_recycler_experimental.swiper

import android.view.View
import android.widget.OverScroller
import kotlin.math.abs

class LeftHorizontalSwiperEXP(menuView: View) : SwiperEXP(BEGIN_DIRECTION, menuView) {
    override fun isMenuOpen(scrollDis: Int): Boolean {
        return scrollDis <= -menuView.width * direction
    }

    override fun isMenuOpenNotEqual(scrollDis: Int): Boolean {
        return scrollDis < -menuView.width * direction
    }

    override fun checkX(x: Int): Checker {
        mChecker.x = x
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

    override fun isClickOnContentView(contentView: View?, clickPoint: Float): Boolean {
        return clickPoint > menuView.width
    }
}