package barbarabilonic.ferit.activitytracker

import android.text.InputFilter
import android.text.Spanned

class EditTextInputFilterDouble() : InputFilter{
    private var minValue: Double=0.0
    private var maxValue: Double=0.0
    constructor(minValue: String, maxValue: String) : this() {
        this.minValue = minValue.toDouble()
        this.maxValue = maxValue.toDouble()
    }
    override fun filter(
        source: CharSequence,
        start: Int,
        end: Int,
        dest: Spanned,
        dStart: Int,
        dEnd: Int
    ): CharSequence? {
        try {
            // Remove the string out of destination that is to be replaced
            var newVal = dest.toString().substring(0, dStart) + dest.toString()
                .substring(dEnd, dest.toString().length)
            newVal = newVal.substring(0, dStart) + source.toString() + newVal.substring(
                dStart,
                newVal.length
            )
            val input = newVal.toDouble()
            if (isInRange(minValue, maxValue, input)) {
                return null
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }
    private fun isInRange(a: Double, b: Double, c: Double): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}


