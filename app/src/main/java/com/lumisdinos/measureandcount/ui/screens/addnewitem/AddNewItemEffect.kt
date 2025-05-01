package com.lumisdinos.measureandcount.ui.screens.addnewitem

import com.lumisdinos.measureandcount.ui.model.ChipboardUi

sealed class AddNewItemEffect {
    data class ShowDeleteConfirmationDialog(val chipboard: ChipboardUi) : AddNewItemEffect()
    data class ShowSnackbar(val message: String) : AddNewItemEffect()
    object FlashAddItemArea : AddNewItemEffect()
    object NavigateBack : AddNewItemEffect()
}