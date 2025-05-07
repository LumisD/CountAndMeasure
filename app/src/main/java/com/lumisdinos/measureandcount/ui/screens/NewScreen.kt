package com.lumisdinos.measureandcount.ui.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.model.NewScreenType
import com.lumisdinos.measureandcount.ui.model.serialize
import com.lumisdinos.measureandcount.ui.Screen
import com.lumisdinos.measureandcount.ui.common.UpArrowIcon
import com.lumisdinos.measureandcount.ui.common.XIcon
import com.lumisdinos.measureandcount.ui.defaultScreenTypes

@Composable
fun NewScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {

        TopBar()
        ListOfNewScreenTypes(navController)

    }
}

@Composable
fun TopBar() {
    Text(
        text = "Choose a type of measurement",
        modifier = Modifier.fillMaxWidth(),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun ListOfNewScreenTypes(navController: NavController) {
    LazyColumn {
        items(defaultScreenTypes) { item ->
            if (item != defaultScreenTypes.last()) {
                ListItem(item = item, navController = navController)
                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Gray
                )
            } else {
                ListItem(item = item, navController = navController, isLast = true)
            }
        }
    }
}


@Composable
fun ListItem(item: NewScreenType, navController: NavController, isLast: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
//                if (isLast) {
//                    navController.navigate(Screen.CreateOwnMeasure)
//                } else {
                val serializedItem = item.serialize()
                Log.d("NewScreen", "serializedItem: $serializedItem")
                navController.navigate(
                    Screen.AddNewItem.createRoute(
                        itemType = serializedItem,
                        //origin = AddNewItemOrigin.NEW_SCREEN
                    )
                )
                //}

            }
            .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        MiddleContent(item)
    }
}

@Composable
fun MiddleContent(type: NewScreenType) {
    val columnNames = type.columnNames
    val directionColumn = type.directionColumn
    val hasColor = type.hasColor

    Row(verticalAlignment = Alignment.CenterVertically) {
//        if (columnNames.isEmpty()) {
//            TextC(stringResource(R.string.create_own_measure))
//            return
//        }
        columnNames.forEachIndexed { index, name ->
            if (directionColumn == index + 1) {
                UpArrowIcon()
            }
            Text(stringResource(name))
            if (index < columnNames.size - 1) {
                XIcon()
            } else if (hasColor) {
                XIcon()
                Text(stringResource(R.string.color_column))
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun NewScreenPreview() {
    NewScreen(rememberNavController())
}