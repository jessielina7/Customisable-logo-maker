package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeShape
import com.example.data.model.FontFamilyStyle
import com.example.data.model.IconSymbol
import com.example.data.model.LayoutType
import com.example.data.model.LetterOption
import com.example.data.model.LogoEntity
import com.example.data.model.LogoStyle
import com.example.ui.canvas.LogoCanvasView
import com.example.ui.viewmodel.LogoViewModel

data class TemplateItem(
    val category: String,
    val title: String,
    val logo: LogoEntity
)

object PresetTemplates {
    val items = listOf(
        TemplateItem(
            category = "Tech Startup",
            title = "Apex Nexus",
            logo = LogoEntity(
                brandName = "Apex Nexus",
                tagline = "Future Computing",
                selectedLetters = "AN",
                letterOption = LetterOption.INITIALS.name,
                logoStyle = LogoStyle.NEON_CYBER.name,
                badgeShape = BadgeShape.HEXAGON.name,
                iconSymbol = IconSymbol.NODE.name,
                paletteId = "INDIGO_PURPLE",
                primaryColorHex = "#6366F1",
                secondaryColorHex = "#A855F7",
                accentColorHex = "#06B6D4",
                canvasBgHex = "#0F172A",
                fontFamilyStyle = FontFamilyStyle.SANS_SERIF_BOLD.name,
                layoutType = LayoutType.STACKED.name
            )
        ),
        TemplateItem(
            category = "Luxury Fashion",
            title = "Aura Atelier",
            logo = LogoEntity(
                brandName = "Aura Atelier",
                tagline = "Haute Couture",
                selectedLetters = "A",
                letterOption = LetterOption.FIRST_1.name,
                logoStyle = LogoStyle.MINIMAL_LUXURY.name,
                badgeShape = BadgeShape.DIAMOND.name,
                iconSymbol = IconSymbol.SPARKLE.name,
                paletteId = "MIDNIGHT_GOLD",
                primaryColorHex = "#F59E0B",
                secondaryColorHex = "#D97706",
                accentColorHex = "#F3F4F6",
                canvasBgHex = "#111827",
                fontFamilyStyle = FontFamilyStyle.SERIF_ELEGANT.name,
                layoutType = LayoutType.STACKED.name
            )
        ),
        TemplateItem(
            category = "Cyber Gaming",
            title = "Cyber Pulse",
            logo = LogoEntity(
                brandName = "Cyber Pulse",
                tagline = "Pro Esports Guild",
                selectedLetters = "CP",
                letterOption = LetterOption.INITIALS.name,
                logoStyle = LogoStyle.NEON_CYBER.name,
                badgeShape = BadgeShape.OCTAGON.name,
                iconSymbol = IconSymbol.NODE.name,
                paletteId = "CYBER_NEON",
                primaryColorHex = "#06B6D4",
                secondaryColorHex = "#3B82F6",
                accentColorHex = "#10B981",
                canvasBgHex = "#020617",
                fontFamilyStyle = FontFamilyStyle.MONOSPACE_TECH.name,
                layoutType = LayoutType.STACKED.name
            )
        ),
        TemplateItem(
            category = "Artisan Cafe",
            title = "Velvet Roasters",
            logo = LogoEntity(
                brandName = "Velvet Roasters",
                tagline = "Est. 2024",
                selectedLetters = "VR",
                letterOption = LetterOption.INITIALS.name,
                logoStyle = LogoStyle.VINTAGE_BADGE.name,
                badgeShape = BadgeShape.DOUBLE_RING.name,
                iconSymbol = IconSymbol.CROWN.name,
                paletteId = "MIDNIGHT_GOLD",
                primaryColorHex = "#D97706",
                secondaryColorHex = "#B45309",
                accentColorHex = "#FBBF24",
                canvasBgHex = "#18181B",
                fontFamilyStyle = FontFamilyStyle.SERIF_ELEGANT.name,
                layoutType = LayoutType.STACKED.name
            )
        ),
        TemplateItem(
            category = "Eco Organic",
            title = "Verdant Bio",
            logo = LogoEntity(
                brandName = "Verdant Bio",
                tagline = "Sustainable Living",
                selectedLetters = "VB",
                letterOption = LetterOption.INITIALS.name,
                logoStyle = LogoStyle.MODERN_MONOGRAM.name,
                badgeShape = BadgeShape.SHIELD.name,
                iconSymbol = IconSymbol.LEAF.name,
                paletteId = "EMERALD_MINT",
                primaryColorHex = "#10B981",
                secondaryColorHex = "#059669",
                accentColorHex = "#34D399",
                canvasBgHex = "#064E3B",
                fontFamilyStyle = FontFamilyStyle.SANS_SERIF_BOLD.name,
                layoutType = LayoutType.STACKED.name
            )
        ),
        TemplateItem(
            category = "Fitness Pro",
            title = "Iron Forge",
            logo = LogoEntity(
                brandName = "Iron Forge",
                tagline = "Unstoppable Strength",
                selectedLetters = "IF",
                letterOption = LetterOption.INITIALS.name,
                logoStyle = LogoStyle.MODERN_MONOGRAM.name,
                badgeShape = BadgeShape.RHOMBUS.name,
                iconSymbol = IconSymbol.FLAME.name,
                paletteId = "SUNSET_ROSE",
                primaryColorHex = "#F43F5E",
                secondaryColorHex = "#FB7185",
                accentColorHex = "#FBBF24",
                canvasBgHex = "#18181B",
                fontFamilyStyle = FontFamilyStyle.SANS_SERIF_BOLD.name,
                layoutType = LayoutType.STACKED.name
            )
        )
    )
}

@Composable
fun TemplatesScreen(
    viewModel: LogoViewModel,
    onTemplateSelected: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = "Curated Logo Templates",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tap any style template to instant load into the studio",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(PresetTemplates.items) { item ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.loadLogoForEditing(item.logo)
                            onTemplateSelected()
                        }
                        .testTag("template_card_${item.title.lowercase().replace(" ", "_")}")
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        ) {
                            LogoCanvasView(logo = item.logo)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = item.category,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.title,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Icon(
                                Icons.Default.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.padding(4.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}
