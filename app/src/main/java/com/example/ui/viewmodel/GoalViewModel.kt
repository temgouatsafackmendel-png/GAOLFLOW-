package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.DisciplineLevel
import com.example.data.model.DisciplineLevelSystem
import com.example.data.model.Goal
import com.example.data.model.GoalCategory
import com.example.data.model.GoalPriority
import com.example.data.model.GoalTargetType
import com.example.data.model.GoalWithDetails
import com.example.data.model.Milestone
import com.example.data.repository.GoalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class GoalStatusFilter(val label: String) {
    ALL("Tous"),
    IN_PROGRESS("En cours"),
    COMPLETED("Atteints 🏆")
}

enum class GoalSortOption(val label: String) {
    DEADLINE_ASC("Échéance proche"),
    PROGRESS_DESC("Meilleure progression"),
    PRIORITY_HIGH("Priorité haute"),
    NEWEST("Plus récents")
}

data class GoalUiState(
    val selectedCategory: GoalCategory? = null,
    val statusFilter: GoalStatusFilter = GoalStatusFilter.ALL,
    val sortOption: GoalSortOption = GoalSortOption.DEADLINE_ASC,
    val searchQuery: String = "",
    val celebratedGoal: GoalWithDetails? = null,
    val currentQuoteIndex: Int = 0
)

data class GoalStats(
    val totalCount: Int = 0,
    val inProgressCount: Int = 0,
    val completedCount: Int = 0,
    val overallPercentage: Int = 0,
    val bestStreak: Int = 0,
    val totalMilestonesCompleted: Int = 0,
    val categoryCounts: Map<GoalCategory, Int> = emptyMap()
)

data class DisciplineProfile(
    val totalXp: Int = 0,
    val level: DisciplineLevel = DisciplineLevelSystem.levels.first(),
    val xpInCurrentLevel: Int = 0,
    val xpRequiredForNext: Int = 120,
    val progressFraction: Float = 0f
)

class GoalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GoalRepository
    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    private val _resistedUrgesCount = MutableStateFlow(0)
    val resistedUrgesCount: StateFlow<Int> = _resistedUrgesCount.asStateFlow()

    val quotes = listOf(
        "L'arbre qui tombe fait plus de bruit que la forêt qui pousse. Travaillez avec discipline en silence." to "Sagesse Africaine",
        "La discipline est le pont inébranlable entre vos objectifs et vos accomplissements." to "Jim Rohn",
        "Ne négociez jamais avec vous-même lorsque l'heure d'agir a sonné." to "Règle d'Auto-Discipline",
        "Le secret pour avancer réside dans la constance des petites victoires chaque matin." to "Principe d'Excellence",
        "Ce n'est pas la force, mais la persévérance qui fait les grands destins." to "Samuel Johnson",
        "L'auto-discipline est la capacité de faire ce que vous devez faire, au moment où vous devez le faire, que vous en ayez envie ou non." to "Elbert Hubbard",
        "Votre futur se construit dans les choix que vous faites aujourd'hui sans procrastiner." to "Mindset de Réussite"
    )

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = GoalRepository(database.goalDao())
    }

    private val rawGoalsFlow = repository.allGoals

    // Filtered and sorted goals list
    val goalsList: StateFlow<List<GoalWithDetails>> = combine(
        rawGoalsFlow,
        _uiState
    ) { goals, state ->
        goals.filter { item ->
            val matchCategory = state.selectedCategory == null || item.goal.category == state.selectedCategory
            val matchStatus = when (state.statusFilter) {
                GoalStatusFilter.ALL -> true
                GoalStatusFilter.IN_PROGRESS -> !item.goal.isCompleted
                GoalStatusFilter.COMPLETED -> item.goal.isCompleted
            }
            val matchQuery = state.searchQuery.isEmpty() ||
                    item.goal.title.contains(state.searchQuery, ignoreCase = true) ||
                    item.goal.description.contains(state.searchQuery, ignoreCase = true)
            matchCategory && matchStatus && matchQuery
        }.sortedWith { a, b ->
            when (state.sortOption) {
                GoalSortOption.DEADLINE_ASC -> a.goal.deadlineEpochDay.compareTo(b.goal.deadlineEpochDay)
                GoalSortOption.PROGRESS_DESC -> b.progressFraction.compareTo(a.progressFraction)
                GoalSortOption.PRIORITY_HIGH -> a.goal.priority.ordinal.compareTo(b.goal.priority.ordinal)
                GoalSortOption.NEWEST -> b.goal.createdAtEpochDay.compareTo(a.goal.createdAtEpochDay)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Overall stats computed from all goals
    val stats: StateFlow<GoalStats> = rawGoalsFlow.combine(_uiState) { goals, _ ->
        val total = goals.size
        val completed = goals.count { it.goal.isCompleted }
        val inProgress = total - completed
        val avgProgress = if (total > 0) {
            (goals.map { it.progressFraction }.average() * 100).toInt()
        } else 0
        val maxStreak = goals.maxOfOrNull { it.currentStreak } ?: 0
        val milestonesDone = goals.sumOf { it.milestones.count { m -> m.isCompleted } }
        val catMap = goals.groupBy { it.goal.category }.mapValues { it.value.size }

        GoalStats(
            totalCount = total,
            inProgressCount = inProgress,
            completedCount = completed,
            overallPercentage = avgProgress,
            bestStreak = maxStreak,
            totalMilestonesCompleted = milestonesDone,
            categoryCounts = catMap
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GoalStats())

    // Reactive Discipline Profile (XP and Progressive Levels)
    val disciplineProfile: StateFlow<DisciplineProfile> = rawGoalsFlow.combine(_uiState) { goals, _ ->
        var xp = 40 // Base initiation XP
        val totalCheckIns = goals.sumOf { it.habitCheckIns.size }
        val milestonesDone = goals.sumOf { it.milestones.count { m -> m.isCompleted } }
        val goalsDone = goals.count { it.goal.isCompleted }
        val logsCount = goals.sumOf { it.progressLogs.size }
        val maxStreak = goals.maxOfOrNull { it.currentStreak } ?: 0

        xp += (totalCheckIns * 15)
        xp += (milestonesDone * 25)
        xp += (goalsDone * 150)
        xp += (logsCount * 20)
        xp += (maxStreak * 20)
        xp += (_resistedUrgesCount.value * 35)

        val currentLvl = DisciplineLevelSystem.getLevelForXp(xp)
        val levelSpan = currentLvl.maxXp - currentLvl.minXp
        val xpInLevel = (xp - currentLvl.minXp).coerceAtLeast(0)
        val fraction = if (levelSpan > 0) (xpInLevel.toFloat() / levelSpan.toFloat()).coerceIn(0f, 1f) else 1f

        DisciplineProfile(
            totalXp = xp,
            level = currentLvl,
            xpInCurrentLevel = xpInLevel,
            xpRequiredForNext = levelSpan,
            progressFraction = fraction
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DisciplineProfile())

    // Today's actionable items
    val todayHabits: StateFlow<List<GoalWithDetails>> = rawGoalsFlow.combine(_uiState) { goals, _ ->
        goals.filter { it.goal.targetType == GoalTargetType.HABIT && !it.goal.isCompleted }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setCategoryFilter(category: GoalCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun setStatusFilter(filter: GoalStatusFilter) {
        _uiState.value = _uiState.value.copy(statusFilter = filter)
    }

    fun setSortOption(option: GoalSortOption) {
        _uiState.value = _uiState.value.copy(sortOption = option)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun nextQuote() {
        val next = (_uiState.value.currentQuoteIndex + 1) % quotes.size
        _uiState.value = _uiState.value.copy(currentQuoteIndex = next)
    }

    fun dismissCelebration() {
        _uiState.value = _uiState.value.copy(celebratedGoal = null)
    }

    fun getGoalFlow(goalId: Long) = repository.getGoalWithDetails(goalId)

    fun recordResistedTemptation(temptationTitle: String) {
        _resistedUrgesCount.value++
    }

    fun createGoal(
        title: String,
        description: String,
        category: GoalCategory,
        targetType: GoalTargetType,
        targetValue: Double,
        unit: String,
        deadlineEpochDay: Long,
        priority: GoalPriority,
        milestones: List<String>,
        shortTermSacrifice: String = "",
        longTermReward: String = ""
    ) {
        viewModelScope.launch {
            val goal = Goal(
                title = title.trim(),
                description = description.trim(),
                category = category,
                targetType = targetType,
                targetValue = targetValue,
                unit = unit.trim(),
                deadlineEpochDay = deadlineEpochDay,
                priority = priority,
                shortTermSacrifice = shortTermSacrifice.trim(),
                longTermReward = longTermReward.trim()
            )
            repository.createGoal(goal, milestones)
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            repository.updateGoal(goal)
        }
    }

    fun deleteGoal(goal: Goal) {
        viewModelScope.launch {
            repository.deleteGoal(goal)
        }
    }

    fun addProgress(goalId: Long, amount: Double, note: String) {
        viewModelScope.launch {
            val reached100 = repository.addProgress(goalId, amount, note)
            if (reached100) {
                val updated = goalsList.value.find { it.goal.id == goalId }
                if (updated != null) {
                    _uiState.value = _uiState.value.copy(celebratedGoal = updated)
                }
            }
        }
    }

    fun toggleMilestone(milestone: Milestone) {
        viewModelScope.launch {
            val completedGoal = repository.toggleMilestone(milestone)
            if (completedGoal) {
                val updated = goalsList.value.find { it.goal.id == milestone.goalId }
                if (updated != null) {
                    _uiState.value = _uiState.value.copy(celebratedGoal = updated)
                }
            }
        }
    }

    fun addMilestone(goalId: Long, title: String) {
        viewModelScope.launch {
            repository.addMilestone(goalId, title)
        }
    }

    fun deleteMilestone(milestone: Milestone) {
        viewModelScope.launch {
            repository.deleteMilestone(milestone)
        }
    }

    fun toggleHabitToday(goalId: Long) {
        viewModelScope.launch {
            val today = LocalDate.now().toEpochDay()
            val reachedTarget = repository.toggleHabitCheckIn(goalId, today)
            if (reachedTarget) {
                val updated = goalsList.value.find { it.goal.id == goalId }
                if (updated != null) {
                    _uiState.value = _uiState.value.copy(celebratedGoal = updated)
                }
            }
        }
    }

    fun toggleGoalCompletion(goalId: Long) {
        viewModelScope.launch {
            val goalWithDetails = goalsList.value.find { it.goal.id == goalId } ?: return@launch
            val newStatus = !goalWithDetails.goal.isCompleted
            repository.setGoalCompletion(goalId, newStatus)
            if (newStatus) {
                _uiState.value = _uiState.value.copy(celebratedGoal = goalWithDetails)
            }
        }
    }
}
