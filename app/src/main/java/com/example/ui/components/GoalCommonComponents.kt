package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalCategory
import com.example.data.model.GoalPriority
import com.example.ui.theme.AchievementAmber
import com.example.ui.theme.DisciplineEmerald
import com.example.ui.theme.TemptationRed

fun getCategoryIcon(category: GoalCategory): ImageVector {
    return when (category) {
        GoalCategory.SANTE_SPORT -> Icons.Default.FitnessCenter
        GoalCategory.CARRIERE_ETUDES -> Icons.Default.Work
        GoalCategory.FINANCES -> Icons.Default.Savings
        GoalCategory.DEV_PERSO -> Icons.Default.AutoStories
        GoalCategory.CREATIVITE_PROJETS -> Icons.Default.Brush
        GoalCategory.RELATIONS_FAMILLE -> Icons.Default.People
        GoalCategory.HABITUDES_ROUTINE -> Icons.Default.Repeat
        GoalCategory.AUTRE -> Icons.Default.Star
    }
}

@Composable
fun CategoryBadge(
    category: GoalCategory,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val catColor = Color(category.colorHex)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = catColor.copy(alpha = 0.12f),
        contentColor = catColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = getCategoryIcon(category),
                contentDescription = category.title,
                modifier = Modifier.size(14.dp),
                tint = catColor
            )
            if (showLabel) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = category.title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = catColor
                )
            }
        }
    }
}

@Composable
fun PriorityBadge(
    priority: GoalPriority,
    modifier: Modifier = Modifier
) {
    val color = Color(priority.colorHex)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        contentColor = color
    ) {
        Text(
            text = priority.label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            color = color
        )
    }
}

@Composable
fun DeadlineBadge(
    daysRemaining: Long,
    isCompleted: Boolean,
    modifier: Modifier = Modifier
) {
    val (label, bgColor, textColor) = when {
        isCompleted -> Triple("Accompli 🏆", DisciplineEmerald.copy(alpha = 0.15f), DisciplineEmerald)
        daysRemaining < 0 -> Triple("Échéance dépassée (${-daysRemaining}j)", TemptationRed.copy(alpha = 0.15f), TemptationRed)
        daysRemaining == 0L -> Triple("Aujourd'hui !", AchievementAmber.copy(alpha = 0.2f), AchievementAmber)
        daysRemaining == 1L -> Triple("Demain !", AchievementAmber.copy(alpha = 0.15f), AchievementAmber)
        daysRemaining <= 7L -> Triple("Dans $daysRemaining jours", AchievementAmber.copy(alpha = 0.15f), AchievementAmber)
        else -> Triple("Reste $daysRemaining jours", MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun AnimatedProgressBar(
    progress: Float,
    categoryColor: Color,
    modifier: Modifier = Modifier,
    height: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 600),
        label = "progressBar"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(height / 2))
            .background(categoryColor.copy(alpha = 0.15f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(animatedProgress)
                .clip(RoundedCornerShape(height / 2))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(categoryColor, categoryColor.copy(alpha = 0.85f))
                    )
                )
        )
    }
}

@Composable
fun CircularGoalProgress(
    progress: Float,
    categoryColor: Color,
    modifier: Modifier = Modifier,
    size: Dp = 100.dp,
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 700),
        label = "circularProgress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Track
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(size),
            color = categoryColor.copy(alpha = 0.15f),
            strokeWidth = strokeWidth
        )
        // Indicator
        CircularProgressIndicator(
            progress = { animatedProgress },
            modifier = Modifier.size(size),
            color = categoryColor,
            strokeWidth = strokeWidth
        )
        // Center text
        Text(
            text = "${(progress * 100).toInt()}%",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
