package com.lumisdinos.measureandcount.ui.model

interface Shareable {
    fun getShareableString(
        union: UnionOfChipboardsUI,
        notFoundText: String = " (Not Found)",
        foundText: String = " (Found)",
        realSizeText: String = "real size"
    ): String
}