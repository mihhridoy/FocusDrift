package com.radonshadow.focusdrift.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.compose.runtime.getValue
import com.radonshadow.focusdrift.ui.screens.habits.AddHabitScreen
import com.radonshadow.focusdrift.ui.screens.habits.HabitsScreen
import com.radonshadow.focusdrift.ui.screens.home.HomeScreen
import com.radonshadow.focusdrift.ui.screens.onboarding.OnboardingScreen
import com.radonshadow.focusdrift.ui.screens.profile.ProfileScreen
import com.radonshadow.focusdrift.ui.screens.rooms.CreateRoomScreen
import com.radonshadow.focusdrift.ui.screens.rooms.RoomDetailScreen
import com.radonshadow.focusdrift.ui.screens.rooms.RoomsListScreen
import com.radonshadow.focusdrift.ui.screens.setup.SetupScreen
import com.radonshadow.focusdrift.ui.screens.shop.ShopScreen
import com.radonshadow.focusdrift.ui.screens.splash.SplashScreen
import com.radonshadow.focusdrift.ui.screens.subscription.SubscriptionScreen
import com.radonshadow.focusdrift.ui.screens.timer.SessionCompleteScreen
import com.radonshadow.focusdrift.ui.screens.timer.TimerScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomNav = BOTTOM_NAV_SCREENS.any { it.route == currentRoute }

    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onNavigate = { screen ->
                        navController.navigate(screen.route) {
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Splash.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Splash.route) {
                SplashScreen(onSplashFinished = { destination ->
                    navController.navigate(destination.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(onFinished = {
                    navController.navigate(Screen.AdhdSetup.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.AdhdSetup.route) {
                SetupScreen(onFinished = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.AdhdSetup.route) { inclusive = true }
                    }
                })
            }

            composable(Screen.Home.route) {
                HomeScreen(
                    onStartFocus = { navController.navigate(Screen.Timer.route) },
                    onJoinRooms = { navController.navigate(Screen.RoomsList.route) }
                )
            }
            composable(Screen.Timer.route) {
                TimerScreen(
                    onSessionComplete = { navController.navigate(Screen.SessionComplete.route) },
                    onGoPro = { navController.navigate(Screen.Subscription.route) }
                )
            }
            composable(Screen.SessionComplete.route) {
                SessionCompleteScreen(onDone = { navController.popBackStack(Screen.Timer.route, inclusive = false) })
            }

            composable(Screen.RoomsList.route) {
                RoomsListScreen(
                    onCreateRoom = { navController.navigate(Screen.CreateRoom.route) },
                    onOpenRoom = { roomId -> navController.navigate(Screen.RoomDetail.createRoute(roomId)) }
                )
            }
            composable(
                route = Screen.RoomDetail.route,
                arguments = listOf(navArgument("roomId") { type = androidx.navigation.NavType.StringType })
            ) { entry ->
                val roomId = entry.arguments?.getString("roomId").orEmpty()
                RoomDetailScreen(roomId = roomId, onBack = { navController.popBackStack() })
            }
            composable(Screen.CreateRoom.route) {
                CreateRoomScreen(onCreated = { roomId ->
                    navController.navigate(Screen.RoomDetail.createRoute(roomId)) {
                        popUpTo(Screen.RoomsList.route)
                    }
                })
            }

            composable(Screen.Habits.route) {
                HabitsScreen(onAddHabit = { navController.navigate(Screen.AddHabit.route) })
            }
            composable(Screen.AddHabit.route) {
                AddHabitScreen(onDone = { navController.popBackStack() })
            }

            composable(Screen.Shop.route) { ShopScreen() }
            composable(Screen.Subscription.route) {
                SubscriptionScreen(onDismiss = { navController.popBackStack() })
            }
            composable(Screen.Profile.route) {
                ProfileScreen(onGoPro = { navController.navigate(Screen.Subscription.route) })
            }
        }
    }
}
