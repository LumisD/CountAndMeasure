package com.lumisdinos.measureandcount.ui.screens.count.model

import com.lumisdinos.measureandcount.ui.model.QuestionType


sealed class DialogType {
    data class Uncheck(val chipboard: ChipboardUi) : DialogType()
    data class SelectNotFoundToFindArea(val chipboard: ChipboardUi) : DialogType()
    data class RemoveNotFoundFromFindArea(val chipboard: ChipboardUi) : DialogType()
    data class SelectUnknownToFindArea(val chipboard: ChipboardUi) : DialogType()
    data class WhatIs(val questionType: QuestionType) : DialogType()
    data object None : DialogType()
}