package ru.ekr.xml_with_compose.util.swipe_recycler.swiper

import android.view.View
import android.widget.OverScroller
import kotlin.math.abs

/**
 * Created by tubingbing on 16/4/11.
 */
class RightHorizontalSwiper(menuView: View?) : Swiper(END_DIRECTION, menuView!!) {
    override fun isMenuOpen(scrollDis: Int): Boolean {
        return scrollDis >= -menuView.width * direction
    }

    override fun isMenuOpenNotEqual(scrollDis: Int): Boolean {
        return scrollDis > -menuView.width * direction
    }

    override fun autoOpenMenu(scroller: OverScroller?, scrollDis: Int, duration: Int) {
        scroller!!.startScroll(abs(scrollDis.toDouble())
            .toInt(), 0, (menuView.width - abs(scrollDis.toDouble())).toInt(), 0, duration)
    }

    override fun autoCloseMenu(scroller: OverScroller?, scrollDis: Int, duration: Int) {
        scroller!!.startScroll((-abs(scrollDis.toDouble())).toInt(), 0, abs(scrollDis.toDouble())
            .toInt(), 0, duration)
    }

    override fun checkXY(x: Int, y: Int): Checker? {
        mChecker.x = x
        mChecker.y = y
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

    override fun isClickOnContentView(contentView: View?, x: Float): Boolean {
        return x < contentView!!.width - menuView.width
    }
}