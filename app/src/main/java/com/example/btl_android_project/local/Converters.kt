package com.example.btl_android_project.local
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import com.example.btl_android_project.local.entity.DailyDiarySnapshot
import com.example.btl_android_project.local.entity.DairyFoodSnapshot
import com.example.btl_android_project.local.entity.DiaryMealSnapshot
import com.example.btl_android_project.local.entity.DiaryRecipeSnapshot
import com.example.btl_android_project.local.entity.Nutrition
import com.example.btl_android_project.local.entity.RecipeIngredient
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.local.enums.MealType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromNutritionList(value: List<Nutrition>?): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toNutritionList(value: String): List<Nutrition> {
        val listType = object : TypeToken<List<Nutrition>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun fromString(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(value, listType)
    }

    @TypeConverter
    fun toString(list: List<String>): String {
        return gson.toJson(list)
    }

    @TypeConverter
    fun fromIngredientList(value: List<StaticRecipeIngredient>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toIngredientList(value: String): List<StaticRecipeIngredient> {
        val listType = object : TypeToken<List<StaticRecipeIngredient>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromRecipeIngredientList(value: List<RecipeIngredient>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toRecipeIngredientList(value: String): List<RecipeIngredient> {
        val listType = object : TypeToken<List<RecipeIngredient>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date?.toString()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalDate(dateString: String?): LocalDate? {
        return dateString?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun fromMealType(value: MealType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toMealType(value: String?): MealType? {
        return value?.let { MealType.valueOf(it) }
    }

    @TypeConverter
    fun fromDailyDiarySnapshot(value: String?): DailyDiarySnapshot? {
        return value?.let { Gson().fromJson(it, DailyDiarySnapshot::class.java) }
    }

    @TypeConverter
    fun toDailyDiarySnapshot(value: DailyDiarySnapshot?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun fromDairyMealSnapshot(value: String?): DiaryMealSnapshot? {
        return value?.let { Gson().fromJson(it, DiaryMealSnapshot::class.java) }
    }

    @TypeConverter
    fun toDairyMealSnapshot(value: DiaryMealSnapshot?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun fromDairyMealSnapshotList(value: List<DiaryMealSnapshot>?): String {
        return value?.let { Gson().toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toDairyMealSnapshotList(value: String): List<DiaryMealSnapshot> {
        val listType = object : TypeToken<List<DiaryMealSnapshot>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDairyFoodSnapshot(value: String?): DairyFoodSnapshot? {
        return value?.let { Gson().fromJson(it, DairyFoodSnapshot::class.java) }
    }

    @TypeConverter
    fun toDairyFoodSnapshot(value: DairyFoodSnapshot?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun fromDairyFoodSnapshotList(value: List<DairyFoodSnapshot>?): String {
        return value?.let { Gson().toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toDairyFoodSnapshotList(value: String): List<DairyFoodSnapshot> {
        val listType = object : TypeToken<List<DairyFoodSnapshot>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromDairyRecipeSnapshot(value: String?): DiaryRecipeSnapshot? {
        return value?.let { Gson().fromJson(it, DiaryRecipeSnapshot::class.java) }
    }
    @TypeConverter
    fun toDairyRecipeSnapshot(value: DiaryRecipeSnapshot?): String? {
        return value?.let { Gson().toJson(it) }
    }

    @TypeConverter
    fun fromDairyRecipeSnapshotList(value: List<DiaryRecipeSnapshot>?): String {
        return value?.let { Gson().toJson(it) } ?: "[]"
    }

    @TypeConverter
    fun toDairyRecipeSnapshotList(value: String): List<DiaryRecipeSnapshot> {
        val listType = object : TypeToken<List<DiaryRecipeSnapshot>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
