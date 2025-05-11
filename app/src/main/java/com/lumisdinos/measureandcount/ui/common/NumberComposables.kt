package com.lumisdinos.measureandcount.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SizeCountOutlinedEditor(
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


@Composable
fun RealSizeInput(
    value: String,
    label: String,
    dimension: Int,
    isEnabled: Boolean,
    onValueChange: (CountIntent) -> Unit,
    intentFactory: (String, Int) -> CountIntent,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .width(80.dp)
            .padding(start = 8.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontStyle = FontStyle.Italic,
                fontSize = 13.sp
            ),
            color = Color.Gray
        )
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                onValueChange(intentFactory(newValue, dimension))
            },
            enabled = isEnabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            textStyle = MaterialTheme.typography.bodySmall.copy(
                color = Color.DarkGray,
                fontStyle = FontStyle.Italic,
                fontSize = 14.sp
            ),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .padding(vertical = 4.dp)
                        .border(
                            width = 0.5.dp,
                            color = Color.LightGray,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = "0",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}


