package com.example.btl_android_project.local.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.btl_android_project.local.entity.StaticFoodEntity

@Dao
interface StaticFoodDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStaticFood(food: StaticFoodEntity)

    @Query("SELECT * FROM static_foods WHERE foodId = :foodId")
    suspend fun getFoodById(foodId: Int): StaticFoodEntity?

    @Query("SELECT * FROM static_foods")
    suspend fun getAllStaticFoods(): List<StaticFoodEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStaticFoods(staticFoods: List<StaticFoodEntity>)
}