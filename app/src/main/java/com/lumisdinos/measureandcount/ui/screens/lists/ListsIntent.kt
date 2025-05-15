package com.lumisdinos.measureandcount.ui.screens.lists

import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

sealed interface ListsIntent {
    data class PressOnItemInList(val union: UnionOfChipboardsUI) : ListsIntent
}