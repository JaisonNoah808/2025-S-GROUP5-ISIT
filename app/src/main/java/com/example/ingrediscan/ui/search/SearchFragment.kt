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
import android.widget.LinearLayout
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import com.example.ingrediscan.BackEnd.ApiCalls.FoodApiService
import com.example.ingrediscan.BackEnd.ApiCalls.FoodApiService.RequiredFoodNutrients
import com.example.ingrediscan.BackEnd.ApiCalls.FoodApiService.FoodItem
import com.example.ingrediscan.BackEnd.ApiCalls.FoodApiService.FoodNutrient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.content.Context
import android.view.inputmethod.InputMethodManager

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

    // Creates layout for 1 SearchResult item
    private fun createSearchResultView(item: SearchResult): View {
        // get new search_result.xml file
        val view = layoutInflater.inflate(R.layout.search_result, null)

        // initialize text, image, pie chart views
        val titleText = view.findViewById<TextView>(R.id.foodDrinkTitle)
        val calorieText = view.findViewById<TextView>(R.id.foodDrinkCaloriesLabel)
        val imageView = view.findViewById<ImageView>(R.id.foodDrinkIcon)
        val pieChartView = view.findViewById<PieChart>(R.id.pieChart)

        // assign variables based on SearchResult object params
        titleText.text = item.name
        calorieText.text = "${item.calories} cal"
        item.makePieChart(requireContext(), pieChartView)
        imageView.setImageResource(item.imageID)

        return view
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
                Log.d("SearchFragment", "User searched: $query")
                query?.let { performSearch(it) }

                // Hide the keyboard
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.searchView.windowToken, 0)

                // Optionally clear focus so keyboard stays hidden
                binding.searchView.clearFocus()

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        // Create test items
        val searchResultsList = listOf(
            SearchResult(1, R.drawable.salad, "Caesar Salad", 200, 7, 10, 18, "B-", "A classic Caesar salad offers crisp romaine lettuce tossed with creamy dressing, crunchy croutons, and a sprinkle of parmesan. While it's flavorful and has some fiber and calcium, it can be high in fat and sodium without much protein unless you add chicken or another topping."),
            SearchResult(2, R.drawable.sandwich, "BLT Sandwich", 500, 13, 30, 25, "C+", "A BLT is a satisfying sandwich with crispy bacon, fresh lettuce, and juicy tomato layered between toasted bread and a swipe of mayo. It's tasty and filling but relatively high in fat and sodium, and not especially nutrient-dense."),
            SearchResult(3, R.drawable.spaghetti, "Spaghetti", 500, 10, 60, 4, "B", "Spaghetti with marinara is a simple, comforting dish made mostly of pasta and tomato-based sauce. It’s low in fat and can be a decent source of fiber and vitamins if whole wheat pasta or extra veggies are added, but it's fairly carb-heavy."),
            SearchResult(4, R.drawable.granola, "Granola Bar", 100, 4, 25, 6, "A-", "A granola bar is a convenient, portable snack made from oats, nuts, and sweeteners, offering a quick source of energy and nutrients for active lifestyles."),
            SearchResult(5, R.drawable.banana, "Banana", 105, 2, 27, 1, "A", "A banana is a naturally sweet, potassium-rich fruit that provides quick-digesting carbs, making it ideal for pre- or post-workout energy.")
        )

        // Combine test items into 1 XML container
        val container = binding.searchResultList

        // Build view for each search result
        for (item in searchResultsList) {
            // create view
            val cardView = createSearchResultView(item)

            // increase spacing between each item
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            layoutParams.bottomMargin = (16 * resources.displayMetrics.density).toInt() // 8dp
            cardView.layoutParams = layoutParams

            // add item to screen
            container.addView(cardView)

            // When "Read More" button is clicked:
            val readMoreButton = cardView.findViewById<Button>(R.id.readMoreButton)
            readMoreButton.setOnClickListener {
                // pass SearchResult arguments to display on new page
                val bundle = Bundle().apply {
                    putString("title", item.name)
                    putString("description", item.description)
                    putInt("imageID", item.imageID)
                    putInt("calories", item.calories)
                    putInt("protein", item.protein)
                    putInt("carbs", item.carbs)
                    putInt("fat", item.fat)
                    putString("grade", item.grade)
                }
                findNavController().navigate(R.id.search_detail_result, bundle)

            }

        }
    }

    private fun performSearch(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val nutrients = FoodApiService.fetchFoodInfo(query)
                val foodItem = requiredNutrientsToFoodItem(query, nutrients)
                Log.d("SearchFragment", "Calling displaySearchResult with: $foodItem")

                withContext(Dispatchers.Main) {
                    displaySearchResult(foodItem, 0)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showError(e.message ?: "Unknown error occurred")
                }
            }
        }
    }

    // Converts RequiredNutrients to FoodItem object for proper searching
    fun requiredNutrientsToFoodItem(query: String, nutrients: RequiredFoodNutrients): FoodItem {
        val foodNutrients = listOf(
            FoodNutrient("Energy", extractFirstNumber(nutrients.calories).toDouble(), "cal"),
            FoodNutrient("Protein", extractFirstNumber(nutrients.protein).toDouble(), "g"),
            FoodNutrient("Carbohydrate, by difference", extractFirstNumber(nutrients.carbs).toDouble(), "g"),
            FoodNutrient("Total lipid (fat)", extractFirstNumber(nutrients.fats).toDouble(), "g")
        )

        return FoodItem(
            description = query,
            foodNutrients = foodNutrients,
            ingredients = "No ingredients info available",
            dataType = "FoodItem"
        )
    }

    // Helper function to convert nutritional info from string to numeric
    fun extractFirstNumber(text: String): Int {
        return text.trim().split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
    }


    private fun displaySearchResult(foodItem: FoodApiService.FoodItem, containerIndex: Int) {
        val nutrients = foodItem.foodNutrients.associate { it.nutrientName to it.value }

        val searchResult = SearchResult(
            id = 1,
            imageID = R.drawable.default_image,
            name = foodItem.description,
            calories = nutrients["Energy"]?.toInt() ?: 0,
            protein = nutrients["Protein"]?.toInt() ?: 0,
            carbs = nutrients["Carbohydrate, by difference"]?.toInt() ?: 0,
            fat = nutrients["Total lipid (fat)"]?.toInt() ?: 0,
            grade = calculateGrade(nutrients),
            description = foodItem.ingredients ?: "No ingredients information available"
        )

        // create view
        Log.d("SearchFragment", "Creating view for: ${searchResult.name}")

        val searchResultList = mutableListOf<SearchResult>()
        searchResultList.add(searchResult)
        val cardView = createSearchResultView(searchResult)
        val container = binding.searchResultList
        container.removeAllViews()


        // increase spacing between each item
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.bottomMargin = (16 * resources.displayMetrics.density).toInt() // 8dp
        cardView.layoutParams = layoutParams


        // When "Read More" button is clicked:
        val readMoreButton = cardView.findViewById<Button>(R.id.readMoreButton)
        readMoreButton.setOnClickListener {
            // pass SearchResult arguments to display on new page
            val bundle = Bundle().apply {
                putString("title", searchResult.name)
                putString("description", searchResult.description)
                putInt("imageID", searchResult.imageID)
            }
            findNavController().navigate(R.id.search_detail_result, bundle)
        }

        container.addView(cardView)
        container.visibility = View.VISIBLE
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
        // binding.foodDrinkContainer.visibility = View.GONE
        // binding.foodDrinkContainer2.visibility = View.GONE
        // binding.foodDrinkContainer3.visibility = View.GONE
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