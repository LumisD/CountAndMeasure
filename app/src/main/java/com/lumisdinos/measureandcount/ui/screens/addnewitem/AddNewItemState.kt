package com.lumisdinos.measureandcount.ui.screens.addnewitem

import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ChipboardUi

data class AddNewItemState(
    val titleOfUnion: String = "",
    val createdChipboards: List<ChipboardUi> = emptyList(),
    val newOrEditChipboard: ChipboardUi = ChipboardUi(),
    val isAddAreaOpen: Boolean = true,
    val isAddButtonAvailable: Boolean = false
)