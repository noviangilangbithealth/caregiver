package com.siloamhospitals.siloamcaregiver.ext.text

fun ellipsizeText(originalText: String, length: Int): String {
    var text = originalText
    if (originalText.length > length) {
        val truncatedText = "${originalText.substring(0, length)}..."
        text = truncatedText
    }
    return text
}
