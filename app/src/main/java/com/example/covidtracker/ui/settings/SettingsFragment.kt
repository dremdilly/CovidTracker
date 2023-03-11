package com.example.covidtracker.ui.settings

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.covidtracker.App
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentSettingsBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.util.*


class SettingsFragment : Fragment() {
    private var _binding: FragmentSettingsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private var defaultStatusBarColor: Int = 0

    private lateinit var mActivity: MainActivity
    val choosePhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            chooseImageForPhoto(result)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mActivity = activity as MainActivity

        getImageForPhoto()

        binding.addPhotoBtn.setOnClickListener {
            val strAvatarPrompt = "Выберите фото для профиля"
            val pickPhoto = Intent(Intent.ACTION_PICK)
            pickPhoto.type = "image/*"
            choosePhoto.launch(Intent.createChooser(pickPhoto, strAvatarPrompt))
        }
        GlobalScope.launch {
            App().getUser().child("username").get().addOnSuccessListener {
                binding.usernameString.text = it.value.toString()
            }
            App().getUser().child("email").get().addOnSuccessListener {
                binding.emailString.text = it.value.toString()
            }
            App().getUser().child("location").get().addOnSuccessListener {
                if(it.value.toString() == "null") {
                    binding.locationString.text = "Choose your city"
                } else {
                    binding.locationString.text = it.value.toString()
                }
            }
        }

        binding.usernameRow.setOnClickListener {
            showSettingsDialog()
        }

        binding.locationRow.setOnClickListener {
            showCityDialog()
        }

        binding.changePassBtn.setOnClickListener {
            showPassFragment()
        }

        binding.exitBtn.setOnClickListener {
            activity?.finish()
        }

        return root
    }

    private fun showPassFragment() {
        val action = SettingsFragmentDirections.actionSettingsFragmentToSettingsPassFragment()

        if (findNavController().currentDestination?.id == R.id.settingsFragment) {
            findNavController().navigate(action)
        }
    }

    private fun chooseImageForPhoto(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            var selectedImageUri = result.data?.data

            try {
                val input = context?.contentResolver?.openInputStream(selectedImageUri!!)
                val bitmap = BitmapFactory.decodeStream(input)
                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                var fileName = ""
                selectedImageUri.let { uri ->
                    context?.contentResolver?.query(uri!!, null, null, null, null)
                }?.use { cursor ->
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    cursor.moveToFirst()
                    fileName = cursor.getString(nameIndex)
                }
                val file = File(context?.cacheDir?.absolutePath + "/" + fileName)
                val fos = FileOutputStream(file)
                fos.write(bos.toByteArray())
                fos.flush()
                fos.close()

                // Get file size
                val imageSizeKb = file.length().toInt() / 1024
                if (imageSizeKb >= (1024 * 100)) {
                    Toast.makeText(context, "Big file size", Toast.LENGTH_SHORT).show()
                }

                binding.profileImage.setImageBitmap(bitmap)

                var temp: String? = null
                val imageDrawable = binding.profileImage.drawable as BitmapDrawable?
                if (imageDrawable?.bitmap != null) {
                    val image = imageDrawable?.bitmap
                    val BAOS = ByteArrayOutputStream()
                    image.compress(Bitmap.CompressFormat.JPEG, 100, BAOS)
                    val imageInBytes = BAOS.toByteArray()
                    temp = Base64.encodeToString(imageInBytes, Base64.DEFAULT)
                    GlobalScope.launch {
                        PreferenceManager.updatePhoto(temp)
                    }
                }

            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }


    fun getImageForPhoto() {
        GlobalScope.launch {
            PreferenceManager.preferencesFlow.collect {
//                val bitmap = BitmapFactory.decodeStream(it[PreferenceManager.PreferencesKeys.PHOTO]?.toByteArray()?.inputStream())
                if(it[PreferenceManager.PreferencesKeys.PHOTO] != "") {
                    val imageBytes = Base64.decode(it[PreferenceManager.PreferencesKeys.PHOTO], 0)
                    val image = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                    GlobalScope.launch(Dispatchers.Main) {
                        binding.profileImage.setImageBitmap(image)
                    }
                } else {
                    binding.profileImage.setImageResource(R.drawable.profile_img)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        controlSystemUi(true)
    }

    override fun onStop() {
        super.onStop()
        controlSystemUi(false)
    }

    private fun showSettingsDialog() {
        val settingsDialog = Dialog(requireContext())
        settingsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        settingsDialog.setContentView(R.layout.settings_dialog)
        settingsDialog.setTitle("Edit information")

        val agreeBtn = settingsDialog.findViewById<Button>(R.id.ok_btn)
        val input = settingsDialog.findViewById<EditText>(R.id.edit_input)
        val ask = settingsDialog.findViewById<TextView>(R.id.dialog_ask)
        ask.setText("Enter your name:")
        input.setText(binding.usernameString.text)
        agreeBtn.setOnClickListener {
            binding.apply {
                GlobalScope.launch {
                    App().getUser().child("username").setValue(input.text.toString())
                }
                usernameString.text = input.text
            }
            settingsDialog.dismiss()
        }

        settingsDialog.show()
    }

    private fun showCityDialog() {
        val settingsDialog = Dialog(requireContext())
        settingsDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        settingsDialog.setContentView(R.layout.city_dialog)
        settingsDialog.setTitle("Choose city")

        val agreeBtn = settingsDialog.findViewById<Button>(R.id.ok_btn)
        val locationSpinner = settingsDialog.findViewById<Spinner>(R.id.spinner_kazakhstan_locations)

        val locationAdapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(requireContext(),
        R.array.array_kazakhstan_locations, R.layout.spinner_layout)

        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = locationAdapter

        var selectedLocation: String = ""

        locationSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long,
            ) {
                //Define City Spinner but we will populate the options through the selected state
                selectedLocation =
                    locationSpinner.getSelectedItem().toString() //Obtain the selected State
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })


        agreeBtn.setOnClickListener {
            binding.apply {
                GlobalScope.launch {
                    App().getUser().child("location").setValue(selectedLocation)
                }
                locationString.text = selectedLocation
            }
            settingsDialog.dismiss()
        }

        settingsDialog.show()
    }

    fun controlSystemUi(fullscreen: Boolean) {
        if(fullscreen) {
            mActivity.navView.visibility = View.GONE
            mActivity.topAppBar.myToolbar.visibility = View.GONE
        } else {
            mActivity.navView.visibility = View.VISIBLE
            mActivity.topAppBar.myToolbar.visibility = View.VISIBLE
        }
    }

}