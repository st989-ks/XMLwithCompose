package ru.ekr.swipe_recycler_experimental.swiper

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import ru.ekr.swipe_recycler_experimental.R
import kotlin.math.abs

private const val DEFAULT_SCROLLER_DURATION = 100
private const val DEFAULT_AUTO_OPEN_PERCENT = 0.2f

class SwiperLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    val autoOpenPercent: Float

    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    val scaledTouchSlop = viewConfig.scaledTouchSlop


    private var menuLeft: SwipeButton = LeftMenuSwipe()
    private var menuRight: SwipeButton = RightMenuSwipe()
    private var currentMenu: SwipeButton? = null

    private var velocityTracker: VelocityTracker? = null
    protected val scaledMinimumFlingVelocity = viewConfig.scaledMinimumFlingVelocity
    protected val scaledMaximumFlingVelocity = viewConfig.scaledMaximumFlingVelocity

    private var lastX = 0f
    private var downX = 0f
    private var dragging = false
    private var shouldResetSwiper = false

    fun setParamsMenu(leftMenu: View, rightMenu: View) {
        menuLeft = LeftMenuSwipe(leftMenu)
        menuRight = RightMenuSwipe(rightMenu)
    }

    val isMenuOpen: Boolean
        get() = menuLeft.isMenuOpen(x.toInt()) || menuRight.isMenuOpen(x.toInt())

    init {
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.SwiperLayout, 0, defStyle)
        autoOpenPercent = typeArray.getFloat(
            R.styleable.SwiperLayout_sml_auto_open_percent, DEFAULT_AUTO_OPEN_PERCENT)
        typeArray.recycle()
    }


    fun smoothCloseMenu(duration: Int = DEFAULT_SCROLLER_DURATION) {
        animate().setDuration(duration.toLong()).x(0f)
        invalidate()
    }

    private fun smoothOpenMenu(duration: Int = DEFAULT_SCROLLER_DURATION) {
        val scrollEnd = if (this.x < 0) -menuRight.width else menuLeft.width
        animate().setDuration(duration.toLong()).x(scrollEnd.toFloat())
        invalidate()
    }

    private fun judgeActionOpenClose() {
        val disOpen = (currentMenu?.width ?: 0) * autoOpenPercent
        if (abs(this.x).toInt() >= disOpen) {
            smoothOpenMenu()
        } else {
            smoothCloseMenu()
        }
    }

    /**TODO(Current Event Acton)*/
    fun onActionDown(event: MotionEvent) {
        lastX = event.x
    }

    fun actionPerformClick(event: MotionEvent) {
        if (velocityTracker == null) velocityTracker = VelocityTracker.obtain()
        // Check if a click is not handled and invoke performClick
        velocityTracker?.addMovement(event)
    }

    fun performClickClear() {
        velocityTracker?.clear()
        velocityTracker?.recycle()
        velocityTracker = null
    }

    fun onActionMoved(event: MotionEvent) {
        val disX = lastX - event.x
        if (!dragging && (abs(disX) > scaledTouchSlop)) dragging = true
        if (dragging) {
            if (currentMenu == null || shouldResetSwiper) currentMenu = when {
                disX < 0 -> menuLeft
                else -> menuRight
            }
            val newX = this.x - disX

            when {
                newX > menuLeft.width -> this.x = menuLeft.width.toFloat()
                newX < -menuRight.width -> this.x = -menuRight.width.toFloat()
                else -> this.x -= disX
            }

            computeResetChecker()
            lastX = event.x
            shouldResetSwiper = false
        }
    }

    fun onActionUp(event: MotionEvent): Boolean {
        val disX: Float = (downX - event.x)
        dragging = false
        velocityTracker?.computeCurrentVelocity(1000, scaledMaximumFlingVelocity.toFloat())
        val velocityX = velocityTracker?.xVelocity ?: 0f
        val velocity = abs(velocityX).toInt()

        if (velocity > scaledMinimumFlingVelocity) judgeActionOpenClose()

        performClickClear()
        computeResetChecker()
        return abs(disX) > scaledTouchSlop || isMenuOpen
    }


    fun onActionCancel() {
        dragging = false
        this.clearAnimation()
        this.animate().cancel()
        judgeActionOpenClose()
    }

    fun computeResetChecker() {
        shouldResetSwiper = this.x.toInt() == 0
    }


    /**TODO(Intercept Event)*/
    fun interceptEventActionDown(event: MotionEvent): Boolean {
        lastX = event.x
        downX = lastX
        return false
    }

    fun interceptEventActionMove(event: MotionEvent): Boolean {
        abs(event.x - downX) > scaledTouchSlop
        return true
    }

    fun interceptEventActionUP(event: MotionEvent): Boolean {
        var isIntercepted = false
        // menu view opened and click on content view,
        // we just close the menu view and intercept the up event
        /**TODO("Тут осталась проблема")*/
        if (isMenuOpen && currentMenu?.isClickOnContentView(this, event.x) == true) {
            smoothCloseMenu()
//            isIntercepted = true
        }
        return isIntercepted
    }

    fun interceptEventActionCancel(event: MotionEvent): Boolean {
        return false
    }
}