package com.example.data.model

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

enum class LetterOption(val title: String, val description: String) {
    FIRST_1("1 Letter", "First letter of brand name"),
    FIRST_2("2 Letters", "First two letters of brand name"),
    FIRST_3("3 Letters", "First three letters of brand name"),
    INITIALS("Initials", "First letter of each word"),
    ALL("Full Word", "All characters from brand name"),
    CUSTOM("Custom Pick", "Specify exact custom letters")
}

enum class LogoStyle(val displayName: String, val category: String) {
    MODERN_MONOGRAM("Modern Monogram", "Monogram"),
    ABSTRACT_ICON("Abstract Graphic", "Icon & Text"),
    GEOMETRIC_FRAME("Geometric Frame", "Emblem"),
    MINIMAL_LUXURY("Minimal Luxury", "Fashion & Fine"),
    NEON_CYBER("Cyber Tech", "Futuristic"),
    VINTAGE_BADGE("Vintage Stamp", "Classic Badge")
}

enum class BadgeShape(val displayName: String) {
    HEXAGON("Hexagon"),
    CIRCLE("Circle"),
    SHIELD("Shield"),
    RHOMBUS("Rhombus"),
    OCTAGON("Octagon"),
    DIAMOND("Diamond"),
    SOFT_SQUARE("Soft Square"),
    DOUBLE_RING("Double Ring"),
    NONE("No Shape (Clean)")
}

enum class IconSymbol(val displayName: String) {
    NODE("Tech Node"),
    SPARKLE("Sparkle Star"),
    CROWN("Crown Crest"),
    FLAME("Ignite Flame"),
    INFINITY("Infinity Loop"),
    ORBIT("Atomic Orbit"),
    CODE("Code Brackets"),
    CUBE("3D Cube"),
    LEAF("Eco Leaf"),
    WINGS("Wings Crest"),
    NONE("Lettermark Only")
}

enum class FontFamilyStyle(val displayName: String) {
    SANS_SERIF_BOLD("Modern Bold Sans"),
    SERIF_ELEGANT("Luxury Editorial Serif"),
    MONOSPACE_TECH("Cyber Monospace"),
    CURSIVE_SCRIPT("Handcrafted Script"),
    GEOMETRIC_HEAVY("Geometric Heavy")
}

enum class LayoutType(val displayName: String) {
    STACKED("Stacked (Badge Top + Text Below)"),
    MONOGRAM_ONLY("Monogram Badge Only"),
    SIDE_BY_SIDE("Horizontal (Icon Left + Text Right)"),
    BADGE_INSIDE("Emblem Frame (Text Inside Frame)")
}

data class ColorPalettePreset(
    val id: String,
    val name: String,
    val primaryHex: String,
    val secondaryHex: String,
    val accentHex: String,
    val bgHex: String
)

object ColorPalettes {
    val presets = listOf(
        ColorPalettePreset("INDIGO_PURPLE", "Indigo Purple", "#6366F1", "#A855F7", "#EC4899", "#0F172A"),
        ColorPalettePreset("CYBER_NEON", "Cyber Neon", "#06B6D4", "#3B82F6", "#10B981", "#020617"),
        ColorPalettePreset("MIDNIGHT_GOLD", "Midnight Gold", "#F59E0B", "#D97706", "#F3F4F6", "#111827"),
        ColorPalettePreset("SUNSET_ROSE", "Sunset Rose", "#F43F5E", "#FB7185", "#FBBF24", "#18181B"),
        ColorPalettePreset("EMERALD_MINT", "Emerald Mint", "#10B981", "#059669", "#34D399", "#064E3B"),
        ColorPalettePreset("NORDIC_SLATE", "Nordic Minimal", "#475569", "#0F172A", "#64748B", "#F8FAFC"),
        ColorPalettePreset("CHERRY_BLOSSOM", "Sakura Pink", "#EC4899", "#F472B6", "#A855F7", "#2E1065"),
        ColorPalettePreset("ROYAL_AZURE", "Royal Azure", "#2563EB", "#1D4ED8", "#60A5FA", "#0F172A")
    )

    fun getById(id: String): ColorPalettePreset {
        return presets.find { it.id == id } ?: presets.first()
    }
}

@Entity(tableName = "saved_logos")
data class LogoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val brandName: String,
    val tagline: String = "",
    val selectedLetters: String,
    val letterOption: String = LetterOption.FIRST_1.name,
    val customLettersInput: String = "",
    val logoStyle: String = LogoStyle.MODERN_MONOGRAM.name,
    val badgeShape: String = BadgeShape.HEXAGON.name,
    val iconSymbol: String = IconSymbol.NONE.name,
    val paletteId: String = "INDIGO_PURPLE",
    val primaryColorHex: String = "#6366F1",
    val secondaryColorHex: String = "#A855F7",
    val accentColorHex: String = "#EC4899",
    val canvasBgHex: String = "#0F172A",
    val fontFamilyStyle: String = FontFamilyStyle.SANS_SERIF_BOLD.name,
    val layoutType: String = LayoutType.STACKED.name,
    val fontSize: Float = 28f,
    val badgeScale: Float = 1.0f,
    val letterSpacing: Float = 4.0f,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
