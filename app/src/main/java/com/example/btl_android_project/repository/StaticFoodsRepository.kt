package com.example.btl_android_project.repository

import com.example.btl_android_project.entity.StaticFoodEntity
import com.example.btl_android_project.firestore.domain.StaticFoodFireStoreDataSource
import com.example.btl_android_project.local.dao.StaticFoodDao
import com.example.btl_android_project.remote.domain.StaticFoodRemoteDataSource
import com.example.btl_android_project.remote.onError
import com.example.btl_android_project.remote.onException
import com.example.btl_android_project.remote.onSuccess
import com.example.btl_android_project.utils.mapToStaticFoodEntity
import com.example.btl_android_project.utils.mapToStaticFoodList
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

class StaticFoodsRepository @Inject constructor(
    private val staticFoodDao: StaticFoodDao,
    private val staticFoodRemoteDataSource: StaticFoodRemoteDataSource,
    private val staticFoodFireStoreDataSource: StaticFoodFireStoreDataSource,
) {
    suspend fun pullStaticFoods() {
        val searchExpressions = listOf(
            // 🍚 Tinh bột & Ngũ cốc
            "rice",
            "corn",
            "wheat",
            "oats",
            "barley",
            "quinoa",
            "bread",
            "pasta",
            "noodles",
            "cereal",

            // 🍗 Thịt, Cá, Hải sản
            "chicken",
            "beef",
            "pork",
            "lamb",
            "turkey",
            "duck",
            "salmon",
            "tuna",
            "shrimp",
            "crab",

            // 🥚 Trứng & Sản phẩm từ sữa
            "egg",
            "milk",
            "cheese",
            "butter",
            "yogurt",
            "cream",
            "sour cream",
            "ice cream",

            // 🥦 Rau củ quả
            "potato",
            "carrot",
            "tomato",
            "onion",
            "garlic",
            "cucumber",
            "lettuce",
            "spinach",
            "broccoli",
            "cauliflower",

            // 🍏 Trái cây
            "apple",
            "banana",
            "orange",
            "lemon",
            "pineapple",
            "strawberry",
            "blueberry",
            "mango",
            "watermelon",
            "grape",

            // 🌰 Các loại hạt & Đậu
            "peanut",
            "almond",
            "cashew",
            "walnut",
            "hazelnut",
            "soybeans",
            "lentils",
            "chickpeas",
            "black beans",
            "kidney beans",

            // 🛢️ Gia vị & Nguyên liệu nấu ăn
            "salt",
            "sugar",
            "pepper",
            "olive oil",
            "vinegar",
            "soy sauce",
            "honey",
            "ketchup",
            "mustard",
            "mayonnaise",

            // 🥤 Đồ uống
            "water",
            "coffee",
            "tea",
            "milkshake",
            "juice",
            "soda",
            "beer",
            "wine"
        )

        searchExpressions.forEach { searchExpression ->
            Timber.d("Searching for: $searchExpression")

            for (pageNumber in 0..10) {
                Timber.d("Fetching page: $pageNumber for $searchExpression")
                delay(1000)

                val staticFoodListResponse = staticFoodRemoteDataSource.getStaticFoodList(
                    PAGE_SIZE,
                    pageNumber,
                    searchExpression
                )
                var isEmptyPage = false

                staticFoodListResponse
                    .onSuccess { result ->
                        val foodList = result?.foods?.foodInFoodsContainerList.orEmpty()
                        if (foodList.isEmpty()) {
                            Timber.d("No data found on page $pageNumber, skipping to next search expression.")
                            isEmptyPage = true
                            return@onSuccess
                        }

                        val foodEntities = mutableListOf<StaticFoodEntity>()
                        foodList.forEach { foodInFoodsContainer ->
                            val foodId = foodInFoodsContainer.foodId ?: return@forEach
                            Timber.d("Fetching food details for ID: $foodId")

                            val staticFoodResponse =
                                staticFoodRemoteDataSource.getStaticFoodByFoodId(foodId)
                            staticFoodResponse
                                .onSuccess { foodResponse ->
                                    foodResponse?.food?.let { food ->
                                        foodEntities.add(mapToStaticFoodEntity(food))
                                    }
                                }
                                .onError { Timber.e("Error fetching food ID $foodId: $it") }
                                .onException { Timber.e("Exception fetching food ID $foodId: $it") }
                        }

                        if (foodEntities.isNotEmpty()) {
                            staticFoodDao.insertAllStaticFoods(foodEntities)
                            Timber.d("Inserted ${foodEntities.size} foods into database")
                        }
                    }
                    .onError { Timber.e("Error fetching food list: $it") }
                    .onException { Timber.e("Exception fetching food list: $it") }

                if (isEmptyPage) break
            }
        }
    }

    suspend fun pushToFireStore() {
        val foods = staticFoodDao.getAllStaticFoods()
        Timber.d("Pushing ${foods.size} foods to Firestore")
        staticFoodFireStoreDataSource.addAllFoods(mapToStaticFoodList(foods))
    }

    companion object {
        private const val PAGE_SIZE = 50
    }
}