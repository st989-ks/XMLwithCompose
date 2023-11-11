package ru.ekr.swipe_recycler

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import androidx.core.view.ViewCompat
import ru.ekr.swipe_recycler.listener.SwipeSwitchListener
import ru.ekr.swipe_recycler.swiper.LeftHorizontalSwiper
import ru.ekr.swipe_recycler.swiper.RightHorizontalSwiper
import ru.ekr.swipe_recycler.swiper.Swiper
import kotlin.math.abs

open class SwipeHorizontalMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : SwipeMenuLayout(context, attrs, defStyle) {
    protected var preScrollX: Int = 0
    protected var preLeftMenuFraction: Float = -1f
    protected var preRightMenuFraction: Float = -1f
    override var isSwipeEnable: Boolean = true
    override val len: Int = currentSwiper?.menuWidth ?: 0

    val isMenuOpen: Boolean
        get() = ((menuSwiperLeft != null && menuSwiperLeft?.isMenuOpen(scrollX) == true)
                || (menuSwiperRight != null && menuSwiperRight?.isMenuOpen(scrollX) == true))

    val isMenuOpenNotEqual: Boolean
        get() {
            return ((menuSwiperLeft != null && menuSwiperLeft?.isMenuOpenNotEqual(scrollX) == true)
                    || (menuSwiperRight != null && menuSwiperRight?.isMenuOpenNotEqual(scrollX) == true))
        }


    override fun setSwipeListener(swipeSwitchListener: SwipeSwitchListener) {
        this.swipeSwitchListener = swipeSwitchListener
    }

    override fun getMoveLen(event: MotionEvent) = (event.x - scrollX).toInt()

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var isIntercepted: Boolean = super.onInterceptTouchEvent(event)
        val action: Int = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = event.x
                downX = lastX
                isIntercepted = false
            }

            MotionEvent.ACTION_MOVE -> {
                val disX = (event.x - downX)
                isIntercepted = abs(disX) > scaledTouchSlop
            }

            MotionEvent.ACTION_UP -> {
                isIntercepted = false
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if ((isMenuOpen && currentSwiper?.isClickOnContentView(this, event.x) == true)) {
                    smoothCloseMenu()
                    isIntercepted = true
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                isIntercepted = false
                if (!scroller.isFinished) scroller.abortAnimation()
            }
        }
        return isIntercepted
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
        // Check if a click is not handled and invoke performClick
        velocityTracker?.addMovement(ev)
        val dx: Float
        val action: Int = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
            }

            MotionEvent.ACTION_MOVE -> if (isSwipeEnable) {
                val disX = (lastX - ev.x)

                if (!dragging
                    && (abs(disX) > scaledTouchSlop)
                ) {
                    dragging = true
                }

                if (dragging) {
                    if (currentSwiper == null || shouldResetSwiper) {
                        currentSwiper = when {
                            disX < 0 && menuSwiperLeft == null -> menuSwiperRight
                            disX < 0 && menuSwiperLeft != null -> menuSwiperLeft
                            menuSwiperRight == null -> menuSwiperLeft
                            else -> menuSwiperRight
                        }
                    }
                    scrollBy(disX.toInt(), 0)
                    lastX = ev.x
                    shouldResetSwiper = false
                }
            }

            MotionEvent.ACTION_UP -> {
                dx = (downX - ev.x)
                dragging = false
                velocityTracker?.computeCurrentVelocity(1000, scaledMaximumFlingVelocity.toFloat())
                val velocityX = velocityTracker?.xVelocity ?: 0f
                val velocity = abs(velocityX).toInt()
                if (velocity > scaledMinimumFlingVelocity) {
                    if (currentSwiper != null) {
                        val duration: Int = getSwipeDuration(ev, velocity)
                        if (currentSwiper is RightHorizontalSwiper) {
                            if (velocityX < 0) { // just open
                                smoothOpenMenu(duration)
                            } else { // just close
                                smoothCloseMenu(duration)
                            }
                        } else {
                            if (velocityX > 0) { // just open
                                smoothOpenMenu(duration)
                            } else { // just close
                                smoothCloseMenu(duration)
                            }
                        }
                        ViewCompat.postInvalidateOnAnimation(this)
                    }
                } else {
                    judgeOpenClose(dx)
                }
                velocityTracker?.clear()
                velocityTracker?.recycle()
                velocityTracker = null

                if (abs(dx) > scaledTouchSlop || isMenuOpen
                ) { // ignore click listener, cancel this event
                    val motionEvent: MotionEvent = MotionEvent.obtain(ev)
                    motionEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onTouchEvent(motionEvent)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                dragging = false
                if (!scroller.isFinished) {
                    scroller.abortAnimation()
                } else {
                    dx = (downX - ev.x)
                    judgeOpenClose(dx)
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun judgeOpenClose(dx: Float) = currentSwiper?.let { currentSwiperNotNull ->
        val openThresholdSize = ((currentSwiperNotNull.menuView.width) * autoOpenPercent)
        when {
            abs(scrollX) >= openThresholdSize && abs(dx) > scaledTouchSlop ->  // auto open // swipe up
                if (isMenuOpenNotEqual) smoothCloseMenu() else smoothOpenMenu()

            abs(scrollX) >= openThresholdSize -> // auto open  // normal up
                if (isMenuOpen) smoothCloseMenu() else smoothOpenMenu()

            else -> smoothCloseMenu() // auto close
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        val checker: Swiper.Checker = currentSwiper?.checkXY(x) ?: return
        shouldResetSwiper = checker.shouldResetSwiper

        if (checker.x != scrollX) super.scrollTo(checker.x,0)

        if (scrollX != preScrollX) {
            val absScrollX: Int = abs(scrollX)
            if (currentSwiper is LeftHorizontalSwiper) {
                if (absScrollX == 0) {
                    swipeSwitchListener?.beginMenuClosed(this)
                } else if (absScrollX == menuSwiperLeft?.menuWidth) {
                    swipeSwitchListener?.beginMenuOpened(
                        this)
                }

                if (swipeFractionListener != null) {
                    var fraction: Float = absScrollX.toFloat() / (menuSwiperLeft?.menuWidth ?: 1)
                    fraction = decimalFormat.format(fraction).toFloat()
                    if (fraction != preLeftMenuFraction) {
                        swipeFractionListener?.beginMenuSwipeFraction(this, fraction)
                    }
                    preLeftMenuFraction = fraction
                }
            } else {

                if (absScrollX == 0) {
                    swipeSwitchListener?.endMenuClosed(this)
                } else if (absScrollX == menuSwiperRight?.menuWidth) {
                    swipeSwitchListener?.endMenuOpened(this)
                }

                if (swipeFractionListener != null) {
                    var fraction: Float = absScrollX.toFloat() / (menuSwiperRight?.menuWidth ?: 1)
                    fraction = decimalFormat.format(fraction).toFloat()
                    if (fraction != preRightMenuFraction) {
                        swipeFractionListener?.endMenuSwipeFraction(this, fraction)
                    }
                    preRightMenuFraction = fraction
                }
            }
        }
        preScrollX = scrollX
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val currX: Int = abs(scroller.currX)
            if (currentSwiper is RightHorizontalSwiper) {
                scrollTo(currX, 0)
                invalidate()
            } else {
                scrollTo(-currX, 0)
                invalidate()
            }
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        isClickable = true
        contentView = findViewById(R.id.content_view)
            ?: throw IllegalArgumentException("Not find content_view by id smContentView")
        menuSwiperLeft = LeftHorizontalSwiper(findViewById(R.id.menu_view_left))
        menuSwiperRight = RightHorizontalSwiper(findViewById(R.id.menu_view_right))
    }


    override fun smoothOpenMenu(duration: Int) {
        currentSwiper?.autoOpenMenu(scroller, scrollX, duration)
        invalidate()
    }

    override fun smoothCloseMenu(duration: Int) {
        currentSwiper?.autoCloseMenu(scroller, scrollX, duration)
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.e("overrideonLayout", "onLayout: $changed  $l $t $r $b", )
        contentView?.let { contentViewNotNull ->

            val parentViewWidth: Int = measuredWidthAndState
            val contentViewWidth: Int = contentViewNotNull.measuredWidthAndState
            val contentViewHeight: Int = contentViewNotNull.measuredHeightAndState
            var lp: LayoutParams = contentViewNotNull.layoutParams as? LayoutParams ?: return
            val lGap: Int = paddingLeft + lp.leftMargin
            var tGap: Int = paddingTop + lp.topMargin
            contentViewNotNull.layout(lGap, tGap, lGap + contentViewWidth, tGap + contentViewHeight)



            menuSwiperLeft?.let { beginSwiperNotNull ->
                val menuViewWidth = beginSwiperNotNull.menuView.measuredWidthAndState
                val menuViewHeight = beginSwiperNotNull.menuView.measuredHeightAndState
                lp = beginSwiperNotNull.menuView.layoutParams as? LayoutParams ?: return
                tGap = paddingTop + lp.topMargin
                beginSwiperNotNull.menuView.layout(
                    -menuViewWidth,
                    tGap,
                    0,
                    tGap + menuViewHeight)
            }

            menuSwiperRight?.let { endSwiperNotNull ->
                val menuViewWidth: Int = endSwiperNotNull.menuView.measuredWidthAndState
                val menuViewHeight: Int = endSwiperNotNull.menuView.measuredHeightAndState
                lp = endSwiperNotNull.menuView.layoutParams as? LayoutParams ?: return
                tGap = paddingTop + lp.topMargin
                endSwiperNotNull.menuView.layout(
                    parentViewWidth,
                    tGap,
                    parentViewWidth + menuViewWidth,
                    tGap + menuViewHeight)
            }
        }
    }
}