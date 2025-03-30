package com.example.btl_android_project.remote.model
import com.google.gson.annotations.SerializedName

data class StaticFoodListResponse(
    @SerializedName("foods")
    val foods: FoodsContainer? = null,
)

data class FoodsContainer(
    @SerializedName("max_results")
    val maxResults: String? = null,

    @SerializedName("page_number")
    val pageNumber: String? = null,

    @SerializedName("food")
    val foodInFoodsContainerList: List<FoodInFoodsContainer>? = null,

    @SerializedName("total_results")
    val totalResults: String? = null
)

data class FoodInFoodsContainer(
    @SerializedName("food_description")
    val foodDescription: String? = null,

    @SerializedName("food_id")
    val foodId: Long? = null,

    @SerializedName("food_name")
    val foodName: String? = null,

    @SerializedName("food_type")
    val foodType: String? = null,

    @SerializedName("food_url")
    val foodUrl: String? = null,

    @SerializedName("brand_name")
    val brandName: String? = null
)


