package id.capstone.wawasan.ui.home

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import id.capstone.wawasan.R
import id.capstone.wawasan.adapter.SupplierAdapter
import id.capstone.wawasan.databinding.ActivityHomeBinding
import id.capstone.wawasan.retrofit.DataItem
import id.capstone.wawasan.retrofit.HostConfiguration
import id.capstone.wawasan.ui.AuthManager
import id.capstone.wawasan.ui.profile.ProfileFragment
import id.capstone.wawasan.ui.setting.SettingActivity
import id.capstone.wawasan.ui.configurehost.ConfigureHostViewModel
import id.capstone.wawasan.ui.supplierproduct.SupplierProductActivity
import java.util.Locale

class HomeActivity : AppCompatActivity(), ProfileFragment.ProfileUpdateListener {

    private lateinit var binding: ActivityHomeBinding
    private lateinit var authManager: AuthManager
    private lateinit var database: FirebaseDatabase
    private val homeViewModel by viewModels<HomeViewModel>()
    private val configureHostViewModel by viewModels<ConfigureHostViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authManager = AuthManager(FirebaseAuth.getInstance())
        val user = authManager.getCurrentUser()

        if (user != null) {
            binding.textName.text = user.displayName

            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivProfile)
            }
        }

        database = FirebaseDatabase.getInstance()
        val uid = user?.uid
        val hostConfigRef = database.getReference("host_config/$uid")

        hostConfigRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val hostConfig = dataSnapshot.getValue(HostConfiguration::class.java)

                if (hostConfig != null) {
                    val username = hostConfig.username
                    val password = hostConfig.password
                    val host = hostConfig.host
                    val port = hostConfig.port
                    val database = hostConfig.database

                    configureHostViewModel.connectToDatabase(
                        username,
                        password,
                        host,
                        port,
                        database
                    )
                    getListData(null)
                    searchData()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan saat membaca konfigurasi host dari Firebase
            }
        })

        homeViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        binding.icSetting.setOnClickListener {
            val intent = Intent(this, SettingActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rv.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this, layoutManager.orientation)
        binding.rv.addItemDecoration(itemDecoration)
    }

    private fun getListData(filter: String?) {
        homeViewModel.data.observe(this) { dataResponse ->
            if (dataResponse != null) {
                val dataItems = dataResponse.data
                val filteredDataItems =
                    filterDataItems(dataItems, filter)
                val adapter = SupplierAdapter(filteredDataItems)
                binding.rv.adapter = adapter

                adapter.setOnItemClickCallback(object : SupplierAdapter.OnItemClickCallback {
                    override fun onItemClicked(list: DataItem) {
                        val intent = Intent(this@HomeActivity, SupplierProductActivity::class.java)
                        intent.putExtra(SupplierProductActivity.KEY_USERS, list)
                        startActivity(intent)
                    }
                })
            }
        }
    }

    private fun searchData() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                getListData(newText)
                return true
            }

        })
    }

    private fun filterDataItems(dataItems: List<DataItem>, filter: String?): List<DataItem> {
        if (filter.isNullOrEmpty()) {
            return dataItems
        }

        val filteredList = mutableListOf<DataItem>()
        val query = filter.lowercase(Locale.getDefault())

        for (item in dataItems) {
            if (item.nama.lowercase(Locale.getDefault()).contains(query)) {
                filteredList.add(item)
            }
        }

        return filteredList
    }


    override fun onProfileUpdated(name: String?, photoUrl: Uri?) {
        refreshProfile()
    }

    private fun refreshProfile() {
        val user = authManager.getCurrentUser()

        if (user != null) {
            binding.textName.text = user.displayName

            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivProfile)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        refreshProfile()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finishAffinity()"))
    override fun onBackPressed() {
        finishAffinity()
    }
}