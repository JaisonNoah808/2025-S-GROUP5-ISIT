package com.example.ingrediscan.ui.logMealPage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import android.util.Log
import com.example.ingrediscan.BackEnd.ApiCalls.FoodApiService
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class LogMealBySearchViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()

    // I set these values to be mutable so that it dynamically displays for my fragment_log_meal_by_search.xml file
    private val _protein = MutableLiveData<String>("")
    val protein: LiveData<String> get() = _protein

    private val _fat = MutableLiveData<String>("")
    val fat: LiveData<String> get() = _fat

    private val _carbs = MutableLiveData<String>("")
    val carbs: LiveData<String> get() = _carbs

    private val _calories = MutableLiveData<String>("")
    val calories: LiveData<String> get() = _calories

    private val _sugar = MutableLiveData<String>("")
    val sugar: LiveData<String> get() = _sugar

    private val _fiber = MutableLiveData<String>("")
    val fiber: LiveData<String> get() = _fiber

    private val _cholesterol = MutableLiveData<String>("")
    val cholesterol: LiveData<String> get() = _cholesterol

    private val _calcium = MutableLiveData<String>("")
    val calcium: LiveData<String> get() = _calcium

    private val _iron = MutableLiveData<String>("")
    val iron: LiveData<String> get() = _iron

    private val _vitaminA = MutableLiveData<String>("")
    val vitaminA: LiveData<String> get() = _vitaminA

    private val _vitaminB6 = MutableLiveData<String>("")
    val vitaminB6: LiveData<String> get() = _vitaminB6

    private val _vitaminB12 = MutableLiveData<String>("")
    val vitaminB12: LiveData<String> get() = _vitaminB12

    private val _vitaminC = MutableLiveData<String>("")
    val vitaminC: LiveData<String> get() = _vitaminC

    private val _vitaminD = MutableLiveData<String>("")
    val vitaminD: LiveData<String> get() = _vitaminD

    private val _magnesium = MutableLiveData<String>("")
    val magnesium: LiveData<String> get() = _magnesium

    private val _quickFacts = MutableLiveData<String>("")
    val quickFacts: LiveData<String> get() = _quickFacts

    private val _microNutrients = MutableLiveData<String>("")
    val microNutrients: LiveData<String> get() = _microNutrients

    private val _caloriesLabel = MutableLiveData<String>("")
    val caloriesLabel: LiveData<String> get() = _caloriesLabel

    private val _proteinLabel = MutableLiveData<String>("")
    val proteinLabel: LiveData<String> get() = _proteinLabel

    private val _carbsLabel = MutableLiveData<String>("")
    val carbsLabel: LiveData<String> get() = _carbsLabel

    private val _sugarLabel = MutableLiveData<String>("")
    val sugarLabel: LiveData<String> get() = _sugarLabel

    private val _fiberLabel = MutableLiveData<String>("")
    val fiberLabel: LiveData<String> get() = _fiberLabel

    private val _fatLabel = MutableLiveData<String>("")
    val fatLabel: LiveData<String> get() = _fatLabel

    private val _ironLabel = MutableLiveData<String>("")
    val ironLabel: LiveData<String> get() = _ironLabel

    private val _calciumLabel = MutableLiveData<String>("")
    val calciumLabel: LiveData<String> get() = _calciumLabel

    private val _vitaminALabel = MutableLiveData<String>("")
    val vitaminALabel: LiveData<String> get() = _vitaminALabel

    private val _vitaminB6Label = MutableLiveData<String>("")
    val vitaminB6Label: LiveData<String> get() = _vitaminB6Label

    private val _vitaminB12Label = MutableLiveData<String>("")
    val vitaminB12Label: LiveData<String> get() = _vitaminB12Label

    private val _vitaminCLabel = MutableLiveData<String>("")
    val vitaminCLabel: LiveData<String> get() = _vitaminCLabel

    private val _vitaminDLabel = MutableLiveData<String>("")
    val vitaminDLabel: LiveData<String> get() = _vitaminDLabel

    private val _cholesterolLabel = MutableLiveData<String>("")
    val cholesterolLabel: LiveData<String> get() = _cholesterolLabel

    private val _magnesiumLabel = MutableLiveData<String>("")
    val magnesiumLabel: LiveData<String> get() = _magnesiumLabel
    /*
    1. Parameters: foodName and mealType that is set in the LogMealBySearchFragment.kt file
    2. Purpose:
        a. From the API Call, it sets the mutable string values from above to the API response values
        b. This way, in the fragment_log_meal_by_search.xml file,
           these values are displayed after the user searches for a food / drink
    3. Returns: N/A
    */
    fun updateNutritionData(query: String, mealType: String?) {
        viewModelScope.launch {
            try {
                val apiResponse = FoodApiService.fetchFoodInfo(query)

                apiResponse?.let {
                    _quickFacts.value = it.quickFactsHeader
                    _microNutrients.value = it.microNutrientsHeader
                    _caloriesLabel.value = "Calories: "
                    _cholesterolLabel.value = "Cholesterol: "
                    _calciumLabel.value = "Calcium: "
                    _proteinLabel.value = "Protein: "
                    _carbsLabel.value = "Carbs: "
                    _sugarLabel.value = "Sugar: "
                    _fiberLabel.value = "Fiber: "
                    _fatLabel.value = "Fat: "
                    _ironLabel.value = "Iron: "
                    _vitaminALabel.value = "Vitamin A: "
                    _vitaminB6Label.value = "Vitamin B-6: "
                    _vitaminB12Label.value = "Vitamin B-12: "
                    _vitaminCLabel.value = "Vitamin C: "
                    _vitaminDLabel.value = "Vitamin D: "
                    _magnesiumLabel.value = "Magnesium: "
                    _calories.value    = it.calories
                    _protein.value     = it.protein
                    _carbs.value       = it.carbs
                    _sugar.value       = it.sugar
                    _fiber.value       = it.fiber
                    _fat.value         = it.fats
                    _cholesterol.value = it.cholesterol
                    _iron.value        = it.iron
                    _calcium.value     = it.calcium
                    _vitaminA.value    = it.vitaminA
                    _vitaminB6.value   = it.vitaminB6
                    _vitaminB12.value  = it.vitaminB12
                    _vitaminC.value    = it.vitaminC
                    _vitaminD.value    = it.vitaminD
                    _magnesium.value   = it.magnesium
                    saveToFirestore(query, mealType)
                }
            } catch (e: Exception) {
                Log.e("LogMealViewModel", "Error fetching food data: ${e.message}")
            }
        }
    }

    /*
        1. Parameters: N/A
        2. Purpose: formats the current date & time to the one below as a string
        3. Returns: the formatted date + time as a string
     */
    fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("yyyy.MM.dd-HH:mm:ss", Locale.getDefault())
        val date = Date()
        return dateFormat.format(date)
    }

    /*
        1. Parameters: foodName and mealType that is set in the LogMealBySearchFragment.kt file
        2. Purpose: sets the food name & its nutrition information and the meal type to the Firebase database
        3. Returns: N/A
     */
    fun saveToFirestore(foodName: String, mealType: String?) {

        /*
           In firebase firestore database, duplicates aren't allowed. Thus, the document (like an ID) must be unique
           Thus, I create a unique document by using the adding the current time & food name together
           This way, if the user logs the same food name back to back, it will show up in the database
         */

        val timestamp = getCurrentTime()
        val uniqueFoodName = "$timestamp+$foodName"

        val nutritionMap = mapOf(
            "Calories" to (_calories.value ?: "0 Calories"),
            "Protein" to (_protein.value ?: "0.0 G"),
            "Fat" to (_fat.value ?: "0.0 G"),
            "Carbs" to (_carbs.value ?: "0.0 G"),
            "MealType" to mealType
        )

        db.collection("loggedMeals")
            //creates the unique document (like a key in a hashmap)
            .document(uniqueFoodName)

            //creates the mapping values for the document (like the values in a hashmap)
            .set(nutritionMap)
            .addOnSuccessListener {
                Log.d("Firestore", "Database Success!")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Database Error!", e)
            }

    }
}
