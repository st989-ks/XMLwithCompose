package ru.ekr.xml_with_compose.util.swipe_recycler.listener

import ru.ekr.xml_with_compose.util.swipe_recycler.SwipeMenuLayout


interface SwipeFractionListener {
    fun beginMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float)
    fun endMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float)
}