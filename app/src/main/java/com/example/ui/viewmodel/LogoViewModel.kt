package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.BadgeShape
import com.example.data.model.ColorPalettePreset
import com.example.data.model.ColorPalettes
import com.example.data.model.FontFamilyStyle
import com.example.data.model.IconSymbol
import com.example.data.model.LayoutType
import com.example.data.model.LetterOption
import com.example.data.model.LogoEntity
import com.example.data.model.LogoStyle
import com.example.data.repository.AiBrandingSuggestion
import com.example.data.repository.GeminiBrandingRepository
import com.example.data.repository.LogoRepository
import com.example.ui.canvas.BitmapExporter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudioUiState(
    val draftLogo: LogoEntity = LogoEntity(
        brandName = "Nexus",
        tagline = "Innovate Tomorrow",
        selectedLetters = "N",
        letterOption = LetterOption.FIRST_1.name,
        customLettersInput = "",
        logoStyle = LogoStyle.MODERN_MONOGRAM.name,
        badgeShape = BadgeShape.HEXAGON.name,
        iconSymbol = IconSymbol.NONE.name,
        paletteId = "INDIGO_PURPLE",
        primaryColorHex = "#6366F1",
        secondaryColorHex = "#A855F7",
        accentColorHex = "#EC4899",
        canvasBgHex = "#0F172A",
        fontFamilyStyle = FontFamilyStyle.SANS_SERIF_BOLD.name,
        layoutType = LayoutType.STACKED.name,
        fontSize = 28f,
        badgeScale = 1.0f,
        letterSpacing = 4.0f
    ),
    val previewMockupMode: MockupMode = MockupMode.CANVAS_LIGHT,
    val isGeneratingAiConcept: Boolean = false,
    val aiSuggestion: AiBrandingSuggestion? = null,
    val aiKeywordInput: String = "Tech Startup",
    val toastMessage: String? = null
)

enum class MockupMode(val displayName: String) {
    CANVAS_DARK("Dark Studio"),
    CANVAS_LIGHT("Clean Light"),
    GRID_SPECS("Design Grid"),
    MOCKUP_CARD("Business Card")
}

class LogoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LogoRepository
    private val geminiRepository = GeminiBrandingRepository()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LogoRepository(database.logoDao())
    }

    val savedLogos: StateFlow<List<LogoEntity>> = repository.allLogos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteLogos: StateFlow<List<LogoEntity>> = repository.favoriteLogos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _uiState = MutableStateFlow(StudioUiState())
    val uiState: StateFlow<StudioUiState> = _uiState.asStateFlow()

    fun updateBrandName(name: String) {
        _uiState.update { state ->
            val updatedLogo = state.draftLogo.copy(brandName = name)
            val computedLetters = computeLetters(updatedLogo)
            state.copy(draftLogo = updatedLogo.copy(selectedLetters = computedLetters))
        }
    }

    fun updateTagline(tagline: String) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(tagline = tagline))
        }
    }

    fun updateLetterOption(option: LetterOption) {
        _uiState.update { state ->
            val updatedLogo = state.draftLogo.copy(letterOption = option.name)
            val computed = computeLetters(updatedLogo)
            state.copy(draftLogo = updatedLogo.copy(selectedLetters = computed))
        }
    }

    fun updateCustomLettersInput(custom: String) {
        _uiState.update { state ->
            val updatedLogo = state.draftLogo.copy(customLettersInput = custom)
            val computed = computeLetters(updatedLogo)
            state.copy(draftLogo = updatedLogo.copy(selectedLetters = computed))
        }
    }

    fun updateLogoStyle(style: LogoStyle) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(logoStyle = style.name))
        }
    }

    fun updateBadgeShape(shape: BadgeShape) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(badgeShape = shape.name))
        }
    }

    fun updateIconSymbol(symbol: IconSymbol) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(iconSymbol = symbol.name))
        }
    }

    fun applyColorPalette(preset: ColorPalettePreset) {
        _uiState.update { state ->
            state.copy(
                draftLogo = state.draftLogo.copy(
                    paletteId = preset.id,
                    primaryColorHex = preset.primaryHex,
                    secondaryColorHex = preset.secondaryHex,
                    accentColorHex = preset.accentHex,
                    canvasBgHex = preset.bgHex
                )
            )
        }
    }

    fun updateCustomColors(primary: String, secondary: String, accent: String) {
        _uiState.update { state ->
            state.copy(
                draftLogo = state.draftLogo.copy(
                    paletteId = "CUSTOM",
                    primaryColorHex = primary,
                    secondaryColorHex = secondary,
                    accentColorHex = accent
                )
            )
        }
    }

    fun updateFontFamily(fontStyle: FontFamilyStyle) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(fontFamilyStyle = fontStyle.name))
        }
    }

    fun updateLayoutType(layout: LayoutType) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(layoutType = layout.name))
        }
    }

    fun updateBadgeScale(scale: Float) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(badgeScale = scale))
        }
    }

    fun updateFontSize(fontSize: Float) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(fontSize = fontSize))
        }
    }

    fun updateLetterSpacing(spacing: Float) {
        _uiState.update { state ->
            state.copy(draftLogo = state.draftLogo.copy(letterSpacing = spacing))
        }
    }

    fun setMockupMode(mode: MockupMode) {
        _uiState.update { it.copy(previewMockupMode = mode) }
    }

    fun updateAiKeywordInput(keyword: String) {
        _uiState.update { it.copy(aiKeywordInput = keyword) }
    }

    fun loadLogoForEditing(logo: LogoEntity) {
        _uiState.update { it.copy(draftLogo = logo) }
    }

    fun saveCurrentLogoToCollection() {
        viewModelScope.launch {
            val logoToSave = uiState.value.draftLogo.copy(id = 0, createdAt = System.currentTimeMillis())
            repository.saveLogo(logoToSave)
            showToast("Saved to collection!")
        }
    }

    fun deleteSavedLogo(id: Int) {
        viewModelScope.launch {
            repository.deleteLogo(id)
            showToast("Logo deleted.")
        }
    }

    fun toggleFavoriteLogo(logo: LogoEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(logo.id, !logo.isFavorite)
        }
    }

    fun exportLogoImage(context: Context) {
        viewModelScope.launch {
            val uri = BitmapExporter.saveToGallery(context, uiState.value.draftLogo)
            if (uri != null) {
                showToast("Logo exported to Gallery (Pictures/LogoGenerator)")
            } else {
                showToast("Export failed. Please check storage permissions.")
            }
        }
    }

    fun generateAiBrandingConcept() {
        val keyword = uiState.value.aiKeywordInput.ifBlank { "Tech Startup" }
        _uiState.update { it.copy(isGeneratingAiConcept = true) }

        viewModelScope.launch {
            val result = geminiRepository.generateBrandConcept(keyword)
            result.onSuccess { suggestion ->
                _uiState.update {
                    it.copy(
                        isGeneratingAiConcept = false,
                        aiSuggestion = suggestion
                    )
                }
            }.onFailure {
                _uiState.update { state ->
                    state.copy(isGeneratingAiConcept = false)
                }
                showToast("Failed to generate concept.")
            }
        }
    }

    fun applyAiSuggestionToStudio() {
        val suggestion = uiState.value.aiSuggestion ?: return
        _uiState.update { state ->
            val updated = state.draftLogo.copy(
                brandName = suggestion.brandName,
                tagline = suggestion.tagline,
                selectedLetters = suggestion.suggestedLetters.ifBlank { suggestion.brandName.take(1) },
                logoStyle = suggestion.logoStyle,
                badgeShape = suggestion.badgeShape,
                iconSymbol = suggestion.iconSymbol,
                primaryColorHex = suggestion.primaryColorHex,
                secondaryColorHex = suggestion.secondaryColorHex,
                accentColorHex = suggestion.accentColorHex
            )
            state.copy(draftLogo = updated)
        }
        showToast("Applied AI Brand Concept to Studio!")
    }

    private fun computeLetters(logo: LogoEntity): String {
        val name = logo.brandName.trim()
        val option = try { LetterOption.valueOf(logo.letterOption) } catch (_: Exception) { LetterOption.FIRST_1 }

        if (name.isBlank() && option != LetterOption.CUSTOM) return "L"

        return when (option) {
            LetterOption.FIRST_1 -> name.take(1).uppercase()
            LetterOption.FIRST_2 -> name.take(2).uppercase()
            LetterOption.FIRST_3 -> name.take(3).uppercase()
            LetterOption.INITIALS -> {
                val words = name.split("\\s+".toRegex()).filter { it.isNotBlank() }
                if (words.size > 1) {
                    words.map { it.first().uppercaseChar() }.joinToString("")
                } else {
                    name.take(2).uppercase()
                }
            }
            LetterOption.ALL -> name.uppercase()
            LetterOption.CUSTOM -> logo.customLettersInput.ifBlank { name.take(1).uppercase() }
        }
    }

    private fun showToast(msg: String) {
        _uiState.update { it.copy(toastMessage = msg) }
    }

    fun clearToast() {
        _uiState.update { it.copy(toastMessage = null) }
    }
}
