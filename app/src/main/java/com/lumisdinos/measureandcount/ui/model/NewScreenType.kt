package com.lumisdinos.measureandcount.ui.model

import android.net.Uri
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class NewScreenType(
    val hasColor: Boolean = false,
    val directionColumn: Int = 0,//0 - no direction, 1 to n - direction column
    val columnNames: List<Int> = emptyList()
)

fun NewScreenType.serialize(): String {
    return Uri.encode(Json.encodeToString(this))
}

fun String.deserializeNewScreenType(): NewScreenType {
    return Json.decodeFromString(Uri.decode(this))
}

