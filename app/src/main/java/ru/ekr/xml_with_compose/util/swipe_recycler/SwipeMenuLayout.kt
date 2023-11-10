package ru.ekr.xml_with_compose.util.swipe_recycler

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.OverScroller
import ru.ekr.xml_with_compose.R
import ru.ekr.xml_with_compose.util.swipe_recycler.listener.SwipeFractionListener
import ru.ekr.xml_with_compose.util.swipe_recycler.listener.SwipeSwitchListener
import ru.ekr.xml_with_compose.util.swipe_recycler.swiper.Swiper
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin


abstract class SwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    protected var mAutoOpenPercent = DEFAULT_AUTO_OPEN_PERCENT
    protected var mScrollerDuration = DEFAULT_SCROLLER_DURATION
    protected var mScaledTouchSlop = 0
    protected var mLastX = 0f
    protected var mLastY = 0f
    protected var mDownX = 0f
    protected var mDownY = 0f
    protected var mContentView: View? = null
    protected var mBeginSwiper: Swiper? = null
    protected var mEndSwiper: Swiper? = null
    protected var mCurrentSwiper: Swiper? = null
    protected var shouldResetSwiper = false
    protected var mDragging = false
    open var isSwipeEnable = true
    protected var mScroller: OverScroller? = null
    protected var mInterpolator: Interpolator? = null
    protected var mVelocityTracker: VelocityTracker? = null
    protected var mScaledMinimumFlingVelocity = 0
    protected var mScaledMaximumFlingVelocity = 0
    protected var mSwipeSwitchListener: SwipeSwitchListener? = null
    protected var mSwipeFractionListener: SwipeFractionListener? = null
    protected var mDecimalFormat: NumberFormat =
        DecimalFormat("#.00", DecimalFormatSymbols(Locale.US))

    init {
        if (!isInEditMode) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.SwipeMenu, 0, defStyle)
            val interpolatorId =
                a.getResourceId(R.styleable.SwipeMenu_sml_scroller_interpolator, -1)
            if (interpolatorId > 0) mInterpolator =
                AnimationUtils.loadInterpolator(getContext(), interpolatorId)
            mAutoOpenPercent =
                a.getFloat(R.styleable.SwipeMenu_sml_auto_open_percent, DEFAULT_AUTO_OPEN_PERCENT)
            mScrollerDuration =
                a.getInteger(R.styleable.SwipeMenu_sml_scroller_duration, DEFAULT_SCROLLER_DURATION)
            a.recycle()
        }
        init()
    }

    fun smoothOpenBeginMenu() {
        requireNotNull(mBeginSwiper) { "Not have begin menu!" }
        mCurrentSwiper = mBeginSwiper
        smoothOpenMenu()
    }

    fun smoothOpenEndMenu() {
        requireNotNull(mEndSwiper) { "Not have end menu!" }
        mCurrentSwiper = mEndSwiper
        smoothOpenMenu()
    }

    fun smoothCloseBeginMenu() {
        requireNotNull(mBeginSwiper) { "Not have begin menu!" }
        mCurrentSwiper = mBeginSwiper
        smoothCloseMenu()
    }

    fun smoothCloseEndMenu() {
        requireNotNull(mEndSwiper) { "Not have end menu!" }
        mCurrentSwiper = mEndSwiper
        smoothCloseMenu()
    }

    abstract fun smoothOpenMenu(duration: Int)
    fun smoothOpenMenu() {
        smoothOpenMenu(mScrollerDuration)
    }

    abstract fun smoothCloseMenu(duration: Int)
    fun smoothCloseMenu() {
        smoothCloseMenu(mScrollerDuration)
    }

    fun init() {
        val mViewConfig = ViewConfiguration.get(
            context)
        mScaledTouchSlop = mViewConfig.scaledTouchSlop
        mScroller = OverScroller(context, mInterpolator)
        mScaledMinimumFlingVelocity = mViewConfig.scaledMinimumFlingVelocity
        mScaledMaximumFlingVelocity = mViewConfig.scaledMaximumFlingVelocity
    }

    open fun setSwipeListener(swipeSwitchListener: SwipeSwitchListener) {
        mSwipeSwitchListener = swipeSwitchListener
    }

    fun setSwipeFractionListener(swipeFractionListener: SwipeFractionListener) {
        mSwipeFractionListener = swipeFractionListener
    }

    abstract fun getMoveLen(event: MotionEvent): Int
    abstract val len: Int

    /**
     * compute finish duration
     *
     * @param ev       up event
     * @param velocity velocity
     * @return finish duration
     */
    fun getSwipeDuration(ev: MotionEvent, velocity: Int): Int {
        val moveLen = getMoveLen(ev)
        val len = len
        val halfLen = len / 2
        val distanceRatio =
            min(1.0, (1.0f * abs(moveLen.toDouble()) / len).toDouble()).toFloat()
        val distance = halfLen + halfLen *
                distanceInfluenceForSnapDuration(distanceRatio)
        var duration: Int
        duration = if (velocity > 0) {
            (4 * Math.round(1000 * abs((distance / velocity).toDouble()))).toInt()
        } else {
            val pageDelta = abs(moveLen.toDouble()).toFloat() / len
            ((pageDelta + 1) * 100).toInt()
        }
        duration = min(duration.toDouble(), mScrollerDuration.toDouble()).toInt()
        return duration
    }

    fun distanceInfluenceForSnapDuration(f: Float): Float {
        var f = f
        f -= 0.5f // center the values about 0.
        f *= (0.3f * Math.PI / 2.0f).toFloat()
        return sin(f.toDouble()).toFloat()
    }

    companion object {
        const val TAG = "sml"
        const val DEFAULT_SCROLLER_DURATION = 250
        const val DEFAULT_AUTO_OPEN_PERCENT = 0.5f
    }
}