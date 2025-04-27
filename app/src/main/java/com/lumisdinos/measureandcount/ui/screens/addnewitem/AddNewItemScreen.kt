import android.util.Log
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.ui.text.font.FontWeight
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.MutableState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.Yellowish
import com.lumisdinos.measureandcount.ui.colorList
import com.lumisdinos.measureandcount.ui.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.ColorItem
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemEffect
import kotlin.text.filter
import kotlin.text.isDigit
import kotlin.text.toFloatOrNull


@Composable
fun AddNewItemScreen(
    navController: NavController,
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
    val dialogState = remember { mutableStateOf<ChipboardUi?>(null) }

    collectEffects(dialogState, viewModel, navController)
    ShowDeleteDialog(dialogState, viewModel::processIntent)

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
            AddNewItemArea(itemType, state, viewModel)
        }
        Box {
            ExpandHideField(state.isAddAreaOpen, viewModel::processIntent)
            ListOfNewItems(state.chipboards, viewModel::processIntent)
        }
    }
}

@Composable
fun AddNewItemArea(type: NewScreenType, state: AddNewItemState, viewModel: AddNewItemViewModel) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(2f / 3f),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                WidthLengthFields(type, state.newOrEditChipboard, viewModel::processIntent)
                if (type.hasColor) {
                    ColorField(state.newOrEditChipboard.color, viewModel::processIntent)
                }
                QuantityField(
                    state.newOrEditChipboard.quantity.toString(),
                    viewModel::processIntent
                )

            }
            AddChipboardButton(viewModel::processIntent)
        }
        ChipboardAsStringField(state.editingChipboardAsString)
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
            //modifier = Modifier.clickable { /* Handle click here, e.g., viewModel.onRowClicked(index) */ },
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (type.directionColumn == index + 1) {
                UpArrowIcon()
            } else {
                Spacer(modifier = Modifier.width(24.dp))
            }
            //Text(text = stringResource(nameResId))
            val sizeOfDim = getSizeForIndex(index, chipboard).toString()
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
            processIntent(AddNewItemIntent.ColorChanged(colorItem.name))
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
fun AddChipboardButton(processIntent: (AddNewItemIntent) -> Unit) {
    Button(
        onClick = { processIntent(AddNewItemIntent.AddChipboard) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Blue.copy(alpha = 0.7f))
    ) {
        Text(text = stringResource(R.string.add))
    }
}

@Composable
fun ChipboardAsStringField(editingChipboardAsString: String) {
    Spacer(modifier = Modifier.height(16.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Yellowish)
            .padding(12.dp), // padding inside yellow background
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = editingChipboardAsString,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ExpandHideField(isAddAreaOpen: Boolean, processIntent: (AddNewItemIntent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent)
            .padding(16.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rotationAngle by animateFloatAsState(
            targetValue = if (isAddAreaOpen) 180f else 0f,
            animationSpec = tween(durationMillis = 300),
            label = "rotation"
        )
        Spacer(modifier = Modifier.weight(2f))
        Icon(
            imageVector = if (isAddAreaOpen) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = "Expand/Collapse",
            modifier = Modifier
                .size(32.dp)
                .rotate(rotationAngle)
                .clickable { processIntent(AddNewItemIntent.ToggleAddAreaVisibility) }
                .weight(1f, fill = false)
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
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = chipboard.chipboardAsString,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = {
                    processIntent(AddNewItemIntent.EditChipboard(chipboard))
                }) {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit")
                }

                IconButton(onClick = {
                    processIntent(AddNewItemIntent.DeleteChipboard(chipboard))
                }) {
                    Icon(Icons.Filled.Close, contentDescription = "Delete")
                }
            }
        }
    }
}

fun getSizeForIndex(index: Int, newOrEditChipboard: ChipboardUi?): Float {
    if (newOrEditChipboard == null) return 0f
    return when (index) {
        0 -> newOrEditChipboard.size1
        1 -> newOrEditChipboard.size2
        2 -> newOrEditChipboard.size3
        3 -> newOrEditChipboard.size4
        4 -> newOrEditChipboard.size5
        else -> 0f
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
        modifier = Modifier.widthIn(min = 60.dp, max = 250.dp),
        value = sizeOfDim,
        onValueChange = { newValue: String ->
            val filteredValue = newValue.filter { it.isDigit() || it == '.' }
            val floatValue = filteredValue.toFloatOrNull() ?: 0f
            onSizeChangedIntent(AddNewItemIntent.SizeChanged(floatValue, dimension))
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
        modifier = Modifier.widthIn(min = 60.dp, max = 250.dp),
        value = quantity,
        onValueChange = { newValue: String ->
            val filteredValue = newValue.filter { it.isDigit() }
            if (filteredValue != newValue) {
                return@OutlinedTextField
            }
            val shortValue = filteredValue.toShortOrNull() ?: 0
            onQuantityChangedIntent(AddNewItemIntent.QuantityChanged(shortValue))
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
                .background(selectedColor.color, shape = CircleShape)
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
                                .background(colorItem.color, shape = CircleShape)
                        )
                    }
                )
            }
        }
    }

}

@Composable
fun TopBar(title: String, processIntent: (AddNewItemIntent) -> Unit) {
    Spacer(modifier = Modifier.height(16.dp))
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
        BasicTextField(
            value = title,
            onValueChange = { newTitle ->
                processIntent(AddNewItemIntent.TitleChanged(newTitle))
            },
            textStyle = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold)
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    HorizontalDivider(thickness = 2.dp, color = Color.Gray)
    Spacer(modifier = Modifier.height(8.dp))
}


@Composable
fun collectEffects(
    dialogState: MutableState<ChipboardUi?>,
    viewModel: AddNewItemViewModel,
    navController: NavController,
) {
    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AddNewItemEffect.ShowDeleteConfirmationDialog -> {
                    dialogState.value = effect.chipboard
                }

                is AddNewItemEffect.ShowSnackbar -> {
                    SnackbarHostState().showSnackbar(effect.message)
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
fun ShowDeleteDialog(
    dialogState: MutableState<ChipboardUi?>,
    processIntent: (AddNewItemIntent) -> Unit
) {
    dialogState.value?.let { chipboard ->
        AlertDialog(
            onDismissRequest = { dialogState.value = null },
            confirmButton = {
                TextButton(onClick = {
                    processIntent(
                        AddNewItemIntent.DeleteChipboardConfirmed(
                            chipboard.id
                        )
                    )
                    dialogState.value = null
                }) {
                    Text(stringResource(R.string.delete))
                }
            },
            dismissButton = {
                TextButton(onClick = { dialogState.value = null }) {
                    Text("Cancel")
                }
            },
            title = { Text(stringResource(R.string.confirm_deletion)) },
            text = {
                Text(
                    stringResource(
                        R.string.are_you_sure_delete,
                        chipboard.chipboardAsString
                    )
                )
            }
        )
    }

}