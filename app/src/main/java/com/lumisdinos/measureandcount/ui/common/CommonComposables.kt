package com.lumisdinos.measureandcount.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemIntent
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.QuestionType
import com.lumisdinos.measureandcount.ui.screens.count.CountIntent
import com.lumisdinos.measureandcount.ui.theme.PrimaryBlue
import com.lumisdinos.measureandcount.ui.theme.Yellowish

@Composable
fun UpArrowIcon(modifier: Modifier = Modifier) {
    Icon(
        imageVector = Icons.Filled.ArrowUpward,
        contentDescription = "Up",
        modifier = modifier
    )
}


@Composable
fun XIcon() {
    Icon(
        imageVector = Icons.Filled.Close,
        contentDescription = "X",
        modifier = Modifier.size(24.dp)
    )
}


@Composable
fun CommonButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    colors: ButtonColors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.7f))
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp),
        enabled = enabled,
        colors = colors
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onPrimary//Color.White
        )
    }
}


@Composable
fun ChipboardAsStringField(chipboardAsString: String, color: Int) {
    Spacer(modifier = Modifier.height(16.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Yellowish)
            .border(width = 1.dp, color = Color.Black),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = chipboardAsString,
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
                .background(Color(color))
        )
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


@Composable
fun ShowDialog(
    title: String,
    text: String,
    confirmText: String,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(confirmText)
            }
        },
        title = { Text(title) },
        text = {
            Text(text)
        }
    )
}

@Composable
private fun <T> ExpandHideFieldInternal(
    isAreaOpen: Boolean,
    processIntent: (T) -> Unit,
    intentFactory: () -> T
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val rotationAngle by animateFloatAsState(
            targetValue = if (isAreaOpen) 0f else 180f,
            animationSpec = tween(durationMillis = 500),
            label = "rotation"
        )

        Icon(
            imageVector = Icons.Filled.KeyboardArrowUp,
            contentDescription = "Expand/Collapse",
            modifier = Modifier
                .size(48.dp)
                .rotate(rotationAngle)
                .clickable { processIntent(intentFactory()) }
        )
    }
}


@Composable
fun ExpandHideNewItemField(isAddAreaOpen: Boolean, processIntent: (AddNewItemIntent) -> Unit) {
    ExpandHideFieldInternal(
        isAddAreaOpen,
        processIntent,
        intentFactory = { AddNewItemIntent.ToggleAddAreaVisibility }
    )
}


@Composable
fun ExpandHideCountField(isFindAreaOpen: Boolean, processIntent: (CountIntent) -> Unit) {
    ExpandHideFieldInternal(
        isFindAreaOpen,
        processIntent,
        intentFactory = { CountIntent.ToggleFindAreaVisibility }
    )
}


@Composable
fun DisabledOverlay(
    isDisabled: Boolean,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.wrapContentSize()) {
        content()

        if (isDisabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    //.background(Grayish.copy(alpha = 0.6f))
                    .clickable(enabled = false) { }
            )
        }
    }
}


@Composable
fun DisabledOverlay(
    isEnabled: Boolean,
    onDisabledClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.wrapContentSize()) {
        content()

        if (!isEnabled) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { onDisabledClick() }
            )
        }
    }
}


@Composable
fun WhatIsIconButton(
    questionType: QuestionType,
    processIntent: (CountIntent) -> Unit,
    contentDescription: String
) {
    IconButton(
        onClick = {
            processIntent(CountIntent.ShowWhatIs(questionType))
        },
        modifier = Modifier.size(48.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.HelpOutline,
            contentDescription = contentDescription,
            modifier = Modifier.size(36.dp),
            tint = PrimaryBlue
        )
    }
}

