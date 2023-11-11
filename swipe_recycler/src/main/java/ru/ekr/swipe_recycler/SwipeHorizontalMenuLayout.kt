package ru.ekr.swipe_recycler

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import androidx.core.view.ViewCompat
import ru.ekr.swipe_recycler.listener.SwipeSwitchListener
import ru.ekr.swipe_recycler.swiper.LeftHorizontalSwiper
import ru.ekr.swipe_recycler.swiper.RightHorizontalSwiper

import ru.ekr.swipe_recycler.swiper.Swiper
import kotlin.math.abs


open class SwipeHorizontalMenuLayout : SwipeMenuLayout {
    protected var mPreScrollX: Int = 0
    protected var mPreLeftMenuFraction: Float = -1f
    protected var mPreRightMenuFraction: Float = -1f

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context,
        attrs,
        defStyle)

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        var isIntercepted: Boolean = super.onInterceptTouchEvent(ev)
        val action: Int = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                run {
                    mLastX = ev.x
                    mDownX = mLastX
                }
                mDownY = ev.y
                isIntercepted = false
            }

            MotionEvent.ACTION_MOVE -> {
                val disX = (ev.x - mDownX)
                val disY = (ev.y - mDownY)
                isIntercepted =
                    abs(disX.toDouble()) > mScaledTouchSlop && abs(disX.toDouble()) > abs(disY.toDouble())
            }

            MotionEvent.ACTION_UP -> {
                isIntercepted = false
                // menu view opened and click on content view,
                // we just close the menu view and intercept the up event
                if ((isMenuOpen && mCurrentSwiper?.isClickOnContentView(this, ev.x) == true)) {
                    smoothCloseMenu()
                    isIntercepted = true
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                isIntercepted = false
                if (mScroller?.isFinished == false) mScroller?.abortAnimation()
            }
        }
        return isIntercepted
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        if (mVelocityTracker == null) mVelocityTracker = VelocityTracker.obtain()
        mVelocityTracker?.addMovement(ev)
        val dx: Float
        val dy: Float
        val action: Int = ev.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mLastX = ev.x
                mLastY = ev.y
            }

             MotionEvent.ACTION_MOVE  -> if (isSwipeEnable)  {
                val disX = (mLastX - ev.x)
                if ((!mDragging
                            && (abs(disX.toDouble()) > mScaledTouchSlop
                            ) && (abs(disX.toDouble()) > abs((mLastY - ev.y).toDouble())))) {
                    mDragging = true
                }
                if (mDragging) {
                    if (mCurrentSwiper == null || shouldResetSwiper) {
                        mCurrentSwiper = when {
                            disX < 0 && mBeginSwiper == null -> mEndSwiper
                            disX < 0 && mBeginSwiper != null -> mBeginSwiper
                            mEndSwiper == null -> mBeginSwiper
                            else -> mEndSwiper
                        }
                    }
                    scrollBy(disX.toInt(), 0)
                    mLastX = ev.x
                    mLastY = ev.y
                    shouldResetSwiper = false
                }
            }

            MotionEvent.ACTION_UP -> {
                dx = (mDownX - ev.x)
                dy = (mDownY - ev.y)
                mDragging = false
                mVelocityTracker?.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity.toFloat())
                val velocityX = mVelocityTracker?.getXVelocity() ?: 0f
                val velocity  = abs(velocityX.toDouble()).toInt()
                if (velocity > mScaledMinimumFlingVelocity) {
                    if (mCurrentSwiper != null) {
                        val duration: Int = getSwipeDuration(ev, velocity)
                        if (mCurrentSwiper is RightHorizontalSwiper) {
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
                    judgeOpenClose(dx, dy)
                }
                mVelocityTracker?.clear()
                mVelocityTracker?.recycle()
                mVelocityTracker = null
                if ((abs(dx.toDouble()) > mScaledTouchSlop
                            ) || (abs(dy.toDouble()) > mScaledTouchSlop
                            ) || isMenuOpen) { // ignore click listener, cancel this event
                    val motionEvent: MotionEvent = MotionEvent.obtain(ev)
                    motionEvent.action = MotionEvent.ACTION_CANCEL
                    return super.onTouchEvent(motionEvent)
                }
            }

            MotionEvent.ACTION_CANCEL -> {
                mDragging = false
                if (mScroller?.isFinished == false) {
                    mScroller?.abortAnimation()
                } else {
                    dx = (mDownX - ev.x)
                    dy = (mDownY - ev.y)
                    judgeOpenClose(dx, dy)
                }
            }
        }
        return super.onTouchEvent(ev)
    }

    private fun judgeOpenClose(dx: Float, dy: Float) {
        if (mCurrentSwiper != null) {
            if (abs(scrollX) >= ((mCurrentSwiper?.menuView?.width ?: 1) * mAutoOpenPercent)) { // auto open
                if (abs(dx.toDouble()) > mScaledTouchSlop || abs(dy.toDouble()) > mScaledTouchSlop) { // swipe up
                    if (isMenuOpenNotEqual) smoothCloseMenu() else smoothOpenMenu()
                } else { // normal up
                    if (isMenuOpen) smoothCloseMenu() else smoothOpenMenu()
                }
            } else { // auto close
                smoothCloseMenu()
            }
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        val checker: Swiper.Checker = mCurrentSwiper?.checkXY(x, y) ?: return
        shouldResetSwiper = checker.shouldResetSwiper
        if (checker.x != scrollX) {
            super.scrollTo(checker.x, checker.y)
        }
        if (scrollX != mPreScrollX) {
            val absScrollX: Int = abs(scrollX)
            if (mCurrentSwiper is LeftHorizontalSwiper) {
                    if (absScrollX == 0) {
                        mSwipeSwitchListener?.beginMenuClosed(this)
                    }
                    else if (absScrollX == mBeginSwiper?.menuWidth) {
                        mSwipeSwitchListener?.beginMenuOpened(
                            this)
                    }

                if (mSwipeFractionListener != null) {
                    var fraction: Float = absScrollX.toFloat() / (mBeginSwiper?.menuWidth ?: 1)
                    fraction = mDecimalFormat.format(fraction).toFloat()
                    if (fraction != mPreLeftMenuFraction) {
                        mSwipeFractionListener?.beginMenuSwipeFraction(this, fraction)
                    }
                    mPreLeftMenuFraction = fraction
                }
            } else {

                    if (absScrollX == 0) {
                        mSwipeSwitchListener?.endMenuClosed(this)
                    } else if (absScrollX == mEndSwiper?.menuWidth) {
                        mSwipeSwitchListener?.endMenuOpened(this)
                    }

                if (mSwipeFractionListener != null) {
                    var fraction: Float = absScrollX.toFloat() / (mEndSwiper?.menuWidth ?: 1)
                    fraction = mDecimalFormat.format(fraction).toFloat()
                    if (fraction != mPreRightMenuFraction) {
                        mSwipeFractionListener?.endMenuSwipeFraction(this, fraction)
                    }
                    mPreRightMenuFraction = fraction
                }
            }
        }
        mPreScrollX = getScrollX()
    }

    override fun computeScroll() {
        if (mScroller?.computeScrollOffset() == true) {
            val currX: Int = abs(mScroller?.currX ?: 0)
            if (mCurrentSwiper is RightHorizontalSwiper) {
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
        setClickable(true)
        mContentView = findViewById(R.id.smContentView)
        if (mContentView == null) {
            throw IllegalArgumentException("Not find contentView by id smContentView")
        }
        val menuViewLeft: View? = findViewById(R.id.smMenuViewLeft)
        val menuViewRight: View? = findViewById(R.id.smMenuViewRight)
        if (menuViewLeft == null && menuViewRight == null) {
            throw IllegalArgumentException("Not find menuView by id (smMenuViewLeft, smMenuViewRight)")
        }
        if (menuViewLeft != null) mBeginSwiper = LeftHorizontalSwiper(menuViewLeft)
        if (menuViewRight != null) mEndSwiper = RightHorizontalSwiper(menuViewRight)
    }

     val isMenuOpen: Boolean
        get() = ((mBeginSwiper != null && mBeginSwiper?.isMenuOpen(scrollX)== true)
                || (mEndSwiper != null && mEndSwiper?.isMenuOpen(scrollX)== true))
    val isMenuOpenNotEqual: Boolean
        get() {
            return ((mBeginSwiper != null && mBeginSwiper?.isMenuOpenNotEqual(scrollX)== true)
                    || (mEndSwiper != null && mEndSwiper?.isMenuOpenNotEqual(scrollX)== true))
        }

    override fun smoothOpenMenu(duration: Int) {
        mCurrentSwiper?.autoOpenMenu(mScroller, scrollX, duration)
        invalidate()
    }

    override fun smoothCloseMenu(duration: Int) {
        mCurrentSwiper?.autoCloseMenu(mScroller, scrollX, duration)
        invalidate()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentViewWidth: Int = ViewCompat.getMeasuredWidthAndState(this)
        val contentViewWidth: Int = ViewCompat.getMeasuredWidthAndState(mContentView)
        val contentViewHeight: Int = ViewCompat.getMeasuredHeightAndState(mContentView)
        var lp: LayoutParams = mContentView?.layoutParams as? LayoutParams ?: return
        val lGap: Int = paddingLeft + lp.leftMargin
        var tGap: Int = paddingTop + lp.topMargin
        mContentView?.layout(lGap,
            tGap,
            lGap + contentViewWidth,
            tGap + contentViewHeight)
        if (mEndSwiper != null) {
            val menuViewWidth: Int = ViewCompat.getMeasuredWidthAndState(mEndSwiper?.menuView)
            val menuViewHeight: Int = ViewCompat.getMeasuredHeightAndState(mEndSwiper?.menuView)
            lp = mEndSwiper?.menuView?.layoutParams as? LayoutParams ?: return
            tGap = getPaddingTop() + lp.topMargin
            mEndSwiper?.menuView?.layout(parentViewWidth,
                tGap,
                parentViewWidth + menuViewWidth,
                tGap + menuViewHeight)
        }
        if (mBeginSwiper != null) {
            val menuViewWidth: Int = ViewCompat.getMeasuredWidthAndState(mBeginSwiper?.menuView)
            val menuViewHeight: Int =
                ViewCompat.getMeasuredHeightAndState(mBeginSwiper?.menuView)
            lp = mBeginSwiper?.menuView?.layoutParams as? LayoutParams ?: return
            tGap = paddingTop + lp.topMargin
            mBeginSwiper?.menuView?.layout(-menuViewWidth,
                tGap,
                0,
                tGap + menuViewHeight)
        }
    }

    override var isSwipeEnable: Boolean = true

    override fun setSwipeListener(swipeSwitchListener: SwipeSwitchListener) {
        mSwipeSwitchListener = swipeSwitchListener
    }

    override val len: Int= mCurrentSwiper?.menuWidth ?:0

    override fun getMoveLen(event: MotionEvent): Int {
        val sx: Int = scrollX
        return (event.x - sx).toInt()
    }
}