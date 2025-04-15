package com.example.btl_android_project.presentation.log_recipe

import androidx.lifecycle.ViewModel
import com.example.btl_android_project.local.entity.StaticRecipeIngredient
import com.example.btl_android_project.repository.StaticRecipeIngredientRepository

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchIngredientViewModel @Inject constructor(
    val ingredientRepository: StaticRecipeIngredientRepository
): ViewModel() {
    val recipeIngredients = ingredientRepository.getAllRecipeIngredients()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val ingredientSearchResults: Flow<List<StaticRecipeIngredient>> = searchQuery
        .debounce(300)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            ingredientRepository.searchRecipeIngredients(query)
        }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}