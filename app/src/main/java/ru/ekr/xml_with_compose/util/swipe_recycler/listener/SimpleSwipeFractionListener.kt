package ru.ekr.xml_with_compose.util.swipe_recycler.listener

import ru.ekr.xml_with_compose.util.swipe_recycler.SwipeMenuLayout


class SimpleSwipeFractionListener : SwipeFractionListener {
    override fun beginMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float) {}
    override fun endMenuSwipeFraction(swipeMenuLayout: SwipeMenuLayout, fraction: Float) {}
}