package com.example.android.unscramble.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    companion object {
        const val TAG = "ViewModel"
    }

    private lateinit var currentWord: String
    private val wordsList: MutableList<String> = mutableListOf()
    private var _score = MutableLiveData(0)
    private var _currentWordCount = MutableLiveData(0)
    private val _currentScrambledWord = MutableLiveData<String>()//This is a LiveData
    val score get() = _score
    val currentWordCount get() = _currentWordCount
    val currentScrambledWord: LiveData<String> get() = _currentScrambledWord

    init {
        Log.d(TAG, "ViewModel is alive....")
        getNextWorld()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onClear is calling...")
    }

    //Để truy cập dữ liệu trong đối tượng LiveData, hãy sử dụng value property.
    private fun getNextWorld() {
        currentWord = allWordsList.random()
        val tempWord = currentWord.toCharArray()
        while (String(tempWord).equals(currentWord, false)) {
            Log.d("Check", "tempWord.shuffle() is running...")
            tempWord.shuffle()
        }
        if (currentWord in wordsList) {
            getNextWorld()
        } else {
            _currentScrambledWord.value = String(tempWord)
            _currentWordCount.value = _currentWordCount.value?.inc()
            wordsList.add(currentWord)
        }
    }

    fun nextWord(): Boolean {
        return if (_currentWordCount.value!! < MAX_NO_OF_WORDS) {
            getNextWorld()
            true
        } else
            false
    }

    fun isUserWordCorrect(playerWord: String): Boolean {
        if (playerWord.equals(this.currentWord, true)) {
            increaseScore()
            return true
        }
        return false
    }

    private fun increaseScore() {
        _score.value = _score.value?.plus(SCORE_INCREASE)
        Log.d("Check", "increase score...")
    }

    fun reinitializeData() {
        _score.value = 0
        _currentWordCount.value = 0
        wordsList.clear()
        getNextWorld()
    }
}