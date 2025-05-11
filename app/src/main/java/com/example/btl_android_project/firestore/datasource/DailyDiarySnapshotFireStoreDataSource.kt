package com.example.btl_android_project.firestore.datasource

import com.example.btl_android_project.local.entity.DailyDiarySnapshot
import com.example.btl_android_project.local.entity.DairyFoodSnapshot
import com.example.btl_android_project.local.entity.DiaryMealSnapshot
import com.example.btl_android_project.local.entity.DiaryRecipeSnapshot
import com.example.btl_android_project.local.entity.NutritionSnapshot
import com.example.btl_android_project.local.entity.RecipeIngredientSnapshot
import com.example.btl_android_project.local.enums.MealType
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DailyDiarySnapshotFireStoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    companion object {
        private const val COLLECTION_NAME = "daily_diary_snapshots"
    }

    suspend fun insertDailyDiarySnapshot(dailyDiarySnapshot: DailyDiarySnapshot): String {
        return suspendCoroutine { continuation ->
            val docRef = firestore.collection(COLLECTION_NAME).document()

            val newId = docRef.id

            val updatedDiary = dailyDiarySnapshot.copy(id = newId)

            val diaryMap = mapFromSnapshotToMap(updatedDiary)

            docRef.set(diaryMap)
                .addOnSuccessListener {
                    continuation.resume(newId)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun getDiaryByDate(userId: String, date: LocalDate): DailyDiarySnapshot {
        return suspendCoroutine { continuation ->
            firestore.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("logDate", date.toString())
                .limit(1)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (querySnapshot.isEmpty) {
                        continuation.resumeWithException(NoSuchElementException("No diary found for date: $date"))
                    } else {
                        val document = querySnapshot.documents[0]
                        val dailyDiarySnapshot = mapFromDocumentToSnapshot(document)
                        continuation.resume(dailyDiarySnapshot)
                    }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    fun mapFromSnapshotToMap(snapshot: DailyDiarySnapshot): Map<String, Any?> {
        return mapOf(
            "id" to snapshot.id,
            "diaryId" to snapshot.diaryId,
            "userId" to snapshot.userId,
            "logDate" to snapshot.logDate.toString(),
            "caloriesRemaining" to snapshot.caloriesRemaining,
            "totalFoodCalories" to snapshot.totalFoodCalories,
            "totalExerciseCalories" to snapshot.totalExerciseCalories,
            "totalWaterMl" to snapshot.totalWaterMl,
            "totalFat" to snapshot.totalFat,
            "totalCarbs" to snapshot.totalCarbs,
            "totalProtein" to snapshot.totalProtein,
            "caloriesGoal" to snapshot.caloriesGoal,
            "foods" to mapFoodsToMap(snapshot.foods),
            "meals" to mapMealsToMap(snapshot.meals),
            "recipes" to mapRecipesToMap(snapshot.recipes),
            "createdAt" to snapshot.createdAt
        )
    }

    private fun mapFoodsToMap(foods: List<DairyFoodSnapshot>): List<Map<String, Any?>> {
        return foods.map { food ->
            mapOf(
                "foodId" to food.foodId,
                "foodName" to food.foodName,
                "servings" to food.servings,
                "mealType" to food.mealType.toString(),
                "calories" to food.calories,
                "protein" to food.protein,
                "carbs" to food.carbs,
                "fat" to food.fat
            )
        }
    }

    private fun mapMealsToMap(meals: List<DiaryMealSnapshot>): List<Map<String, Any?>> {
        return meals.map { meal ->
            mapOf(
                "mealId" to meal.mealId,
                "mealName" to meal.mealName,
                "servings" to meal.servings,
                "mealType" to meal.mealType.toString(),
                "calories" to meal.calories,
                "protein" to meal.protein,
                "carbs" to meal.carbs,
                "fat" to meal.fat,
                "foods" to mapFoodsToMap(meal.foods),
                "recipes" to mapMealRecipesToMap(meal.recipes)
            )
        }
    }

    private fun mapMealRecipesToMap(recipes: List<DiaryRecipeSnapshot>): List<Map<String, Any?>> {
        return recipes.map { recipe ->
            mapOf(
                "recipeId" to recipe.recipeId,
                "recipeName" to recipe.recipeName,
                "servings" to recipe.servings,
                "mealType" to recipe.mealType.toString(),
                "calories" to recipe.calories,
                "protein" to recipe.protein,
                "carbs" to recipe.carbs,
                "fat" to recipe.fat,
                "ingredients" to mapIngredientsToMap(recipe.ingredients)
            )
        }
    }

    private fun mapRecipesToMap(recipes: List<DiaryRecipeSnapshot>): List<Map<String, Any?>> {
        return recipes.map { recipe ->
            mapOf(
                "recipeId" to recipe.recipeId,
                "recipeName" to recipe.recipeName,
                "servings" to recipe.servings,
                "mealType" to recipe.mealType.toString(),
                "calories" to recipe.calories,
                "protein" to recipe.protein,
                "carbs" to recipe.carbs,
                "fat" to recipe.fat,
                "ingredients" to mapIngredientsToMap(recipe.ingredients)
            )
        }
    }

    private fun mapIngredientsToMap(ingredients: List<RecipeIngredientSnapshot>): List<Map<String, Any?>> {
        return ingredients.map { ingredient ->
            mapOf(
                "id" to ingredient.id,
                "fdcId" to ingredient.fdcId,
                "description" to ingredient.description,
                "numberOfServings" to ingredient.numberOfServings,
                "servingSize" to ingredient.servingSize,
                "foodNutrients" to mapNutrientsToMap(ingredient.foodNutrients)
            )
        }
    }

    private fun mapNutrientsToMap(nutrients: List<NutritionSnapshot>): List<Map<String, Any?>> {
        return nutrients.map { nutrition ->
            mapOf(
                "number" to nutrition.number,
                "name" to nutrition.name,
                "amount" to nutrition.amount,
                "unitName" to nutrition.unitName
            )
        }
    }

    private fun mapFromDocumentToSnapshot(document: DocumentSnapshot): DailyDiarySnapshot {
        val data = document.data ?: throw IllegalStateException("Document data is null")

        return DailyDiarySnapshot(
            id = document.id,
            diaryId = data["diaryId"] as String,
            userId = data["userId"] as String,
            logDate = LocalDate.parse(data["logDate"] as String),
            caloriesRemaining = (data["caloriesRemaining"] as Number).toFloat(),
            totalFoodCalories = (data["totalFoodCalories"] as Number).toFloat(),
            totalExerciseCalories = (data["totalExerciseCalories"] as Number).toFloat(),
            totalWaterMl = (data["totalWaterMl"] as Number).toInt(),
            totalFat = (data["totalFat"] as Number).toFloat(),
            totalCarbs = (data["totalCarbs"] as Number).toFloat(),
            totalProtein = (data["totalProtein"] as Number).toFloat(),
            caloriesGoal = (data["caloriesGoal"] as Number).toFloat(),
            foods = mapFirestoreListToFoods(data["foods"] as? List<Map<String, Any>> ?: emptyList()),
            meals = mapFirestoreListToMeals(data["meals"] as? List<Map<String, Any>> ?: emptyList()),
            recipes = mapFirestoreListToRecipes(data["recipes"] as? List<Map<String, Any>> ?: emptyList()),
            createdAt = (data["createdAt"] as Number).toLong(),
            syncedToFirestore = true
        )
    }

    private fun mapFirestoreListToFoods(foodsList: List<Map<String, Any>>): List<DairyFoodSnapshot> {
        return foodsList.map { foodMap ->
            DairyFoodSnapshot(
                foodId = foodMap["foodId"] as String,
                foodName = foodMap["foodName"] as String,
                servings = (foodMap["servings"] as Number).toInt(),
                mealType = MealType.valueOf(foodMap["mealType"] as String),
                calories = (foodMap["calories"] as Number).toFloat(),
                protein = (foodMap["protein"] as Number).toFloat(),
                carbs = (foodMap["carbs"] as Number).toFloat(),
                fat = (foodMap["fat"] as Number).toFloat()
            )
        }
    }

    private fun mapFirestoreListToMeals(mealsList: List<Map<String, Any>>): List<DiaryMealSnapshot> {
        return mealsList.map { mealMap ->
            DiaryMealSnapshot(
                mealId = mealMap["mealId"] as String,
                mealName = mealMap["mealName"] as String,
                servings = (mealMap["servings"] as Number).toInt(),
                mealType = MealType.valueOf(mealMap["mealType"] as String),
                calories = (mealMap["calories"] as Number).toFloat(),
                protein = (mealMap["protein"] as Number).toFloat(),
                carbs = (mealMap["carbs"] as Number).toFloat(),
                fat = (mealMap["fat"] as Number).toFloat(),
                foods = mapFirestoreListToFoods(mealMap["foods"] as? List<Map<String, Any>> ?: emptyList()),
                recipes = mapFirestoreListToMealRecipes(mealMap["recipes"] as? List<Map<String, Any>> ?: emptyList())
            )
        }
    }

    private fun mapFirestoreListToMealRecipes(recipesList: List<Map<String, Any>>): List<DiaryRecipeSnapshot> {
        return recipesList.map { recipeMap ->
            DiaryRecipeSnapshot(
                recipeId = recipeMap["recipeId"] as String,
                recipeName = recipeMap["recipeName"] as String,
                servings = (recipeMap["servings"] as Number).toInt(),
                mealType = MealType.valueOf(recipeMap["mealType"] as String),
                calories = (recipeMap["calories"] as Number).toFloat(),
                protein = (recipeMap["protein"] as Number).toFloat(),
                carbs = (recipeMap["carbs"] as Number).toFloat(),
                fat = (recipeMap["fat"] as Number).toFloat(),
                ingredients = mapFirestoreListToIngredients(recipeMap["ingredients"] as? List<Map<String, Any>> ?: emptyList())
            )
        }
    }

    private fun mapFirestoreListToRecipes(recipesList: List<Map<String, Any>>): List<DiaryRecipeSnapshot> {
        return recipesList.map { recipeMap ->
            DiaryRecipeSnapshot(
                recipeId = recipeMap["recipeId"] as String,
                recipeName = recipeMap["recipeName"] as String,
                servings = (recipeMap["servings"] as Number).toInt(),
                mealType = MealType.valueOf(recipeMap["mealType"] as String),
                calories = (recipeMap["calories"] as Number).toFloat(),
                protein = (recipeMap["protein"] as Number).toFloat(),
                carbs = (recipeMap["carbs"] as Number).toFloat(),
                fat = (recipeMap["fat"] as Number).toFloat(),
                ingredients = mapFirestoreListToIngredients(recipeMap["ingredients"] as? List<Map<String, Any>> ?: emptyList())
            )
        }
    }

    private fun mapFirestoreListToIngredients(ingredientsList: List<Map<String, Any>>): List<RecipeIngredientSnapshot> {
        return ingredientsList.map { ingredientMap ->
            RecipeIngredientSnapshot(
                id = ingredientMap["id"] as String,
                fdcId = (ingredientMap["fdcId"] as Number).toInt(),
                description = ingredientMap["description"] as String,
                numberOfServings = (ingredientMap["numberOfServings"] as Number).toInt(),
                servingSize = (ingredientMap["servingSize"] as String),
                foodNutrients = mapFirestoreListToNutrients(ingredientMap["foodNutrients"] as? List<Map<String, Any>> ?: emptyList())
            )
        }
    }

    private fun mapFirestoreListToNutrients(nutrientsList: List<Map<String, Any>>): List<NutritionSnapshot> {
        return nutrientsList.map { nutrientMap ->
            NutritionSnapshot(
                number = nutrientMap["number"] as String,
                name = nutrientMap["name"] as String,
                amount = (nutrientMap["amount"] as Number).toFloat(),
                unitName = nutrientMap["unitName"] as String
            )
        }
    }
}