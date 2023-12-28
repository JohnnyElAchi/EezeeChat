package com.johnnyelachi

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.auth.User

class SwipeleftrightViewModel : ViewModel() {
    private val _favoriteUsers = MutableLiveData<List<Person>>()
    val favoriteUsers: LiveData<List<Person>> get() = _favoriteUsers

    fun addFavoriteUser(person: Person) {
        val currentList = _favoriteUsers.value ?: emptyList()
        val updatedList = currentList + person
        _favoriteUsers.value = updatedList
    }
}