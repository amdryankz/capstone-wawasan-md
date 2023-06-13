package id.capstone.wawasan.ui.profile

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.squareup.picasso.Picasso
import id.capstone.wawasan.R
import id.capstone.wawasan.databinding.FragmentProfileBinding
import id.capstone.wawasan.ui.AuthManager
import id.capstone.wawasan.ui.ImageUploader
import id.capstone.wawasan.ui.setting.SettingActivity

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var authManager: AuthManager
    private lateinit var imageUploader: ImageUploader
    private var profileUpdateListener: ProfileUpdateListener? = null
    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                uploadImage(it)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authManager = AuthManager(FirebaseAuth.getInstance())
        imageUploader = ImageUploader()

        val user = authManager.getCurrentUser()

        if (user != null) {
            user.photoUrl?.let { photoUrl ->
                Picasso.get().load(photoUrl).into(binding.ivProfile)
            }
        }

        binding.etName.setText(user?.displayName)

        binding.btnProfile.setOnClickListener {
            showImagePickerDialog()
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        profileUpdateListener = activity as? ProfileUpdateListener

        binding.btnUpdate.setOnClickListener {
            val image = when {
                ::imageUri.isInitialized -> imageUri
                else -> user?.photoUrl
            }

            val name = binding.etName.text.toString().trim()

            if (name.isEmpty()) {
                binding.etName.error = "Name is required"
                binding.etName.requestFocus()
                return@setOnClickListener
            }

            val profileUpdateRequest = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build()

            user?.updateProfile(profileUpdateRequest)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity, "Profile updated", Toast.LENGTH_SHORT).show()
                    profileUpdateListener?.onProfileUpdated(name, image)
                } else {
                    Toast.makeText(activity, "${task.exception?.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun uploadImage(imageUri: Uri) {
        imageUploader.uploadImage(imageUri) { downloadUri ->
            downloadUri?.let {
                this.imageUri = it
                binding.ivProfile.setImageURI(imageUri)
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Choose Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> intentCamera()
                    1 -> getContent.launch("image/*")
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it)?.let {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    interface ProfileUpdateListener {
        fun onProfileUpdated(name: String?, photoUrl: Uri?)
    }

    override fun onPause() {
        super.onPause()
        activity?.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_CAMERA = 100
    }
}