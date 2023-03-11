package com.example.covidtracker.ui.status

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.covidtracker.App
import com.example.covidtracker.MainActivity
import com.example.covidtracker.PreferenceManager
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentHomeBinding
import com.example.covidtracker.databinding.FragmentStatusBinding
import com.example.covidtracker.models.StatusModel
import com.example.covidtracker.ui.home.HomeFragment
import com.example.covidtracker.ui.home.HomeViewModel
import com.google.android.material.datepicker.MaterialStyledDatePickerDialog
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class StatusFragment : DialogFragment() {

    private var _binding: FragmentStatusBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var mActivity: MainActivity
    private lateinit var homeViewModel: HomeViewModel

    override fun onStart() {
        super.onStart()

        dialog?.setCanceledOnTouchOutside(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentStatusBinding.inflate(inflater, container, false)
        val root: View = binding.root

        mActivity = activity as MainActivity
        if (getDialog() != null && getDialog()?.getWindow() != null) {
            getDialog()?.getWindow()?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
            getDialog()?.getWindow()?.requestFeature(Window.FEATURE_NO_TITLE);
        }
        binding.vaccinatedToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.vaccinated_btn -> {
                        binding.pcrLabel.visibility = View.GONE
                        binding.pcrToggleGroup.visibility = View.GONE
                        binding.resultToggleGroup.visibility = View.GONE
                        binding.resultLabel.visibility = View.GONE
                        binding.chosenDate.text = "__.__.____"
                        binding.saveBtn.isEnabled = false
                        binding.addDateHeader.visibility = View.VISIBLE
                        binding.calendarViewBtn.visibility = View.VISIBLE
                        binding.pcrToggleGroup.clearChecked()
                        binding.resultToggleGroup.clearChecked()

                    }
                    R.id.not_vaccinated_btn -> {
                        binding.saveBtn.isEnabled = false
                        binding.pcrLabel.visibility = View.VISIBLE
                        binding.pcrToggleGroup.visibility = View.VISIBLE
                        binding.addDateHeader.visibility = View.GONE
                        binding.calendarViewBtn.visibility = View.GONE
                    }
                }
            }
        }

        binding.pcrToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.pcr_btn -> {
                        binding.resultLabel.visibility = View.VISIBLE
                        binding.resultToggleGroup.visibility = View.VISIBLE
                        binding.saveBtn.isEnabled = false
                    }
                    R.id.no_pcr_btn -> {
                        binding.resultLabel.visibility = View.GONE
                        binding.resultToggleGroup.visibility = View.GONE
                        binding.addDateHeader.visibility = View.GONE
                        binding.calendarViewBtn.visibility = View.GONE
                        binding.saveBtn.isEnabled = true
                        binding.resultToggleGroup.clearChecked()
                    }
                }
            }
        }

        binding.resultToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.positive_btn -> {
//                        binding.pcrLabel.visibility = View.GONE
//                        binding.pcrToggleGroup.visibility = View.GONE
                        binding.addDateHeader.visibility = View.VISIBLE
                        binding.calendarViewBtn.visibility = View.VISIBLE
                        binding.saveBtn.isEnabled = false
                        binding.chosenDate.text = "__.__.____"
                    }
                    R.id.negative_btn -> {
//                        binding.pcrLabel.visibility = View.VISIBLE
//                        binding.pcrToggleGroup.visibility = View.VISIBLE
                        binding.addDateHeader.visibility = View.VISIBLE
                        binding.calendarViewBtn.visibility = View.VISIBLE
                        binding.saveBtn.isEnabled = false
                        binding.chosenDate.text = "__.__.____"
                    }
                }
            }
        }

        binding.calendarViewBtn.setOnClickListener {
            openDatePicker()
        }

        binding.saveBtn.setOnClickListener {
            saveStatus()
            dismiss()
        }

        binding.cancelBtn.setOnClickListener {
            dismiss()
        }

        return root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun saveStatus() {
//        GlobalScope.launch {
        if (binding.vaccinatedToggleGroup.checkedButtonId == R.id.vaccinated_btn) {
            val updatedText = DateFormat.format("HH:mm", System.currentTimeMillis())
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val dateInTime = sdf.parse(binding.chosenDate.text.toString()).time
            var dateText = DateFormat.format("MMM. yyyy", dateInTime)
            if(dateText.take(3) == "May") {
                dateText = "May ${dateText.takeLast(4)}"
            }
            val statusModel = StatusModel(
                "vaccinated",
                "On $dateText",
                "Your status is secure",
                "Updated: Today, $updatedText"
            )
            (parentFragment as HomeFragment).homeViewModel.setStatus(statusModel)
            (parentFragment as HomeFragment).homeViewModel.updateStatus("vaccinated")
        } else if (binding.pcrToggleGroup.checkedButtonId == R.id.no_pcr_btn) {
            val updatedText = DateFormat.format("HH:mm", System.currentTimeMillis())

            val statusModel = StatusModel(
                "blue",
                binding.chosenDate.text.toString(),
                "You don't have PCR result",
                "Updated: Today, $updatedText"
            )
            (parentFragment as HomeFragment).homeViewModel.setStatus(statusModel)
            (parentFragment as HomeFragment).homeViewModel.updateStatus("blue")
        } else if (binding.resultToggleGroup.checkedButtonId == R.id.positive_btn) {
            val updatedText = DateFormat.format("HH:mm", System.currentTimeMillis())
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val dateInTime = sdf.parse(binding.chosenDate.text.toString()).time
            var dateText = DateFormat.format("MMM. yyyy", dateInTime)
            if(dateText.take(3) == "May") {
                dateText = "May ${dateText.takeLast(4)}"
            }
            val statusModel = StatusModel(
                "red",
                "On $dateText",
                "Infected status expires in 2 weeks",
                "Updated: Today, $updatedText"
            )
            (parentFragment as HomeFragment).homeViewModel.setStatus(statusModel)
            (parentFragment as HomeFragment).homeViewModel.updateStatus("red")
        } else {
            val updatedText = DateFormat.format("HH:mm", System.currentTimeMillis())
            val sdf = SimpleDateFormat("dd.MM.yyyy")
            val dateInTime = sdf.parse(binding.chosenDate.text.toString()).time
            var dateText = DateFormat.format("MMM. yyyy", dateInTime)
            if(dateText.take(3) == "May") {
                dateText = "May ${dateText.takeLast(4)}"
            }

            val statusModel = StatusModel(
                "green",
                "On $dateText",
                "Your status is secure",
                "Updated: Today, $updatedText"
            )
            (parentFragment as HomeFragment).homeViewModel.setStatus(statusModel)
            (parentFragment as HomeFragment).homeViewModel.updateStatus("green")
        }
//        }
    }

    @SuppressLint("RestrictedApi")
    private fun openDatePicker() {
        val year: Int
        val month: Int
        val day: Int
        val currentChosenDate = binding.chosenDate.text.toString()
        val myCalendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale("ru", "RU"))

        if (binding.chosenDate?.text != "__.__.____") {
            val theDate = sdf.parse(currentChosenDate)

            year = DateFormat.format("yyyy", theDate).toString().toInt()
            month = DateFormat.format("MM", theDate).toString().toInt() - 1
            day = DateFormat.format("dd", theDate).toString().toInt()
        } else {
            year = myCalendar.get(Calendar.YEAR)
            month = myCalendar.get(Calendar.MONTH)
            day = myCalendar.get(Calendar.DAY_OF_MONTH)
        }
        val dpd = MaterialStyledDatePickerDialog(requireContext(),
            R.style.MyDatePickerDialogTheme,
            { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                val selectedDate = "$selectedDayOfMonth.${selectedMonth + 1}.$selectedYear"
                val chosenDate: TextView = binding.chosenDate

                val theDate = sdf.parse(selectedDate)
                theDate?.let {
                    chosenDate.text = sdf.format(theDate)
                    binding.saveBtn.isEnabled = true
                }
            },
            year,
            month,
            day)
        dpd.setButton(DatePickerDialog.BUTTON_POSITIVE, "Choose", dpd)
        dpd.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dpd)
        dpd.datePicker.maxDate = System.currentTimeMillis()

        dpd.show()
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