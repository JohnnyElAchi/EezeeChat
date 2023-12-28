package com.johnnyelachi


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    private val _numberOfCoinsLiveData = MutableLiveData<Int>(0)

    var numberOfPurchasedMinutes: MutableLiveData<Int> = MutableLiveData(0)


    fun updateNumberOfCoins(newValue: Int) {
        numberOfCoins = newValue
        _numberOfCoinsLiveData.value = newValue
    }

    val numberOfCoinsLiveData: LiveData<Int> = _numberOfCoinsLiveData

    var numberOfCoins: Int = 0

    fun addPurchasedMinutes(minutesPurchased: Int) {
        numberOfCoins += minutesPurchased
        _numberOfCoinsLiveData.value = numberOfCoins
    }

}