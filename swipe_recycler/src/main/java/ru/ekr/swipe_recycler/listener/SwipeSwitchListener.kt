package ru.ekr.swipe_recycler.listener

import ru.ekr.swipe_recycler.SwipeMenuLayout


interface SwipeSwitchListener {
    fun beginMenuClosed(swipeMenuLayout: SwipeMenuLayout?)
    fun beginMenuOpened(swipeMenuLayout: SwipeMenuLayout?)
    fun endMenuClosed(swipeMenuLayout: SwipeMenuLayout?)
    fun endMenuOpened(swipeMenuLayout: SwipeMenuLayout?)
}