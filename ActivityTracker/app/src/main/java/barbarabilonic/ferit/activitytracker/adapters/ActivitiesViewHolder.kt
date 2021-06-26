package barbarabilonic.ferit.activitytracker.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import barbarabilonic.ferit.activitytracker.dataModel.ActivityInfo
import barbarabilonic.ferit.activitytracker.databinding.ActivityItemBinding
import barbarabilonic.ferit.activitytracker.formatDate
import barbarabilonic.ferit.activitytracker.formatDuration
import barbarabilonic.ferit.activitytracker.getIcon

class ActivitiesViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    fun bind(activityInfo: ActivityInfo){
        val itemBinding=ActivityItemBinding.bind(itemView)
        itemBinding.ivItemPicture.setImageResource(getIcon(activityInfo.activityType))
        itemBinding.tvAvgSpeedItem.text= String.format("%.2f",activityInfo.averageSpeed)+"km/h"
        itemBinding.tvItemTime.text= formatDuration(activityInfo.duration)
        itemBinding.tvCaloriesItem.text=activityInfo.caloriesBurned.toString()+"kcal"
        itemBinding.tvDistanceItem.text=String.format("%.2f",activityInfo.distance)+"km"
        itemBinding.tvDateItem.text= formatDate(activityInfo.date)
    }



}