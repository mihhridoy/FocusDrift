package com.radonshadow.focusdrift.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object AdhdSetup : Screen("adhd_setup")

    object Home : Screen("home")
    object Timer : Screen("timer")
    object SessionComplete : Screen("session_complete")

    object RoomsList : Screen("rooms_list")
    object RoomDetail : Screen("room_detail/{roomId}") {
        fun createRoute(roomId: String) = "room_detail/$roomId"
    }
    object CreateRoom : Screen("create_room")

    object Habits : Screen("habits")
    object AddHabit : Screen("add_habit")

    object Shop : Screen("shop")
    object Subscription : Screen("subscription")
    object Profile : Screen("profile")
}

val BOTTOM_NAV_SCREENS = listOf(Screen.Home, Screen.Timer, Screen.RoomsList, Screen.Habits, Screen.Profile)
