package com.lumisdinos.measureandcount.ui.screens.lists

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.Screen
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.theme.Grayish
import com.lumisdinos.measureandcount.ui.theme.Purple80

@Composable
fun ListsScreen(
    navController: NavController,
    viewModel: ListsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    CollectEffects(navController, viewModel)

    //Actual screen
    if (state.listOfUnions.isEmpty()) {
        EmptyList()
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TopBar()
            ListOfItems(state.listOfUnions, viewModel::processIntent)
        }
    }
}


@Composable
fun ListOfItems(unions: List<UnionOfChipboardsUI>, processIntent: (ListsIntent) -> Unit) {
    LazyColumn {
        items(items = unions, key = { it.id }) { union ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(if (union.isFinished) Grayish else Color.White)
                    .clickable { processIntent(ListsIntent.PressOnItemInList(union)) }
                    .padding(start = 16.dp, end = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = union.title,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                            .padding(end = 8.dp),
                        minLines = 2,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                if (union.isMarkedAsDeleted) {
                    Text(
                        text = stringResource(R.string.marked_as_deleted),
                        color = Color.Red,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .rotate(-10f)
                            .border(
                                2.dp, Color.Red.copy(alpha = 0.5f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(4.dp)
                    )
                }
            }
            HorizontalDivider(thickness = 4.dp, color = Purple80)
        }
    }
}


@Composable
fun EmptyList() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.press_new_screen_create_chipboard_sheet_list),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun TopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.chipboard_sheet_list),
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(8.dp))
}


@Composable
fun CollectEffects(
    navController: NavController,
    viewModel: ListsViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is ListsEffects.NavigateToCountScreen -> {
                    navController.navigate(Screen.Count.routeWithArgs(effect.unionId)) { }
                }
            }
        }
    }
}