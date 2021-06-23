package barbarabilonic.ferit.activitytracker.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import barbarabilonic.ferit.activitytracker.ActivityInfo
import barbarabilonic.ferit.activitytracker.R

class ActivityAdapter(
    activityInfos:MutableList<ActivityInfo>

) : RecyclerView.Adapter<ActivitiesViewHolder>() {

    private val activityInfos:MutableList<ActivityInfo> = mutableListOf()

    init {
        refreshData(activityInfos)
    }

    fun refreshData(people: List<ActivityInfo>){
        this.activityInfos.clear()
        this.activityInfos.addAll(activityInfos)
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActivitiesViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_item,parent,false)
        return ActivitiesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ActivitiesViewHolder, position: Int) {
        val activity=activityInfos[position]
        holder.bind(activity)

    }

    override fun getItemCount(): Int {
        return activityInfos.size
    }

}