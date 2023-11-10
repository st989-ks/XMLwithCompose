package ru.ekr.xml_with_compose.util.swipe_recycler.listener

import ru.ekr.xml_with_compose.util.swipe_recycler.SwipeMenuLayout


interface SwipeSwitchListener {
    fun beginMenuClosed(swipeMenuLayout: SwipeMenuLayout?)
    fun beginMenuOpened(swipeMenuLayout: SwipeMenuLayout?)
    fun endMenuClosed(swipeMenuLayout: SwipeMenuLayout?)
    fun endMenuOpened(swipeMenuLayout: SwipeMenuLayout?)
}