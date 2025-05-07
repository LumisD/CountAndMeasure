package com.lumisdinos.measureandcount.ui.common

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemIntent
import com.lumisdinos.measureandcount.ui.screens.count.CountIntent

@Composable
private fun <T> OutlinedEditorInternal(
    width: Dp,
    height: Dp,
    label: String,
    value: String,
    onSizeChanged: (T) -> Unit,
    intentFactory: (String) -> T
) {
    OutlinedTextField(
        modifier = Modifier.width(width).height(height),
        value = value,
        onValueChange = { newValue ->
            onSizeChanged(intentFactory(newValue))
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    )
}

@Composable
private fun <T> OutlinedEditorInternal(
    width: Dp,
    height: Dp,
    label: String,
    value: String,
    dimension: Int,
    onSizeChanged: (T) -> Unit,
    intentFactory: (String, Int) -> T
) {
    OutlinedTextField(
        modifier = Modifier
            .width(width)
            .height(height),
        value = value,
        onValueChange = { newValue ->
            onSizeChanged(intentFactory(newValue, dimension))
        },
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        colors = TextFieldDefaults.colors(
            unfocusedContainerColor = Color.Transparent,
            focusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
        )
    )
}

@Composable
fun CountOutlinedEditor(
    label: String,
    value: String,
    dimension: Int,
    onSizeChanged: (CountIntent) -> Unit,
    width: Dp = 150.dp,
    height: Dp = 60.dp
) {
    OutlinedEditorInternal(
        width, height, label, value, dimension,
        onSizeChanged,
        intentFactory = { vl, dim -> CountIntent.SizeChanged(vl, dim) }
    )
}

@Composable
fun NewItemOutlinedEditor(
    label: String,
    value: String,
    dimension: Int,
    onSizeChanged: (AddNewItemIntent) -> Unit,
    width: Dp = 150.dp,
    height: Dp = 60.dp
) {
    OutlinedEditorInternal(
        width, height, label, value, dimension,
        onSizeChanged,
        intentFactory = { vl, dim -> AddNewItemIntent.SizeChanged(vl, dim) }
    )
}

@Composable
fun QuantityNewItemOutlinedEditor(
    label: String,
    value: String,
    onQuantityChanged: (AddNewItemIntent) -> Unit,
    width: Dp = 150.dp,
    height: Dp = 60.dp
) {
    OutlinedEditorInternal(
        width, height, label, value,
        onQuantityChanged,
        intentFactory = { vl -> AddNewItemIntent.QuantityChanged(vl) }
    )
}

@Composable
fun QuantityCountOutlinedEditor(
    label: String,
    value: String,
    onQuantityChanged: (CountIntent) -> Unit,
    width: Dp = 150.dp,
    height: Dp = 60.dp
) {
    OutlinedEditorInternal(
        width, height, label, value,
        onQuantityChanged,
        intentFactory = { vl -> CountIntent.QuantityChanged(vl) }
    )
}
