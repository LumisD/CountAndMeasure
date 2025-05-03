package com.lumisdinos.measureandcount.ui.model

sealed class DialogType {
    data class Delete(val chipboard: ChipboardUi) : DialogType()
    data class Edit(val chipboard: ChipboardUi) : DialogType()
    object None : DialogType()
}