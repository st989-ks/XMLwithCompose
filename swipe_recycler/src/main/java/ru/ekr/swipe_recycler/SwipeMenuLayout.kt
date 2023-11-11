package ru.ekr.swipe_recycler

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
import ru.ekr.swipe_recycler.listener.SwipeFractionListener
import ru.ekr.swipe_recycler.listener.SwipeSwitchListener
import ru.ekr.swipe_recycler.swiper.Swiper
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

private const val DEFAULT_SCROLLER_DURATION = 250
private const val DEFAULT_AUTO_OPEN_PERCENT = 0.5f

abstract class SwipeMenuLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {
    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    protected val scaledTouchSlop = viewConfig.scaledTouchSlop
    protected val scaledMinimumFlingVelocity = viewConfig.scaledMinimumFlingVelocity
    protected val scaledMaximumFlingVelocity = viewConfig.scaledMaximumFlingVelocity
    protected var autoOpenPercent = DEFAULT_AUTO_OPEN_PERCENT
    protected var scrollerDuration = DEFAULT_SCROLLER_DURATION

    protected var lastX = 0f
    protected var downX = 0f

    protected var contentView: View? = null
    protected var beginSwiper: Swiper? = null
    protected var endSwiper: Swiper? = null
    protected var currentSwiper: Swiper? = null

    protected var shouldResetSwiper = false
    protected var dragging = false
    open var isSwipeEnable = true
    protected var scroller: OverScroller = OverScroller(context)
    protected var interpolator: Interpolator? = null
    protected var velocityTracker: VelocityTracker? = null

    protected var swipeSwitchListener: SwipeSwitchListener? = null
    protected var swipeFractionListener: SwipeFractionListener? = null
    protected var decimalFormat: NumberFormat = DecimalFormat(
        "#.00", DecimalFormatSymbols(Locale.getDefault()))

    init {
        if (!isInEditMode) {
            val typeArray =
                context.obtainStyledAttributes(attrs, R.styleable.SwipeMenuLayout, 0, defStyle)

            val interpolatorId = typeArray
                .getResourceId(R.styleable.SwipeMenuLayout_sml_scroller_interpolator, -1)

            if (interpolatorId > 0) interpolator = AnimationUtils
                .loadInterpolator(getContext(), interpolatorId)

            autoOpenPercent =
                typeArray.getFloat(R.styleable.SwipeMenuLayout_sml_auto_open_percent,
                DEFAULT_AUTO_OPEN_PERCENT)

            scrollerDuration =
                typeArray.getInteger(R.styleable.SwipeMenuLayout_sml_scroller_duration,
                    DEFAULT_SCROLLER_DURATION)

            typeArray.recycle()
        }
        scroller = OverScroller(context, interpolator)
    }

    fun smoothOpenBeginMenu() {
        requireNotNull(beginSwiper) { "Not have begin menu!" }
        currentSwiper = beginSwiper ?: return
        smoothOpenMenu()
    }

    fun smoothOpenEndMenu() {
        requireNotNull(endSwiper) { "Not have end menu!" }
        currentSwiper = endSwiper ?: return
        smoothOpenMenu()
    }

    fun smoothCloseBeginMenu() {
        requireNotNull(beginSwiper) { "Not have begin menu!" }
        currentSwiper = beginSwiper ?: return
        smoothCloseMenu()
    }

    fun smoothCloseEndMenu() {
        requireNotNull(endSwiper) { "Not have end menu!" }
        currentSwiper = endSwiper ?: return
        smoothCloseMenu()
    }

    abstract fun smoothOpenMenu(duration: Int)
    fun smoothOpenMenu() = smoothOpenMenu(scrollerDuration)

    abstract fun smoothCloseMenu(duration: Int)
    fun smoothCloseMenu() = smoothCloseMenu(scrollerDuration)

    open fun setSwipeListener(swipeSwitchListener: SwipeSwitchListener) {
        this.swipeSwitchListener = swipeSwitchListener
    }

    abstract fun getMoveLen(event: MotionEvent): Int
    abstract val len: Int

    /**
     * compute finish duration
     *
     * @param event       up event
     * @param velocity velocity
     * @return finish duration
     */
    fun getSwipeDuration(event: MotionEvent, velocity: Int): Int {
        val moveLen = getMoveLen(event)
        val len = len
        val halfLen = len / 2
        val distanceRatio = min(1.0, (1.0 * abs(moveLen) / len))
        val distance = halfLen + halfLen * distanceInfluenceForSnapDuration(distanceRatio)
        val duration = when {
            velocity > 0 -> (4 * Math.round(1000 * abs((distance / velocity)))).toInt()
            else -> {
                val pageDelta = abs(moveLen).toFloat() / len
                ((pageDelta + 1) * 100).toInt()
            }
        }
        return min(duration, scrollerDuration).toInt()
    }

    companion object {
        fun distanceInfluenceForSnapDuration(value: Double): Double {
            var f = value
            f -= 0.5f // center the values about 0.
            f *= (0.3f * Math.PI / 2.0f)
            return sin(f)
        }
    }
}