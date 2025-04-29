package com.example.btl_android_project.local.dao

import androidx.room.*
import com.example.btl_android_project.local.entity.Food
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFood(food: Food): Long

    @Update
    suspend fun updateFood(food: Food)

    @Delete
    suspend fun deleteFood(food: Food)

    @Query("SELECT * FROM foods WHERE id = :foodId")
    suspend fun getFoodById(foodId: Int): Food?

    @Query("SELECT * FROM foods WHERE userId = :userId")
    fun getAllFoodsByUser(userId: Int): Flow<List<Food>>

    @Query("SELECT * FROM foods WHERE name LIKE '%' || :query || '%' AND userId = :userId")
    fun searchFoods(query: String, userId: Int): List<Food>

    @Query("DELETE FROM foods WHERE id = :foodId")
    suspend fun deleteFoodById(foodId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllFoods(foods: List<Food>)

    @Query("SELECT * FROM foods WHERE id IN (:ids)")
    suspend fun getFoodsByIds(ids: List<Int>): List<Food>
}