package com.lumisdinos.measureandcount.ui.screens.addnewitem

import android.content.Intent
import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ChipboardUi

sealed class AddNewItemEffect {
    data class ShowDeleteConfirmationDialog(val chipboard: ChipboardUi) : AddNewItemEffect()
    data class ShowEditConfirmationDialog(val chipboard: ChipboardUi) : AddNewItemEffect()
    data class ShowSnackbar(val message: String) : AddNewItemEffect()
    data object ShowRemoveUnionDialog : AddNewItemEffect()
    data object ShowShareUnionDialog : AddNewItemEffect()
    data object FlashAddItemArea : AddNewItemEffect()
    data class ShareUnion(val shareIntent: Intent) : AddNewItemEffect()
    data object NavigateBack : AddNewItemEffect()
}