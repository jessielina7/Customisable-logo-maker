package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.BadgeShape
import com.example.data.model.IconSymbol
import com.example.data.model.LogoStyle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiBrandingSuggestion(
    val brandName: String,
    val tagline: String,
    val suggestedLetters: String,
    val logoStyle: String,
    val badgeShape: String,
    val iconSymbol: String,
    val primaryColorHex: String,
    val secondaryColorHex: String,
    val accentColorHex: String,
    val rationale: String
)

class GeminiBrandingRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateBrandConcept(industryOrKeyword: String): Result<AiBrandingSuggestion> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig::class.java.getField("GEMINI_API_KEY").get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(createLocalFallbackConcept(industryOrKeyword))
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"
            val promptText = """
                You are a graphic designer.
                Given industry: "$industryOrKeyword", generate 1 logo concept JSON:
                {
                  "brandName": "Nexus AI",
                  "tagline": "Next-Gen Intelligence",
                  "suggestedLetters": "NX",
                  "logoStyle": "MODERN_MONOGRAM",
                  "badgeShape": "HEXAGON",
                  "iconSymbol": "NODE",
                  "primaryColorHex": "#6366F1",
                  "secondaryColorHex": "#A855F7",
                  "accentColorHex": "#EC4899",
                  "rationale": "High-tech geometric hexagon mark with vibrant gradient."
                }
                Valid logoStyle options: MODERN_MONOGRAM, ABSTRACT_ICON, GEOMETRIC_FRAME, MINIMAL_LUXURY, NEON_CYBER, VINTAGE_BADGE
                Valid badgeShape options: HEXAGON, CIRCLE, SHIELD, RHOMBUS, OCTAGON, DIAMOND, SOFT_SQUARE, NONE
                Valid iconSymbol options: NODE, SPARKLE, CROWN, FLAME, INFINITY, ORBIT, CODE, CUBE, LEAF, WINGS, NONE
            """.trimIndent()

            val partsArray = JSONArray().apply {
                put(JSONObject().apply { put("text", promptText) })
            }
            val contentObj = JSONObject().apply {
                put("parts", partsArray)
            }
            val contentsArray = JSONArray().apply {
                put(contentObj)
            }
            val genConfig = JSONObject().apply {
                put("responseMimeType", "application/json")
                put("temperature", 0.7)
            }

            val requestJson = JSONObject().apply {
                put("contents", contentsArray)
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful || responseBody.isBlank()) {
                Log.w("GeminiBranding", "API failed ${response.code}: $responseBody")
                return@withContext Result.success(createLocalFallbackConcept(industryOrKeyword))
            }

            val root = JSONObject(responseBody)
            val candidates = root.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val rawText = parts?.optJSONObject(0)?.optString("text") ?: ""

            val parsedJson = JSONObject(rawText)
            val suggestion = AiBrandingSuggestion(
                brandName = parsedJson.optString("brandName", "Nexus"),
                tagline = parsedJson.optString("tagline", "Innovate"),
                suggestedLetters = parsedJson.optString("suggestedLetters", "N"),
                logoStyle = parsedJson.optString("logoStyle", "MODERN_MONOGRAM"),
                badgeShape = parsedJson.optString("badgeShape", "HEXAGON"),
                iconSymbol = parsedJson.optString("iconSymbol", "NONE"),
                primaryColorHex = parsedJson.optString("primaryColorHex", "#6366F1"),
                secondaryColorHex = parsedJson.optString("secondaryColorHex", "#A855F7"),
                accentColorHex = parsedJson.optString("accentColorHex", "#EC4899"),
                rationale = parsedJson.optString("rationale", "AI generated logo design.")
            )

            Result.success(suggestion)
        } catch (e: Exception) {
            Log.e("GeminiBranding", "Gemini API error, using fallback", e)
            Result.success(createLocalFallbackConcept(industryOrKeyword))
        }
    }

    private fun createLocalFallbackConcept(keyword: String): AiBrandingSuggestion {
        val cleanKeyword = keyword.trim().ifBlank { "Apex Innovations" }
        val words = cleanKeyword.split(" ")
        val name = if (words.size == 1) "$cleanKeyword Studio" else cleanKeyword
        val letters = if (words.size > 1) {
            "${words[0].firstOrNull()?.uppercase() ?: "A"}${words[1].firstOrNull()?.uppercase() ?: "B"}"
        } else {
            name.take(2).uppercase()
        }

        val style = when {
            keyword.contains("tech", true) || keyword.contains("cyber", true) -> LogoStyle.NEON_CYBER
            keyword.contains("luxury", true) || keyword.contains("fashion", true) -> LogoStyle.MINIMAL_LUXURY
            keyword.contains("coffee", true) || keyword.contains("vintage", true) -> LogoStyle.VINTAGE_BADGE
            else -> LogoStyle.MODERN_MONOGRAM
        }

        val shape = when (style) {
            LogoStyle.NEON_CYBER -> BadgeShape.HEXAGON
            LogoStyle.MINIMAL_LUXURY -> BadgeShape.DIAMOND
            LogoStyle.VINTAGE_BADGE -> BadgeShape.CIRCLE
            else -> BadgeShape.SHIELD
        }

        val icon = when (style) {
            LogoStyle.NEON_CYBER -> IconSymbol.NODE
            LogoStyle.MINIMAL_LUXURY -> IconSymbol.SPARKLE
            LogoStyle.VINTAGE_BADGE -> IconSymbol.CROWN
            else -> IconSymbol.INFINITY
        }

        return AiBrandingSuggestion(
            brandName = name,
            tagline = "Elevate Your Digital Presence",
            suggestedLetters = letters,
            logoStyle = style.name,
            badgeShape = shape.name,
            iconSymbol = icon.name,
            primaryColorHex = "#6366F1",
            secondaryColorHex = "#A855F7",
            accentColorHex = "#06B6D4",
            rationale = "Harmonized $letters mark featuring geometric framing and dynamic gradient depth tailored for $name."
        )
    }
}
