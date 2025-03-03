package com.lumisdinos.measureandcount.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lumisdinos.measureandcount.data.NewScreenType

@Composable
fun NewScreen(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp)) {
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
                HorizontalDivider(
                    thickness = 2.dp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NewScreenListItem(item: NewScreenType, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Filled.QuestionMark,
            contentDescription = "Description",
            modifier = Modifier
                .size(32.dp)
                .background(Color.Blue)
                .clickable { /* Will be implemented in the next step */ }
        )

        Row(
            modifier = Modifier.weight(1f)
                .padding(start = 8.dp, end = 8.dp)
                .background(Color.Yellow),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MiddleContent(item)
        }

        Icon(
            imageVector = if (false) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
            contentDescription = "Select",
            modifier = Modifier
                .size(32.dp)
                .clickable { /* Will be implemented in the next step */ }
        )
    }
}

@Composable
fun MiddleContent(type: NewScreenType) {
    when (type) {
        NewScreenType.WIDTH_LENGTH -> {
            Text(text = "Width x Length")
        }

        NewScreenType.WIDTH_LENGTH_COLOR -> {
            Text(text = "Width x Length x Color")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR -> {
            UpArrowIcon()
            Text(text = "Width x Length x Color")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_2 -> {
            UpArrowIcon()
            Text(text = "Width x Length x Color")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_PLUS_WIDTH_LENGTH_COLOR -> {
            Text(text = "(")
            UpArrowIcon()
            Text(text = "W x L x C|K) + (W x L x C|K)")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_PLUS_UP_WIDTH_LENGTH_COLOR -> {
            Text(text = "(")
            UpArrowIcon()
            Text(text = "W x L x C|K) + (")
            UpArrowIcon()
            Text(text = "W x L x C|K)")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_X_3 -> {
            Text(text = "(")
            UpArrowIcon()
            Text(text = "W x L x C|K) x 3")
        }

        NewScreenType.CUSTOM -> {
            Text(text = "Custom")
        }
    }
}

@Composable
fun UpArrowIcon() {
    Icon(
        imageVector = Icons.Filled.ArrowUpward,
        contentDescription = "Up",
        modifier = Modifier.size(24.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun NewScreenPreview() {
    NewScreen(rememberNavController())
}