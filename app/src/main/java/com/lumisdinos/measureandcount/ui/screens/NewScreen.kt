package com.lumisdinos.measureandcount.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.lumisdinos.measureandcount.data.NewScreenType

@Composable
fun NewScreen(navController: NavController) {
    LazyColumn {
        items(NewScreenType.entries) { item ->
            NewScreenListItem(item = item, navController = navController)
        }
    }
}

@Composable
fun NewScreenListItem(item: NewScreenType, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Filled.QuestionMark,
            contentDescription = "Description",
            modifier = Modifier
                .size(48.dp)
                .clickable { /* Will be implemented in the next step */ }
        )

        MiddleContent(item)

        Icon(
            imageVector = if (false) Icons.Filled.RadioButtonChecked else Icons.Filled.RadioButtonUnchecked,
            contentDescription = "Select",
            modifier = Modifier
                .size(48.dp)
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
            Text(text = "↑Width x Length x Color")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_2 -> {
            Text(text = "↑Width x Length x Color")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_PLUS_WIDTH_LENGTH_COLOR -> {
            Text(text = "(↑W x L x C|K) + (W x L x C|K)")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_PLUS_UP_WIDTH_LENGTH_COLOR -> {
            Text(text = "(↑W x L x C|K) + (↑W x L x C|K)")
        }

        NewScreenType.UP_WIDTH_LENGTH_COLOR_X_3 -> {
            Text(text = "(↑W x L x C|K) x 3")
        }

        NewScreenType.CUSTOM -> {
            Text(text = "Custom")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewScreenPreview() {
    NewScreen(rememberNavController())
}