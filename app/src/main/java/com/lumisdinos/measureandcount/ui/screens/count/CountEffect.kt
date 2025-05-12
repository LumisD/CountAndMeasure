package com.lumisdinos.measureandcount.ui.screens.count

import com.lumisdinos.measureandcount.ui.screens.count.model.QuestionType
import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi

sealed class CountEffect {
    data class ShowUncheckConfirmationDialog(val chipboard: ChipboardUi) : CountEffect()
    data class ShowSelectNotFoundToFindAreaConfirmationDialog(val chipboard: ChipboardUi) : CountEffect()
    data class ShowRemoveNotFoundFromFindAreaConfirmationDialog(val chipboard: ChipboardUi) : CountEffect()
    data class ShowSelectUnknownToFindAreaConfirmationDialog(val chipboard: ChipboardUi) : CountEffect()
    data class ShowNotExceedingTargetQuantityDialog(val targetQuantity: Int, val enteredQuantity: Int) : CountEffect()
    data class ShowWhatIsDialog(val questionType: QuestionType) : CountEffect()
    data class ShowSnackbar(val message: String) : CountEffect()
    data object ShowFieldDisabled : CountEffect()
    data object FlashFindItemArea : CountEffect()
}