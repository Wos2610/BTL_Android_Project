package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.DailyDiarySnapshotFireStoreDataSource
import com.example.btl_android_project.local.dao.DailyDiarySnapshotDao
import com.example.btl_android_project.local.entity.DailyDiary
import com.example.btl_android_project.local.entity.DailyDiarySnapshot
import com.example.btl_android_project.local.entity.DairyFoodSnapshot
import com.example.btl_android_project.local.entity.DiaryExerciseCrossRef
import com.example.btl_android_project.local.entity.DiaryExerciseSnapshot
import com.example.btl_android_project.local.entity.DiaryFoodCrossRef
import com.example.btl_android_project.local.entity.DiaryMealCrossRef
import com.example.btl_android_project.local.entity.DiaryMealSnapshot
import com.example.btl_android_project.local.entity.DiaryRecipeCrossRef
import com.example.btl_android_project.local.entity.DiaryRecipeSnapshot
import com.example.btl_android_project.local.entity.Exercise
import com.example.btl_android_project.local.entity.Food
import com.example.btl_android_project.local.entity.LogWater
import com.example.btl_android_project.local.entity.LogWaterSnapshot
import com.example.btl_android_project.local.entity.MealWithFoodsAndRecipes
import com.example.btl_android_project.local.entity.NutritionSnapshot
import com.example.btl_android_project.local.entity.Recipe
import com.example.btl_android_project.local.entity.RecipeIngredientSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

class DailyDiarySnapshotRepository @Inject constructor(
    private val dailyDiarySnapshotDao: DailyDiarySnapshotDao,
    private val dailyDiaryRepository: DailyDiaryRepository,
    private val dailyDiarySnapshotFireStoreDataSource: DailyDiarySnapshotFireStoreDataSource,
    private val diaryFoodCrossRefRepository: DiaryFoodCrossRefRepository,
    private val diaryMealCrossRefRepository: DiaryMealCrossRefRepository,
    private val diaryRecipeCrossRefRepository: DiaryRecipeCrossRefRepository,
    private val mealRepository: MealRepository,
    private val waterLogRepository: LogWaterRepository,
    private val diaryExerciseCrossRefRepository: DiaryExerciseCrossRefRepository
) {
    suspend fun createDailyDiarySnapshot(
        diary: DailyDiary,
        foods: List<Pair<Food, DiaryFoodCrossRef>>,
        meals: List<Pair<MealWithFoodsAndRecipes, DiaryMealCrossRef>>,
        recipes: List<Pair<Recipe, DiaryRecipeCrossRef>>,
        waters: List<LogWater>,
        exercises: List<Pair<Exercise, DiaryExerciseCrossRef>>
    ): DailyDiarySnapshot {
        val foodSnapshots = foods.map { pair ->
            val food = pair.first
            val crossRef = pair.second
            DairyFoodSnapshot(
                foodId = food.id,
                foodName = food.name,
                servings = crossRef.servings,
                mealType = crossRef.mealType,
                calories = food.calories ,
                protein = food.protein,
                carbs = food.carbs,
                fat = food.fat
            )
        }

        val mealSnapshots = meals.map { pair ->
            val mealWithFoodsAndRecipes = pair.first
            val meal = mealWithFoodsAndRecipes.meal
            val foods = mealWithFoodsAndRecipes.foods
            val recipes = mealWithFoodsAndRecipes.recipes
            val crossRef = pair.second
            DiaryMealSnapshot(
                mealId = meal.id,
                mealName = meal.name,
                servings = crossRef.servings,
                mealType = crossRef.mealType,
                calories = meal.totalCalories,
                protein = meal.totalProtein,
                carbs = meal.totalCarbs,
                fat = meal.totalFat,
                foods = foods.map { food ->
                    DairyFoodSnapshot(
                        foodId = food.id,
                        foodName = food.name,
                        servings = crossRef.servings,
                        mealType = crossRef.mealType,
                        calories = food.calories * crossRef.servings,
                        protein = food.protein * crossRef.servings,
                        carbs = food.carbs * crossRef.servings,
                        fat = food.fat * crossRef.servings
                    )
                },
                recipes = recipes.map { recipe ->
                    DiaryRecipeSnapshot(
                        recipeId = recipe.id,
                        recipeName = recipe.name,
                        servings = crossRef.servings,
                        mealType = crossRef.mealType,
                        calories = recipe.calories.toFloat() * crossRef.servings,
                        protein = recipe.protein.toFloat() * crossRef.servings,
                        carbs = recipe.carbs.toFloat() * crossRef.servings,
                        fat = recipe.fat.toFloat() * crossRef.servings,
                        ingredients = recipe.ingredients.map { ingredient ->
                            RecipeIngredientSnapshot(
                                id = ingredient.id,
                                fdcId = ingredient.fdcId,
                                description = ingredient.description,
                                foodNutrients = ingredient.foodNutrients.map { nutrition ->
                                    NutritionSnapshot(
                                        number = nutrition.number,
                                        name = nutrition.name,
                                        amount = nutrition.amount,
                                        unitName = nutrition.unitName
                                    )
                                },
                                numberOfServings = ingredient.numberOfServings,
                                servingSize = ingredient.servingSize
                            )
                        }
                    )
                },
            )
        }

        val recipeSnapshots = recipes.map { pair ->
            val recipe = pair.first
            val crossRef = pair.second
            DiaryRecipeSnapshot(
                recipeId = recipe.id,
                recipeName = recipe.name,
                servings = crossRef.servings,
                mealType = crossRef.mealType,
                calories = recipe.calories.toFloat(),
                protein = recipe.protein.toFloat(),
                carbs = recipe.carbs.toFloat(),
                fat = recipe.fat.toFloat(),
                ingredients = recipe.ingredients.map { ingredient ->
                    RecipeIngredientSnapshot(
                        id = ingredient.id,
                        fdcId = ingredient.fdcId,
                        description = ingredient.description,
                        foodNutrients = ingredient.foodNutrients.map { nutrition ->
                            NutritionSnapshot(
                                number = nutrition.number,
                                name = nutrition.name,
                                amount = nutrition.amount,
                                unitName = nutrition.unitName
                            )
                        },
                        numberOfServings = ingredient.numberOfServings,
                        servingSize = ingredient.servingSize
                    )
                }
            )
        }

        val waterSnapshots = waters.map { water ->
            LogWaterSnapshot(
                id = water.id,
                userId = water.userId,
                dailyDiaryId = water.dailyDiaryId,
                amountMl = water.amountMl,
                createdAt = water.createdAt,
                updatedAt = water.updatedAt
            )
        }

        val exerciseSnapshots = exercises.map { pair ->
            val exercise = pair.first
            val crossRef = pair.second
            DiaryExerciseSnapshot(
                exerciseId = exercise.id,
                description = exercise.description,
                minutesPerformed = exercise.minutesPerformed,
                caloriesBurned = exercise.caloriesBurned,
                servings = crossRef.servings,
                userId = exercise.userId,
            )
        }

        val snapshot = DailyDiarySnapshot(
            diaryId = diary.id,
            userId = diary.userId,
            logDate = diary.logDate ?: LocalDate.now(),
            caloriesRemaining = diary.caloriesRemaining,
            totalFoodCalories = diary.totalFoodCalories,
            totalExerciseCalories = diary.totalExerciseCalories,
            totalWaterMl = diary.totalWaterMl,
            totalFat = diary.totalFat,
            totalCarbs = diary.totalCarbs,
            totalProtein = diary.totalProtein,
            caloriesGoal = diary.caloriesGoal,
            foods = foodSnapshots,
            meals = mealSnapshots,
            recipes = recipeSnapshots,
            waters = waterSnapshots,
            exercises = exerciseSnapshots,
        )

        val snapshotId = dailyDiarySnapshotFireStoreDataSource.insertDailyDiarySnapshot(snapshot)
        val completeSnapshot = snapshot.copy(id = snapshotId)
        dailyDiarySnapshotDao.insertSnapshot(completeSnapshot)

        return completeSnapshot
    }

    suspend fun execute(userId: String, date: LocalDate): Result<String> {
        return try {
            val diaryWithAllNutrition = dailyDiaryRepository.getDiaryByDate(userId, date)
                ?: return Result.failure(IllegalArgumentException("Diary not found for userId: $userId and date: $date"))

            val diary = diaryWithAllNutrition.diary

            val diaryFoodCrossRefs = diaryFoodCrossRefRepository.getDiaryFoodCrossRefsByDiaryId(diary.id)
            val diaryMealCrossRefs = diaryMealCrossRefRepository.getDiaryMealCrossRefsByDiaryId(diary.id)
            val diaryRecipeCrossRefs = diaryRecipeCrossRefRepository.getDiaryRecipeCrossRefsByDiaryId(diary.id)
            val waterLogs = waterLogRepository.getLogWaterByDailyDiaryId(diary.id) ?: emptyList()
            val exerciseCrossRefs = diaryExerciseCrossRefRepository.getDiaryExerciseCrossRefsByDiaryId(diary.id) ?: emptyList()


            val diaryFoods = diaryFoodCrossRefs?.map { crossRef ->
                val food = diaryWithAllNutrition.foods.find { it.id == crossRef.foodId}
                    ?: return Result.failure(IllegalArgumentException("Food not found for foodId: ${crossRef.foodId}"))
                Pair(food, crossRef)
            } ?: emptyList()

            val diaryMeals = diaryWithAllNutrition.meals.map { meal ->
                val mealWithFoodsAndRecipes = mealRepository.getMealWithFoodsAndRecipes(meal.id)
                val crossRef = diaryMealCrossRefs?.find { it.mealId == meal.id && it.diaryId == diary.id }
                    ?: return Result.failure(IllegalArgumentException("Meal CrossRef not found for meal: ${meal.id}"))
                Pair(mealWithFoodsAndRecipes, crossRef)
            }

            val diaryRecipes = diaryRecipeCrossRefs?.map { crossRef ->
                val recipe = diaryWithAllNutrition.recipes.find { it.id == crossRef.recipeId }
                    ?: return Result.failure(IllegalArgumentException("Recipe not found for recipeId: ${crossRef.recipeId}"))
                Pair(recipe, crossRef)
            } ?: emptyList()

            val diaryExercises = exerciseCrossRefs.map { crossRef ->
                val exercise = diaryWithAllNutrition.exercises.find { it.id == crossRef.exerciseId }
                    ?: return Result.failure(IllegalArgumentException("Exercise not found for exerciseId: ${crossRef.exerciseId}"))
                Pair(exercise, crossRef)
            }

            val snapshot = createDailyDiarySnapshot(
                diary = diary,
                foods = diaryFoods,
                meals = diaryMeals,
                recipes = diaryRecipes,
                waters = waterLogs,
                exercises = diaryExercises
            )

            val updatedDiary = diary.copy(isSaveSnapshot = true)
            dailyDiaryRepository.updateDailyDiary(updatedDiary)

            Result.success(snapshot.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getByDate(userId: String, date: LocalDate): DailyDiarySnapshot? {
        return withContext(Dispatchers.IO) {
            try {
                dailyDiarySnapshotFireStoreDataSource.getDiaryByDate(userId, date)
            }catch (e: Exception){
                null
            }
        }
    }

}