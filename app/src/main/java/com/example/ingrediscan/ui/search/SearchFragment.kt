package com.example.ingrediscan.ui.search

import com.example.ingrediscan.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.SearchView
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.ingrediscan.databinding.FragmentSearchBinding
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import androidx.core.content.ContextCompat
import com.example.ingrediscan.BackEnd.ApiCalls.FoodApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val searchViewModel =
            ViewModelProvider(this).get(SearchViewModel::class.java)

        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val searchTitleTextView: TextView = binding.searchTitle

        searchTitleTextView.text = "Need meal inspiration?"

        return root
    }

    // Display search bar after start up
    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the SearchView
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            // Call the function when user submits a search
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { performSearch(it) }
                return true
            }

            // Future TODO: filter results live as the user types (set to false right now)
            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Create test items
        val salad = SearchResult(1, "Caesar Salad", 200, 7, 10, 18, "B-", "A classic Caesar salad offers crisp romaine lettuce tossed with creamy dressing, crunchy croutons, and a sprinkle of parmesan. While it's flavorful and has some fiber and calcium, it can be high in fat and sodium without much protein unless you add chicken or another topping.")
        salad.makePieChart(requireContext(), binding.pieChart)

        val blt = SearchResult(2, "BLT Sandwich", 500, 13, 30, 25, "C+", "A BLT is a satisfying sandwich with crispy bacon, fresh lettuce, and juicy tomato layered between toasted bread and a swipe of mayo. It's tasty and filling but relatively high in fat and sodium, and not especially nutrient-dense.")
        blt.makePieChart(requireContext(), binding.pieChart2)

        val spaghetti = SearchResult(3, "Spaghetti", 500, 10, 60, 4, "B", "Spaghetti with marinara is a simple, comforting dish made mostly of pasta and tomato-based sauce. It’s low in fat and can be a decent source of fiber and vitamins if whole wheat pasta or extra veggies are added, but it's fairly carb-heavy.")
        spaghetti.makePieChart(requireContext(), binding.pieChart3)
    }

    private fun performSearch(query: String) {
        Log.d("SearchFragment", "Searching for: $query")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val foodItem = FoodApiService.fetchFoodInfo(query)
                withContext(Dispatchers.Main) {
                    if (foodItem != null) {
                        displaySearchResult(foodItem, 0) // Display in first container
                    } else {
                        showNoResultsMessage()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    private fun displaySearchResult(foodItem: FoodApiService.FoodItem, containerIndex: Int) {
        val nutrients = foodItem.foodNutrients.associate { it.nutrientName to it.value }

        val searchResult = SearchResult(
            id = 1,
            name = foodItem.description,
            calories = nutrients["Energy"]?.toInt() ?: 0,
            protein = nutrients["Protein"]?.toInt() ?: 0,
            carbs = nutrients["Carbohydrate, by difference"]?.toInt() ?: 0,
            fat = nutrients["Total lipid (fat)"]?.toInt() ?: 0,
            grade = calculateGrade(nutrients),
            description = foodItem.ingredients ?: "No ingredients information available"
        )

        when (containerIndex) {
            0 -> {
                binding.foodDrinkContainer.visibility = View.VISIBLE
                binding.foodDrinkTitle.text = searchResult.name
                binding.foodDrinkCaloriesLabel.text = "${searchResult.calories} cal"
                searchResult.makePieChart(requireContext(), binding.pieChart)
            }
            1 -> {
                binding.foodDrinkContainer2.visibility = View.VISIBLE
                binding.foodDrinkTitle2.text = searchResult.name
                binding.foodDrinkCaloriesLabel2.text = "${searchResult.calories} cal"
                searchResult.makePieChart(requireContext(), binding.pieChart2)
            }
            2 -> {
                binding.foodDrinkContainer3.visibility = View.VISIBLE
                binding.foodDrinkTitle3.text = searchResult.name
                binding.foodDrinkCaloriesLabel3.text = "${searchResult.calories} cal"
                searchResult.makePieChart(requireContext(), binding.pieChart3)
            }
        }
    }

    private fun calculateGrade(nutrients: Map<String, Double>): String {
        // Implement your grading logic here
        return when {
            (nutrients["Protein"] ?: 0.0) > 20 -> "A"
            (nutrients["Total lipid (fat)"] ?: 0.0) > 30 -> "C"
            else -> "B"
        }
    }

    private fun showNoResultsMessage() {
        binding.searchTitle.text = "No results found"
        // Hide all containers
        binding.foodDrinkContainer.visibility = View.GONE
        binding.foodDrinkContainer2.visibility = View.GONE
        binding.foodDrinkContainer3.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.searchTitle.text = "Error: $message"
        Log.e("SearchFragment", message)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
