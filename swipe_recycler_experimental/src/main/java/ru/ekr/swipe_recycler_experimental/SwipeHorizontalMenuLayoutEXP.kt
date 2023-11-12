package ru.ekr.swipe_recycler_experimental

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.animation.Animation
import android.widget.OverScroller
import androidx.core.view.ViewCompat
import ru.ekr.swipe_recycler_experimental.swiper.LeftHorizontalSwiperEXP
import ru.ekr.swipe_recycler_experimental.swiper.RightHorizontalSwiperEXP
import kotlin.math.abs

open class SwipeHorizontalMenuLayoutEXP @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SwipeMenuLayoutEXP(context, attrs, defStyle) {

    private var preScrollX: Int = 0

    override var isSwipeEnable: Boolean = true
    override val len: Int = currentSwiperEXP?.menuWidth ?: 0

    val isMenuOpen: Boolean
        get() = ((menuSwiperLeftEXP != null && menuSwiperLeftEXP?.isMenuOpen(scrollX) == true)
                || (menuSwiperRightEXP != null && menuSwiperRightEXP?.isMenuOpen(scrollX) == true))

    val isMenuOpenNotEqual: Boolean
        get() = ((menuSwiperLeftEXP != null && menuSwiperLeftEXP?.isMenuOpenNotEqual(scrollX) == true)
                || (menuSwiperRightEXP != null && menuSwiperRightEXP?.isMenuOpenNotEqual(scrollX) == true))


    override fun getMoveLen(event: MotionEvent) = (event.x - scrollX).toInt()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var isIntercepted: Boolean = super.onInterceptTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                downX = lastX
                isIntercepted = false
            }

            MotionEvent.ACTION_MOVE -> {
                isIntercepted = abs(event.x - downX) > scaledTouchSlop
            }

            MotionEvent.ACTION_UP -> {
                isIntercepted = false
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if ((isMenuOpen && currentSwiperEXP?.isClickOnContentView(this, event.x) == true)) {
                    smoothCloseMenu()
                    isIntercepted = true
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                isIntercepted = false
                if (!scrollerV2.isFinished) scrollerV2.abortAnimation()
            }
        }
        return isIntercepted
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
        // Check if a click is not handled and invoke performClick
        velocityTracker?.addMovement(ev)

        when (ev.action) {
            MotionEvent.ACTION_DOWN -> lastX = ev.x
            MotionEvent.ACTION_MOVE -> if (isSwipeEnable) {
                val disX = (lastX - ev.x)
                lastX = ev.x
                if (!dragging && (abs(disX) > scaledTouchSlop)) dragging = true

                if (dragging) {
                    if (currentSwiperEXP == null || shouldResetSwiper) currentSwiperEXP = when {
                        disX < 0 && menuSwiperLeftEXP == null -> menuSwiperRightEXP
                        disX < 0 && menuSwiperLeftEXP != null -> menuSwiperLeftEXP
                        menuSwiperRightEXP == null -> menuSwiperLeftEXP
                        else -> menuSwiperRightEXP
                    }
                    Log.d("onTouchEventACTION_MOVE", "currentSwiperEXP ${currentSwiperEXP?.javaClass?.name}")
                    scrollBy(disX.toInt(), 0)
                    shouldResetSwiper = false
                }
            }

            MotionEvent.ACTION_UP -> {
                val disX = (downX - ev.x)
                dragging = false
                velocityTracker?.computeCurrentVelocity(1000, scaledMaximumFlingVelocity.toFloat())
                val velocityX = velocityTracker?.xVelocity ?: 0f
                val velocity = abs(velocityX).toInt()
                Log.d("onTouchEventACTION_UP", "velocity $velocity; velocityX $velocityX; scaledMinimumFlingVelocity $scaledMinimumFlingVelocity")
                if (velocity > scaledMinimumFlingVelocity)
                    when (currentSwiperEXP) {
                        is LeftHorizontalSwiperEXP -> {
                            val duration: Int = getSwipeDuration(ev, velocity)

                            if (velocityX > 0) smoothOpenMenu(duration)// just open
                            else smoothCloseMenu(duration)// just close

                            ViewCompat.postInvalidateOnAnimation(this)
                        }

                        is RightHorizontalSwiperEXP -> {
                            val duration: Int = getSwipeDuration(ev, velocity)

                            if (velocityX < 0) smoothOpenMenu(duration) // just open
                            else smoothCloseMenu(duration)// just close

                            ViewCompat.postInvalidateOnAnimation(this)
                        }
                    }
                else judgeOpenClose(disX)

                velocityTracker?.clear()
                velocityTracker?.recycle()
                velocityTracker = null

                if (abs(disX) > scaledTouchSlop
                    || isMenuOpen) { // ignore click listener, cancel this event
                    val motionEvent: MotionEvent = MotionEvent.obtain(ev)
                    motionEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onTouchEvent(motionEvent)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                dragging = false
                if (!scrollerV2.isFinished) scrollerV2.abortAnimation()
                else judgeOpenClose((downX - ev.x))
            }
        }
        return super.onTouchEvent(ev)
    }

    override fun computeScroll() {
        if (scrollerV2.computeScrollOffset()) {
            when (currentSwiperEXP) {
                is RightHorizontalSwiperEXP -> {
                    scrollTo(abs(scrollerV2.currX), 0)
                    invalidate()
                }
                is LeftHorizontalSwiperEXP -> {
                    scrollTo(-abs(scrollerV2.currX), 0)
                    invalidate()
                }
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        contentSwiper?.offsetLeftAndRight(-x)
    }

    private fun judgeOpenClose(dx: Float) = currentSwiperEXP?.let { currentSwiperNotNull ->
        val openThresholdSize = ((currentSwiperNotNull.menuView.width) * autoOpenPercent)
        when {
            abs(scrollX) >= openThresholdSize && abs(dx) > scaledTouchSlop ->  // auto open // swipe up
                if (isMenuOpenNotEqual) smoothCloseMenu() else smoothOpenMenu()

            abs(scrollX) >= openThresholdSize -> // auto open  // normal up
                if (isMenuOpen) smoothCloseMenu() else smoothOpenMenu()

            else -> smoothCloseMenu() // auto close
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        isClickable = true
        contentSwiper = findViewById(R.id.content_view_smlexp)
            ?: throw IllegalArgumentException("Not find content_view by id smContentView")
        menuSwiperLeftEXP = LeftHorizontalSwiperEXP(findViewById(R.id.menu_view_left_smlexp))
        menuSwiperRightEXP = RightHorizontalSwiperEXP(findViewById(R.id.menu_view_right_smlexp))
    }

    override fun smoothOpenMenu(duration: Int) {
        autoOpenMainMenu(scrollerV2, scrollX, duration)
        invalidate()
    }

    override fun smoothCloseMenu(duration: Int) {
        autoCloseMainMenu(scrollerV2, scrollX, duration)
        invalidate()
    }

    private fun autoOpenMainMenu(scroller: OverScroller, scrollDis: Int, duration: Int) {
        currentSwiperEXP?.menuView?.width?.let {
            scroller.startScroll(abs(scrollDis), 0, (it - abs(scrollDis)), 0, duration)
        }
    }
    private fun autoCloseMainMenu(scroller: OverScroller, scrollDis: Int, duration: Int) {
        scroller.startScroll(-abs(scrollDis), 0, abs(scrollDis), 0, duration)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        contentSwiper?.let { contentViewNotNull ->
            val parentViewWidth: Int = measuredWidthAndState
            val contentViewWidth: Int = contentViewNotNull.measuredWidthAndState
            val contentViewHeight: Int = contentViewNotNull.measuredHeightAndState
            var lp: LayoutParams = contentViewNotNull.layoutParams as? LayoutParams ?: return
            val lGap: Int = paddingLeft + lp.leftMargin
            var tGap: Int = paddingTop + lp.topMargin

            contentViewNotNull.layout(
                /* l = */ lGap,
                /* t = */ tGap,
                /* r = */ lGap + contentViewWidth,
                /* b = */ tGap + contentViewHeight
            )

            menuSwiperLeftEXP?.let { beginSwiperNotNull ->
                val menuViewWidth = beginSwiperNotNull.menuView.measuredWidthAndState
                val menuViewHeight = beginSwiperNotNull.menuView.measuredHeightAndState
                lp = beginSwiperNotNull.menuView.layoutParams as? LayoutParams ?: return
                tGap = paddingTop + lp.topMargin

                beginSwiperNotNull.menuView.layout(
                    /* l = */ 0,
                    /* t = */ tGap,
                    /* r = */ menuViewWidth,
                    /* b = */ tGap + menuViewHeight)
            }

            menuSwiperRightEXP?.let { endSwiperNotNull ->
                val menuViewWidth: Int = endSwiperNotNull.menuView.measuredWidthAndState
                val menuViewHeight: Int = endSwiperNotNull.menuView.measuredHeightAndState
                lp = endSwiperNotNull.menuView.layoutParams as? LayoutParams ?: return
                tGap = paddingTop + lp.topMargin

                endSwiperNotNull.menuView.layout(
                    /* l = */ parentViewWidth - menuViewWidth,
                    /* t = */ tGap,
                    /* r = */ parentViewWidth,
                    /* b = */ tGap + menuViewHeight)
            }
        }
    }
}