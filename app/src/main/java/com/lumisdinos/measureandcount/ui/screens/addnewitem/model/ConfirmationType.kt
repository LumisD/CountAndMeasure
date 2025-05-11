package com.lumisdinos.measureandcount.ui.screens.addnewitem.model

import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi

sealed class ConfirmationType {
    data class UncheckChipboardConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data class SelectNotFoundToFindAreaConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data class RemoveNotFoundFromFindAreaConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data class SelectUnknownToFindAreaConfirmed(val chipboard: ChipboardUi): ConfirmationType()
}