package com.example.btl_android_project.remote.model
import com.google.gson.annotations.SerializedName

data class StaticFoodResponse(
    @SerializedName("food") val food: StaticFood? = null
)

data class StaticFood(
    @SerializedName("food_id") val foodId: Long? = null,
    @SerializedName("food_name") val foodName: String? = null,
    @SerializedName("food_type") val foodType: String? = null,
    @SerializedName("food_url") val foodUrl: String? = null,
    @SerializedName("servings") val servings: Servings? = null,
)

data class Servings(
    @SerializedName("serving") val servingList: List<ServingOnFood?>? = null,
)

data class ServingOnFood(
    @SerializedName("serving_id") val servingId: String? = null,
    @SerializedName("serving_description") val servingDescription: String? = null,
    @SerializedName("serving_url") val servingUrl: String? = null,
    @SerializedName("metric_serving_amount") val metricServingAmount: String? = null,
    @SerializedName("metric_serving_unit") val metricServingUnit: String? = null,
    @SerializedName("number_of_units") val numberOfUnits: String? = null,
    @SerializedName("measurement_description") val measurementDescription: String? = null,
    @SerializedName("calories") val calories: String? = null,
    @SerializedName("carbohydrate") val carbohydrate: String? = null,
    @SerializedName("protein") val protein: String? = null,
    @SerializedName("fat") val fat: String? = null,
    @SerializedName("saturated_fat") val saturatedFat: String? = null,
    @SerializedName("polyunsaturated_fat") val polyunsaturatedFat: String? = null,
    @SerializedName("monounsaturated_fat") val monounsaturatedFat: String? = null,
    @SerializedName("cholesterol") val cholesterol: String? = null,
    @SerializedName("sodium") val sodium: String? = null,
    @SerializedName("potassium") val potassium: String? = null,
    @SerializedName("fiber") val fiber: String? = null,
    @SerializedName("sugar") val sugar: String? = null,
    @SerializedName("vitamin_a") val vitaminA: String? = null,
    @SerializedName("vitamin_c") val vitaminC: String? = null,
    @SerializedName("calcium") val calcium: String? = null,
    @SerializedName("iron") val iron: String? = null,
)