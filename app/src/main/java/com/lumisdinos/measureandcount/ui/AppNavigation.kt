package com.lumisdinos.measureandcount.ui

import AddNewItemScreen
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
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lumisdinos.measureandcount.R
//import com.lumisdinos.measureandcount.ui.model.AddNewItemOrigin
import com.lumisdinos.measureandcount.ui.screens.CreateOwnMeasureScreen
import com.lumisdinos.measureandcount.ui.screens.CountScreen
import com.lumisdinos.measureandcount.ui.screens.NewScreen
import com.lumisdinos.measureandcount.ui.screens.ListsScreen

interface BottomBarDestination {
    val route: String
    val title: String
    val icon: Int
}

sealed class Screen(val route: String) {
    data object Lists : Screen("lists"), BottomBarDestination {
        override val title: String = "Lists"
        override val icon: Int = R.drawable.ic_old
    }
    data object Count : Screen("count"), BottomBarDestination {
        override val title: String = "Count"
        override val icon: Int = R.drawable.ic_current
    }
    data object New : Screen("new"), BottomBarDestination {
        override val title: String = "New"
        override val icon: Int = R.drawable.ic_new
    }
//    data object AddNewItem : Screen("add_new_item/{itemType}") {
//        fun createRoute(serializedItem: String): String = "add_new_item/${serializedItem}"
////        val arguments: List<NamedNavArgument> = listOf(
////            navArgument("itemType") { type = NavType.StringType }
////        )
//    }
    data object AddNewItem : Screen("add_new_item/{itemType}?origin={origin}") {
        fun createRoute(itemType: String): String {
            return "add_new_item/$itemType"//?origin=${origin.name}
        }

        val arguments: List<NamedNavArgument> = listOf(
            navArgument("itemType") { type = NavType.StringType },
            //navArgument("origin") { type = NavType.StringType }
        )
    }
    data object CreateOwnMeasure : Screen("create_own_measure") {
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val bottomBarRoutes = listOf(Screen.Lists.route, Screen.Count.route, Screen.New.route)

    Scaffold(
        bottomBar = {
            if (currentDestination?.route in bottomBarRoutes) {
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
        composable(Screen.Lists.route) { ListsScreen() }
        composable(Screen.Count.route) { CountScreen() }
        composable(Screen.New.route) { NewScreen(navController) }
        composable(Screen.AddNewItem.route) { AddNewItemScreen(navController) }
        composable(Screen.CreateOwnMeasure.route) { CreateOwnMeasureScreen(navController) }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController, currentDestination: NavDestination?) {
    val screens = listOf(
        Screen.Lists,
        Screen.Count,
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