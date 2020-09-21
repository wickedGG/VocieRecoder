package yb.com.vocieRecoder.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_training_content.view.*
import yb.com.vocieRecoder.R
import yb.com.vocieRecoder.base.BaseViewHolder
import yb.com.vocieRecoder.base.BindableAdapter
import yb.com.vocieRecoder.databinding.ItemTrainingContentBinding
import yb.com.vocieRecoder.model.entity.TrainingEntity
import yb.com.vocieRecoder.model.repository.AdapterRepository
import yb.com.vocieRecoder.util.InjectorUtils

class TrainingContentAdapter(val repository: AdapterRepository) : RecyclerView.Adapter<BaseViewHolder<TrainingEntity>>(),    BindableAdapter<ArrayList<TrainingEntity>> {
    var itemList = ArrayList<TrainingEntity>()

    var selectedItem: TrainingEntity? = null

    override fun setData(items: ArrayList<TrainingEntity>?) {
        itemList = items ?: ArrayList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): BaseViewHolder<TrainingEntity> {
        return TrainingContentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_training_content, parent, false))
    }

    override fun getItemCount(): Int = itemList?.size

    override fun getItemId(position: Int): Long {
        return if (itemList.isEmpty()) {
            0
        } else {
            itemList[position].hashCode().toLong()
        }
    }

    fun getItemSelectedPosition(): Int {
        return itemList.indexOf(selectedItem)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<TrainingEntity>, position: Int) = holder.bind(itemList?.get(position))


    inner class TrainingContentViewHolder(val binding: ItemTrainingContentBinding) : BaseViewHolder<TrainingEntity>(binding.root) {

        override fun bind(item: TrainingEntity?) {
            binding.position = adapterPosition
            binding.item = item
            binding.adapterViewModel = InjectorUtils.provideAdapterViewModel(repository)
            with(itemView) {
                if (item == selectedItem) {
                    iv_check.visibility = View.VISIBLE
                    itemView.setBackgroundResource(R.drawable.bg_round_black_3)
                } else {
                    iv_check.visibility = View.GONE
                    itemView.setBackgroundColor(Color.parseColor("#1c1c1c"))
                }
            }
        }
    }
}

