package com.mohamedabdelazeim.islamicapp.data

import android.content.Context
import com.google.gson.Gson

object ZekrData {

    fun loadMorningAdhkar(ctx: Context): List<AdhkarItem> = loadFromAsset(ctx, "morning_adhkar.json")

    fun loadEveningAdhkar(ctx: Context): List<AdhkarItem> = loadFromAsset(ctx, "evening_adhkar.json")

    fun loadAllAdhkar(ctx: Context): List<AdhkarItem> =
        loadMorningAdhkar(ctx) + loadEveningAdhkar(ctx)

    private fun loadFromAsset(ctx: Context, fileName: String): List<AdhkarItem> {
        return try {
            val json = ctx.assets.open(fileName).bufferedReader().use { it.readText() }
            val file = Gson().fromJson(json, AdhkarFile::class.java)
            file.adhkar
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
