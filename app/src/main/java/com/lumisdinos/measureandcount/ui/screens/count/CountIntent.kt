package com.lumisdinos.measureandcount.ui.screens.count

import com.lumisdinos.measureandcount.ui.model.QuestionType
import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi

sealed interface CountIntent {
    data class SetUnionId(val unionId: Int?) : CountIntent
    data class SizeChanged(val newSizeAsString: String, val dimension: Int) : CountIntent
    data class DifferenceChanged(val newDiffAsString: String, val dimension: Int) : CountIntent
    data class QuantityChanged(val newQuantityAsString: String) : CountIntent
    data class ColorChanged(val colorName: String, val color: Int) : CountIntent
    data object SetFoundChipboard : CountIntent
    data object CreateUnknownChipboard : CountIntent
    data class PressOnItemInList(val chipboard: ChipboardUi) : CountIntent
    data class ShowWhatIs(val questionType: QuestionType) : CountIntent
    //data class AskUncheckChipboard(val chipboard: ChipboardUi) : CountIntent
    data class UncheckChipboardConfirmed(val chipboard: ChipboardUi) : CountIntent
    //data class AskSelectToFindArea(val chipboard: ChipboardUi) : CountIntent
    data class SelectNotFoundToFindAreaConfirmed(val chipboard: ChipboardUi) : CountIntent
    data class RemoveNotFoundFromFindAreaConfirmed(val chipboard: ChipboardUi) : CountIntent
    data object ToggleFindAreaVisibility : CountIntent
    data object SetListDone: CountIntent
}