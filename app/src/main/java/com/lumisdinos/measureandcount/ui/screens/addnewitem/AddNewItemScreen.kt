import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lumisdinos.measureandcount.ui.model.NewScreenType
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import com.lumisdinos.measureandcount.ui.model.deserializeNewScreenType
import com.lumisdinos.measureandcount.ui.common.UpArrowIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemIntent
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemState
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.common.AddItemColorField
import com.lumisdinos.measureandcount.ui.common.ChipboardAsStringField
import com.lumisdinos.measureandcount.ui.common.ChooseDialogTypes
import com.lumisdinos.measureandcount.ui.common.CommonButton
import com.lumisdinos.measureandcount.ui.common.ExpandHideNewItemField
import com.lumisdinos.measureandcount.ui.common.NewItemOutlinedEditor
import com.lumisdinos.measureandcount.ui.common.QuantityNewItemOutlinedEditor
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.DialogType
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemEffect
import com.lumisdinos.measureandcount.ui.theme.Purple80
import kotlinx.coroutines.delay


@Composable
fun AddNewItemScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: AddNewItemViewModel = hiltViewModel()
) {
    val navigationArg =
        navController.currentBackStackEntryAsState().value?.arguments?.getString("itemType")
    val itemType = navigationArg?.deserializeNewScreenType()
    Log.d("AddNewItemScreen", "ItemType: $itemType")

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    LaunchedEffect(key1 = itemType) {
        viewModel.processIntent(AddNewItemIntent.SetItemType(itemType))
    }
    DisposableEffect(lifecycleOwner.lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                viewModel.processIntent(AddNewItemIntent.HandleScreenExit)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val state by viewModel.state.collectAsState()
    val dialogState = remember { mutableStateOf<DialogType>(DialogType.None) }
    val shouldFlash = remember { mutableStateOf(false) }

    CollectEffects(dialogState, shouldFlash, viewModel, navController, snackbarHostState)
    ChooseDialogTypes(dialogState, viewModel::processIntent)

    //Actual screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopBar(state.unionOfChipboards.title, viewModel::processIntent)
        AnimatedVisibility(visible = state.isAddAreaOpen) {
            if (itemType != null) AddNewItemArea(itemType, state, shouldFlash, viewModel)
        }
        ExpandHideNewItemField(state.isAddAreaOpen, viewModel::processIntent)
        ListOfNewItems(
            state.unionOfChipboards.hasColor,
            state.createdChipboards,
            viewModel::processIntent
        )
    }
}

@Composable
fun AddNewItemArea(
    type: NewScreenType,
    state: AddNewItemState,
    shouldFlash: MutableState<Boolean>,
    viewModel: AddNewItemViewModel,
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
            .padding(top = 0.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(0.6f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                WidthLengthFields(type, state.newOrEditChipboard, viewModel::processIntent)
                if (type.hasColor) {
                    AddItemColorField(state.newOrEditChipboard.colorName, viewModel::processIntent)
                }
                Spacer(modifier = Modifier.height(16.dp))
                QuantityField(
                    state.newOrEditChipboard.quantityAsString,
                    viewModel::processIntent
                )
            }

            AddChipboardButton(
                Modifier.align(Alignment.CenterVertically),
                state.isAddButtonAvailable,
                viewModel::processIntent
            )
        }
        ChipboardAsStringField(
            state.newOrEditChipboard.chipboardAsString,
            state.unionOfChipboards.hasColor,
            state.newOrEditChipboard.color
        )
    }

}


@Composable
fun WidthLengthFields(
    type: NewScreenType,
    chipboard: ChipboardUi,
    processIntent: (AddNewItemIntent) -> Unit
) {
    type.columnNames.forEachIndexed { index, nameResId ->
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = 8.dp)
        ) {
            if (type.directionColumn == index + 1) {
                UpArrowIcon()
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
            val sizeOfDim = getSizeForIndex(index, chipboard)
            NumberEditor(nameResId, sizeOfDim, index + 1, processIntent)
        }
    }
}


@Composable
fun QuantityField(quantity: String, processIntent: (AddNewItemIntent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(24.dp))
        QuantityNewItemOutlinedEditor(
            label = stringResource(R.string.quantity),
            value = quantity,
            onQuantityChanged = processIntent
        )
    }
}


@Composable
fun AddChipboardButton(
    modifier: Modifier = Modifier,
    isAddButtonAvailable: Boolean,
    processIntent: (AddNewItemIntent) -> Unit
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        CommonButton(
            stringResource(R.string.add),
            isAddButtonAvailable,
            { processIntent(AddNewItemIntent.AddChipboardToDb) },
        )
    }
}


@Composable
fun ListOfNewItems(
    hasColor: Boolean,
    chipboards: List<ChipboardUi>,
    processIntent: (AddNewItemIntent) -> Unit
) {

    LazyColumn {
        items(chipboards, key = { it.id }) { chipboard ->
            Row(
                modifier = Modifier
                    .clickable { processIntent(AddNewItemIntent.AskEditChipboard(chipboard)) },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chipboard.chipboardAsString,
                        modifier = Modifier.weight(1f)
                    )

                    if (hasColor) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height(42.dp)
                                .background(Color(chipboard.color))
                                .border(
                                    width = 1.dp,
                                    color = Color.Black,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                IconButton(onClick = {
                    processIntent(AddNewItemIntent.AskDeleteChipboard(chipboard))
                }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = stringResource(R.string.delete),
                        modifier = Modifier.scale(1.3f)
                    )
                }
            }
            HorizontalDivider(thickness = 4.dp, color = Purple80)
        }
    }
}


fun getSizeForIndex(index: Int, newOrEditChipboard: ChipboardUi?): String {
    if (newOrEditChipboard == null) return ""
    return when (index) {
        0 -> newOrEditChipboard.size1AsString
        1 -> newOrEditChipboard.size2AsString
        2 -> newOrEditChipboard.size3AsString
        else -> ""
    }
}


@Composable
fun NumberEditor(
    label: Int,
    sizeOfDim: String,
    dimension: Int,
    onSizeChangedIntent: (AddNewItemIntent) -> Unit
) {
    NewItemOutlinedEditor(
        label = stringResource(id = label),
        value = sizeOfDim,
        dimension = dimension,
        onSizeChanged = onSizeChangedIntent
    )
}


@Composable
fun TopBar(title: String, processIntent: (AddNewItemIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { processIntent(AddNewItemIntent.Back) },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.back),
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        BasicTextField(
            value = title,
            onValueChange = { newTitle ->
                processIntent(AddNewItemIntent.TitleOfUnionChanged(newTitle))
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(
                fontSize = 19.sp,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier.weight(1f)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(8.dp))
}


@Composable
fun CollectEffects(
    dialogState: MutableState<DialogType>,
    shouldFlash: MutableState<Boolean>,
    viewModel: AddNewItemViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {

                is AddNewItemEffect.ShowEditConfirmationDialog -> {
                    dialogState.value = DialogType.Edit(effect.chipboard)
                }

                is AddNewItemEffect.ShowDeleteConfirmationDialog -> {
                    dialogState.value = DialogType.Delete(effect.chipboard)
                }

                is AddNewItemEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }

                is AddNewItemEffect.FlashAddItemArea -> {
                    shouldFlash.value = true
                }

                is AddNewItemEffect.NavigateBack -> {
                    navController.popBackStack()
                }

                AddNewItemEffect.ShowShareUnionDialog -> {
                    dialogState.value = DialogType.ShareCurrentUnion
                }

                AddNewItemEffect.ShowRemoveUnionDialog -> {
                    dialogState.value = DialogType.RemoveCurrentUnion
                }

            }
        }
    }
}
