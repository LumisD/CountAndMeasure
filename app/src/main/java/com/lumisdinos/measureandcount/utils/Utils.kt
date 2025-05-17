package com.lumisdinos.measureandcount.utils

import android.content.Context
import com.lumisdinos.measureandcount.R
import com.lumisdinos.measureandcount.ui.model.Shareable
import com.lumisdinos.measureandcount.ui.model.UnionOfChipboardsUI
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun getCurrentDateTime(): String {
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy, hh:mm a")
    return currentDateTime.format(formatter)
}


fun getDefaultUnionTitle(context: Context): String {
    return String.format(
        context.getString(R.string.chipboard_sheet_list_title),
        getCurrentDateTime()
    )
}
