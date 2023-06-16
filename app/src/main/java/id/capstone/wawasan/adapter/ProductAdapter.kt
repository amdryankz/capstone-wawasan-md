package id.capstone.wawasan.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.capstone.wawasan.databinding.ItemProductBinding
import id.capstone.wawasan.retrofit.PredictionItem

class ProductAdapter(private val list: List<PredictionItem>) : RecyclerView.Adapter<ProductAdapter.DataViewHolder>() {

    class DataViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: PredictionItem) {
            with(binding) {
                tvCode.text = product.code
                tvQty.text = product.qty.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DataViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        holder.bind(list[position])
    }
}