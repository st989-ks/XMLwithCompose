package ru.ekr.swipe_recycler_experimental.swipe_layout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import ru.ekr.swipe_recycler_experimental.R
import kotlin.math.abs

private const val DEFAULT_AUTO_OPEN_PERCENT = 0.44f
private const val DEFAULT_DURATION = 100

@SuppressLint("CustomViewStyleable")
abstract class BaseSwipeLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    private lateinit var baseContent: View
    private lateinit var menuLeft: View
    private lateinit var menuRight: View
    private val autoOpenRatio: Float
    private val scrollerDuration: Long

    protected var lastX = 0f
    protected var downX = 0f
    protected var dragging = false
    private var isMenuLeftEnable = true
    private var isMenuRightEnable = true
    val isSwipeEnable get() = isMenuLeftEnable || isMenuRightEnable

    var positionX = 0f
        get() = baseContent.x
        set(value) {
            baseContent.x = when {
                value <= -widthRight -> -widthRight.toFloat()
                value >= widthLeft -> widthLeft.toFloat()
                value <= 0 && !isMenuRightEnable -> 0f
                value >= 0 && !isMenuLeftEnable -> 0f
                else -> value
            }
            field = baseContent.x
        }

    private val isMenuPreparedOpenLeft get() = positionX > 0
    private val isMenuPreparedOpenRight get() = positionX < 0
    private val isMenuOpenLeft get() = positionX >= menuLeft.width
    private val isMenuOpenRight get() = positionX <= -menuRight.width
    val isMenuOpen get() = isMenuOpenLeft || isMenuOpenRight
    private val widthLeft get() = menuLeft.width
    private val widthRight get() = menuRight.width
    private val widthMenuOpen
        get() = if (isMenuPreparedOpenLeft) widthLeft
        else if (isMenuPreparedOpenRight) widthRight else 0
    private val isNeedOpen get() = abs(positionX) >= (widthMenuOpen * autoOpenRatio)
    private val isNeedOpenLeft get() = isNeedOpen && isMenuPreparedOpenLeft
    private val isNeedOpenRight get() = isNeedOpen && isMenuPreparedOpenRight

    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    protected val scaledTouchSlop = viewConfig.scaledTouchSlop

    init {
        val typeArray =
            context.obtainStyledAttributes(attrs, R.styleable.SwipeLayout, defStyle, defStyleRes)
        autoOpenRatio =
            typeArray.getFloat(R.styleable.SwipeLayout_auto_open_ratio, DEFAULT_AUTO_OPEN_PERCENT)
        scrollerDuration =
            typeArray.getInt(R.styleable.SwipeLayout_scroller_duration, DEFAULT_DURATION).toLong()
        typeArray.recycle()
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

    private fun onAnimateCancel() {
        baseContent.clearAnimation()
        baseContent.animate().cancel()
    }

    fun closeMenu() {
        onAnimateCancel()
        baseContent.animate().setDuration(scrollerDuration).x(0f)
        invalidate()
    }

    private fun openRightMenu() {
        onAnimateCancel()
        baseContent.animate().setDuration(scrollerDuration).x(-widthRight.toFloat())
        invalidate()
    }

    private fun openLeftMenu() {
        onAnimateCancel()
        baseContent.animate().setDuration(scrollerDuration).x(widthLeft.toFloat())
        invalidate()
    }

    fun judgeActionOpenClose() = when {
        isNeedOpenLeft && isMenuPreparedOpenLeft -> openLeftMenu()
        isNeedOpenRight && isMenuPreparedOpenRight -> openRightMenu()
        isMenuOpenLeft -> {}
        isMenuOpenRight -> {}
        else -> closeMenu()
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
            menuRight.layoutParams = this
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