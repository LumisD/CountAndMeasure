package com.lumisdinos.measureandcount.ui.screens.addnewitem

import com.lumisdinos.measureandcount.ui.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.NewScreenType

sealed interface AddNewItemIntent {
    data class TitleChanged(val newTitle: String) : AddNewItemIntent
    data class SizeChanged(val newSizeAsString: String, val dimension: Int) : AddNewItemIntent
    data class QuantityChanged(val newQuantityAsString: String) : AddNewItemIntent
    data class ColorChanged(val newColorName: String, val newColor: Int) : AddNewItemIntent
    data class EditChipboard(val chipboard: ChipboardUi) : AddNewItemIntent
    data class SetItemType(val itemType: NewScreenType) : AddNewItemIntent
    data class DeleteChipboard(val chipboard: ChipboardUi) : AddNewItemIntent
    data class EditChipboardConfirmed(val chipboard: ChipboardUi) : AddNewItemIntent
    data class DeleteChipboardConfirmed(val chipboardId: Int) : AddNewItemIntent
    data object AddChipboard : AddNewItemIntent
    data object ToggleAddAreaVisibility : AddNewItemIntent
    data object Back : AddNewItemIntent
}