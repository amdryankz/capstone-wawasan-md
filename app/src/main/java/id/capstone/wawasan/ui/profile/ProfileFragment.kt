package id.capstone.wawasan.ui.profile

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import id.capstone.wawasan.databinding.FragmentProfileBinding
import id.capstone.wawasan.ui.setting.SettingActivity
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var imageUri: Uri
    private lateinit var firebaseAuth: FirebaseAuth
    private var profileUpdateListener: ProfileUpdateListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseAuth = FirebaseAuth.getInstance()

        val user = firebaseAuth.currentUser

        if (user != null) {
            if (user.photoUrl != null) {
                Picasso.get().load(user.photoUrl).into(binding.ivProfile)
            }
        }

        binding.etName.setText(user?.displayName)

        binding.btnProfile.setOnClickListener {
            showImagePickerDialog()
        }

        binding.backBtn.setOnClickListener {
            val intent = Intent(requireContext(), SettingActivity::class.java)
            startActivity(intent)
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

            UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .setPhotoUri(image)
                .build().also {
                    user?.updateProfile(it)?.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(activity, "Profile Updated", Toast.LENGTH_SHORT).show()
                            profileUpdateListener?.onProfileUpdated(name, image)
                        } else {
                            Toast.makeText(activity, "${it.exception?.message}", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }
        }
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun intentCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            activity?.packageManager?.let {
                intent.resolveActivity(it).also {
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            val imgBitmap = data?.extras?.get("data") as Bitmap
            uploadImage(imgBitmap)
        } else if (requestCode == REQUEST_GALLERY && resultCode == RESULT_OK) {
            val selectedImage = data?.data
            selectedImage?.let {
                val imgBitmap =
                    MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
                uploadImage(imgBitmap)
            }
        }
    }

    private fun uploadImage(imgBitmap: Bitmap) {
        val baos = ByteArrayOutputStream()
        val ref =
            FirebaseStorage.getInstance().reference.child("img/${FirebaseAuth.getInstance().currentUser?.uid}")

        imgBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val image = baos.toByteArray()

        ref.putBytes(image)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    ref.downloadUrl.addOnCompleteListener {
                        it.result?.let {
                            imageUri = it
                            binding.ivProfile.setImageBitmap(imgBitmap)
                        }
                    }
                }
            }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Camera", "Gallery")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> intentCamera()
                    1 -> choosePictureFromGallery()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    @SuppressLint("IntentReset")
    private fun choosePictureFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_GALLERY)
    }

    interface ProfileUpdateListener {
        fun onProfileUpdated(name: String?, photoUrl: Uri?)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val REQUEST_CAMERA = 100
        const val REQUEST_GALLERY = 101
    }
}