package com.lumisdinos.measureandcount.ui.screens.count

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.common.AddCountColorField
import com.lumisdinos.measureandcount.ui.common.ChipboardAsStringField
import com.lumisdinos.measureandcount.ui.common.ChooseDialogType
import com.lumisdinos.measureandcount.ui.common.CommonButton
import com.lumisdinos.measureandcount.ui.common.DisabledOverlay
import com.lumisdinos.measureandcount.ui.common.RealSizeInput
import com.lumisdinos.measureandcount.ui.common.ExpandHideCountField
import com.lumisdinos.measureandcount.ui.common.QuantityCountOutlinedEditor
import com.lumisdinos.measureandcount.ui.common.SizeCountOutlinedEditor
import com.lumisdinos.measureandcount.ui.common.UpArrowIcon
import com.lumisdinos.measureandcount.ui.common.WhatIsIconButton
import com.lumisdinos.measureandcount.ui.screens.count.model.QuestionType
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.screens.count.model.DialogType
import com.lumisdinos.measureandcount.ui.theme.Grayish
import com.lumisdinos.measureandcount.ui.theme.Greenish
import com.lumisdinos.measureandcount.ui.theme.PrimaryBlue
import com.lumisdinos.measureandcount.ui.theme.Purple80
import com.lumisdinos.measureandcount.ui.theme.Yellowish
import kotlinx.coroutines.delay


@Composable
fun CountScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    unionId: Int? = null,
    viewModel: CountViewModel = hiltViewModel()
) {
    Log.d("CountScreen", "unionId: $unionId")

    LaunchedEffect(key1 = unionId) {
        viewModel.processIntent(CountIntent.SetUnionId(unionId))
    }

    val state by viewModel.state.collectAsState()
    val dialogState = remember { mutableStateOf<DialogType>(DialogType.None) }
    val shouldFlash = remember { mutableStateOf(false) }

    CollectEffects(navController, dialogState, shouldFlash, viewModel, snackbarHostState)
    ChooseDialogType(dialogState, viewModel::processIntent)

    //Actual screen
    if (state.messageForEmptyList != null) {
        EmptyList(state.messageForEmptyList!!)
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            TopBar(state.unionOfChipboards, viewModel::processIntent)
            AnimatedVisibility(visible = state.isFoundAreaOpen) {
                FindArea(state, shouldFlash, viewModel)
            }
            ExpandHideCountField(state.isFoundAreaOpen, viewModel::processIntent)
            ListOfItems(state.chipboards, viewModel::processIntent)
        }
    }
}

@Composable
fun FindArea(
    state: CountState,
    shouldFlash: MutableState<Boolean>,
    viewModel: CountViewModel,
) {
    val animatedColor by animateColorAsState(
        targetValue = if (shouldFlash.value) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 600),
        label = stringResource(R.string.background_color)
    )

    LaunchedEffect(key1 = shouldFlash.value) {
        if (shouldFlash.value) {
            delay(1200)
            shouldFlash.value = false
        }
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(animatedColor)
            .padding(top = 8.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        WidthLengthFields(state.unionOfChipboards, state.chipboardToFind, viewModel::processIntent)

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                if (state.chipboardToFind.color != 0) {
                    DisabledOverlay(
                        isEnabled = !state.chipboardToFind.isUnderReview,
                        onDisabledClick = { viewModel.processIntent(CountIntent.FieldDisabled) },
                        content = {
                            AddCountColorField(
                                state.chipboardToFind.colorName,
                                viewModel::processIntent
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(24.dp))
                    QuantityCountOutlinedEditor(
                        label = stringResource(R.string.quantity),
                        value = state.chipboardToFind.quantityAsString,
                        onQuantityChanged = viewModel::processIntent
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                Buttons(
                    state.isFoundButtonAvailable,
                    state.isUnknownButtonAvailable,
                    viewModel::processIntent
                )
            }

        }
        ChipboardAsStringField(state.chipboardToFind.chipboardAsString, state.chipboardToFind.color)
    }

}


@Composable
fun WidthLengthFields(
    unionOfChipboards: UnionOfChipboardsUI,
    chipboard: ChipboardUi,
    processIntent: (CountIntent) -> Unit
) {
    for (i in 1..unionOfChipboards.dimensions) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (unionOfChipboards.direction == i) {
                UpArrowIcon()
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
            val name = when (i) {
                1 -> chipboard.title1
                2 -> chipboard.title2
                3 -> chipboard.title3
                else -> ""
            }
            val sizeOfDim = getSizeForIndex(i, chipboard)
            val realSizeOfDim = getRealSizeForIndex(i, chipboard)

            Row(
                verticalAlignment = Alignment.Bottom
            ) {

                DisabledOverlay(
                    isEnabled = !chipboard.isUnderReview,
                    onDisabledClick = { processIntent(CountIntent.FieldDisabled) },
                    content = {
                        SizeCountOutlinedEditor(
                            label = name,
                            value = sizeOfDim,
                            dimension = i,
                            onSizeChanged = processIntent,
                            width = 150.dp,
                            height = 60.dp
                        )
                    }
                )

                DisabledOverlay(
                    isEnabled = chipboard.isUnderReview,
                    onDisabledClick = { processIntent(CountIntent.FieldDisabled) },
                    content = {
                        RealSizeInput(
                            value = realSizeOfDim,
                            label = stringResource(R.string.real_size),
                            dimension = i,
                            isEnabled = chipboard.isUnderReview,
                            onValueChange = processIntent,
                            intentFactory = { value, dim ->
                                CountIntent.RealSizeChanged(value, dim)
                            }
                        )
                    }
                )

            }

            if (i == 1) {
                Spacer(modifier = Modifier.width(8.dp))
                WhatIsIconButton(
                    questionType = QuestionType.RealSize,
                    processIntent = processIntent,
                    contentDescription = stringResource(R.string.what_is_diff_question)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

    }
}


@Composable
fun Buttons(
    isFoundButtonAvailable: Boolean,
    isUnknownButtonAvailable: Boolean,
    processIntent: (CountIntent) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonButton(
            stringResource(R.string.found),
            onClick = { processIntent(CountIntent.SetFoundChipboard) },
            enabled = isFoundButtonAvailable
        )
        Spacer(modifier = Modifier.width(8.dp))
        WhatIsIconButton(
            questionType = QuestionType.Found,
            processIntent = processIntent,
            contentDescription = stringResource(R.string.what_is_found_question)
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        CommonButton(
            stringResource(R.string.unknown),
            onClick = { processIntent(CountIntent.CreateUnknownChipboard) },
            enabled = isUnknownButtonAvailable
        )
        Spacer(modifier = Modifier.width(8.dp))
        WhatIsIconButton(
            questionType = QuestionType.Unknown,
            processIntent = processIntent,
            contentDescription = stringResource(R.string.what_is_unknown_question)
        )
    }

}


@Composable
fun ListOfItems(
    chipboards: List<ChipboardUi>,
    processIntent: (CountIntent) -> Unit
) {

    LazyColumn {
        items(chipboards, key = { it.id }) { chipboard ->
            val backgroundColor = when {
                chipboard.isUnderReview -> Yellowish
                chipboard.state == 2 -> Greenish
                chipboard.state == 1 -> Grayish
                else -> Color.Transparent
            }
            Row(
                modifier = Modifier
                    .clickable { processIntent(CountIntent.PressOnItemInList(chipboard)) }
                    .background(backgroundColor)
                    .padding(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 0.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                    ) {

                        Text(
                            text = chipboard.chipboardAsString,
                        )

                        if (chipboard.isUnderReview) {
                            Text(
                                text = stringResource(R.string.under_review),
                                color = Color.Red.copy(alpha = 0.5f),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
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
                        Text(
                            text = chipboard.allRealsAsString,
                            color = Color.Red,
                            fontSize = 14.sp,
                            fontStyle = FontStyle.Italic,
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(top = 16.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(42.dp)
                            .background(Color(chipboard.color))
                            .border(width = 1.dp, color = Color.Black)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                IconAtTheEnd(chipboard.state)
            }
            HorizontalDivider(thickness = 4.dp, color = Purple80)
        }
    }
}


@Composable
fun IconAtTheEnd(state: Int) {
    when (state) {
        1 -> Icon(
            Icons.Filled.Check,
            contentDescription = stringResource(R.string.check),
            tint = PrimaryBlue,
            modifier = Modifier.scale(1.3f)
        )

        2 -> Icon(
            Icons.Filled.ErrorOutline,
            contentDescription = stringResource(R.string.unknown),
            tint = PrimaryBlue,
            modifier = Modifier.scale(1.3f)
        )

        else -> Spacer(modifier = Modifier.size(24.dp))
    }
}


@Composable
fun EmptyList(messageForEmptyList: Int) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(messageForEmptyList),
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun TopBar(unionOfChipboards: UnionOfChipboardsUI, processIntent: (CountIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { processIntent(CountIntent.Back) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = unionOfChipboards.title,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp),
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )
        IconButton(
            onClick = { processIntent(CountIntent.SetListDone) },
            modifier = Modifier.size(32.dp)
        ) {
            val icon = if (unionOfChipboards.isFinished) {
                Icons.AutoMirrored.Filled.Undo
            } else {
                Icons.Filled.Done
            }
            val contentDescription = if (unionOfChipboards.isFinished) {
                stringResource(R.string.undone)
            } else {
                stringResource(R.string.done)
            }

            Icon(
                icon,
                contentDescription = contentDescription,
                modifier = Modifier.size(32.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(8.dp))
}


@Composable
fun CollectEffects(
    navController: NavController,
    dialogState: MutableState<DialogType>,
    shouldFlash: MutableState<Boolean>,
    viewModel: CountViewModel,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                is CountEffect.ShowSelectNotFoundToFindAreaConfirmationDialog -> {
                    dialogState.value = DialogType.SelectNotFoundToFindArea(effect.chipboard)
                }

                is CountEffect.ShowRemoveNotFoundFromFindAreaConfirmationDialog -> {
                    dialogState.value = DialogType.RemoveNotFoundFromFindArea(effect.chipboard)
                }

                is CountEffect.ShowSelectUnknownToFindAreaConfirmationDialog -> {
                    dialogState.value = DialogType.SelectUnknownToFindArea(effect.chipboard)
                }

                is CountEffect.ShowUncheckConfirmationDialog -> {
                    dialogState.value = DialogType.Uncheck(effect.chipboard)
                }

                is CountEffect.ShowNotExceedingTargetQuantityDialog -> {
                    dialogState.value = DialogType.NotExceedingTargetQuantity(
                        effect.targetQuantity,
                        effect.enteredQuantity
                    )
                }

                is CountEffect.ShowWhatIsDialog -> {
                    dialogState.value = DialogType.WhatIs(effect.questionType)
                }

                is CountEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is CountEffect.FlashFindItemArea -> {
                    shouldFlash.value = true
                }

                CountEffect.ShowFieldDisabled -> {
                    dialogState.value = DialogType.FieldDisabled
                }

                CountEffect.NavigateBack -> {
                    navController.popBackStack()
                }
            }
        }
    }
}


fun getSizeForIndex(index: Int, chipboard: ChipboardUi?): String {
    if (chipboard == null) return ""
    return when (index) {
        1 -> chipboard.size1AsString
        2 -> chipboard.size2AsString
        3 -> chipboard.size3AsString
        else -> ""
    }
}


fun getRealSizeForIndex(index: Int, chipboard: ChipboardUi?): String {
    if (chipboard == null) return ""
    return when (index) {
        1 -> chipboard.real1AsString
        2 -> chipboard.real2AsString
        3 -> chipboard.real3AsString
        else -> ""
    }
}