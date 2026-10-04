package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.sin

enum class DiagramType {
    PARABOLA_GRAPH,
    RAY_OPTICS,
    ELECTRIC_CIRCUIT,
    PLANT_CELL_DIAGRAM,
    SINE_WAVE
}

@Composable
fun EducationalDiagramCanvas(
    diagramType: DiagramType,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val outlineColor = MaterialTheme.colorScheme.outline
    val surfaceColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(240.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(surfaceColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            when (diagramType) {
                DiagramType.PARABOLA_GRAPH -> {
                    // Coordinate Axes
                    drawLine(color = outlineColor, start = Offset(w * 0.1f, h * 0.85f), end = Offset(w * 0.9f, h * 0.85f), strokeWidth = 3f)
                    drawLine(color = outlineColor, start = Offset(w * 0.5f, h * 0.1f), end = Offset(w * 0.5f, h * 0.9f), strokeWidth = 3f)

                    // Draw Parabola y = ax²
                    val path = Path()
                    val cx = w * 0.5f
                    val cy = h * 0.8f
                    val scale = w * 0.0018f

                    path.moveTo(w * 0.15f, cy - scale * (-120f) * (-120f) * 0.08f)
                    for (x in -120..120 step 4) {
                        val px = cx + x * scale * 2.2f
                        val py = cy - (x * x * 0.009f * scale * 100f)
                        if (x == -120) path.moveTo(px, py) else path.lineTo(px, py)
                    }
                    drawPath(path = path, color = primaryColor, style = Stroke(width = 6f))

                    // Roots intersection circles
                    drawCircle(color = tertiaryColor, radius = 8f, center = Offset(cx - 75f * scale, h * 0.85f))
                    drawCircle(color = tertiaryColor, radius = 8f, center = Offset(cx + 75f * scale, h * 0.85f))
                }

                DiagramType.RAY_OPTICS -> {
                    // Principal Axis
                    drawLine(color = outlineColor, start = Offset(0f, h * 0.5f), end = Offset(w, h * 0.5f), strokeWidth = 3f)

                    // Convex Lens
                    val lensX = w * 0.5f
                    val lensPath = Path().apply {
                        moveTo(lensX, h * 0.15f)
                        quadraticTo(lensX + 24f, h * 0.5f, lensX, h * 0.85f)
                        quadraticTo(lensX - 24f, h * 0.5f, lensX, h * 0.15f)
                    }
                    drawPath(lensPath, color = primaryColor.copy(alpha = 0.35f))
                    drawPath(lensPath, color = primaryColor, style = Stroke(width = 4f))

                    // Focal points
                    val f1 = lensX - w * 0.22f
                    val f2 = lensX + w * 0.22f
                    drawCircle(color = tertiaryColor, radius = 6f, center = Offset(f1, h * 0.5f))
                    drawCircle(color = tertiaryColor, radius = 6f, center = Offset(f2, h * 0.5f))

                    // Object arrow
                    val objX = lensX - w * 0.35f
                    val objY = h * 0.25f
                    drawLine(color = secondaryColor, start = Offset(objX, h * 0.5f), end = Offset(objX, objY), strokeWidth = 6f)

                    // Parallel incident ray refracting through F2
                    drawLine(color = primaryColor, start = Offset(objX, objY), end = Offset(lensX, objY), strokeWidth = 4f)
                    drawLine(color = primaryColor, start = Offset(lensX, objY), end = Offset(w * 0.95f, h * 0.85f), strokeWidth = 4f)

                    // Central ray through optical center
                    drawLine(color = tertiaryColor, start = Offset(objX, objY), end = Offset(w * 0.95f, h * 0.85f), strokeWidth = 4f)
                }

                DiagramType.ELECTRIC_CIRCUIT -> {
                    // Rectangular circuit loop
                    val left = w * 0.15f
                    val right = w * 0.85f
                    val top = h * 0.2f
                    val bottom = h * 0.8f

                    drawLine(color = primaryColor, start = Offset(left, top), end = Offset(right, top), strokeWidth = 5f)
                    drawLine(color = primaryColor, start = Offset(right, top), end = Offset(right, bottom), strokeWidth = 5f)
                    drawLine(color = primaryColor, start = Offset(right, bottom), end = Offset(left, bottom), strokeWidth = 5f)
                    drawLine(color = primaryColor, start = Offset(left, bottom), end = Offset(left, top), strokeWidth = 5f)

                    // Battery in top wire
                    val midX = w * 0.5f
                    drawRect(color = surfaceColor, topLeft = Offset(midX - 35f, top - 15f), size = Size(70f, 30f))
                    drawLine(color = secondaryColor, start = Offset(midX - 15f, top - 25f), end = Offset(midX - 15f, top + 25f), strokeWidth = 6f)
                    drawLine(color = secondaryColor, start = Offset(midX + 15f, top - 15f), end = Offset(midX + 15f, top + 15f), strokeWidth = 4f)

                    // Resistor zigzag on bottom wire
                    drawRect(color = surfaceColor, topLeft = Offset(midX - 60f, bottom - 15f), size = Size(120f, 30f))
                    val resistorPath = Path().apply {
                        moveTo(midX - 50f, bottom)
                        lineTo(midX - 35f, bottom - 15f)
                        lineTo(midX - 15f, bottom + 15f)
                        lineTo(midX + 5f, bottom - 15f)
                        lineTo(midX + 25f, bottom + 15f)
                        lineTo(midX + 40f, bottom - 15f)
                        lineTo(midX + 50f, bottom)
                    }
                    drawPath(resistorPath, color = tertiaryColor, style = Stroke(width = 5f))
                }

                DiagramType.SINE_WAVE -> {
                    // Axis
                    drawLine(color = outlineColor, start = Offset(w * 0.05f, h * 0.5f), end = Offset(w * 0.95f, h * 0.5f), strokeWidth = 3f)
                    val wavePath = Path()
                    val amplitude = h * 0.32f
                    for (x in 0..(w.toInt())) {
                        val rad = (x.toFloat() / w) * (4 * Math.PI.toFloat())
                        val y = h * 0.5f + amplitude * sin(rad)
                        if (x == 0) wavePath.moveTo(0f, y) else wavePath.lineTo(x.toFloat(), y)
                    }
                    drawPath(wavePath, color = primaryColor, style = Stroke(width = 6f))
                }

                DiagramType.PLANT_CELL_DIAGRAM -> {
                    // Cell wall and membrane
                    drawRoundRect(color = primaryColor, topLeft = Offset(w * 0.15f, h * 0.15f), size = Size(w * 0.7f, h * 0.7f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(30f, 30f), style = Stroke(width = 8f))
                    drawRoundRect(color = secondaryColor.copy(alpha = 0.2f), topLeft = Offset(w * 0.18f, h * 0.18f), size = Size(w * 0.64f, h * 0.64f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f))

                    // Central Vacuole
                    drawOval(color = Color(0xFF38BDF8).copy(alpha = 0.4f), topLeft = Offset(w * 0.3f, h * 0.3f), size = Size(w * 0.35f, h * 0.4f))

                    // Nucleus
                    drawCircle(color = tertiaryColor, radius = 28f, center = Offset(w * 0.72f, h * 0.4f))
                }
            }
        }
    }
}
