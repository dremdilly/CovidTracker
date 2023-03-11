package com.example.covidtracker.ui.settings

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentSettingsBinding
import com.example.covidtracker.databinding.FragmentSettingsPassBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream

class SettingsPassFragment : Fragment() {
    private var _binding: FragmentSettingsPassBinding? = null

    private val binding get() = _binding!!

    private lateinit var mActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsPassBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mActivity = activity as MainActivity



        return root
    }

    override fun onResume() {
        super.onResume()
        controlSystemUi(true)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}