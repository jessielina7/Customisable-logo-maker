package com.example.ui.canvas

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.example.data.model.BadgeShape
import com.example.data.model.FontFamilyStyle
import com.example.data.model.IconSymbol
import com.example.data.model.LayoutType
import com.example.data.model.LogoEntity
import com.example.data.model.LogoStyle
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object BitmapExporter {

    fun generateHighResBitmap(logo: LogoEntity, size: Int = 1024): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val width = size.toFloat()
        val height = size.toFloat()
        val center = android.graphics.PointF(width / 2f, height / 2f)

        val primaryInt = parseColorHex(logo.primaryColorHex, 0xFF6366F1.toInt())
        val secondaryInt = parseColorHex(logo.secondaryColorHex, 0xFFA855F7.toInt())
        val accentInt = parseColorHex(logo.accentColorHex, 0xFFEC4899.toInt())
        val bgInt = parseColorHex(logo.canvasBgHex, 0xFF0F172A.toInt())

        val badgeShape = try { BadgeShape.valueOf(logo.badgeShape) } catch (_: Exception) { BadgeShape.HEXAGON }
        val fontStyle = try { FontFamilyStyle.valueOf(logo.fontFamilyStyle) } catch (_: Exception) { FontFamilyStyle.SANS_SERIF_BOLD }
        val layoutType = try { LayoutType.valueOf(logo.layoutType) } catch (_: Exception) { LayoutType.STACKED }

        // 1. Fill Background
        val bgPaint = Paint().apply {
            color = bgInt
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width, height, bgPaint)

        // Subtle Radial Background Glow
        val radialGlow = android.graphics.RadialGradient(
            center.x, center.y, width * 0.65f,
            intColorWithAlpha(primaryInt, 0.25f),
            0x00000000,
            Shader.TileMode.CLAMP
        )
        val glowPaint = Paint().apply { shader = radialGlow }
        canvas.drawCircle(center.x, center.y, width * 0.65f, glowPaint)

        val isMonogramOnly = layoutType == LayoutType.MONOGRAM_ONLY
        val isStacked = layoutType == LayoutType.STACKED || isMonogramOnly
        val isSideBySide = layoutType == LayoutType.SIDE_BY_SIDE

        val badgeCenter = when {
            isMonogramOnly -> center
            isStacked -> android.graphics.PointF(width / 2f, height * 0.38f)
            isSideBySide -> android.graphics.PointF(width * 0.30f, height / 2f)
            else -> android.graphics.PointF(width / 2f, height * 0.40f)
        }

        val badgeRadius = (width * 0.22f) * logo.badgeScale.coerceIn(0.5f, 1.8f)

        // 2. Draw Badge Geometry
        if (badgeShape != BadgeShape.NONE) {
            val badgePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.FILL
                shader = LinearGradient(
                    badgeCenter.x - badgeRadius, badgeCenter.y - badgeRadius,
                    badgeCenter.x + badgeRadius, badgeCenter.y + badgeRadius,
                    primaryInt, secondaryInt, Shader.TileMode.CLAMP
                )
            }

            val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = width * 0.008f
                color = accentInt
            }

            when (badgeShape) {
                BadgeShape.HEXAGON -> {
                    val path = createPolygonPath(badgeCenter, badgeRadius, 6, 30f)
                    canvas.drawPath(path, badgePaint)
                    canvas.drawPath(path, strokePaint)
                }
                BadgeShape.CIRCLE -> {
                    canvas.drawCircle(badgeCenter.x, badgeCenter.y, badgeRadius, badgePaint)
                    canvas.drawCircle(badgeCenter.x, badgeCenter.y, badgeRadius + 8f, strokePaint)
                }
                BadgeShape.SHIELD -> {
                    val path = Path().apply {
                        moveTo(badgeCenter.x, badgeCenter.y - badgeRadius * 1.1f)
                        cubicTo(
                            badgeCenter.x + badgeRadius * 0.8f, badgeCenter.y - badgeRadius * 1.1f,
                            badgeCenter.x + badgeRadius * 1.0f, badgeCenter.y - badgeRadius * 0.4f,
                            badgeCenter.x + badgeRadius * 0.9f, badgeCenter.y + badgeRadius * 0.1f
                        )
                        cubicTo(
                            badgeCenter.x + badgeRadius * 0.7f, badgeCenter.y + badgeRadius * 0.7f,
                            badgeCenter.x, badgeCenter.y + badgeRadius * 1.1f,
                            badgeCenter.x, badgeCenter.y + badgeRadius * 1.1f
                        )
                        cubicTo(
                            badgeCenter.x, badgeCenter.y + badgeRadius * 1.1f,
                            badgeCenter.x - badgeRadius * 0.7f, badgeCenter.y + badgeRadius * 0.7f,
                            badgeCenter.x - badgeRadius * 0.9f, badgeCenter.y + badgeRadius * 0.1f
                        )
                        cubicTo(
                            badgeCenter.x - badgeRadius * 1.0f, badgeCenter.y - badgeRadius * 0.4f,
                            badgeCenter.x - badgeRadius * 0.8f, badgeCenter.y - badgeRadius * 1.1f,
                            badgeCenter.x, badgeCenter.y - badgeRadius * 1.1f
                        )
                        close()
                    }
                    canvas.drawPath(path, badgePaint)
                    canvas.drawPath(path, strokePaint)
                }
                BadgeShape.RHOMBUS, BadgeShape.DIAMOND -> {
                    val path = createPolygonPath(badgeCenter, badgeRadius * 1.1f, 4, 45f)
                    canvas.drawPath(path, badgePaint)
                    canvas.drawPath(path, strokePaint)
                }
                BadgeShape.OCTAGON -> {
                    val path = createPolygonPath(badgeCenter, badgeRadius, 8, 22.5f)
                    canvas.drawPath(path, badgePaint)
                    canvas.drawPath(path, strokePaint)
                }
                BadgeShape.SOFT_SQUARE -> {
                    val rect = RectF(
                        badgeCenter.x - badgeRadius, badgeCenter.y - badgeRadius,
                        badgeCenter.x + badgeRadius, badgeCenter.y + badgeRadius
                    )
                    canvas.drawRoundRect(rect, badgeRadius * 0.35f, badgeRadius * 0.35f, badgePaint)
                }
                BadgeShape.DOUBLE_RING -> {
                    canvas.drawCircle(badgeCenter.x, badgeCenter.y, badgeRadius, badgePaint)
                    canvas.drawCircle(badgeCenter.x, badgeCenter.y, badgeRadius + 12f, strokePaint)
                }
                else -> {}
            }
        }

        // 3. Draw Lettermark Text Inside Badge
        val letters = logo.selectedLetters.ifBlank { "L" }
        val typeface = when (fontStyle) {
            FontFamilyStyle.SERIF_ELEGANT -> Typeface.SERIF
            FontFamilyStyle.MONOSPACE_TECH -> Typeface.MONOSPACE
            else -> Typeface.DEFAULT_BOLD
        }

        val letterTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = if (badgeShape == BadgeShape.NONE) primaryInt else 0xFFFFFFFF.toInt()
            textSize = (badgeRadius * 0.75f / (letters.length.coerceAtLeast(1) * 0.55f)).coerceIn(24f, 200f)
            textAlign = Paint.Align.CENTER
            this.typeface = typeface
        }

        val fontMetrics = letterTextPaint.fontMetrics
        val textY = badgeCenter.y - (fontMetrics.ascent + fontMetrics.descent) / 2f
        canvas.drawText(letters, badgeCenter.x, textY, letterTextPaint)

        // 4. Draw Brand Name & Tagline Text
        if (!isMonogramOnly) {
            val nameText = logo.brandName.ifBlank { "BRAND NAME" }
            val nameTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0xFFFFFFFF.toInt()
                textSize = width * 0.055f * (logo.fontSize / 28f)
                this.typeface = typeface
                textAlign = if (isSideBySide) Paint.Align.LEFT else Paint.Align.CENTER
            }

            val nameY = if (isStacked) height * 0.68f else height / 2f
            val nameX = if (isStacked) width / 2f else width * 0.48f

            canvas.drawText(nameText, nameX, nameY, nameTextPaint)

            if (logo.tagline.isNotBlank()) {
                val taglineText = logo.tagline.uppercase()
                val taglinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    color = accentInt
                    textSize = nameTextPaint.textSize * 0.48f
                    this.typeface = Typeface.DEFAULT
                    textAlign = if (isSideBySide) Paint.Align.LEFT else Paint.Align.CENTER
                }

                val taglineY = nameY + nameTextPaint.textSize + height * 0.015f
                canvas.drawText(taglineText, nameX, taglineY, taglinePaint)
            }
        }

        return bitmap
    }

    fun saveToGallery(context: Context, logo: LogoEntity): Uri? {
        val bitmap = generateHighResBitmap(logo)
        val filename = "Logo_${logo.brandName.replace("\\s+".toRegex(), "_")}_${System.currentTimeMillis()}.png"

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/LogoGenerator")
            }
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            uri?.let {
                context.contentResolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
                }
            }
            uri
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val logoDir = File(imagesDir, "LogoGenerator")
            if (!logoDir.exists()) logoDir.mkdirs()
            val imageFile = File(logoDir, filename)
            FileOutputStream(imageFile).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", imageFile)
        }
    }

    fun saveToCacheAndGetUri(context: Context, logo: LogoEntity): Uri {
        val bitmap = generateHighResBitmap(logo)
        val cachePath = File(context.cacheDir, "images")
        cachePath.mkdirs()
        val file = File(cachePath, "shared_logo_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { stream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
        }
        return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    private fun createPolygonPath(center: android.graphics.PointF, radius: Float, sides: Int, rotationDeg: Float): Path {
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

    private fun parseColorHex(hex: String, defaultColor: Int): Int {
        return try {
            val clean = hex.replace("#", "").trim()
            val colorLong = when (clean.length) {
                6 -> "FF$clean".toLong(16)
                8 -> clean.toLong(16)
                else -> return defaultColor
            }
            colorLong.toInt()
        } catch (_: Exception) {
            defaultColor
        }
    }

    private fun intColorWithAlpha(color: Int, alpha: Float): Int {
        val a = (alpha * 255).toInt().coerceIn(0, 255)
        return (color and 0x00FFFFFF) or (a shl 24)
    }
}
