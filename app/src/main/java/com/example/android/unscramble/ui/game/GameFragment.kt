/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {

    private val viewModel: GameViewModel by viewModels()

    // Binding object instance with access to the views in the game_fragment.xml layout
    //Binding object dai dien cho file layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
//        binding = GameFragmentBinding.inflate(inflater, container, false)
        //inflater để inflate các view binding with fragment, layoutId là data binding layout,container là viewGroup(view root trong non-binding data layout) ,attachToParent tùy chọn có gắn View với UI Controller khong
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
        Log.d(GameViewModel.TAG, "onCreateView is calling ...")
//        Log.d(
//            "GameFragment", "Word: ${viewModel.currentScrambledWord} " +
//                    "Score: ${viewModel.score} WordCount: ${viewModel.currentWordCount}"
//        )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(GameViewModel.TAG, "onDestroyed is calling...")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(GameViewModel.TAG, "onDetach is calling...")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Initialize variables layout
        binding.gameViewModel = viewModel
        binding.maxNoOfWords = MAX_NO_OF_WORDS
        //pass the attached lifecycleOwner with observe to the lifecycleOwner of the data binding layout
        //khi mình sử dụng layout variable truy cập vào các LiveData object, các LiveData object cần quan sát theo vòng đời,do đó,
        //cần phải set lifecycleOwner cho layout thông qua binding object đại diện cho layout, để observe của LiveData object được update khi data được giữ bởi LiveData object bị thay đổi
        // cụ thể là các binding expression sẽ được Data Binding Library gọi lại khi data thay đổi
        binding.lifecycleOwner = viewLifecycleOwner
        // Setup a click listener for the Submit and Skip buttons.
        binding.submit.setOnClickListener { onSubmitWord() }
        binding.skip.setOnClickListener { onSkipWord() }
        // Update the UI
//        updateNextWordOnScreen()
        binding.score.text = getString(R.string.score, 0)
        binding.wordCount.text = getString(
            R.string.word_count, viewModel.currentWordCount.value, MAX_NO_OF_WORDS
        )
//        Log.d("Check", viewModel.currentScrambledWord.value!!)
        Log.d("Check", "onViewCreate is calling ...")
//        viewModel.currentScrambledWord.observe(viewLifecycleOwner) {
//            binding.textViewUnscrambledWord.text = it
//            Log.d(GameViewModel.TAG, "observe attach with scramble word is calling...")
//        }
//        viewModel.currentWordCount.observe(viewLifecycleOwner) {
//            binding.wordCount.text = requireContext().getString(
//                R.string.word_count, it,
//                MAX_NO_OF_WORDS
//            )
//            Log.d(GameViewModel.TAG, "observe attach with word count word is calling...")
//        }
//        viewModel.score.observe(viewLifecycleOwner) {
//            binding.score.text = getString(R.string.score, it)
//            Log.d(GameViewModel.TAG, "observe attach with score is calling...")
//        }
    }

    /*
    * Checks the user's word, and updates the score accordingly.
    * Displays the next scrambled word.
    */
    private fun onSubmitWord() {
        val playerWord = binding.textInputEditText.text.toString()
        if (viewModel.isUserWordCorrect(playerWord)) {
//            updateScoreOnScreen()
            setErrorTextField(false)
            if (viewModel.nextWord()) {
//                updateNextWordOnScreen()
//                updateCurrentWordCount()
            } else
                showFinalScoreDialog()
        } else
            setErrorTextField(true)
    }

    /*
     * Skips the current word without changing the score.
     * Increases the word count.
     */
    private fun onSkipWord() {
        if (viewModel.nextWord()) {
            setErrorTextField(false)
//            updateNextWordOnScreen()
//            updateCurrentWordCount()
        } else
            showFinalScoreDialog()
    }

    /*
     * Gets a random word for the list of words and shuffles the letters in it.
     */
//    private fun getNextScrambledWord(): String {
//        val tempWord = allWordsList.random().toCharArray()
//        tempWord.shuffle()
//        return String(tempWord)
//    }

    /*
     * Re-initializes the data in the ViewModel and updates the views with the new data, to
     * restart the game.
     */
    private fun restartGame() {
        setErrorTextField(false)
        viewModel.reinitializeData()
//        updateNextWordOnScreen()
//        updateCurrentWordCount()
//        updateScoreOnScreen()
    }

    /*
     * Exits the game.
     */
    private fun exitGame() {
        activity?.finish()
    }

    /*
    * Sets and resets the text field error status.
    */
    private fun setErrorTextField(error: Boolean) {
        if (error) {
            binding.textField.isErrorEnabled = true
            binding.textField.error = getString(R.string.try_again)
        } else {
            binding.textField.isErrorEnabled = false
            binding.textInputEditText.text = null
        }
    }

    /*
     * Displays the next scrambled word on screen.
     */
    private fun showFinalScoreDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(requireContext().getString(R.string.congratulations))
            .setMessage(requireContext().getString(R.string.you_scored, viewModel.score.value))
            .setCancelable(false)
            .setNegativeButton(R.string.exit) { _, _ -> exitGame() }
            .setPositiveButton(R.string.play_again) { _, _ -> restartGame() }.show()
    }

//    private fun updateNextWordOnScreen() {
//        binding.textViewUnscrambledWord.text = viewModel.currentScrambledWord.value
//    }

//    private fun updateCurrentWordCount() {
//        binding.wordCount.text = getString(
//            R.string.word_count, viewModel.currentWordCount,
//            MAX_NO_OF_WORDS
//        )
//    }

//    private fun updateScoreOnScreen() {
//        binding.score.text = getString(R.string.score, viewModel.score)
//    }
}
//set observer in app component, GameFragment.
//Observer bạn sẽ add sẽ quan sát những thay đổi đối với dữ liệu của ứng dụng currentScrambledWord.
//LiveData là một lớp lưu trữ dữ liệu có thể quan sát được, nhận biết được vòng đời.
//Thư viện liên kết dữ liệu cũng là một phần của thư viện Android Jetpack. Liên kết dữ liệu liên kết các thành phần giao diện người dùng trong bố cục của bạn với
//các nguồn dữ liệu trong ứng dụng của bạn bằng cách sử dụng định dạng khai báo
//In simpler terms Data binding is binding data (from code) to views + view binding (binding views to code)
//Biểu thức ràng buộc được viết trong bố cục trong các thuộc tính thuộc tính (chẳng hạn như android: text) tham chiếu đến các thuộc tính bố cục.