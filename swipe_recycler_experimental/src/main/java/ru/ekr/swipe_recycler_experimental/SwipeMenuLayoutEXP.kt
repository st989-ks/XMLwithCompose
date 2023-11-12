package ru.ekr.swipe_recycler_experimental

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AnimationUtils
import android.view.animation.Interpolator
import android.widget.FrameLayout
import android.widget.OverScroller
import ru.ekr.swipe_recycler_experimental.swiper.SwiperEXP
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.Locale
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.sin

private const val DEFAULT_SCROLLER_DURATION = 250
private const val DEFAULT_AUTO_OPEN_PERCENT = 0.5f

abstract class SwipeMenuLayoutEXP @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    protected val scaledTouchSlop = viewConfig.scaledTouchSlop
    protected val scaledMinimumFlingVelocity = viewConfig.scaledMinimumFlingVelocity
    protected val scaledMaximumFlingVelocity = viewConfig.scaledMaximumFlingVelocity

    private  val typeArray = context.obtainStyledAttributes(
        attrs, R.styleable.SwipeMenuLayoutEXP, 0, defStyle)

    private  val interpolatorIdV2 = typeArray
        .getResourceId(R.styleable.LinearLayout_content_view_smlexp_scroller_interpolator_content, -1)

    protected var autoOpenPercent =  typeArray.getFloat(
        R.styleable.SwipeMenuLayoutEXP_smlexp_auto_open_percent, DEFAULT_AUTO_OPEN_PERCENT)

    protected var scrollerDuration = typeArray.getInteger(
        R.styleable.SwipeMenuLayoutEXP_smlexp_scroller_duration, DEFAULT_SCROLLER_DURATION)


    protected var lastX = 0f
    protected var downX = 0f

    protected var contentSwiper: View? = null
    protected var menuSwiperLeftEXP: SwiperEXP? = null
    protected var menuSwiperRightEXP: SwiperEXP? = null
    protected var currentSwiperEXP: SwiperEXP? = null

    protected var shouldResetSwiper = false
    protected var dragging = false
    open var isSwipeEnable = true

    protected var interpolatorV2: Interpolator? =if (interpolatorIdV2 > 0)
        AnimationUtils.loadInterpolator(getContext(), interpolatorIdV2)
    else null
    protected var scrollerV2: OverScroller = OverScroller(context, interpolatorV2)




    protected var velocityTracker: VelocityTracker? = null

    init { typeArray.recycle() }

    abstract fun smoothOpenMenu(duration: Int)
    fun smoothOpenMenu() = smoothOpenMenu(scrollerDuration)

    abstract fun smoothCloseMenu(duration: Int)
    fun smoothCloseMenu() = smoothCloseMenu(scrollerDuration)

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
        Log.e("Swipe", "getSwipeDuration x - y: ${event.x} - ${event.y}  velocity: $velocity")
        val moveLen = getMoveLen(event)
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
        return min(duration, scrollerDuration)
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