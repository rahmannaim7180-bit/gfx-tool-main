package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun GamingButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isDestructive: Boolean = false,
    isSecondary: Boolean = false,
    testTag: String = "gaming_button"
) {
    val bg = when {
        isDestructive -> NeonRedAlert
        isSecondary -> GamingSurfaceVariant
        else -> ElectricCyan
    }
    val contentColor = if (isSecondary) TextPrimary else GamingDarkBackground
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = bg,
            contentColor = contentColor,
            disabledContainerColor = GamingSurfaceVariant,
            disabledContentColor = TextMuted
        ),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .testTag(testTag)
            .heightIn(min = 48.dp)
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun GamingCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GamingCardBorder,
    backgroundColor: Color = GamingSurface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val baseModifier = modifier
        .clip(RoundedCornerShape(12.dp))
        .background(backgroundColor)
        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
        .let { if (onClick != null) it.clickable { onClick() } else it }
        .padding(16.dp)

    Column(modifier = baseModifier) {
        content()
    }
}

@Composable
fun MetricBox(
    title: String,
    value: String,
    unit: String = "",
    subtitle: String = "",
    accentColor: Color = ElectricCyan,
    color: Color = accentColor,
    modifier: Modifier = Modifier
) {
    val displayColor = if (color != ElectricCyan) color else accentColor
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GamingSurfaceVariant)
            .border(1.dp, GamingCardBorder, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Text(text = title, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(text = value, color = displayColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            if (unit.isNotEmpty()) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = unit, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.padding(bottom = 2.dp))
            }
        }
        if (subtitle.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, color = TextMuted, fontSize = 10.sp)
        }
    }
}

@Composable
fun RealTimeLineChart(
    dataPoints: List<Float>,
    modifier: Modifier = Modifier,
    lineColor: Color = ElectricCyan,
    minY: Float = 0f,
    maxY: Float = 120f,
    minVal: Float = minY,
    maxVal: Float = maxY
) {
    val effMin = if (minVal != 0f) minVal else minY
    val effMax = if (maxVal != 120f) maxVal else maxY
    Canvas(modifier = modifier) {
        if (dataPoints.size < 2) return@Canvas
        val rangeY = if (effMax - effMin <= 0f) 1f else (effMax - effMin)
        val stepX = size.width / (dataPoints.size - 1).coerceAtLeast(1)

        val path = Path()
        dataPoints.forEachIndexed { index, point ->
            val normalizedY = ((point - effMin) / rangeY).coerceIn(0f, 1f)
            val y = size.height - (normalizedY * size.height)
            val x = index * stepX
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 2.dp.toPx())
        )
    }
}

@Composable
fun StatusBadge(
    text: String,
    color: Color = NeonGreenActive,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
