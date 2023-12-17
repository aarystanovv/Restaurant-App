package com.example.final_project_android

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.final_project_android.model.Category

class CategoryViewModel : ViewModel() {
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> get() = _categories

    init {
        fetchCategories()
    }

    private fun fetchCategories() {
        _categories.value = listOf(
            Category("Italian", R.drawable.ic_android_black_24dp),
            Category("Chinese", R.drawable.ic_android_black_24dp),
            Category("Mexican", R.drawable.ic_android_black_24dp),
        )
    }
}