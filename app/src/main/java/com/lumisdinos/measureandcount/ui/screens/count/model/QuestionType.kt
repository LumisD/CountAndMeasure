package com.lumisdinos.measureandcount.ui.screens.count.model

sealed class QuestionType {
    data object Found : QuestionType()
    data object Unknown : QuestionType()
    data object RealSize : QuestionType()
}