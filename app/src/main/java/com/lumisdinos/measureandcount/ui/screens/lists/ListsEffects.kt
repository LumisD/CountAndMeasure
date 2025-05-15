package com.lumisdinos.measureandcount.ui.screens.lists

import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI

sealed interface ListsEffects {
    data class NavigateToCountScreen(val unionId: Int) : ListsEffects
}