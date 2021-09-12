package com.michaelpohl.loopyplayer2.ui.util

private var lastPercentage: Float = 0F
fun calculateConversionProgress(
    totalFilesToConvert: Int,
    currentIndex: Int,
    currentStep: Int
): Int {

    var index = currentIndex.toFloat()

    // smoothingValue adds a few percent so that it looks nicer to the user
    val smoothingValue = 1F / totalFilesToConvert

    // add 1 in the second half to smoothen it out, since index is always 1 less than size
    if (index > totalFilesToConvert / 2) index += 1

    val percentage =
        ((if (index == 0F) 0F else index) + smoothingValue) / totalFilesToConvert * 100

    val range = percentage - lastPercentage
    val actualPercentage = lastPercentage + (range / 6 * currentStep)

    return when {
        actualPercentage > lastPercentage -> {
            lastPercentage = actualPercentage
            actualPercentage.toInt()
        }
        actualPercentage > 100F -> 100
        else -> lastPercentage.toInt()
    }
}
