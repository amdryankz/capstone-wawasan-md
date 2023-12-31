package id.capstone.wawasan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.capstone.wawasan.databinding.ItemSupplierBinding
import id.capstone.wawasan.retrofit.DataItem

class SupplierAdapter(private val list: List<DataItem>) : RecyclerView.Adapter<SupplierAdapter.DataViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    class DataViewHolder(private val binding: ItemSupplierBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(supplier: DataItem) {
            with(binding) {
                tvSupplier.text = supplier.nama
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemSupplierBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(list[position])

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(list[holder.adapterPosition])
        }
    }

    interface OnItemClickCallback {
        fun onItemClicked(list: DataItem)
    }
}