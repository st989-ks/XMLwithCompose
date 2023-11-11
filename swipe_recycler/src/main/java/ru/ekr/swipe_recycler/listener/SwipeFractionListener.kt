package ru.ekr.swipe_recycler.listener

import ru.ekr.swipe_recycler.SwipeMenuLayout


interface SwipeFractionListener {
    fun beginMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float)
    fun endMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float)
}