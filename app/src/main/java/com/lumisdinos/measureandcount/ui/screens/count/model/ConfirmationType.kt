package com.lumisdinos.measureandcount.ui.screens.count.model

sealed class ConfirmationType {
    data class UncheckChipboardConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data class SelectNotFoundToFindAreaConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data class RemoveNotFoundFromFindAreaConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data class SelectUnknownToFindAreaConfirmed(val chipboard: ChipboardUi): ConfirmationType()
    data object DeletingUnionConfirmed: ConfirmationType()
    data object SharingUnionConfirmed: ConfirmationType()
}