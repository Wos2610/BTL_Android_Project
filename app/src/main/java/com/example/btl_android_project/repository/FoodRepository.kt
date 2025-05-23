package com.example.btl_android_project.repository

import com.example.btl_android_project.firestore.datasource.FoodFireStoreDataSourceImpl
import com.example.btl_android_project.local.dao.FoodDao
import com.example.btl_android_project.local.entity.Food
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FoodRepository @Inject constructor(
    private val foodDao: FoodDao,
    private val foodFireStoreDataSource: FoodFireStoreDataSourceImpl,
    private val mealFoodCrossRefRepository: MealFoodCrossRefRepository,
    private val mealRepository: MealRepository,
    private val dailyDiaryRepository: DailyDiaryRepository,
) {
    suspend fun insertFood(food: Food): String {
        return try {
            val firestoreId = foodFireStoreDataSource.addFood(food)
            val updatedFood = food.copy(id = firestoreId)
            foodDao.insertFood(updatedFood)

            Timber.d("Food inserted with local ID: $firestoreId and Firestore ID: $firestoreId")
            firestoreId
        } catch (e: Exception) {
            Timber.e("Error inserting food: ${e.message}")
            throw e
        }
    }

    suspend fun updateFood(food: Food) {
        try {
            foodDao.updateFood(food)

            foodFireStoreDataSource.updateFood(food)

            mealRepository.calculateWhenFoodChange(food.id.toString())

            dailyDiaryRepository.recalculateWhenChanging(userId = food.userId)

            Timber.d("Food updated with ID: ${food.id}")
        } catch (e: Exception) {
            Timber.e("Error updating food: ${e.message}")
            throw e
        }
    }

    suspend fun deleteFood(food: Food) {
        try {
            val deletedFood = food.copy(deleted = true)

//            mealFoodCrossRefRepository.deleteMealFoodCrossRefByFoodId(food.id.toString())

//            foodDao.deleteFood(food)

            foodDao.updateFood(deletedFood)

            foodFireStoreDataSource.updateFood(deletedFood)

//            foodFireStoreDataSource.deleteFood(food.id.toString())

            Timber.d("Food deleted with ID: ${food.id}")
        } catch (e: Exception) {
            Timber.e("Error deleting food: ${e.message}")
            throw e
        }
    }

    suspend fun getFoodById(foodId: String): Food? {
        return try {
            val localFood = foodDao.getFoodById(foodId)

            if (localFood != null) {
                localFood
            } else {
                val firestoreFood = foodFireStoreDataSource.getFoodById(foodId)

                if (firestoreFood != null) {
                    foodDao.insertFood(firestoreFood)
                }

                firestoreFood
            }
        } catch (e: Exception) {
            Timber.e("Error getting food by ID: ${e.message}")
            null
        }
    }

    fun getAllFoodsByUser(userId: String): Flow<List<Food>> {
        return try {
            foodDao.getAllFoodsByUser(userId)
        } catch (e: Exception) {
            Timber.e("Error getting all foods by user: ${e.message}")
            flowOf(emptyList())
        }
    }

    suspend fun searchFoods(query: String, userId: String): List<Food> {
        return withContext(Dispatchers.IO) {
            try {
                foodDao.searchFoods(query, userId)
            } catch (e: Exception) {
                Timber.e("Error searching foods: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun syncFoodsFromFirestore(userId: String) {
        try {
            val firestoreFoods = foodFireStoreDataSource.getAllFoodsByUser(userId)
            foodDao.insertAllFoods(firestoreFoods)
            Timber.d("Synced ${firestoreFoods.size} foods from Firestore")
        } catch (e: Exception) {
            Timber.e("Error syncing foods from Firestore: ${e.message}")
        }
    }

    suspend fun getFoodByFoodId(id: String) : Food? {
        return withContext(Dispatchers.IO){
            foodDao.getFoodById(id)
        }
    }
}