package com.lumisdinos.measureandcount.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.colorList
import com.lumisdinos.measureandcount.ui.model.ColorItem
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemIntent
import com.lumisdinos.measureandcount.ui.screens.count.CountIntent
import com.lumisdinos.measureandcount.ui.screens.count.CountState

@Composable
private fun <T> ColorFieldInternal(
    color: String,
    processIntent: (T) -> Unit,
    intentFactory: (String, Int) -> T
) {
    val selectedColor = colorList.firstOrNull { it.name == color } ?: colorList.first()
    Spacer(modifier = Modifier.height(16.dp))
    ColorPickerRow(
        selectedColor = selectedColor,
        onColorSelected = { colorItem ->
            processIntent(intentFactory(colorItem.name, colorItem.color))
        }
    )
    Spacer(modifier = Modifier.height(16.dp))
}


@Composable
fun AddItemColorField(color: String, processIntent: (AddNewItemIntent) -> Unit) {
    ColorFieldInternal(
        color = color,
        processIntent = processIntent,
        intentFactory = { name, value -> AddNewItemIntent.ColorChanged(name, value) }
    )
}


@Composable
fun CountColorField(
    state: CountState,
    processIntent: (CountIntent) -> Unit
) {
    Box {
        AddCountColorField(
            state.chipboardToFind.colorName,
            processIntent
        )

        if (state.chipboardToFind.isUnderReview) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable(enabled = false) { }
            )
        }
    }
}


@Composable
fun AddCountColorField(color: String, processIntent: (CountIntent) -> Unit) {
    ColorFieldInternal(
        color = color,
        processIntent = processIntent,
        intentFactory = { name, value -> CountIntent.ColorChanged(name, value) }
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
        Spacer(modifier = Modifier.weight(1f))
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
