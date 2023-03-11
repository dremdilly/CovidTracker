package com.example.covidtracker.ui.dashboard

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.covidtracker.App
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentDashboardBinding
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.messages.*
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.nio.charset.Charset

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null


    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this).get(DashboardViewModel::class.java)

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val btBtn = binding.btBtn
        dashboardViewModel.btBtnText.observe(viewLifecycleOwner) {
            btBtn.text = it
        }
        dashboardViewModel.btBtnColor.observe(viewLifecycleOwner) {
            btBtn.setBackgroundColor(it)
        }

        binding.btBtn.setOnClickListener {
            if (binding.btBtn.text == "Enable Bluetooth") {
                (activity as MainActivity).publish()
                (activity as MainActivity).subscribe()
//                binding.btBtn.text = "Disable Bluetooth"
//                binding.btBtn.setBackgroundColor(resources.getColor(R.color.bluetooth_disable_color))
                GlobalScope.launch {
                    PreferenceManager.updateBluetoothMode(false)
                }
                dashboardViewModel.updateBtBtnText("Disable Bluetooth")
                dashboardViewModel.updateBtBtnColor(resources.getColor(R.color.bluetooth_disable_color))
            } else if(binding.btBtn.text == "Disable Bluetooth"){
                (activity as MainActivity).unpublish()
                (activity as MainActivity).unsubscribe()
//                binding.btBtn.text = "Enable Bluetooth"
//                binding.btBtn.setBackgroundColor(resources.getColor(R.color.bluetooth_enable_color))
                GlobalScope.launch {
                    PreferenceManager.updateBluetoothMode(true)
                }
                dashboardViewModel.updateBtBtnText("Enable Bluetooth")
                dashboardViewModel.updateBtBtnColor(resources.getColor(R.color.bluetooth_enable_color))
            }
        }

//        val nearbyDevicesArrayList: List<String> = ArrayList()
//        mNearbyDevicesArrayAdapter = ArrayAdapter<String>(requireContext(),
//            android.R.layout.simple_list_item_1,
//            nearbyDevicesArrayList)
//        val nearbyDevicesListView = binding.nearbyDevicesListView
//        nearbyDevicesListView.adapter = mNearbyDevicesArrayAdapter

        return root
    }
//
//    private suspend fun getStatus(): DatabaseReference {
//        return App.database.getReference("users")
//            .child(PreferenceManager.getUserId())
//            .child("status")
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}