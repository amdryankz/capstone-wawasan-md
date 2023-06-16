package id.capstone.wawasan.ui.supplierproduct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import id.capstone.wawasan.R
import id.capstone.wawasan.adapter.ProductAdapter
import id.capstone.wawasan.databinding.ActivitySupplierProductBinding
import id.capstone.wawasan.retrofit.DataItem
import id.capstone.wawasan.ui.home.HomeActivity
import id.capstone.wawasan.ui.home.HomeViewModel

class SupplierProductActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupplierProductBinding
    private val supplierProductViewModel by viewModels<SupplierProductViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupplierProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supplier = intent.getParcelableExtra<DataItem>(KEY_USERS) as DataItem
        supplierProductViewModel.supplierName = supplier.nama

        binding.backBtn.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        supplierProductViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rv.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rv.addItemDecoration(itemDecoration)

        getListProduct()
    }

    private fun getListProduct() {
        supplierProductViewModel.data.observe(this) {
            if (it != null) {
                val productItems = it.prediction
                val adapter = ProductAdapter(productItems)
                binding.rv.adapter = adapter
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val KEY_USERS = "key_users"
    }
}