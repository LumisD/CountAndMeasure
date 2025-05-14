package com.lumisdinos.measureandcount.ui.screens.addnewitem

import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ChipboardUi
import com.lumisdinos.measureandcount.ui.model.NewScreenType

sealed interface AddNewItemIntent {
    data class SetItemType(val itemType: NewScreenType?) : AddNewItemIntent
    data class TitleOfUnionChanged(val newTitle: String) : AddNewItemIntent
    data class SizeChanged(val newSizeAsString: String, val dimension: Int) : AddNewItemIntent
    data class QuantityChanged(val newQuantityAsString: String) : AddNewItemIntent
    data class ColorChanged(val newColorName: String, val newColor: Int) : AddNewItemIntent
    data class AskEditChipboard(val chipboard: ChipboardUi) : AddNewItemIntent
    data class EditChipboardConfirmed(val chipboard: ChipboardUi) : AddNewItemIntent
    data class AskDeleteChipboard(val chipboard: ChipboardUi) : AddNewItemIntent
    data class DeleteChipboardConfirmed(val chipboardId: Int) : AddNewItemIntent
    data object AddChipboardToDb : AddNewItemIntent
    data object ToggleAddAreaVisibility : AddNewItemIntent
    data object HandleScreenExit : AddNewItemIntent
    data object Back : AddNewItemIntent
}