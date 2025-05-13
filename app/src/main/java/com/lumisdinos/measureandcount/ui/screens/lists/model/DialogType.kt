package com.lumisdinos.measureandcount.ui.screens.lists.model

import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI


sealed class DialogType {
    data class DeleteUnion(val union: UnionOfChipboardsUI) : DialogType()
    data object None : DialogType()
}