package barbarabilonic.ferit.activitytracker

interface OnActivityButtonsClickListener {
    fun onStartActivityButtonClicked()
    fun onSortSpinnerItemClicked(sort:Int,filter:Int)
    fun onActivitiesSpinnerItemClicked(filter: Int,sort: Int)

}