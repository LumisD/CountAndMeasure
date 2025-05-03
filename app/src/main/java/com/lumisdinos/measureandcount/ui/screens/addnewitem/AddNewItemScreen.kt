import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.lumisdinos.measureandcount.ui.model.deserializeNewScreenType
import com.lumisdinos.measureandcount.ui.common.UpArrowIcon
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.lumisdinos.measureandcount.ui.defaultScreenTypes
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemIntent
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemState
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.Yellowish
import com.lumisdinos.measureandcount.ui.colorList
import com.lumisdinos.measureandcount.ui.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.ColorItem
import com.lumisdinos.measureandcount.ui.model.DialogType
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemEffect
import kotlinx.coroutines.delay


@Composable
fun AddNewItemScreen(
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    viewModel: AddNewItemViewModel = hiltViewModel()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val navigationArg = navBackStackEntry?.arguments?.getString("itemType")
    val itemType = navigationArg?.deserializeNewScreenType() ?: defaultScreenTypes.first()
    //val originString = navBackStackEntry?.arguments?.getString("origin")
    //val origin = originString?.let { AddNewItemOrigin.valueOf(it) }
    Log.d("AddNewItemScreen", "ItemType: $itemType")

    LaunchedEffect(key1 = itemType) {
        viewModel.processIntent(AddNewItemIntent.SetItemType(itemType))
    }

    val state by viewModel.state.collectAsState()
    val dialogState = remember { mutableStateOf<DialogType>(DialogType.None) }
    val shouldFlash = remember { mutableStateOf(false) }

    CollectEffects(dialogState, shouldFlash, viewModel, navController, snackbarHostState)
    ChooseDialogType(dialogState, viewModel::processIntent)

    //Actual screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        TopBar(state.title, viewModel::processIntent)
        AnimatedVisibility(visible = state.isAddAreaOpen) {
            AddNewItemArea(itemType, state, shouldFlash, viewModel)
        }
        ExpandHideField(state.isAddAreaOpen, viewModel::processIntent)
        ListOfNewItems(state.chipboards, viewModel::processIntent)
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
        targetValue = if (shouldFlash.value) Color.Blue.copy(alpha = 0.5f) else Color.Transparent,
        animationSpec = tween(durationMillis = 600),
        label = "backgroundColor"
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
            .padding(16.dp),
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
                    ColorField(state.newOrEditChipboard.colorName, viewModel::processIntent)
                }
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
        ChipboardAsStringField(state.newOrEditChipboard)
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (type.directionColumn == index + 1) {
                UpArrowIcon()
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
            //Text(text = stringResource(nameResId))
            val sizeOfDim = getSizeForIndex(index, chipboard)
            NumberEditor(nameResId, sizeOfDim, index + 1, processIntent)
        }
    }
}


@Composable
fun ColorField(color: String, processIntent: (AddNewItemIntent) -> Unit) {
    val selectedColor = colorList.firstOrNull { it.name == color } ?: colorList.first()
    Spacer(modifier = Modifier.height(16.dp))
    ColorPickerRow(
        selectedColor = selectedColor,
        onColorSelected = { colorItem ->
            processIntent(AddNewItemIntent.ColorChanged(colorItem.name, colorItem.color))
        }
    )
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun QuantityField(quantity: String, processIntent: (AddNewItemIntent) -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Text(text = stringResource(R.string.quantity))
        Spacer(modifier = Modifier.width(24.dp))
        QuantityEditor(R.string.quantity, quantity, processIntent)
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
        Button(
            onClick = { processIntent(AddNewItemIntent.AddChipboard) },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(50.dp),
            enabled = isAddButtonAvailable,
            colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.7f))
        ) {
            Text(
                text = stringResource(R.string.add),
                color = Color.White
            )
        }
    }
}


@Composable
fun ChipboardAsStringField(editingChipboard: ChipboardUi) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Yellowish)
            .border(width = 1.dp, color = Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = editingChipboard.chipboardAsString,
            modifier = Modifier
                .weight(1f)
                .wrapContentHeight(),
            textAlign = TextAlign.Center,
            fontSize = 18.sp
        )
        VerticalDivider(
            modifier = Modifier
                .height(42.dp)
                .width(1.dp),
            color = Color.Black
        )
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(42.dp)
                .background(Color(editingChipboard.color))
        )
    }
}


@Composable
fun ExpandHideField(isAddAreaOpen: Boolean, processIntent: (AddNewItemIntent) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rotationAngle by animateFloatAsState(
            targetValue = if (isAddAreaOpen) 0f else 180f,
            animationSpec = tween(durationMillis = 500),
            label = "rotation"
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = "Expand/Collapse",
            modifier = Modifier
                .size(48.dp)
                .rotate(rotationAngle)
                .clickable { processIntent(AddNewItemIntent.ToggleAddAreaVisibility) }
        )
    }
}


@Composable
fun ListOfNewItems(
    chipboards: List<ChipboardUi>,
    processIntent: (AddNewItemIntent) -> Unit
) {

    LazyColumn {
        items(chipboards, key = { it.id }) { chipboard ->
            Row(
                modifier = Modifier
                    .clickable { processIntent(AddNewItemIntent.EditChipboard(chipboard)) },
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

                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(42.dp)
                            .background(Color(chipboard.color))
                            .border(width = 1.dp, color = Color.Black)
                    )
                }

                IconButton(onClick = {
                    processIntent(AddNewItemIntent.DeleteChipboard(chipboard))
                }) {
                    Icon(
                        Icons.Filled.Close,
                        contentDescription = "Delete",
                        modifier = Modifier.scale(1.3f)
                    )
                }
            }
            HorizontalDivider(thickness = 2.dp, color = Color.Gray)
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
    OutlinedTextField(
        modifier = Modifier.widthIn(min = 60.dp, max = 150.dp),
        value = sizeOfDim,
        onValueChange = { newValue: String ->
            onSizeChangedIntent(AddNewItemIntent.SizeChanged(newValue, dimension))
        },
        label = { Text(text = stringResource(id = label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    )
}


@Composable
fun QuantityEditor(
    label: Int,
    quantity: String,
    onQuantityChangedIntent: (AddNewItemIntent) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier.widthIn(min = 60.dp, max = 150.dp),
        value = quantity,
        onValueChange = { newValue: String ->
            onQuantityChangedIntent(AddNewItemIntent.QuantityChanged(newValue))
        },
        label = { Text(text = stringResource(id = label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    )
}


@Composable
fun ColorPickerRow(selectedColor: ColorItem, onColorSelected: (ColorItem) -> Unit) {
    var showColorPicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .clickable { showColorPicker = !showColorPicker }
    ) {
        Text(text = stringResource(R.string.color_column))
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(Color(selectedColor.color), shape = CircleShape)
                .border(1.dp, Color.Gray, CircleShape)
        )
        Spacer(modifier = Modifier.weight(1f)) // <--- this pushes content to the start and fills remaining space
        DropdownMenu(
            expanded = showColorPicker,
            onDismissRequest = { showColorPicker = false }
        ) {
            colorList.forEach { colorItem ->
                DropdownMenuItem(
                    text = { Text(text = colorItem.name) },
                    onClick = {
                        onColorSelected(colorItem)
                        showColorPicker = false
                    },
                    leadingIcon = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(Color(colorItem.color), shape = CircleShape)
                        )
                    }
                )
            }
        }
    }
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
                contentDescription = "Back",
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        BasicTextField(
            value = title,
            onValueChange = { newTitle ->
                processIntent(AddNewItemIntent.TitleChanged(newTitle))
            },
            textStyle = MaterialTheme.typography.titleLarge.copy(fontSize = 19.sp)
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
//            when (origin) {
//                AddNewItemOrigin.NEW_SCREEN -> {
//                    navController.popBackStack(Screen.New.route, inclusive = false)
//                }
//
//                AddNewItemOrigin.CREATE_OWN_MEASURE -> {
//                    navController.popBackStack(Screen.CreateOwnMeasure.route, inclusive = false)
//                }
//
//                null -> {
//                    navController.popBackStack()
//                }
//            }
                    navController.popBackStack()
                }

            }
        }
    }
}


@Composable
fun ChooseDialogType(
    dialogState: MutableState<DialogType>,
    processIntent: (AddNewItemIntent) -> Unit
) {
    when (val dialog = dialogState.value) {
        is DialogType.Delete -> {
            ShowDialog(
                title = stringResource(R.string.confirm_deletion),
                text = stringResource(
                    R.string.are_you_sure_delete,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                ),
                confirmText = stringResource(R.string.delete),
                dismissText = stringResource(R.string.cancel),
                onDismiss = { dialogState.value = DialogType.None },
                onConfirm = {
                    processIntent(AddNewItemIntent.DeleteChipboardConfirmed(dialog.chipboard.id))
                    dialogState.value = DialogType.None
                }
            )
        }

        is DialogType.Edit -> {
            ShowDialog(
                title = stringResource(R.string.confirm_editing),
                text = stringResource(
                    R.string.are_you_sure_edit,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                ),
                confirmText = stringResource(R.string.edit),
                dismissText = stringResource(R.string.cancel),
                onDismiss = { dialogState.value = DialogType.None },
                onConfirm = {
                    processIntent(AddNewItemIntent.EditChipboardConfirmed(dialog.chipboard))
                    dialogState.value = DialogType.None
                }
            )
        }

        DialogType.None -> {}
    }
}


@Composable
fun ShowDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        },
        title = { Text(title) },
        text = {
            Text(text)
        }
    )
}