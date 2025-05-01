package com.example.btl_android_project.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.btl_android_project.local.entity.MealFoodCrossRef

@Dao
interface MealFoodCrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMealFoodCrossRef(food: MealFoodCrossRef)

    @Query("SELECT * FROM meal_food_cross_ref")
    suspend fun getAllMealFoodCrossRefs(): List<MealFoodCrossRef>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMealFoodCrossRefs(mealFoodCrossRefs: List<MealFoodCrossRef>)

    @Query("DELETE FROM meal_food_cross_ref WHERE foodId = :foodId")
    suspend fun deleteMealFoodCrossRefByFoodId(foodId: String)

    @Query("SELECT * FROM meal_food_cross_ref WHERE mealId = :mealId")
    suspend fun getMealFoodCrossRefById(mealId: Int): List<MealFoodCrossRef>?

    @Query("DELETE FROM meal_food_cross_ref WHERE mealId = :mealId")
    suspend fun deleteMealFoodCrossRefByMealId(mealId: Int)

    @Query("DELETE FROM meal_food_cross_ref")
    suspend fun deleteAllMealFoodCrossRefs()
}