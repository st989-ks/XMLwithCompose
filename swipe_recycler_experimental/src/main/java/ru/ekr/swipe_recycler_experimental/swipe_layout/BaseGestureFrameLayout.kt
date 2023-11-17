package ru.ekr.swipe_recycler_experimental.swipe_layout

import android.animation.ObjectAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import ru.ekr.swipe_recycler_experimental.R
import kotlin.math.abs


private const val DEFAULT_AUTO_OPEN_PERCENT = 0.44f
private const val DEFAULT_DURATION = 100

abstract class BaseGestureFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    protected lateinit var baseContent: View
    private lateinit var menuLeft: View
    private lateinit var menuRight: View
    private val autoOpenRatio: Float
    private val scrollerDuration: Long
    var objectAnim: ObjectAnimator = ObjectAnimator.ofFloat(
        /* target = */ this,
        /* propertyName = */ "positionX",
        /* ...values = */ 0f
    )
    var isProcessingSwipe = false

    protected var lastX = 0f
    protected var lastY = 0f
    private var isMenuLeftEnable = true
    private var isMenuRightEnable = true
    val isSwipeEnable get() = isMenuLeftEnable || isMenuRightEnable

    var positionX = 0f
        get() = baseContent.x
        set(value) {
            baseContent.translationX = when {
                value <= -widthRight -> -widthRight.toFloat()
                value >= widthLeft -> widthLeft.toFloat()
                value <= 0 && !isMenuRightEnable -> 0f
                value >= 0 && !isMenuLeftEnable -> 0f
                else -> value
            }
            field = baseContent.x
        }

    var positionBufferX = 0f
        set(value) {
            field = when {
                value <= -widthRight -> -widthRight.toFloat()
                value >= widthLeft -> widthLeft.toFloat()
                value <= 0 && !isMenuRightEnable -> 0f
                value >= 0 && !isMenuLeftEnable -> 0f
                else -> value
            }
        }

    protected val isMenuOpenLeft get() = positionX >= menuLeft.width
    protected val isMenuOpenRight get() = positionX <= -menuRight.width
    val isMenuOpen get() = isMenuOpenLeft || isMenuOpenRight
    private val widthLeft get() = menuLeft.width
    private val widthRight get() = menuRight.width
    protected val widthMenuForeOpen
        get() = if (positionBufferX > 0f) widthLeft
        else if (positionBufferX < 0f) widthRight else 0
    val autoOpenWidth get() = widthMenuForeOpen * autoOpenRatio
    val autoCloseWidth get() = widthMenuForeOpen - autoOpenWidth

    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    protected val scaledTouchSlop = viewConfig.scaledEdgeSlop

    init {
        val typeArray =
            context.obtainStyledAttributes(attrs,
                R.styleable.BaseGestureFrameLayout,
                defStyle,
                defStyleRes)
        autoOpenRatio =
            typeArray.getFloat(R.styleable.BaseGestureFrameLayout_gfl_auto_open_ratio,
                DEFAULT_AUTO_OPEN_PERCENT)
        scrollerDuration =
            typeArray.getInt(R.styleable.BaseGestureFrameLayout_gfl_scroller_duration,
                DEFAULT_DURATION).toLong()
        isMenuLeftEnable =
            typeArray.getBoolean(R.styleable.BaseGestureFrameLayout_gfl_left_swipe_enabled, true)
        isMenuRightEnable =
            typeArray.getBoolean(R.styleable.BaseGestureFrameLayout_gfl_right_swipe_enabled, true)
        typeArray.recycle()
    }

    protected fun setDistanceLastEvent(event: MotionEvent) {
        lastX = event.x
        lastY = event.y
    }

    private fun startAnimation(newPositionX: Float, duration: Long = scrollerDuration) {
        if (!objectAnim.isRunning) {
            objectAnim = ObjectAnimator.ofFloat(
                /* target = */ this,
                /* propertyName = */ "positionX",
                /* ...values = */ newPositionX
            ).setDuration(duration)
            objectAnim.start()
        }
    }

    fun setCanRightSwipe(value: Boolean) {
        isMenuRightEnable = value
    }

    fun setCanLeftSwipe(value: Boolean) {
        isMenuLeftEnable = value
    }

    fun setCanSwipe(value: Boolean) {
        isMenuLeftEnable = value
        isMenuRightEnable = value
    }

    fun closeMenu() {
        positionBufferX = 0f
        startAnimation(0f)
    }

    fun openRightMenu()  {
        positionBufferX = widthLeft.toFloat()
        startAnimation(-widthRight.toFloat())
    }
    fun openLeftMenu()  {
        positionBufferX = -widthRight.toFloat()
        startAnimation(widthLeft.toFloat())
    }

    fun isButtonPosition(event: MotionEvent):Boolean{
        val leftPosition = event.x in 0f..widthLeft.toFloat()
        val rightPosition = event.x in (width - widthRight).toFloat()..width.toFloat()
        return leftPosition || rightPosition
    }

    fun isDragInScaled(event: MotionEvent): Boolean {
        val disX = event.x - lastX
        val disY = event.y - lastY
        return abs(disX) > scaledTouchSlop && abs(disX) > abs(disY)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        baseContent = findViewById(R.id.base_content)
        menuLeft = findViewById(R.id.menu_left)
        menuRight = findViewById(R.id.menu_right)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        (baseContent.layoutParams as? LayoutParams)?.apply {
            width = LayoutParams.MATCH_PARENT
            baseContent.layoutParams = this
        }

        (menuRight.layoutParams as? LayoutParams)?.apply {
            gravity = Gravity.END
            menuRight.layoutParams = this
        }

        (menuLeft.layoutParams as? LayoutParams)?.apply {
            gravity = Gravity.START
            menuLeft.layoutParams = this
        }
    }
}