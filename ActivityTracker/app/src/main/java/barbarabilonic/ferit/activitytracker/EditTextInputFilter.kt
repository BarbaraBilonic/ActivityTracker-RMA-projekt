package barbarabilonic.ferit.activitytracker

import android.text.InputFilter
import android.text.Spanned


class EditTextInputFilter() : InputFilter {
    private var intMin: Int = 0
    private var intMax: Int = 0
    constructor(minValue: String, maxValue: String) : this() {
        this.intMin = Integer.parseInt(minValue)
        this.intMax = Integer.parseInt(maxValue)
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

            val input = Integer.parseInt(dest.toString() + source.toString())
            if (isInRange(intMin, intMax, input)) {

                return null
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
        return ""
    }
    private fun isInRange(a: Int, b: Int, c: Int): Boolean {
        return if (b > a) c in a..b else c in b..a
    }
}