package com.lumisdinos.measureandcount.ui.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.screens.addnewitem.AddNewItemIntent
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ConfirmationType
import com.lumisdinos.measureandcount.ui.screens.count.model.QuestionType
import com.lumisdinos.measureandcount.ui.screens.count.CountIntent
import com.lumisdinos.measureandcount.ui.screens.count.model.DialogType
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.DialogType as AddNewItemDialogType


@Composable
fun ShowDialog(
    title: String,
    text: String,
    confirmText: String,
    dismissText: String? = null,
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
            if (dismissText != null) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
            }
        },
        title = { Text(title) },
        text = {
            Text(text)
        }
    )
}


@Composable
fun ChooseDialogType(
    dialogState: MutableState<DialogType>,
    processIntent: (CountIntent) -> Unit
) {
    val dialog = dialogState.value

    if (dialog != DialogType.None) {
        val title: String
        val text: String
        val confirmText: String
        val dismissText: String?
        val onConfirm: () -> Unit
        val onDismiss: () -> Unit = { dialogState.value = DialogType.None }

        when (dialog) {
            is DialogType.Uncheck -> {
                title = stringResource(R.string.confirm_uncheck)
                text = stringResource(
                    R.string.are_you_sure_uncheck,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                )
                confirmText = stringResource(R.string.uncheck)
                dismissText = stringResource(R.string.cancel)
                onConfirm = {
                    processIntent(
                        CountIntent.ActionConfirmed(
                            ConfirmationType.UncheckChipboardConfirmed(
                                dialog.chipboard
                            )
                        )
                    )
                    dialogState.value = DialogType.None
                }
            }

            is DialogType.SelectNotFoundToFindArea -> {
                title = stringResource(R.string.confirm_selection)
                text = stringResource(
                    R.string.are_you_sure_select_not_found,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                )
                confirmText = stringResource(R.string.select)
                dismissText = stringResource(R.string.cancel)
                onConfirm = {
                    processIntent(
                        CountIntent.ActionConfirmed(
                            ConfirmationType.SelectNotFoundToFindAreaConfirmed(
                                dialog.chipboard
                            )
                        )
                    )
                    dialogState.value = DialogType.None
                }
            }

            is DialogType.RemoveNotFoundFromFindArea -> {
                title = stringResource(R.string.confirm_selection)
                text = stringResource(
                    R.string.are_you_sure_remove_not_found,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                )
                confirmText = stringResource(R.string.remove)
                dismissText = stringResource(R.string.cancel)
                onConfirm = {
                    processIntent(
                        CountIntent.ActionConfirmed(
                            ConfirmationType.RemoveNotFoundFromFindAreaConfirmed(
                                dialog.chipboard
                            )
                        )
                    )
                    dialogState.value = DialogType.None
                }
            }

            is DialogType.SelectUnknownToFindArea -> {
                title = stringResource(R.string.confirm_selection)
                text = stringResource(
                    R.string.are_you_sure_select_unknown,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                )
                confirmText = stringResource(R.string.select)
                dismissText = stringResource(R.string.cancel)
                onConfirm = {
                    processIntent(
                        CountIntent.ActionConfirmed(
                            ConfirmationType.SelectUnknownToFindAreaConfirmed(
                                dialog.chipboard
                            )
                        )
                    )
                    dialogState.value = DialogType.None
                }
            }

            is DialogType.NotExceedingTargetQuantity -> {
                title = stringResource(R.string.not_exceeding_target_quantity)
                text = stringResource(
                    R.string.not_exceeding_target_quantity_explanation,
                    dialog.enteredQuantity,
                    dialog.targetQuantity)
                confirmText = stringResource(R.string.ok)
                dismissText = null
                onConfirm = { dialogState.value = DialogType.None }
            }

            is DialogType.WhatIs -> {
                title = stringResource(R.string.what_is)
                text = when (dialog.questionType) {
                    QuestionType.Found -> stringResource(R.string.what_is_found)
                    QuestionType.Unknown -> stringResource(R.string.what_is_unknown)
                    QuestionType.RealSize -> stringResource(R.string.what_is_difference)
                }
                confirmText = stringResource(R.string.ok)
                dismissText = null
                onConfirm = { dialogState.value = DialogType.None }
            }

            DialogType.FieldDisabled -> {
                title = stringResource(R.string.field_disabled)
                text = stringResource(R.string.disabled_explanation)
                confirmText = stringResource(R.string.ok)
                dismissText = null // No dismiss button
                onConfirm = { dialogState.value = DialogType.None }
            }

            DialogType.None -> {
                title = ""
                text = ""
                confirmText = ""
                dismissText = null
                onConfirm = {}
            }
        }

        ShowDialog(
            title = title,
            text = text,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}


@Composable
fun ChooseDialogTypes(
    dialogState: MutableState<AddNewItemDialogType>,
    processIntent: (AddNewItemIntent) -> Unit
) {
    val dialog = dialogState.value

    if (dialog != AddNewItemDialogType.None) {
        val title: String
        val text: String
        val confirmText: String
        val dismissText: String?
        val onConfirm: () -> Unit
        val onDismiss: () -> Unit = { dialogState.value = AddNewItemDialogType.None }

        when (dialog) {
            is AddNewItemDialogType.Delete -> {
                title = stringResource(R.string.confirm_deletion)
                text = stringResource(
                    R.string.are_you_sure_delete,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                )
                confirmText = stringResource(R.string.delete)
                dismissText = stringResource(R.string.cancel)
                onConfirm = {
                    processIntent(AddNewItemIntent.DeleteChipboardConfirmed(dialog.chipboard.id))
                    dialogState.value = AddNewItemDialogType.None
                }
            }

            is AddNewItemDialogType.Edit -> {
                title = stringResource(R.string.confirm_editing)
                text = stringResource(
                    R.string.are_you_sure_edit,
                    dialog.chipboard.chipboardAsString,
                    dialog.chipboard.colorName
                )
                confirmText = stringResource(R.string.edit)
                dismissText = stringResource(R.string.cancel)
                onConfirm = {
                    processIntent(AddNewItemIntent.EditChipboardConfirmed(dialog.chipboard))
                    dialogState.value = AddNewItemDialogType.None
                }
            }

            AddNewItemDialogType.None -> {
                title = ""
                text = ""
                confirmText = ""
                dismissText = null
                onConfirm = {}
            }
        }

        ShowDialog(
            title = title,
            text = text,
            confirmText = confirmText,
            dismissText = dismissText,
            onConfirm = onConfirm,
            onDismiss = onDismiss
        )
    }
}