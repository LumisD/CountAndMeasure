package com.lumisdinos.measureandcount.ui

import AddNewItemScreen
import android.app.Activity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.screens.count.CountScreen
import com.lumisdinos.measureandcount.ui.screens.NewScreen
import com.lumisdinos.measureandcount.ui.screens.ListsScreen
import com.lumisdinos.measureandcount.ui.theme.MainBg

interface BottomBarDestination {
    val baseRoute: String
    val title: String
    val icon: Int
}

sealed class Screen(val route: String) {
    data object Lists : Screen("lists"), BottomBarDestination {
        override val baseRoute = "lists"
        override val title = "Lists"
        override val icon = R.drawable.ic_old
    }

    data object Count : Screen("count"), BottomBarDestination {
        override val baseRoute = "count"
        override val title = "Count"
        override val icon = R.drawable.ic_current

        fun routeWithArgs(unionId: Int) = "count/$unionId"
    }

    data object New : Screen("new"), BottomBarDestination {
        override val baseRoute = "new"
        override val title = "New"
        override val icon = R.drawable.ic_new
    }

    data object AddNewItem : Screen("add_new_item") {
        fun routeWithArgs(itemType: String, origin: String? = null): String {
            return if (origin != null) {
                "add_new_item/$itemType?origin=$origin"
            } else {
                "add_new_item/$itemType"
            }
        }
    }
}


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route?.substringBefore("/")

    val bottomBarScreens = listOf(Screen.Lists, Screen.Count, Screen.New)
    val snackbarHostState = remember { SnackbarHostState() }

    SetSystemBarColor()

    Scaffold(
        containerColor = MainBg,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (bottomBarScreens.any { it.baseRoute == currentRoute }) {
                BottomNavigationBar(navController, currentRoute)
            }
        }
    ) { innerPadding ->
        Navigation(navController, snackbarHostState, Modifier.padding(innerPadding))
    }
}


@Composable
fun Navigation(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    modifier: Modifier
) {
    NavHost(navController, startDestination = Screen.New.baseRoute, modifier = modifier) {
        composable(Screen.Lists.baseRoute) { ListsScreen() }

        composable(Screen.Count.baseRoute) {
            CountScreen(
                snackbarHostState,
                unionId = null
            )
        }

        composable(
            route = "count/{unionId}",
            arguments = listOf(navArgument("unionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val unionId = backStackEntry.arguments?.getInt("unionId")
            CountScreen(snackbarHostState, unionId)
        }

        composable(Screen.New.baseRoute) {
            NewScreen(navController)
        }

        composable(
            route = "add_new_item/{itemType}?origin={origin}",
            arguments = listOf(
                navArgument("itemType") { type = NavType.StringType },
                navArgument("origin") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) {
            AddNewItemScreen(navController, snackbarHostState)
        }

    }
}


@Composable
fun BottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(Screen.Lists, Screen.Count, Screen.New)

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = screen.icon),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = screen.baseRoute == currentRoute,
                onClick = {
                    navController.navigate(screen.baseRoute) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}


@Composable
fun SetSystemBarColor() {
    val window = (LocalActivity.current as Activity).window
    window.statusBarColor = MainBg.toArgb()
    window.navigationBarColor = MainBg.toArgb()
}


@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}