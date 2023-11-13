package ru.ekr.swipe_recycler_experimental

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import ru.ekr.swipe_recycler_experimental.swiper.SwiperLayout

class SwipeMainLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : FrameLayout(context, attrs, defStyle, defStyleRes) {

    lateinit var swiperContent: SwiperLayout
    private lateinit var menuLeft: View
    private lateinit var menuRight: View

    private var isSwipeEnable: Boolean = true

    override fun onFinishInflate() {
        super.onFinishInflate()
        isClickable = true
        swiperContent = findViewById(R.id.content_view_smlexp)
        menuLeft = findViewById(R.id.menu_view_left_smlexp)
        menuRight = findViewById(R.id.menu_view_right_smlexp)
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean = when (event.action) {
        MotionEvent.ACTION_DOWN -> swiperContent.interceptEventActionDown(event)
        MotionEvent.ACTION_MOVE -> swiperContent.interceptEventActionMove(event)
        MotionEvent.ACTION_UP -> swiperContent.interceptEventActionUP(event)
        MotionEvent.ACTION_CANCEL -> swiperContent.interceptEventActionCancel(event)
        else -> super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        swiperContent.actionPerformClick(event)
        when (event.action) {
            MotionEvent.ACTION_DOWN -> swiperContent.onActionDown(event)
            MotionEvent.ACTION_MOVE -> if (isSwipeEnable) swiperContent.onActionMoved(event)
            MotionEvent.ACTION_UP -> {
                if (swiperContent.onActionUp(event)) {
                    val motionEvent: MotionEvent = MotionEvent.obtain(event)
                    motionEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onTouchEvent(motionEvent)
                }
            }

            MotionEvent.ACTION_CANCEL -> swiperContent.onActionCancel()
        }
        return super.onTouchEvent(event)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        (menuRight.layoutParams as? LayoutParams)?.apply {
            gravity = Gravity.END
            menuRight.layoutParams = this
        }

        (menuLeft.layoutParams as? LayoutParams)?.apply {
            gravity = Gravity.START
            menuLeft.layoutParams = this
        }
        swiperContent.setParamsMenu(menuLeft, menuRight)
    }
}