package com.kozak.samorobkin

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL


private fun <T> List<T>.rand() = shuffled().first()
private fun Int.roll() = (0 until this)
    .sumOf { (1..6).toList().rand() }
    .toString()
private val firstName = listOf("Eli", "Alex", "Sophie")
private val lastName = listOf("Lightweaver", "Greatfoot", "Oakenfeld")

private const val CHARACTER_DATA_API = "http://10.0.2.2:8080"
private const val CONNECTION_TIMEOUT_1_SEC = 1000

object CharacterGenerator {
    data class CharacterData(val name: String,
                             val race: String,
                             val dex: String,
                             val wis: String,
                             val str: String) : Serializable

    private fun name() = "${firstName.rand()} ${lastName.rand()}"
    private fun race() = listOf("dwarf", "elf", "human", "halfling").rand()
    private fun dex() = 4.roll()
    private fun wis() = 3.roll()
    private fun str() = 5.roll()
    fun generate() = CharacterData(name = name(),
        race = race(),
        dex = dex(),
        wis = wis(),
        str = str())

    fun fromApiData(apiData: String): CharacterData {
        val (name, race, dex, wis, str) = apiData.split(",")
        return CharacterData(name, race, dex, wis, str)
    }
}

/**
 * It`s recomended to run server before invoking this function.
 */
suspend fun fetchCharacterData(): CharacterGenerator.CharacterData? {
    return withContext(Dispatchers.IO) {
        val connection = URL(CHARACTER_DATA_API).openConnection() as HttpURLConnection
        connection.connectTimeout = CONNECTION_TIMEOUT_1_SEC
        connection.readTimeout = CONNECTION_TIMEOUT_1_SEC
        try {
            val apiData = connection.inputStream.bufferedReader().use { it.readText() }
            CharacterGenerator.fromApiData(apiData)
        } catch (e: Exception) {
            null
        } finally {
            connection.disconnect()
        }
    }
}
