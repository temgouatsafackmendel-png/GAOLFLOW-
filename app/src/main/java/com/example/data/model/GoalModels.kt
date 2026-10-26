package com.example.data.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate

enum class GoalCategory(
    val title: String,
    val colorHex: Long,
    val iconId: String
) {
    SANTE_SPORT("Santé & Sport", 0xFF10B981, "fitness"),
    CARRIERE_ETUDES("Carrière & Études", 0xFF3B82F6, "work"),
    FINANCES("Finances & Épargne", 0xFFEAB308, "savings"),
    DEV_PERSO("Développement Personnel", 0xFF8B5CF6, "book"),
    CREATIVITE_PROJETS("Créativité & Projets", 0xFFEC4899, "palette"),
    RELATIONS_FAMILLE("Relations & Famille", 0xFF06B6D4, "people"),
    HABITUDES_ROUTINE("Habitudes & Routine", 0xFFF97316, "repeat"),
    AUTRE("Autre Ambition", 0xFF64748B, "star")
}

enum class GoalTargetType(val label: String, val shortDesc: String) {
    NUMERIC("Mesurable", "Progression chiffrée (km, €, livres, etc.)"),
    CHECKLIST("Jalons", "Étapes clés et sous-tâches à valider"),
    HABIT("Habitude", "Répétition quotidienne avec suivi de série")
}

enum class GoalPriority(val label: String, val colorHex: Long) {
    HIGH("Priorité Haute", 0xFFEF4444),
    MEDIUM("Priorité Moyenne", 0xFFF59E0B),
    LOW("Priorité Basse", 0xFF3B82F6)
}

@Entity(tableName = "goals")
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "", // "Mon Pourquoi" - anchor of motivation
    val category: GoalCategory = GoalCategory.DEV_PERSO,
    val targetType: GoalTargetType = GoalTargetType.NUMERIC,
    val currentValue: Double = 0.0,
    val targetValue: Double = 100.0,
    val unit: String = "%", // e.g., "km", "€", "pages", "jours", "séances"
    val deadlineEpochDay: Long = LocalDate.now().plusMonths(1).toEpochDay(),
    val createdAtEpochDay: Long = LocalDate.now().toEpochDay(),
    val priority: GoalPriority = GoalPriority.MEDIUM,
    val isCompleted: Boolean = false,
    val completedAtEpochDay: Long? = null,
    val customColorHex: Long? = null,
    val shortTermSacrifice: String = "", // L'effort / plaisir immédiat sacrifié
    val longTermReward: String = "" // La gratification différée / récompense future
)

@Entity(
    tableName = "milestones",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class Milestone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val title: String,
    val isCompleted: Boolean = false,
    val orderIndex: Int = 0,
    val completedAtEpochDay: Long? = null
)

@Entity(
    tableName = "progress_logs",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class ProgressLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val amount: Double,
    val valueAfter: Double,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "habit_checkins",
    foreignKeys = [
        ForeignKey(
            entity = Goal::class,
            parentColumns = ["id"],
            childColumns = ["goalId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("goalId")]
)
data class DailyHabitCheckIn(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val goalId: Long,
    val epochDay: Long = LocalDate.now().toEpochDay(),
    val note: String = ""
)

data class GoalWithDetails(
    @Embedded
    val goal: Goal,
    @Relation(parentColumn = "id", entityColumn = "goalId")
    val milestones: List<Milestone> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "goalId")
    val progressLogs: List<ProgressLog> = emptyList(),
    @Relation(parentColumn = "id", entityColumn = "goalId")
    val habitCheckIns: List<DailyHabitCheckIn> = emptyList()
) {
    val progressFraction: Float
        get() {
            if (goal.isCompleted) return 1f
            return when (goal.targetType) {
                GoalTargetType.NUMERIC -> {
                    if (goal.targetValue <= 0) 0f
                    else (goal.currentValue / goal.targetValue).toFloat().coerceIn(0f, 1f)
                }
                GoalTargetType.CHECKLIST -> {
                    if (milestones.isEmpty()) 0f
                    else (milestones.count { it.isCompleted }.toFloat() / milestones.size).coerceIn(0f, 1f)
                }
                GoalTargetType.HABIT -> {
                    if (goal.targetValue <= 0) 0f
                    else (habitCheckIns.size.toFloat() / goal.targetValue.toFloat()).coerceIn(0f, 1f)
                }
            }
        }

    val progressPercent: Int
        get() = (progressFraction * 100).toInt()

    fun isCheckedInOn(epochDay: Long): Boolean {
        return habitCheckIns.any { it.epochDay == epochDay }
    }

    val currentStreak: Int
        get() {
            val checkInDays = habitCheckIns.map { it.epochDay }.toSet()
            if (checkInDays.isEmpty()) return 0
            val today = LocalDate.now().toEpochDay()
            var streak = 0
            var checkDay = if (checkInDays.contains(today)) today else today - 1
            while (checkInDays.contains(checkDay)) {
                streak++
                checkDay--
            }
            return streak
        }

    val daysRemaining: Long
        get() = goal.deadlineEpochDay - LocalDate.now().toEpochDay()
}
