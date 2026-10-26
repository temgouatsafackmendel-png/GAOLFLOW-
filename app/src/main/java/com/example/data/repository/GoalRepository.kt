package com.example.data.repository

import com.example.data.db.GoalDao
import com.example.data.model.DailyHabitCheckIn
import com.example.data.model.Goal
import com.example.data.model.GoalTargetType
import com.example.data.model.GoalWithDetails
import com.example.data.model.Milestone
import com.example.data.model.ProgressLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate

class GoalRepository(private val goalDao: GoalDao) {

    val allGoals: Flow<List<GoalWithDetails>> = goalDao.getAllGoalsWithDetails()

    fun getGoalWithDetails(goalId: Long): Flow<GoalWithDetails?> {
        return goalDao.getGoalWithDetailsById(goalId)
    }

    suspend fun createGoal(
        goal: Goal,
        milestones: List<String> = emptyList()
    ): Long {
        val goalId = goalDao.insertGoal(goal)
        if (milestones.isNotEmpty()) {
            val milestoneEntities = milestones.mapIndexed { index, title ->
                Milestone(
                    goalId = goalId,
                    title = title.trim(),
                    isCompleted = false,
                    orderIndex = index
                )
            }
            goalDao.insertMilestones(milestoneEntities)
        }
        return goalId
    }

    suspend fun updateGoal(goal: Goal) {
        goalDao.updateGoal(goal)
    }

    suspend fun deleteGoal(goal: Goal) {
        goalDao.deleteGoal(goal)
    }

    suspend fun deleteGoalById(goalId: Long) {
        goalDao.deleteGoalById(goalId)
    }

    suspend fun addProgress(
        goalId: Long,
        amount: Double,
        note: String
    ): Boolean {
        val currentGoalWithDetails = goalDao.getGoalWithDetailsById(goalId).firstOrNull() ?: return false
        val currentGoal = currentGoalWithDetails.goal
        val newValue = (currentGoal.currentValue + amount).coerceAtLeast(0.0)
        val isNowCompleted = newValue >= currentGoal.targetValue

        val updatedGoal = currentGoal.copy(
            currentValue = newValue,
            isCompleted = isNowCompleted,
            completedAtEpochDay = if (isNowCompleted && !currentGoal.isCompleted) LocalDate.now().toEpochDay() else currentGoal.completedAtEpochDay
        )
        goalDao.updateGoal(updatedGoal)

        goalDao.insertProgressLog(
            ProgressLog(
                goalId = goalId,
                amount = amount,
                valueAfter = newValue,
                note = note.trim()
            )
        )
        return isNowCompleted && !currentGoal.isCompleted
    }

    suspend fun toggleMilestone(milestone: Milestone): Boolean {
        val newStatus = !milestone.isCompleted
        val updated = milestone.copy(
            isCompleted = newStatus,
            completedAtEpochDay = if (newStatus) LocalDate.now().toEpochDay() else null
        )
        goalDao.updateMilestone(updated)

        // Check if all milestones are completed to auto-complete the goal
        val goalWithDetails = goalDao.getGoalWithDetailsById(milestone.goalId).firstOrNull()
        if (goalWithDetails != null && goalWithDetails.goal.targetType == GoalTargetType.CHECKLIST) {
            val allDone = goalWithDetails.milestones.all { if (it.id == milestone.id) newStatus else it.isCompleted }
            val currentGoal = goalWithDetails.goal
            if (allDone != currentGoal.isCompleted) {
                val updatedGoal = currentGoal.copy(
                    isCompleted = allDone,
                    completedAtEpochDay = if (allDone) LocalDate.now().toEpochDay() else null
                )
                goalDao.updateGoal(updatedGoal)
                return allDone
            }
        }
        return false
    }

    suspend fun addMilestone(goalId: Long, title: String) {
        val trimmed = title.trim()
        if (trimmed.isEmpty()) return
        val current = goalDao.getGoalWithDetailsById(goalId).firstOrNull()
        val nextIndex = current?.milestones?.size ?: 0
        goalDao.insertMilestone(
            Milestone(
                goalId = goalId,
                title = trimmed,
                isCompleted = false,
                orderIndex = nextIndex
            )
        )
    }

    suspend fun deleteMilestone(milestone: Milestone) {
        goalDao.deleteMilestone(milestone)
    }

    suspend fun toggleHabitCheckIn(goalId: Long, epochDay: Long): Boolean {
        val goalWithDetails = goalDao.getGoalWithDetailsById(goalId).firstOrNull() ?: return false
        val alreadyChecked = goalWithDetails.habitCheckIns.any { it.epochDay == epochDay }
        if (alreadyChecked) {
            goalDao.deleteHabitCheckIn(goalId, epochDay)
            return false
        } else {
            goalDao.insertHabitCheckIn(
                DailyHabitCheckIn(
                    goalId = goalId,
                    epochDay = epochDay
                )
            )
            // If target habits reached
            val newCount = goalWithDetails.habitCheckIns.size + 1
            if (newCount >= goalWithDetails.goal.targetValue && !goalWithDetails.goal.isCompleted) {
                goalDao.updateGoal(
                    goalWithDetails.goal.copy(
                        isCompleted = true,
                        completedAtEpochDay = LocalDate.now().toEpochDay()
                    )
                )
                return true
            }
            return false
        }
    }

    suspend fun setGoalCompletion(goalId: Long, isCompleted: Boolean) {
        val goalWithDetails = goalDao.getGoalWithDetailsById(goalId).firstOrNull() ?: return
        val goal = goalWithDetails.goal
        val updated = goal.copy(
            isCompleted = isCompleted,
            currentValue = if (isCompleted && goal.targetType == GoalTargetType.NUMERIC) goal.targetValue else goal.currentValue,
            completedAtEpochDay = if (isCompleted) LocalDate.now().toEpochDay() else null
        )
        goalDao.updateGoal(updated)
    }
}
