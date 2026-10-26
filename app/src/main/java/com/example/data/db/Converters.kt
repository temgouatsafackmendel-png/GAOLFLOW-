package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.GoalCategory
import com.example.data.model.GoalPriority
import com.example.data.model.GoalTargetType

class Converters {
    @TypeConverter
    fun fromCategory(category: GoalCategory): String = category.name

    @TypeConverter
    fun toCategory(value: String): GoalCategory = try {
        GoalCategory.valueOf(value)
    } catch (_: Exception) {
        GoalCategory.AUTRE
    }

    @TypeConverter
    fun fromTargetType(type: GoalTargetType): String = type.name

    @TypeConverter
    fun toTargetType(value: String): GoalTargetType = try {
        GoalTargetType.valueOf(value)
    } catch (_: Exception) {
        GoalTargetType.NUMERIC
    }

    @TypeConverter
    fun fromPriority(priority: GoalPriority): String = priority.name

    @TypeConverter
    fun toPriority(value: String): GoalPriority = try {
        GoalPriority.valueOf(value)
    } catch (_: Exception) {
        GoalPriority.MEDIUM
    }
}
