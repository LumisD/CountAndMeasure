package com.lumisdinos.measureandcount.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lumisdinos.measureandcount.data.NewScreenType
import com.lumisdinos.measureandcount.ui.Screen

@Composable
fun NewScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Choose a type of measurement",
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(NewScreenType.entries) { item ->
                NewScreenListItem(item = item, navController = navController)
                if (item != NewScreenType.entries.last()) {
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
fun NewScreenListItem(item: NewScreenType, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp)
            .clickable {
                navController.navigate(Screen.AddNewItem.route)
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        MiddleContent(item)
    }
}

@Composable
fun MiddleContent(type: NewScreenType) {
    when (type) {
        NewScreenType.WIDTH_LENGTH -> {
            TextC("Width ")
            XIcon()
            TextC(" Length")
        }

        NewScreenType.WIDTH_LENGTH_COLOR -> {
            TextC("Width ")
            XIcon()
            TextC(" Length ")
            XIcon()
            TextC(" Color")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR -> {
            UpArrowIcon()
            TextC("Width ")
            XIcon()
            TextC(" Length ")
            XIcon()
            TextC(" Color")
        }
        NewScreenType.WIDTH_UP_LENGTH_COLOR -> {
            TextC("Width ")
            XIcon()
            UpArrowIcon()
            TextC("Length ")
            XIcon()
            TextC(" Color")
        }
        NewScreenType.WIDTH_LENGTH_COLOR_HEIGHT -> {
            TextC("Width ")
            XIcon()
            TextC(" Length ")
            XIcon()
            TextC(" Height")
        }
        NewScreenType.CUSTOM -> {
            TextC("Custom")
        }
    }
}

@Composable
fun UpArrowIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.ArrowUpward,
        contentDescription = "Up",
        modifier = modifier
    )
}

@Composable
fun XIcon() {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "Close",
        modifier = Modifier.size(24.dp)
    )
}

@Composable
fun TextC(text: String) {
    Text(
        text = text,
        style = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            color = Color.Blue
        )
    )
}

@Preview(showBackground = true)
@Composable
fun NewScreenPreview() {
    NewScreen(rememberNavController())
}