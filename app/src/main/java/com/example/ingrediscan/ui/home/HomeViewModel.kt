
package com.example.ingrediscan.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ingrediscan.ui.previous_results.ScannedItem
import kotlinx.coroutines.launch
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

class HomeViewModel : ViewModel() {

    data class FoodItem(
        val name: String = " ",
        val calories: String = " "
    )

    private val _caloriesTracked = MutableLiveData<Int>(0)
    val caloriesTracked: LiveData<Int> = _caloriesTracked

    private val _progress = MutableLiveData<Int>(20)
    val progress: LiveData<Int> = _progress

    private val _label = MutableLiveData<String>("Calories Tracked Today")
    val label: LiveData<String> = _label

    private val _foodItems = MutableLiveData<List<FoodItem>>()
    val foodItems: LiveData<List<FoodItem>> = _foodItems

    private var totalCaloriesConsumed = 0

    private val db = FirebaseFirestore.getInstance()


    fun calculateProgress(): Int {
        Log.d("TotalCalories", "${totalCaloriesConsumed}")
        return ((totalCaloriesConsumed / 2000.0) * 100).toInt()
    }

    fun loadTotalCaloriesFromDB() {
        db.collection("loggedMeals")
            .get()
            .addOnSuccessListener { documents ->
                var totalCalories = 0
                for (document in documents) {
                    val caloriesStr = document.getString("Calories") ?: "0 Calories"
                    val calories = caloriesStr.split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
                    totalCalories += calories
                }
                //updates the live data that is present in the home fragment on the app
                //is dynamic so that every time there is a food item logged, the calories value is updated
                _caloriesTracked.value = totalCalories
                totalCaloriesConsumed = totalCalories
            }
            .addOnFailureListener {
                _caloriesTracked.value = 0
            }
    }

    fun loadLatestFoods() {
        db.collection("loggedMeals")
            .get()
            .addOnSuccessListener { documents ->
                val foods = mutableListOf<FoodItem>()
                var foodCounter = 0 // Limit to 4 latest foods
                for (document in documents) {
                    if (foodCounter < 4) {
                        val foodNameUnfiltered = document.id
                        val foodNameFiltered = foodNameUnfiltered.split("+").getOrNull(1) ?: foodNameUnfiltered
                        val caloriesStr = document.getString("Calories") ?: "0 Calories"
                        val calories = caloriesStr.split(" ").firstOrNull()?.toDoubleOrNull()?.toInt() ?: 0
                        val caloriesStrUpdated = calories.toString()
                        Log.d("HomeViewModel", "Calories: " + caloriesStr)
                        Log.d("HomeViewModel", "Calories Int: " + "${calories}")
                        // Add the food item to the list
                        foods.add(FoodItem(foodNameFiltered, caloriesStrUpdated))
                        foodCounter++
                    } else {
                        break
                    }
                }
                _foodItems.value = foods
            }
            .addOnFailureListener {
                Log.e("HomeViewModel", "Error loading foods", it)
                _foodItems.value = emptyList()
            }
    }

}






