package com.kozak.samorobkin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kozak.samorobkin.databinding.ActivityNewCharacterBinding
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

private const val CHARACTER_DATA_KEY = "CHARACTER_DATA_KEY"

class NewCharacterActivity : AppCompatActivity() {

    private lateinit var characterData: CharacterGenerator.CharacterData
    private lateinit var binding: ActivityNewCharacterBinding

    private var Bundle.characterData
        get() = getSerializable(CHARACTER_DATA_KEY) as CharacterGenerator.CharacterData
        set(value) = putSerializable(CHARACTER_DATA_KEY, value)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        characterData = savedInstanceState?.characterData ?: CharacterGenerator.generate()

        binding = ActivityNewCharacterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        displayCharacterData()

        binding.generateButton.setOnClickListener {
            val activity = this
            MainScope().launch {
                characterData = fetchCharacterData() ?: let {
                    Toast.makeText(activity, "Generated offline! Run server!", Toast.LENGTH_SHORT).show()
                    CharacterGenerator.generate()
                }
                displayCharacterData()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.characterData = characterData
    }

    private fun displayCharacterData() {
        characterData.run {
            binding.nameTextView.text = name
            binding.raceTextView.text = race
            binding.dexterityTextView.text = dex
            binding.wisdomTextView.text = wis
            binding.strengthTextView.text = str
        }
    }
}