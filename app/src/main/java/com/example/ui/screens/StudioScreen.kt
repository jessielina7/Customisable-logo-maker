package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeShape
import com.example.data.model.ColorPalettes
import com.example.data.model.FontFamilyStyle
import com.example.data.model.IconSymbol
import com.example.data.model.LayoutType
import com.example.data.model.LetterOption
import com.example.data.model.LogoStyle
import com.example.ui.canvas.BitmapExporter
import com.example.ui.canvas.LogoCanvasView
import com.example.ui.viewmodel.LogoViewModel
import com.example.ui.viewmodel.MockupMode
import com.example.ui.viewmodel.StudioUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudioScreen(
    viewModel: LogoViewModel,
    uiState: StudioUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val draft = uiState.draftLogo

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // --- Live Logo Canvas Stage ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(12.dp),
            contentAlignment = Alignment.Center
        ) {
            val canvasBgColor = when (uiState.previewMockupMode) {
                MockupMode.CANVAS_LIGHT -> Color(0xFFF8FAFC)
                MockupMode.CANVAS_DARK -> Color(0xFF0F172A)
                MockupMode.GRID_SPECS -> Color(0xFF1E293B)
                MockupMode.MOCKUP_CARD -> Color(0xFFF1F5F9)
            }

            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = canvasBgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxSize()
                    .testTag("logo_canvas_card")
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    LogoCanvasView(
                        logo = draft,
                        overrideBgColor = canvasBgColor,
                        showGrid = uiState.previewMockupMode == MockupMode.GRID_SPECS
                    )

                    // Top Bar overlay inside canvas (Mockup Switcher)
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(12.dp)
                            .background(Color.Black.copy(alpha = 0.45f), CircleShape)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MockupMode.entries.forEach { mode ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (uiState.previewMockupMode == mode) MaterialTheme.colorScheme.primary
                                        else Color.Transparent
                                    )
                                    .clickable { viewModel.setMockupMode(mode) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = mode.displayName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- Studio Controls Panel ---
        Card(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Category Tabs
                PrimaryTabRow(selectedTabIndex = selectedTab) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Letters", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Style & Shape", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Style, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Color & Font", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("Fine Tune", fontSize = 12.sp) },
                        icon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) }
                    )
                }

                // Tab Contents
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    when (selectedTab) {
                        0 -> BrandLettersTab(viewModel, uiState)
                        1 -> StyleShapeTab(viewModel, uiState)
                        2 -> ColorFontTab(viewModel, uiState)
                        3 -> FineTuneTab(viewModel, uiState)
                    }
                }

                // Action Bar: Save, Export PNG, Share
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = { viewModel.saveCurrentLogoToCollection() },
                        modifier = Modifier.weight(1f).testTag("save_logo_btn"),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save")
                    }

                    Button(
                        onClick = { viewModel.exportLogoImage(context) },
                        modifier = Modifier.weight(1.3f).testTag("export_png_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Export PNG")
                    }

                    IconButton(
                        onClick = {
                            val uri = BitmapExporter.saveToCacheAndGetUri(context, draft)
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "image/png"
                                putExtra(Intent.EXTRA_STREAM, uri)
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share Logo"))
                        },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .testTag("share_logo_btn")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share Logo", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun BrandLettersTab(viewModel: LogoViewModel, uiState: StudioUiState) {
    val draft = uiState.draftLogo

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = draft.brandName,
            onValueChange = { viewModel.updateBrandName(it) },
            label = { Text("Brand / Company Name") },
            placeholder = { Text("e.g. Nexus Innovations") },
            modifier = Modifier.fillMaxWidth().testTag("brand_name_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        OutlinedTextField(
            value = draft.tagline,
            onValueChange = { viewModel.updateTagline(it) },
            label = { Text("Tagline (Optional)") },
            placeholder = { Text("e.g. Innovate Tomorrow") },
            modifier = Modifier.fillMaxWidth().testTag("tagline_input"),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Letter Count Option Selector
        Text(
            text = "Select How Many Letters to Use in Logo Mark",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LetterOption.entries.forEach { option ->
                val isSelected = draft.letterOption == option.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateLetterOption(option) },
                    label = { Text(option.title) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null,
                    modifier = Modifier.testTag("letter_option_${option.name.lowercase()}")
                )
            }
        }

        // Custom Letter Entry if CUSTOM option chosen
        if (draft.letterOption == LetterOption.CUSTOM.name) {
            OutlinedTextField(
                value = draft.customLettersInput,
                onValueChange = { viewModel.updateCustomLettersInput(it) },
                label = { Text("Custom Letters") },
                placeholder = { Text("e.g. N, NT, or XYZ") },
                modifier = Modifier.fillMaxWidth().testTag("custom_letters_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        // Letter Badge Extractor Pill Display
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.primary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = draft.selectedLetters,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Extracted Mark: '${draft.selectedLetters}'",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Using ${LetterOption.valueOf(draft.letterOption).description}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun StyleShapeTab(viewModel: LogoViewModel, uiState: StudioUiState) {
    val draft = uiState.draftLogo

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Logo Style
        Text("Logo Graphic Style", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LogoStyle.entries.forEach { style ->
                val isSelected = draft.logoStyle == style.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateLogoStyle(style) },
                    label = { Text(style.displayName) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        // Badge Shape
        Text("Badge Background Frame", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            BadgeShape.entries.forEach { shape ->
                val isSelected = draft.badgeShape == shape.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateBadgeShape(shape) },
                    label = { Text(shape.displayName) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        // Icon Symbol Overlay
        Text("Icon Graphic Accent", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconSymbol.entries.forEach { symbol ->
                val isSelected = draft.iconSymbol == symbol.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateIconSymbol(symbol) },
                    label = { Text(symbol.displayName) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun ColorFontTab(viewModel: LogoViewModel, uiState: StudioUiState) {
    val draft = uiState.draftLogo

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("Color Palette Presets", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ColorPalettes.presets.forEach { preset ->
                val isSelected = draft.paletteId == preset.id
                Card(
                    modifier = Modifier
                        .width(110.dp)
                        .clickable { viewModel.applyColorPalette(preset) },
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(modifier = Modifier.size(18.dp).background(parseHex(preset.primaryHex), CircleShape))
                            Box(modifier = Modifier.size(18.dp).background(parseHex(preset.secondaryHex), CircleShape))
                            Box(modifier = Modifier.size(18.dp).background(parseHex(preset.accentHex), CircleShape))
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(preset.name, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                    }
                }
            }
        }

        // Font Style
        Text("Brand Typography", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FontFamilyStyle.entries.forEach { font ->
                val isSelected = draft.fontFamilyStyle == font.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateFontFamily(font) },
                    label = { Text(font.displayName) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }

        // Layout Type
        Text("Logo Composition Layout", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            LayoutType.entries.forEach { layout ->
                val isSelected = draft.layoutType == layout.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.updateLayoutType(layout) },
                    label = { Text(layout.displayName) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun FineTuneTab(viewModel: LogoViewModel, uiState: StudioUiState) {
    val draft = uiState.draftLogo

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Text("Badge Scale (${String.format("%.1f", draft.badgeScale)}x)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Slider(
            value = draft.badgeScale,
            onValueChange = { viewModel.updateBadgeScale(it) },
            valueRange = 0.6f..1.6f,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
        )

        Text("Font Size (${draft.fontSize.toInt()}sp)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Slider(
            value = draft.fontSize,
            onValueChange = { viewModel.updateFontSize(it) },
            valueRange = 18f..48f,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
        )

        Text("Letter Spacing (${draft.letterSpacing.toInt()}dp)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        Slider(
            value = draft.letterSpacing,
            onValueChange = { viewModel.updateLetterSpacing(it) },
            valueRange = 0f..12f,
            colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary)
        )
    }
}

private fun parseHex(hex: String): Color {
    return try {
        val clean = hex.replace("#", "").trim()
        Color(clean.toLong(16) or 0xFF000000)
    } catch (_: Exception) {
        Color.Gray
    }
}
