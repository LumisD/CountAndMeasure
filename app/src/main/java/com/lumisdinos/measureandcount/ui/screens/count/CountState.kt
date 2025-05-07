package com.lumisdinos.measureandcount.ui.screens.count

import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi


data class CountState(
    val unionOfChipboards: UnionOfChipboardsUI = UnionOfChipboardsUI(),
    val chipboards: List<ChipboardUi> = emptyList(),
    val chipboardToFind: ChipboardUi = ChipboardUi(),
    val isFoundAreaOpen: Boolean = true,
    val isFoundButtonAvailable: Boolean = false,
    val isUnknownButtonAvailable: Boolean = false,
    val isNoLists: Boolean = false,
)