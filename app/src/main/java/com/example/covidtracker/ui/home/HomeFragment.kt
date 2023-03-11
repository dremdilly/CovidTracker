package com.example.covidtracker.ui.home

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.covidtracker.App
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentHomeBinding
import com.example.covidtracker.models.StatusModel
import com.example.covidtracker.ui.status.StatusFragment
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mActivity: MainActivity
    lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mActivity = activity as MainActivity

        getImageForPhoto()

        binding.statusEditBtn.setOnClickListener {
            var dialog = StatusFragment()

            dialog.show(childFragmentManager, "StatusFragmentDialog")
        }

        homeViewModel.userNameText.observe(viewLifecycleOwner) {
            binding.userNameText.text = it
        }
        homeViewModel.emailText.observe(viewLifecycleOwner) {
            binding.emailText.text = it
        }
        homeViewModel.locationText.observe(viewLifecycleOwner) {
            binding.locationText.text = it
        }



        homeViewModel.statusInfoText.observe(viewLifecycleOwner) {
            binding.statusInfoText.text = it
        }
        homeViewModel.statusDateText.observe(viewLifecycleOwner) {
            binding.statusResultDateText.text = it
        }
        homeViewModel.statusUpdatedText.observe(viewLifecycleOwner) {
            binding.statusUpdateDateText.text = it
        }

        homeViewModel.statusText.observe(viewLifecycleOwner) {
            if(it == "vaccinated") {
                binding.statusText.text = "Vaccinated"
                binding.statusContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_status_color)
            }  else if(it == "green") {
                binding.statusText.text = "Safe"
                binding.statusContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.green_status_color)
            } else if (it == "red") {
                binding.statusText.text = "Infected"
                binding.statusContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.red_status_color)
            } else if(it == "yellow") {
                binding.statusText.text = "High Risk"
                binding.statusContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.yellow_status_color)
            } else {
                binding.statusText.text = "Low Risk"
                binding.statusContainer.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.blue_status_color)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        GlobalScope.launch(Dispatchers.Main) {
            App().getUser().child("username").get().addOnSuccessListener {
                if(it.value.toString() != "null" || it.value != null) {
                    homeViewModel.updateUsername(it.value.toString())
                }
            }
            App().getUser().child("location").get().addOnSuccessListener {
                if (it.value.toString() != "null" || it.value != null) {
                    homeViewModel.updateLocation(it.value.toString())
                }
            }

        }
    }

    override fun onResume() {
        super.onResume()

        GlobalScope.launch(Dispatchers.Main) {
            App().getUser().child("status").get().addOnSuccessListener {
                if (it.value.toString() != "null" || it.value != null) {
                    val color = it.child("color").value.toString()
                    if (color == "yellow" || color == "red") {
                        val date = it.child("date").value.toString()
                        val info = it.child("info").value.toString()
                        val updated = it.child("updated").value.toString()
                        val status = StatusModel(color, date, info, updated)
                        homeViewModel.setStatus(status)
                    }
                }
            }
        }
    }


    private fun showStatusDialog() {
        val statusDialog = Dialog(requireContext())
        statusDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        statusDialog.setContentView(R.layout.status_dialog)
        statusDialog.setTitle("Выберите статус")

        statusDialog.findViewById<Button>(R.id.green_btn).setOnClickListener {
            GlobalScope.launch {
                getStatus().setValue("Green Status")
                updateStatus()
            }
            statusDialog.dismiss()
        }

        statusDialog.findViewById<Button>(R.id.red_btn).setOnClickListener {
            GlobalScope.launch {
                getStatus().setValue("Red Status")
                updateStatus()
            }
            statusDialog.dismiss()
        }


        statusDialog.show()
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
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private suspend fun updateStatus() {
        getStatus().get().addOnSuccessListener {
            homeViewModel.updateStatus(it.value.toString())
        }
    }

    private suspend fun getStatus(): DatabaseReference {
        return App.database.getReference("users")
            .child(PreferenceManager.getUserId())
            .child("status")
    }
}