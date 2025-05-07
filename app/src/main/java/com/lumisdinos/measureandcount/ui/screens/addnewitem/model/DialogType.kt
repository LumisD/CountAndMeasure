package com.lumisdinos.measureandcount.ui.screens.addnewitem.model

sealed class DialogType {
    data class Delete(val chipboard: ChipboardUi) : DialogType()
    data class Edit(val chipboard: ChipboardUi) : DialogType()
    data object None : DialogType()
}