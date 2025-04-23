package com.example.ingrediscan.ui.logMealPage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ingrediscan.databinding.FragmentLogMealBySearchBinding

class LogMealBySearchFragment : Fragment() {

    private lateinit var binding: FragmentLogMealBySearchBinding
    private lateinit var logMealBySearchViewModel: LogMealBySearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLogMealBySearchBinding.inflate(inflater, container, false)
        logMealBySearchViewModel = ViewModelProvider(this)[LogMealBySearchViewModel::class.java]

        // Bind ViewModel to layout
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = logMealBySearchViewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Retrieve the argument passed from the HomeFragment
        var mealType = arguments?.getString("mealType")

        // Set SearchView listener to update ViewModel data
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    logMealBySearchViewModel.updateNutritionData(it, mealType)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}
