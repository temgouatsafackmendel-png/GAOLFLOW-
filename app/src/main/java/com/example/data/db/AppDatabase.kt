package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.DailyHabitCheckIn
import com.example.data.model.Goal
import com.example.data.model.GoalCategory
import com.example.data.model.GoalPriority
import com.example.data.model.GoalTargetType
import com.example.data.model.Milestone
import com.example.data.model.ProgressLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(
    entities = [
        Goal::class,
        Milestone::class,
        ProgressLog::class,
        DailyHabitCheckIn::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "objectifs_database.db"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialGoals(database.goalDao())
                    }
                }
            }
        }

        suspend fun populateInitialGoals(dao: GoalDao) {
            val today = LocalDate.now().toEpochDay()

            // 1. Goal: Reading
            val readingGoalId = dao.insertGoal(
                Goal(
                    title = "Lire 12 livres inspirants",
                    description = "Nourrir mon esprit, développer mon sens critique et élargir mes horizons de réflexion.",
                    category = GoalCategory.DEV_PERSO,
                    targetType = GoalTargetType.NUMERIC,
                    currentValue = 4.0,
                    targetValue = 12.0,
                    unit = "livres",
                    deadlineEpochDay = today + 120,
                    createdAtEpochDay = today - 30,
                    priority = GoalPriority.MEDIUM
                )
            )
            dao.insertMilestones(
                listOf(
                    Milestone(goalId = readingGoalId, title = "Choisir ma liste de lectures", isCompleted = true, orderIndex = 0),
                    Milestone(goalId = readingGoalId, title = "Terminer 3 livres au 1er trimestre", isCompleted = true, orderIndex = 1),
                    Milestone(goalId = readingGoalId, title = "Franchir le cap des 6 livres (mi-parcours)", isCompleted = false, orderIndex = 2),
                    Milestone(goalId = readingGoalId, title = "Atteindre les 12 livres", isCompleted = false, orderIndex = 3)
                )
            )
            dao.insertProgressLog(
                ProgressLog(goalId = readingGoalId, amount = 1.0, valueAfter = 4.0, note = "L'Art de la Victoire (Phil Knight) terminé !")
            )

            // 2. Goal: Running 10 km
            val runningGoalId = dao.insertGoal(
                Goal(
                    title = "Courir 10 km en continu",
                    description = "Améliorer mon cardio, augmenter ma discipline physique et me sentir en pleine vitalité.",
                    category = GoalCategory.SANTE_SPORT,
                    targetType = GoalTargetType.NUMERIC,
                    currentValue = 7.0,
                    targetValue = 10.0,
                    unit = "km",
                    deadlineEpochDay = today + 25,
                    createdAtEpochDay = today - 20,
                    priority = GoalPriority.HIGH
                )
            )
            dao.insertMilestones(
                listOf(
                    Milestone(goalId = runningGoalId, title = "Courir 3 km sans s'arrêter", isCompleted = true, orderIndex = 0),
                    Milestone(goalId = runningGoalId, title = "Atteindre 5 km en moins de 30 min", isCompleted = true, orderIndex = 1),
                    Milestone(goalId = runningGoalId, title = "Passer le cap des 7 km", isCompleted = true, orderIndex = 2),
                    Milestone(goalId = runningGoalId, title = "Boucler la sortie de 10 km", isCompleted = false, orderIndex = 3)
                )
            )
            dao.insertProgressLog(
                ProgressLog(goalId = runningGoalId, amount = 2.0, valueAfter = 7.0, note = "Super sortie du dimanche matin en forêt.")
            )

            // 3. Goal: Portfolio & Carrière
            val portfolioGoalId = dao.insertGoal(
                Goal(
                    title = "Lancer mon portfolio professionnel",
                    description = "Valoriser mes réalisations et décrocher de nouveaux projets ambitieux.",
                    category = GoalCategory.CARRIERE_ETUDES,
                    targetType = GoalTargetType.CHECKLIST,
                    currentValue = 2.0,
                    targetValue = 4.0,
                    unit = "étapes",
                    deadlineEpochDay = today + 45,
                    createdAtEpochDay = today - 10,
                    priority = GoalPriority.HIGH
                )
            )
            dao.insertMilestones(
                listOf(
                    Milestone(goalId = portfolioGoalId, title = "Sélectionner mes 3 meilleurs projets", isCompleted = true, orderIndex = 0),
                    Milestone(goalId = portfolioGoalId, title = "Rédiger les études de cas détaillées", isCompleted = true, orderIndex = 1),
                    Milestone(goalId = portfolioGoalId, title = "Finaliser la maquette et le code", isCompleted = false, orderIndex = 2),
                    Milestone(goalId = portfolioGoalId, title = "Publier en ligne et annoncer sur LinkedIn", isCompleted = false, orderIndex = 3)
                )
            )

            // 4. Goal: Habit Daily Meditation
            val habitGoalId = dao.insertGoal(
                Goal(
                    title = "Méditation & Clarté d'esprit",
                    description = "10 minutes chaque matin pour démarrer la journée avec calme et concentration.",
                    category = GoalCategory.HABITUDES_ROUTINE,
                    targetType = GoalTargetType.HABIT,
                    currentValue = 18.0,
                    targetValue = 30.0,
                    unit = "jours",
                    deadlineEpochDay = today + 12,
                    createdAtEpochDay = today - 18,
                    priority = GoalPriority.MEDIUM
                )
            )
            // Insert recent check-ins for streak
            dao.insertHabitCheckIn(DailyHabitCheckIn(goalId = habitGoalId, epochDay = today - 2))
            dao.insertHabitCheckIn(DailyHabitCheckIn(goalId = habitGoalId, epochDay = today - 1))
            dao.insertHabitCheckIn(DailyHabitCheckIn(goalId = habitGoalId, epochDay = today))
        }
    }
}
