package com.lumisdinos.measureandcount.ui.screens.lists

import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

data class ListsState(
    val listOfUnions: List<UnionOfChipboardsUI> = emptyList()
)