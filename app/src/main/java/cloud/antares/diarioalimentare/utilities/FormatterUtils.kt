package cloud.antares.diarioalimentare.utilities

import java.text.DecimalFormat

fun formatDecimals(number: Double): String {
    val dec = DecimalFormat("#.##")
    return dec.format(number)
}