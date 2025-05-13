package com.lumisdinos.measureandcount.ui.screens.count

import com.lumisdinos.measureandcount.ui.screens.addnewitem.model.ConfirmationType
import com.lumisdinos.measureandcount.ui.screens.count.model.QuestionType
import com.lumisdinos.measureandcount.ui.screens.count.model.ChipboardUi

sealed interface CountIntent {
    data class SetUnionId(val unionId: Int?) : CountIntent
    data class SizeChanged(val newSizeAsString: String, val dimension: Int) : CountIntent
    data class RealSizeChanged(val newDiffAsString: String, val dimension: Int) : CountIntent
    data class QuantityChanged(val newQuantityAsString: String) : CountIntent
    data class ColorChanged(val colorName: String, val color: Int) : CountIntent
    data object SetFoundChipboard : CountIntent
    data object CreateUnknownChipboard : CountIntent
    data class PressOnItemInList(val chipboard: ChipboardUi) : CountIntent
    data class ShowWhatIs(val questionType: QuestionType) : CountIntent
    data class ActionConfirmed(val confirmationType: ConfirmationType) : CountIntent
    data object FieldDisabled : CountIntent
    data object ToggleFindAreaVisibility : CountIntent
    data object Back : CountIntent
    data object SetListDone: CountIntent
}