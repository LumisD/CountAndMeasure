package com.lumisdinos.measureandcount.ui.screens.addnewitem

import com.lumisdinos.measureandcount.ui.model.ChipboardUi

data class AddNewItemState(
    val title: String = "",
    val chipboards: List<ChipboardUi> = emptyList(),
    val newOrEditChipboard: ChipboardUi = ChipboardUi(),
    val isAddAreaOpen: Boolean = true,
    val isAddButtonAvailable: Boolean = false
)