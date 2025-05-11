package com.example.btl_android_project.presentation.diary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.btl_android_project.auth.FirebaseAuthDataSource
import com.example.btl_android_project.local.entity.DailyDiarySnapshot
import com.example.btl_android_project.local.entity.DiaryWithAllNutrition
import com.example.btl_android_project.local.enums.MealType
import com.example.btl_android_project.repository.DailyDiaryRepository
import com.example.btl_android_project.repository.DailyDiarySnapshotRepository
import com.example.btl_android_project.repository.DiaryFoodCrossRefRepository
import com.example.btl_android_project.repository.DiaryMealCrossRefRepository
import com.example.btl_android_project.repository.DiaryRecipeCrossRefRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class TodayDiaryViewModel @Inject constructor(
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val firebaseAuthDataSource: FirebaseAuthDataSource,
    private val foodCrossRefRepository: DiaryFoodCrossRefRepository,
    private val recipeCrossRefRepository: DiaryRecipeCrossRefRepository,
    private val mealCrossRefRepository: DiaryMealCrossRefRepository,
    private val dailyDiarySnapshotRepository: DailyDiarySnapshotRepository,
): ViewModel() {
    val currentUserId = firebaseAuthDataSource.getCurrentUserId().toString()
    private var _selectedDate : MutableStateFlow<LocalDate> = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    var year = selectedDate.value.year
    var month = selectedDate.value.monthValue
    var day = selectedDate.value.dayOfMonth

    private var _dailyDiary: MutableStateFlow<DailyDiarySnapshot?> = MutableStateFlow(null)
    val dailyDiary = _dailyDiary.asStateFlow()

    private var _todayDiary: MutableStateFlow<DiaryWithAllNutrition?> = MutableStateFlow(null)
    val todayDiary = _todayDiary.asStateFlow()


    fun setSelectedDate(date: LocalDate) {
        _selectedDate.value = date
        year = date.year
        month = date.monthValue
        day = date.dayOfMonth
    }

    fun getDiaryByDate(
        date: LocalDate,
        onSuccess: (List<MealSection>) -> Unit,
    ){
        viewModelScope.launch {
            val today = LocalDate.now()
            if(date == today) {
                val diaryWithNutrition = dailyDiaryRepository.getDiaryByDate(
                    userId = currentUserId,
                    date = date
                )

                _todayDiary.value = diaryWithNutrition

                if (diaryWithNutrition != null) {
                    val mealSections = transformTodayDiaryDataToMealSections(diaryWithNutrition)
                    onSuccess(mealSections)
                } else {
                    val emptySections = listOf(
                        MealSection("Breakfast", 0, emptyList()),
                        MealSection("Lunch", 0, emptyList()),
                        MealSection("Dinner", 0, emptyList()),
                        MealSection("Snacks", 0, emptyList())
                    )
                    onSuccess(emptySections)
                }
            }
            else{
                val diarySnapshot = dailyDiarySnapshotRepository.getByDate(
                    userId = currentUserId,
                    date = date
                )

                _dailyDiary.value = diarySnapshot

                if (diarySnapshot != null) {
                    val mealSections = transformDiaryDataToMealSections(diarySnapshot)
                    onSuccess(mealSections)
                } else {
                    val emptySections = listOf(
                        MealSection("Breakfast", 0, emptyList()),
                        MealSection("Lunch", 0, emptyList()),
                        MealSection("Dinner", 0, emptyList()),
                        MealSection("Snacks", 0, emptyList())
                    )
                    onSuccess(emptySections)
                }
            }
        }
    }

    suspend fun transformDiaryDataToMealSections(diaryWithNutrition: DailyDiarySnapshot): List<MealSection> {
        val breakfastItems = mutableListOf<MealItem>()
        val lunchItems = mutableListOf<MealItem>()
        val dinnerItems = mutableListOf<MealItem>()
        val snackItems = mutableListOf<MealItem>()


        diaryWithNutrition.foods.forEach { foodSnapshot ->
            val servingText = "${foodSnapshot.servings} serving"
            val item = MealItem(foodSnapshot.foodName, servingText, foodSnapshot.calories.toInt())

            when(foodSnapshot.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
            }
        }

        diaryWithNutrition.meals.forEach { mealSnapshot ->
            val servingText = "${mealSnapshot.servings} serving"
            val item = MealItem(mealSnapshot.mealName, servingText, mealSnapshot.calories.toInt())

            when(mealSnapshot.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
            }
        }

        diaryWithNutrition.recipes.forEach { recipeSnapshot ->
            val servingText = "${recipeSnapshot.servings} serving"
            val item = MealItem(recipeSnapshot.recipeName, servingText, recipeSnapshot.calories.toInt())

            when(recipeSnapshot.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
            }
        }

        // Calculate total calories for each section
        val breakfastCalories = breakfastItems.sumOf { it.calories }
        val lunchCalories = lunchItems.sumOf { it.calories }
        val dinnerCalories = dinnerItems.sumOf { it.calories }
        val snackCalories = snackItems.sumOf { it.calories }

        // Create and return sections
        return listOf(
            MealSection("Breakfast", breakfastCalories, breakfastItems),
            MealSection("Lunch", lunchCalories, lunchItems),
            MealSection("Dinner", dinnerCalories, dinnerItems),
            MealSection("Snacks", snackCalories, snackItems)
        )
    }

    suspend fun transformTodayDiaryDataToMealSections(diaryWithNutrition: DiaryWithAllNutrition): List<MealSection> {
        val breakfastItems = mutableListOf<MealItem>()
        val lunchItems = mutableListOf<MealItem>()
        val dinnerItems = mutableListOf<MealItem>()
        val snackItems = mutableListOf<MealItem>()

        // Process foods
        val foodCrossRefs = diaryWithNutrition.diary.id.let { diaryId ->
            foodCrossRefRepository.getDiaryFoodCrossRefsByDiaryId(diaryId)
        }

        foodCrossRefs?.forEach { crossRef ->
            val food = diaryWithNutrition.foods.find { it.id == crossRef.foodId }
            if (food == null) return@forEach
            val calories = food.calories * crossRef.servings
            val servingText = "${crossRef.servings} serving"
            val item = MealItem(food.name, servingText, calories.toInt())

            when(crossRef.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
            }
        }

        // Process meals
        val mealCrossRefs = diaryWithNutrition.diary.id.let { diaryId ->
            mealCrossRefRepository.getDiaryMealCrossRefsByDiaryId(diaryId)
        }

        mealCrossRefs?.forEach { crossRef ->
            val meal = diaryWithNutrition.meals.find { it.id == crossRef.mealId }
            if (meal == null) return@forEach
            val calories = meal.totalCalories * crossRef.servings
            val servingText = "${crossRef.servings} serving"
            val item = MealItem(meal.name, servingText, calories.toInt())

            when(crossRef.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
            }
        }

        // Process recipes
        val recipeCrossRefs = diaryWithNutrition.diary.id.let { diaryId ->
            recipeCrossRefRepository.getDiaryRecipeCrossRefsByDiaryId(diaryId)
        }

        recipeCrossRefs?.forEach { crossRef ->
            val recipe = diaryWithNutrition.recipes.find { it.id == crossRef.recipeId }
            if (recipe == null) return@forEach
            val calories = recipe.calories * crossRef.servings
            val servingText = "${crossRef.servings} serving"
            val item = MealItem(recipe.name, servingText, calories.toInt())

            when(crossRef.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
            }
        }

        // Calculate total calories for each section
        val breakfastCalories = breakfastItems.sumOf { it.calories }
        val lunchCalories = lunchItems.sumOf { it.calories }
        val dinnerCalories = dinnerItems.sumOf { it.calories }
        val snackCalories = snackItems.sumOf { it.calories }

        // Create and return sections
        return listOf(
            MealSection("Breakfast", breakfastCalories, breakfastItems),
            MealSection("Lunch", lunchCalories, lunchItems),
            MealSection("Dinner", dinnerCalories, dinnerItems),
            MealSection("Snacks", snackCalories, snackItems)
        )
    }

    fun saveSnapshot(){
        viewModelScope.launch {
            val today = LocalDate.now()
            dailyDiarySnapshotRepository.execute(userId = currentUserId, date = today)
        }
    }
}