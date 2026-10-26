package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.DailyHabitCheckIn
import com.example.data.model.Goal
import com.example.data.model.GoalWithDetails
import com.example.data.model.Milestone
import com.example.data.model.ProgressLog
import kotlinx.coroutines.flow.Flow

@Dao
interface GoalDao {

    @Transaction
    @Query("SELECT * FROM goals ORDER BY isCompleted ASC, priority ASC, deadlineEpochDay ASC")
    fun getAllGoalsWithDetails(): Flow<List<GoalWithDetails>>

    @Transaction
    @Query("SELECT * FROM goals WHERE id = :goalId LIMIT 1")
    fun getGoalWithDetailsById(goalId: Long): Flow<GoalWithDetails?>

    @Query("SELECT COUNT(*) FROM goals")
    suspend fun getGoalCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoal(goal: Goal): Long

    @Update
    suspend fun updateGoal(goal: Goal)

    @Delete
    suspend fun deleteGoal(goal: Goal)

    @Query("DELETE FROM goals WHERE id = :goalId")
    suspend fun deleteGoalById(goalId: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestone(milestone: Milestone): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMilestones(milestones: List<Milestone>)

    @Update
    suspend fun updateMilestone(milestone: Milestone)

    @Delete
    suspend fun deleteMilestone(milestone: Milestone)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgressLog(log: ProgressLog): Long

    @Query("SELECT * FROM progress_logs WHERE goalId = :goalId ORDER BY timestamp DESC")
    fun getProgressLogsForGoal(goalId: Long): Flow<List<ProgressLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCheckIn(checkIn: DailyHabitCheckIn): Long

    @Query("DELETE FROM habit_checkins WHERE goalId = :goalId AND epochDay = :epochDay")
    suspend fun deleteHabitCheckIn(goalId: Long, epochDay: Long)

    @Query("SELECT * FROM habit_checkins WHERE goalId = :goalId ORDER BY epochDay DESC")
    fun getHabitCheckInsForGoal(goalId: Long): Flow<List<DailyHabitCheckIn>>
}
