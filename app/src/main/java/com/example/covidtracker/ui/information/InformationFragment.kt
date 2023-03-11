package com.example.covidtracker.ui.information

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListAdapter
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.example.covidtracker.App
import com.example.covidtracker.R
import com.example.covidtracker.databinding.FragmentInformationBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class InformationFragment : Fragment() {
    private var _binding: FragmentInformationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        _binding = FragmentInformationBinding.inflate(inflater, container, false)
        val root: View = binding.root


        GlobalScope.launch {
            App().getUser().child("location").get().addOnSuccessListener {
                val location = it.value.toString()
                showMeds(location)
            }
        }

        return root
    }

    private fun showMeds(location: String) {
        val listView = binding.medList

        when(location) {
            "г. Нур-Султан" -> {
                val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.array_nursultan_medics, R.layout.spinner_layout)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)
            }
            "г. Алматы" -> {
                val listItem = resources.getStringArray(R.array.array_almaty_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)
            }
            "Акмолинская обл." -> {
                val listItem = resources.getStringArray(R.array.array_akmolinsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Актюбинская обл." -> {
                val listItem = resources.getStringArray(R.array.array_aktubinsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Алматинская обл." -> {
                val listItem = resources.getStringArray(R.array.array_almatinsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Атырауская обл." -> {
                val listItem = resources.getStringArray(R.array.array_atyrausk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Жамбылская обл." -> {
                val listItem = resources.getStringArray(R.array.array_zhambylsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Карагандинская обл." -> {
                val listItem = resources.getStringArray(R.array.array_karagandinsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Костанайская обл." -> {
                val listItem = resources.getStringArray(R.array.array_kostanaysk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Кызылординская обл." -> {
                val listItem = resources.getStringArray(R.array.array_kyzylordinsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Мангыстауская обл." -> {
                val listItem = resources.getStringArray(R.array.array_mangystausk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Туркестанская обл." -> {
                val listItem = resources.getStringArray(R.array.array_turkestansk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Павлодарская обл." -> {
                val listItem = resources.getStringArray(R.array.array_pavlodarsk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Северо-Казахстанская обл." -> {
                val listItem = resources.getStringArray(R.array.array_severokazakhstansk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "Восточно-Казахстанская обл." -> {
                val listItem = resources.getStringArray(R.array.array_vostochnokazakhstansk_obl_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }
            "г. Шымкент" -> {
                val listItem = resources.getStringArray(R.array.array_shymkent_medics)
                val adapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, android.R.id.text1, listItem)
                listView.adapter = adapter
                listView.layoutParams.height = getListViewHeightBasedOnChildren(listView)

            }

        }
    }

    fun getListViewHeightBasedOnChildren(listView: ListView): Int {
        val listAdapter: ListAdapter = listView.getAdapter()
        var totalHeight = 0
        val size: Int = listAdapter.getCount()
        for (i in 0 until size) {
            val listItem: View = listAdapter.getView(i, null, listView)
            listItem.measure(0, 0)
            totalHeight += listItem.measuredHeight
        }
        totalHeight = totalHeight + listView.getDividerHeight() * (listAdapter.getCount() - 1) + 500
        return totalHeight
        return 0
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}