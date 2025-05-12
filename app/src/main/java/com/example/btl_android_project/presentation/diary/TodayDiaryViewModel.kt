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
import com.example.btl_android_project.repository.LogWaterRepository
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
    private val waterLogRepository: LogWaterRepository,
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

    private var _totalCalories: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalCalories = _totalCalories.asStateFlow()

    private var _totalFoodCalories: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalFoodCalories = _totalFoodCalories.asStateFlow()

    private var _totalExerciseCalories: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalExerciseCalories = _totalExerciseCalories.asStateFlow()

    private var _totalRemainingCalories: MutableStateFlow<Int> = MutableStateFlow(0)
    val totalRemainingCalories = _totalRemainingCalories.asStateFlow()

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
                dailyDiaryRepository.recalculateWhenChanging(currentUserId)

                val diaryWithNutrition = dailyDiaryRepository.getDiaryByDate(
                    userId = currentUserId,
                    date = date
                )

                _todayDiary.value = diaryWithNutrition

                if (diaryWithNutrition != null) {
                    _totalCalories.value = diaryWithNutrition.diary.caloriesGoal.toInt()
                    _totalFoodCalories.value = diaryWithNutrition.diary.totalFoodCalories.toInt()
                    _totalExerciseCalories.value = diaryWithNutrition.diary.totalExerciseCalories.toInt()
                    _totalRemainingCalories.value = diaryWithNutrition.diary.caloriesRemaining.toInt()
                    val mealSections = transformTodayDiaryDataToMealSections(diaryWithNutrition)
                    onSuccess(mealSections)
                } else {
                    _totalCalories.value = 0
                    _totalFoodCalories.value = 0
                    _totalExerciseCalories.value = 0
                    _totalRemainingCalories.value = 0

                    val emptySections = listOf(
                        MealSection("Breakfast", 0, emptyList()),
                        MealSection("Lunch", 0, emptyList()),
                        MealSection("Dinner", 0, emptyList()),
                        MealSection("Snacks", 0, emptyList()),
                        MealSection("Water", 0, emptyList())
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
                    _totalCalories.value = diarySnapshot.caloriesGoal.toInt()
                    _totalFoodCalories.value = diarySnapshot.totalFoodCalories.toInt()
                    _totalExerciseCalories.value = diarySnapshot.totalExerciseCalories.toInt()
                    _totalRemainingCalories.value = diarySnapshot.caloriesRemaining.toInt()
                    val mealSections = transformDiaryDataToMealSections(diarySnapshot)
                    onSuccess(mealSections)
                } else {
                    _totalCalories.value = 0
                    _totalFoodCalories.value = 0
                    _totalExerciseCalories.value = 0
                    _totalRemainingCalories.value = 0

                    val emptySections = listOf(
                        MealSection("Breakfast", 0, emptyList()),
                        MealSection("Lunch", 0, emptyList()),
                        MealSection("Dinner", 0, emptyList()),
                        MealSection("Snacks", 0, emptyList()),
                        MealSection("Water", 0, emptyList())
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
            val item = MealItem(foodSnapshot.foodName, servingText, foodSnapshot.calories.toInt(), foodSnapshot.foodId, Type.FOOD, mealType = foodSnapshot.mealType, servings = foodSnapshot.servings)

            when(foodSnapshot.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
                MealType.NONE -> {}
            }
        }

        diaryWithNutrition.meals.forEach { mealSnapshot ->
            val servingText = "${mealSnapshot.servings} serving"
            val item = MealItem(mealSnapshot.mealName, servingText, mealSnapshot.calories.toInt(), mealSnapshot.mealId, Type.MEAL, mealType = mealSnapshot.mealType, servings = mealSnapshot.servings)

            when(mealSnapshot.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
                MealType.NONE -> {}
            }
        }

        diaryWithNutrition.recipes.forEach { recipeSnapshot ->
            val servingText = "${recipeSnapshot.servings} serving"
            val item = MealItem(recipeSnapshot.recipeName, servingText, recipeSnapshot.calories.toInt(), recipeSnapshot.recipeId, Type.RECIPE, mealType = recipeSnapshot.mealType, servings = recipeSnapshot.servings)

            when(recipeSnapshot.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
                MealType.NONE -> {}
            }
        }


        val breakfastCalories = breakfastItems.sumOf { it.calories }
        val lunchCalories = lunchItems.sumOf { it.calories }
        val dinnerCalories = dinnerItems.sumOf { it.calories }
        val snackCalories = snackItems.sumOf { it.calories }

        return listOf(
            MealSection("Breakfast", breakfastCalories, breakfastItems),
            MealSection("Lunch", lunchCalories, lunchItems),
            MealSection("Dinner", dinnerCalories, dinnerItems),
            MealSection("Snacks", snackCalories, snackItems),
        )
    }

    suspend fun transformTodayDiaryDataToMealSections(diaryWithNutrition: DiaryWithAllNutrition): List<MealSection> {
        val breakfastItems = mutableListOf<MealItem>()
        val lunchItems = mutableListOf<MealItem>()
        val dinnerItems = mutableListOf<MealItem>()
        val snackItems = mutableListOf<MealItem>()
        val waterItems = mutableListOf<MealItem>()

        val foodCrossRefs = diaryWithNutrition.diary.id.let { diaryId ->
            foodCrossRefRepository.getDiaryFoodCrossRefsByDiaryId(diaryId)
        }

        foodCrossRefs?.forEach { crossRef ->
            val food = diaryWithNutrition.foods.find { it.id == crossRef.foodId }
            if (food == null) return@forEach
            val calories = food.calories * crossRef.servings
            val servingText = "${crossRef.servings} serving"
            val item = MealItem(food.name, servingText, calories.toInt(), food.id, Type.FOOD, mealType = crossRef.mealType, servings = crossRef.servings)

            when(crossRef.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
                MealType.NONE -> {}
            }
        }

        val mealCrossRefs = diaryWithNutrition.diary.id.let { diaryId ->
            mealCrossRefRepository.getDiaryMealCrossRefsByDiaryId(diaryId)
        }

        mealCrossRefs?.forEach { crossRef ->
            val meal = diaryWithNutrition.meals.find { it.id == crossRef.mealId }
            if (meal == null) return@forEach
            val calories = meal.totalCalories * crossRef.servings
            val servingText = "${crossRef.servings} serving"
            val item = MealItem(meal.name, servingText, calories.toInt(), meal.id, Type.MEAL, mealType = crossRef.mealType, servings = crossRef.servings)

            when(crossRef.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
                MealType.NONE -> {}
            }
        }

        val recipeCrossRefs = diaryWithNutrition.diary.id.let { diaryId ->
            recipeCrossRefRepository.getDiaryRecipeCrossRefsByDiaryId(diaryId)
        }

        recipeCrossRefs?.forEach { crossRef ->
            val recipe = diaryWithNutrition.recipes.find { it.id == crossRef.recipeId }
            if (recipe == null) return@forEach
            val calories = recipe.calories * crossRef.servings
            val servingText = "${crossRef.servings} serving"
            val item = MealItem(recipe.name, servingText, calories.toInt(), recipe.id, Type.RECIPE, mealType = crossRef.mealType, servings = crossRef.servings)

            when(crossRef.mealType) {
                MealType.BREAKFAST -> breakfastItems.add(item)
                MealType.LUNCH -> lunchItems.add(item)
                MealType.DINNER -> dinnerItems.add(item)
                MealType.SNACK -> snackItems.add(item)
                MealType.NONE -> {}
            }
        }

        val waterLogs = diaryWithNutrition.diary.id.let { diaryId ->
            waterLogRepository.getLogWaterByDailyDiaryId(diaryId)
        }

        waterLogs?.forEach { waterLog ->
            val servingText = "${waterLog.amountMl} ml"
            val item = MealItem(
                "Water",
                servingText,
                waterLog.amountMl,
                waterLog.id,
                Type.WATER,
                mealType = MealType.NONE,
                servings = 1
            )
            waterItems.add(item)
        }


        val breakfastCalories = breakfastItems.sumOf { it.calories }
        val lunchCalories = lunchItems.sumOf { it.calories }
        val dinnerCalories = dinnerItems.sumOf { it.calories }
        val snackCalories = snackItems.sumOf { it.calories }

        return listOf(
            MealSection("Breakfast", breakfastCalories, breakfastItems),
            MealSection("Lunch", lunchCalories, lunchItems),
            MealSection("Dinner", dinnerCalories, dinnerItems),
            MealSection("Snacks", snackCalories, snackItems),
            MealSection("Water", waterLogs?.sumOf { it.amountMl } ?: 0, waterItems)
        )
    }

    fun saveSnapshot(){
        viewModelScope.launch {
            val today = LocalDate.now()
            dailyDiarySnapshotRepository.execute(userId = currentUserId, date = today)
        }
    }

    fun deleteItemFromDiary(
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
        mealItem: MealItem,
    ){
        val today = LocalDate.now()
        if(selectedDate.value == today) {
            if(mealItem.type == Type.FOOD) {
                viewModelScope.launch {
                    foodCrossRefRepository.deleteDiaryFoodCrossRef(
                        userId = currentUserId,
                        diaryId = todayDiary.value!!.diary.id,
                        foodId = mealItem.id,
                        mealType = mealItem.mealType
                    )
                    onSuccess()
                }
            }
            else if(mealItem.type == Type.RECIPE) {
                viewModelScope.launch {
                    recipeCrossRefRepository.deleteDiaryRecipeCrossRef(
                        userId = currentUserId,
                        diaryId = todayDiary.value!!.diary.id,
                        recipeId = mealItem.id,
                        mealType = mealItem.mealType
                    )
                    onSuccess()
                }
            }
            else if(mealItem.type == Type.MEAL) {
                viewModelScope.launch {
                    mealCrossRefRepository.deleteDiaryMealCrossRef(
                        userId = currentUserId,
                        diaryId = todayDiary.value!!.diary.id,
                        mealId = mealItem.id,
                        mealType = mealItem.mealType
                    )
                    onSuccess()
                }
            }
        }
        else{
            onFailure()
        }
    }

    fun updateDiary(
        servings: Int,
        mealItem: MealItem,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        val today = LocalDate.now()
        if(selectedDate.value == today) {
            if(mealItem.type == Type.FOOD) {
                viewModelScope.launch {
                    foodCrossRefRepository.updateDiaryFoodCrossRef(
                        userId = currentUserId,
                        diaryId = todayDiary.value!!.diary.id,
                        foodId = mealItem.id,
                        mealType = mealItem.mealType,
                        servings = servings
                    )
                    onSuccess()
                }
            }
            else if(mealItem.type == Type.RECIPE) {
                viewModelScope.launch {
                    recipeCrossRefRepository.updateDiaryRecipeCrossRef(
                        userId = currentUserId,
                        diaryId = todayDiary.value!!.diary.id,
                        recipeId = mealItem.id,
                        mealType = mealItem.mealType,
                        servings = servings
                    )
                    onSuccess()
                }
            }
            else if(mealItem.type == Type.MEAL) {
                viewModelScope.launch {
                    mealCrossRefRepository.updateDiaryMealCrossRef(
                        userId = currentUserId,
                        diaryId = todayDiary.value!!.diary.id,
                        mealId = mealItem.id,
                        mealType = mealItem.mealType,
                        servings = servings
                    )
                    onSuccess()
                }
            }
        }
        else{
            onFailure()
        }
    }
}