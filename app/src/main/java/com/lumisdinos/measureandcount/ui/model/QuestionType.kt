package com.lumisdinos.measureandcount.ui.model

sealed class QuestionType {
    data object Found : QuestionType()
    data object Unknown : QuestionType()
    data object Difference : QuestionType()
}