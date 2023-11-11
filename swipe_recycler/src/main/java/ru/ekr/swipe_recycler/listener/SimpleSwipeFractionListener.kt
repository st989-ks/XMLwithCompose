package ru.ekr.swipe_recycler.listener

import ru.ekr.swipe_recycler.SwipeMenuLayout


class SimpleSwipeFractionListener : SwipeFractionListener {
    override fun beginMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float) {}
    override fun endMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float) {}
}