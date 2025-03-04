package com.lumisdinos.measureandcount.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.screens.AddNewItemScreen
import com.lumisdinos.measureandcount.ui.screens.CurrentScreen
import com.lumisdinos.measureandcount.ui.screens.NewScreen
import com.lumisdinos.measureandcount.ui.screens.OldScreen

interface BottomBarDestination {
    val route: String
    val title: String
    val icon: Int
}

sealed class Screen(val route: String) {
    data object Old : Screen("old"), BottomBarDestination {
        override val title: String = "Old"
        override val icon: Int = R.drawable.ic_old
    }
    data object Current : Screen("current"), BottomBarDestination {
        override val title: String = "Current"
        override val icon: Int = R.drawable.ic_current
    }
    data object New : Screen("new"), BottomBarDestination {
        override val title: String = "New"
        override val icon: Int = R.drawable.ic_new
    }
    data object AddNewItem : Screen("add_new_item")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarRoutes = listOf(Screen.Old.route, Screen.Current.route, Screen.New.route)

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomBarRoutes) { // Change this line
                BottomNavigationBar(navController, currentDestination)
            }
        }
    ) { innerPadding ->
        Navigation(navController, Modifier.padding(innerPadding))
    }
}

@Composable
fun Navigation(navController: NavHostController, modifier: Modifier) {
    NavHost(navController, startDestination = Screen.New.route, modifier = modifier) {
        composable(Screen.Old.route) { OldScreen() }
        composable(Screen.Current.route) { CurrentScreen() }
        composable(Screen.New.route) { NewScreen(navController) }
        composable(Screen.AddNewItem.route) { AddNewItemScreen(navController) }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?) {
    val screens = listOf(
        Screen.Old,
        Screen.Current,
        Screen.New
    )

    NavigationBar {
        screens.forEach { screen ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painterResource(id = screen.icon),
                        contentDescription = screen.title
                    )
                },
                label = { Text(screen.title) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
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

@Preview(showBackground = true)
@Composable
fun AppNavigationPreview() {
    AppNavigation()
}