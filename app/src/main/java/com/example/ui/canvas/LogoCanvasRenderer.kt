package com.example.ui.canvas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.data.model.BadgeShape
import com.example.data.model.FontFamilyStyle
import com.example.data.model.IconSymbol
import com.example.data.model.LayoutType
import com.example.data.model.LogoEntity
import com.example.data.model.LogoStyle
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun LogoCanvasView(
    logo: LogoEntity,
    modifier: Modifier = Modifier,
    overrideBgColor: Color? = null,
    showGrid: Boolean = false
) {
    val textMeasurer = rememberTextMeasurer()

    val primaryColor = parseHexColor(logo.primaryColorHex, Color(0xFF6366F1))
    val secondaryColor = parseHexColor(logo.secondaryColorHex, Color(0xFFA855F7))
    val accentColor = parseHexColor(logo.accentColorHex, Color(0xFFEC4899))
    val canvasBg = overrideBgColor ?: parseHexColor(logo.canvasBgHex, Color(0xFF0F172A))

    val badgeShape = try { BadgeShape.valueOf(logo.badgeShape) } catch (_: Exception) { BadgeShape.HEXAGON }
    val logoStyle = try { LogoStyle.valueOf(logo.logoStyle) } catch (_: Exception) { LogoStyle.MODERN_MONOGRAM }
    val iconSymbol = try { IconSymbol.valueOf(logo.iconSymbol) } catch (_: Exception) { IconSymbol.NONE }
    val fontStyle = try { FontFamilyStyle.valueOf(logo.fontFamilyStyle) } catch (_: Exception) { FontFamilyStyle.SANS_SERIF_BOLD }
    val layoutType = try { LayoutType.valueOf(logo.layoutType) } catch (_: Exception) { LayoutType.STACKED }

    Canvas(modifier = modifier.aspectRatio(1f).fillMaxSize()) {
        val width = size.width
        val height = size.height
        val center = Offset(width / 2f, height / 2f)

        // 1. Background Fill & Subtle Glow
        drawRect(color = canvasBg)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(primaryColor.copy(alpha = 0.22f), Color.Transparent),
                center = center,
                radius = width * 0.65f
            )
        )

        // Optional Transparent / Design Grid Lines
        if (showGrid) {
            val gridStep = width / 12f
            for (i in 0..12) {
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(i * gridStep, 0f),
                    end = Offset(i * gridStep, height),
                    strokeWidth = 1f
                )
                drawLine(
                    color = Color.White.copy(alpha = 0.08f),
                    start = Offset(0f, i * gridStep),
                    end = Offset(width, i * gridStep),
                    strokeWidth = 1f
                )
            }
        }

        // Layout positioning calculation
        val isStacked = layoutType == LayoutType.STACKED || layoutType == LayoutType.MONOGRAM_ONLY
        val isMonogramOnly = layoutType == LayoutType.MONOGRAM_ONLY
        val isSideBySide = layoutType == LayoutType.SIDE_BY_SIDE

        val badgeCenter = when {
            isMonogramOnly -> center
            isStacked -> Offset(width / 2f, height * 0.38f)
            isSideBySide -> Offset(width * 0.30f, height / 2f)
            else -> Offset(width / 2f, height * 0.40f)
        }

        val badgeRadius = (width * 0.22f) * logo.badgeScale.coerceIn(0.5f, 1.8f)

        // 2. Draw Badge Background Geometry
        drawBadgeShape(
            shape = badgeShape,
            center = badgeCenter,
            radius = badgeRadius,
            primaryColor = primaryColor,
            secondaryColor = secondaryColor,
            accentColor = accentColor,
            logoStyle = logoStyle
        )

        // 3. Draw Icon Symbol / Graphic Accent
        if (iconSymbol != IconSymbol.NONE) {
            drawIconSymbol(
                symbol = iconSymbol,
                center = badgeCenter,
                radius = badgeRadius * 0.85f,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                accentColor = accentColor
            )
        }

        // 4. Draw Lettermark Text Inside Badge
        val letters = logo.selectedLetters.ifBlank { "L" }
        val letterFontWeight = when (fontStyle) {
            FontFamilyStyle.SERIF_ELEGANT -> FontWeight.Bold
            FontFamilyStyle.MONOSPACE_TECH -> FontWeight.SemiBold
            FontFamilyStyle.SANS_SERIF_BOLD -> FontWeight.ExtraBold
            else -> FontWeight.Bold
        }
        val fontFamily = when (fontStyle) {
            FontFamilyStyle.SERIF_ELEGANT -> FontFamily.Serif
            FontFamilyStyle.MONOSPACE_TECH -> FontFamily.Monospace
            FontFamilyStyle.CURSIVE_SCRIPT -> FontFamily.Cursive
            else -> FontFamily.Default
        }

        val letterSizeSp = (badgeRadius * 0.75f / (letters.length.coerceAtLeast(1) * 0.55f)).sp.value
            .coerceIn(16f, 96f).sp

        val letterStyle = TextStyle(
            color = if (badgeShape == BadgeShape.NONE) primaryColor else Color.White,
            fontSize = letterSizeSp,
            fontWeight = letterFontWeight,
            fontFamily = fontFamily,
            textAlign = TextAlign.Center
        )

        val measuredLetters = textMeasurer.measure(
            text = AnnotatedString(letters),
            style = letterStyle
        )

        // Draw lettermark at center of badge
        drawText(
            textLayoutResult = measuredLetters,
            topLeft = Offset(
                badgeCenter.x - measuredLetters.size.width / 2f,
                badgeCenter.y - measuredLetters.size.height / 2f
            )
        )

        // 5. Draw Brand Name & Tagline Text
        if (!isMonogramOnly) {
            val nameText = logo.brandName.ifBlank { "BRAND NAME" }
            val nameFontSize = (width * 0.055f * (logo.fontSize / 28f)).sp

            val brandNameStyle = TextStyle(
                brush = Brush.linearGradient(listOf(Color.White, Color.White.copy(alpha = 0.9f))),
                fontSize = nameFontSize,
                fontWeight = FontWeight.Bold,
                fontFamily = fontFamily,
                letterSpacing = (logo.letterSpacing * 0.5f).sp,
                textAlign = if (isSideBySide) TextAlign.Start else TextAlign.Center
            )

            val measuredName = textMeasurer.measure(
                text = AnnotatedString(nameText),
                style = brandNameStyle
            )

            val namePosition = if (isStacked) {
                Offset(width / 2f - measuredName.size.width / 2f, height * 0.68f)
            } else {
                Offset(width * 0.48f, height / 2f - measuredName.size.height / 2f)
            }

            drawText(textLayoutResult = measuredName, topLeft = namePosition)

            // Tagline
            if (logo.tagline.isNotBlank()) {
                val taglineStyle = TextStyle(
                    color = accentColor,
                    fontSize = (nameFontSize.value * 0.48f).sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Default,
                    letterSpacing = (logo.letterSpacing * 0.8f).sp,
                    textAlign = if (isSideBySide) TextAlign.Start else TextAlign.Center
                )

                val measuredTagline = textMeasurer.measure(
                    text = AnnotatedString(logo.tagline.uppercase()),
                    style = taglineStyle
                )

                val taglinePosition = if (isStacked) {
                    Offset(width / 2f - measuredTagline.size.width / 2f, namePosition.y + measuredName.size.height + height * 0.015f)
                } else {
                    Offset(width * 0.48f, namePosition.y + measuredName.size.height + height * 0.01f)
                }

                drawText(textLayoutResult = measuredTagline, topLeft = taglinePosition)
            }
        }
    }
}

private fun DrawScope.drawBadgeShape(
    shape: BadgeShape,
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color,
    logoStyle: LogoStyle
) {
    if (shape == BadgeShape.NONE) return

    val gradientBrush = Brush.linearGradient(
        colors = listOf(primaryColor, secondaryColor, accentColor),
        start = Offset(center.x - radius, center.y - radius),
        end = Offset(center.x + radius, center.y + radius)
    )

    when (shape) {
        BadgeShape.HEXAGON -> {
            val path = createPolygonPath(center, radius, 6, rotationDeg = 30f)
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = accentColor.copy(alpha = 0.8f), style = Stroke(width = 4f))
        }
        BadgeShape.CIRCLE -> {
            drawCircle(brush = gradientBrush, center = center, radius = radius)
            drawCircle(color = accentColor.copy(alpha = 0.6f), center = center, radius = radius + 6f, style = Stroke(width = 3f))
        }
        BadgeShape.SHIELD -> {
            val path = Path().apply {
                moveTo(center.x, center.y - radius * 1.1f)
                cubicTo(
                    center.x + radius * 0.8f, center.y - radius * 1.1f,
                    center.x + radius * 1.0f, center.y - radius * 0.4f,
                    center.x + radius * 0.9f, center.y + radius * 0.1f
                )
                cubicTo(
                    center.x + radius * 0.7f, center.y + radius * 0.7f,
                    center.x, center.y + radius * 1.1f,
                    center.x, center.y + radius * 1.1f
                )
                cubicTo(
                    center.x, center.y + radius * 1.1f,
                    center.x - radius * 0.7f, center.y + radius * 0.7f,
                    center.x - radius * 0.9f, center.y + radius * 0.1f
                )
                cubicTo(
                    center.x - radius * 1.0f, center.y - radius * 0.4f,
                    center.x - radius * 0.8f, center.y - radius * 1.1f,
                    center.x, center.y - radius * 1.1f
                )
                close()
            }
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = Color.White.copy(alpha = 0.4f), style = Stroke(width = 3f))
        }
        BadgeShape.RHOMBUS, BadgeShape.DIAMOND -> {
            val path = createPolygonPath(center, radius * 1.1f, 4, rotationDeg = 45f)
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = Color.White.copy(alpha = 0.5f), style = Stroke(width = 3f))
        }
        BadgeShape.OCTAGON -> {
            val path = createPolygonPath(center, radius, 8, rotationDeg = 22.5f)
            drawPath(path, brush = gradientBrush)
            drawPath(path, color = secondaryColor, style = Stroke(width = 4f))
        }
        BadgeShape.SOFT_SQUARE -> {
            val rect = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)
            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(rect.left, rect.top),
                size = Size(rect.width, rect.height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(radius * 0.35f, radius * 0.35f)
            )
        }
        BadgeShape.DOUBLE_RING -> {
            drawCircle(brush = gradientBrush, center = center, radius = radius)
            drawCircle(color = accentColor, center = center, radius = radius + 8f, style = Stroke(width = 4f))
            drawCircle(color = primaryColor, center = center, radius = radius + 16f, style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 12f))))
        }
        else -> {}
    }

    // Glow line overlay for NEON_CYBER style
    if (logoStyle == LogoStyle.NEON_CYBER) {
        drawCircle(
            color = Color.Cyan.copy(alpha = 0.9f),
            center = center,
            radius = radius * 1.08f,
            style = Stroke(width = 2f, pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 16f)))
        )
    }
}

private fun DrawScope.drawIconSymbol(
    symbol: IconSymbol,
    center: Offset,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    accentColor: Color
) {
    when (symbol) {
        IconSymbol.NODE -> {
            drawCircle(color = Color.White.copy(alpha = 0.3f), center = center, radius = radius, style = Stroke(width = 2f))
            drawCircle(color = accentColor, center = Offset(center.x - radius * 0.7f, center.y - radius * 0.5f), radius = 8f)
            drawCircle(color = primaryColor, center = Offset(center.x + radius * 0.7f, center.y - radius * 0.5f), radius = 8f)
            drawCircle(color = secondaryColor, center = Offset(center.x, center.y + radius * 0.7f), radius = 8f)
        }
        IconSymbol.SPARKLE -> {
            val sparklePath = Path().apply {
                moveTo(center.x, center.y - radius * 1.2f)
                quadraticTo(center.x, center.y, center.x + radius * 1.2f, center.y)
                quadraticTo(center.x, center.y, center.x, center.y + radius * 1.2f)
                quadraticTo(center.x, center.y, center.x - radius * 1.2f, center.y)
                quadraticTo(center.x, center.y, center.x, center.y - radius * 1.2f)
                close()
            }
            drawPath(sparklePath, color = Color.White.copy(alpha = 0.35f))
        }
        IconSymbol.CROWN -> {
            val crownPath = Path().apply {
                moveTo(center.x - radius * 0.8f, center.y - radius * 0.8f)
                lineTo(center.x - radius * 0.4f, center.y - radius * 0.3f)
                lineTo(center.x, center.y - radius * 0.9f)
                lineTo(center.x + radius * 0.4f, center.y - radius * 0.3f)
                lineTo(center.x + radius * 0.8f, center.y - radius * 0.8f)
                lineTo(center.x + radius * 0.6f, center.y - radius * 0.1f)
                lineTo(center.x - radius * 0.6f, center.y - radius * 0.1f)
                close()
            }
            drawPath(crownPath, color = Color.White.copy(alpha = 0.45f))
        }
        IconSymbol.ORBIT -> {
            rotate(45f, pivot = center) {
                drawOval(
                    color = Color.White.copy(alpha = 0.3f),
                    topLeft = Offset(center.x - radius * 1.1f, center.y - radius * 0.4f),
                    size = Size(radius * 2.2f, radius * 0.8f),
                    style = Stroke(width = 3f)
                )
            }
        }
        else -> {}
    }
}

private fun createPolygonPath(center: Offset, radius: Float, sides: Int, rotationDeg: Float = 0f): Path {
    val path = Path()
    val angleStep = (2 * PI / sides).toFloat()
    val rotationRad = (rotationDeg * PI / 180f).toFloat()

    for (i in 0 until sides) {
        val angle = i * angleStep - (PI / 2).toFloat() + rotationRad
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    return path
}

private fun parseHexColor(hexString: String, defaultColor: Color): Color {
    return try {
        val cleanHex = hexString.replace("#", "").trim()
        val colorInt = when (cleanHex.length) {
            6 -> "FF$cleanHex".toLong(16).toInt()
            8 -> cleanHex.toLong(16).toInt()
            else -> return defaultColor
        }
        Color(colorInt)
    } catch (_: Exception) {
        defaultColor
    }
}
