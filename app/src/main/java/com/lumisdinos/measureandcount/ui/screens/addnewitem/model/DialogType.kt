package com.lumisdinos.measureandcount.ui.screens.addnewitem.model

sealed class DialogType {
    data class Delete(val chipboard: ChipboardUi, val hasColor: Boolean) : DialogType()
    data class Edit(val chipboard: ChipboardUi, val hasColor: Boolean) : DialogType()
    data object ShareCurrentUnion: DialogType()
    data object RemoveCurrentUnion: DialogType()
    data object None : DialogType()
}