package ru.ekr.swipe_recycler_experimental.swipe_layout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent

class GestureFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : BaseGestureFrameLayout(context, attrs, defStyle, defStyleRes) {

    /**
     * Переопределено в целях гарантирования правильной работы onInterceptTouchEvent.
     * По умолчанию dispatchTouchEvent возвращает true.
     *
     * @param event Событие касания
     * @return Всегда возвращает результат вызова super.dispatchTouchEvent(event)
     */
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return super.dispatchTouchEvent(event)
    }

    /**
     * Определяет, следует ли перехватывать движение.
     * Если метод возвращает true, вызывается onTouchEvent,
     * и фактическая прокрутка выполняется там.
     *
     * @param event Событие касания
     * @return Возвращает true, если событие было перехвачено, в противном случае возвращает результат вызова super.onInterceptTouchEvent(event)
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                // Перехватываем событие и сбрасываем скролл.
                // Child справится с этим лучше.
                isProcessingSwipe = false
                return super.onInterceptTouchEvent(event)
            }

            MotionEvent.ACTION_DOWN -> {
                setDistanceLastEvent(event)
                if (!isProcessingSwipe) return super.onInterceptTouchEvent(event)
            }

            MotionEvent.ACTION_MOVE -> {
                return when {
                    // Если пользователь уже прокручивает, перехватываем событие.
                    isProcessingSwipe -> true

                    // Если пользователь проводит пальцем по горизонтали больше,
                    // чем касание, начинаем прокрутку и перехватываем событие.
                    // Наклон касания рассчитывается с использованием констант ViewConfiguration.
                    isDragInScaled(event) -> {
                        isProcessingSwipe = true
                        true
                    }

                    else -> super.onInterceptTouchEvent(event)
                }

            }
        }
        // В общем случае, не перехватываем события. Child сами разберутся.
        return super.onInterceptTouchEvent(event)
    }

    /**
     * Обрабатывает событие касания.
     * Например, если действие — ACTION_MOVE, производит прокрутку этого контейнера.
     * Этот метод вызывается только в том случае,
     * если событие касания было перехвачено в onInterceptTouchEvent.
     *
     * @param event Событие касания
     * @return Возвращает true, чтобы указать, что событие было обработано
     */
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isSwipeEnable) return false
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                if (!isProcessingSwipe && isDragInScaled(event)) isProcessingSwipe = true
                if (isProcessingSwipe && !objectAnim.isRunning) {
                    val defDisX = lastX - event.x
                    setDistanceLastEvent(event)
                    positionBufferX -= defDisX
                    when {
                        positionBufferX >= autoOpenWidth && !isMenuOpen -> openLeftMenu()
                        positionBufferX <= -autoOpenWidth && !isMenuOpen -> openRightMenu()
                        else -> return false
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                when {
                    isMenuOpen
                            && positionBufferX > -autoCloseWidth
                            && positionBufferX < 0 -> positionBufferX = -widthMenuForeOpen.toFloat()

                    isMenuOpen
                            && positionBufferX < autoCloseWidth
                            && positionBufferX > 0 -> positionBufferX = widthMenuForeOpen.toFloat()

                    else -> closeMenu()
                }
                isProcessingSwipe = false
                val newEvent = MotionEvent.obtain(event)
                newEvent.action = MotionEvent.ACTION_CANCEL
                return super.onTouchEvent(newEvent)
            }
        }
        return super.onTouchEvent(event)
    }
}