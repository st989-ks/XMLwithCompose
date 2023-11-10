package ru.ekr.xml_with_compose.util.swipe_recycler.listener

import ru.ekr.xml_with_compose.util.swipe_recycler.SwipeMenuLayout


class SimpleSwipeSwitchListener : SwipeSwitchListener {
    override fun beginMenuClosed(swipeMenuLayout: SwipeMenuLayout?) {}
    override fun beginMenuOpened(swipeMenuLayout: SwipeMenuLayout?) {}
    override  fun endMenuClosed(swipeMenuLayout: SwipeMenuLayout?) {}
    override  fun endMenuOpened(swipeMenuLayout: SwipeMenuLayout?) {}
}