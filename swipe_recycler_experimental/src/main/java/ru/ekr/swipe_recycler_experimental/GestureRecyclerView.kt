package ru.ekr.swipe_recycler_experimental

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.ekr.swipe_recycler_experimental.swipe_layout.GestureFrameLayout
import kotlin.math.abs


class GestureRecyclerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : RecyclerView(context, attrs, defStyle) {

    private var gestureLayout: GestureFrameLayout? = null
    private var touchedPosition = NO_POSITION
    private val viewConfig: ViewConfiguration = ViewConfiguration.get(context)
    private val scaledTouchSlop = viewConfig.scaledEdgeSlop
    private var downX = 0f
    private var downY = 0f

    /**
     * Определяет, следует ли перехватывать событие касания.
     * Если метод возвращает true, вызывается onTouchEvent,
     * и вы можете выполнить фактическую прокрутку там.
     *
     * @param event Событие касания
     * @return Возвращает true, если событие касания было перехвачено, иначе false
     */
    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        var preparer = super.onInterceptTouchEvent(event)
        when (event.action) {
            MotionEvent.ACTION_MOVE -> {
                // Если пользователь проводит пальцем по горизонтали больше,
                // чем касание, начинаем прокрутку и передаем событие дальше.
                // Наклон касания рассчитывается с использованием констант ViewConfiguration.
                if (isDragInScaled(event)) return false
            }

            MotionEvent.ACTION_DOWN -> {
                // Сбрасываем preparer в false, чтобы передать событие дальше к дочернему View
                preparer = false
                // Запоминаем текущее событие касания
                setDownEvent(event)
                val touchingView = findChildViewUnder(event.x, event.y)
                val newTouchingPosition = getChildAdapterPositionX(touchingView)
                // Закрываем открытое меню, если палец касается новой позиции и есть открытое меню
                if (newTouchingPosition != touchedPosition
                    && gestureLayout?.isMenuOpen == true) {
                    gestureLayout?.closeMenu()
                    // Сбрасываем состояние свайпа
                    resetSwipeItems()
                }
                // Обновляем меню, если находим новый Holder
                updateMenuWhenFindHolder(newTouchingPosition)
            }
        }
        return preparer
    }

    private fun isDragInScaled(event: MotionEvent): Boolean {
        val disX = event.x - downX
        val disY = event.y - downY
        return abs(disX) > scaledTouchSlop && abs(disX) > abs(disY)
    }

    private fun setDownEvent(event: MotionEvent) {
        downX = event.x
        downY = event.y
    }

    /**The position is determined by the Holder and updated.*/
    private fun updateMenuWhenFindHolder(touchingPosition: Int) =
        findViewHolderForAdapterPosition(touchingPosition)?.let { viewHolder ->
            getGestureMenuLayout(viewHolder)?.let { itemView ->
                gestureLayout = itemView
                touchedPosition = touchingPosition
            }
        }

    private fun getGestureMenuLayout(viewHolder: ViewHolder): GestureFrameLayout? {
        val itemView = viewHolder.itemView
        if (itemView is GestureFrameLayout) return itemView
        val unvisited = mutableListOf(itemView)
        while (unvisited.isNotEmpty()) {
            val child = unvisited.removeAt(0) as? ViewGroup ?: continue
            if (child is GestureFrameLayout) return child
            for (i in 0 until child.childCount) unvisited.add(child.getChildAt(i))
        }
        return null
    }


    private fun getChildAdapterPositionX(child: View?): Int {
        child ?: return NO_POSITION
        return getChildAdapterPosition(child)
    }

    /** if we intercept the event, just reset*/
    private fun resetSwipeItems() {
        gestureLayout = null
        touchedPosition = NO_POSITION
    }
}