package ru.ekr.swipe_recycler.listener

import ru.ekr.swipe_recycler.SwipeMenuLayout


class SimpleSwipeSwitchListener : SwipeSwitchListener {
    override fun beginMenuClosed(swipeMenuLayout: SwipeMenuLayout?) {}
    override fun beginMenuOpened(swipeMenuLayout: SwipeMenuLayout?) {}
    override  fun endMenuClosed(swipeMenuLayout: SwipeMenuLayout?) {}
    override  fun endMenuOpened(swipeMenuLayout: SwipeMenuLayout?) {}
}