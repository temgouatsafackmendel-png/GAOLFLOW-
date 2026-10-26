package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalPriority
import com.example.data.model.GoalTargetType
import com.example.data.model.GoalWithDetails
import com.example.data.model.Milestone
import com.example.ui.components.CategoryBadge
import com.example.ui.components.CircularGoalProgress
import com.example.ui.components.DeadlineBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.GoalViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailScreen(
    goalId: Long,
    viewModel: GoalViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit
) {
    BackHandler { onNavigateBack() }

    val goalWithDetailsFlow = remember(goalId) { viewModel.getGoalFlow(goalId) }
    val goalWithDetails by goalWithDetailsFlow.collectAsState(initial = null)

    var showDeleteConfirmDialog by remember { mutableStateOf(false) }
    var showCustomProgressDialog by remember { mutableStateOf(false) }
    var newMilestoneTitle by remember { mutableStateOf("") }

    if (goalWithDetails == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Chargement de l'objectif...")
        }
        return
    }

    val item = goalWithDetails!!
    val goal = item.goal
    val catColor = Color(goal.category.colorHex)
    val progress = item.progressFraction
    val percent = item.progressPercent

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Détail de l'objectif", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("detail_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { onNavigateToEdit(goal.id) }, modifier = Modifier.testTag("edit_goal_button")) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Modifier", tint = PrimaryIndigo)
                    }
                    IconButton(onClick = { showDeleteConfirmDialog = true }, modifier = Modifier.testTag("delete_goal_button")) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Supprimer", tint = DangerRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Hero Banner Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CategoryBadge(category = goal.category)
                            PriorityBadge(priority = goal.priority)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = goal.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DeadlineBadge(
                                daysRemaining = item.daysRemaining,
                                isCompleted = goal.isCompleted
                            )
                            val deadlineDate = LocalDate.ofEpochDay(goal.deadlineEpochDay)
                            Text(
                                text = "Échéance : ${deadlineDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // "Mon Pourquoi" Card (The psychological motivation anchor)
            if (goal.description.isNotBlank()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        ),
                        border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryIndigo.copy(alpha = 0.25f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = null,
                                tint = PrimaryIndigo,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Mon « Pourquoi »",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryIndigo
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = goal.description,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // Gratification Immédiate vs Long Terme Card
            if (goal.shortTermSacrifice.isNotBlank() || goal.longTermReward.isNotBlank()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Gratification : Court Terme vs Long Terme",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            if (goal.shortTermSacrifice.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = DangerRed.copy(alpha = 0.08f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Inconfort / Plaisir immédiat sacrifié :",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = DangerRed
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = goal.shortTermSacrifice,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }

                            if (goal.shortTermSacrifice.isNotBlank() && goal.longTermReward.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            if (goal.longTermReward.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SuccessGreen.copy(alpha = 0.08f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Text(
                                            text = "Gratification à Long Terme (Votre Victoire) :",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = SuccessGreen
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = goal.longTermReward,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Progress Gauge & Main Controls
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularGoalProgress(
                            progress = progress,
                            categoryColor = if (goal.isCompleted) SuccessGreen else catColor,
                            size = 130.dp,
                            strokeWidth = 12.dp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        when (goal.targetType) {
                            GoalTargetType.NUMERIC -> {
                                val currentStr = if (goal.currentValue % 1.0 == 0.0) goal.currentValue.toInt().toString() else "%.1f".format(goal.currentValue)
                                val targetStr = if (goal.targetValue % 1.0 == 0.0) goal.targetValue.toInt().toString() else "%.1f".format(goal.targetValue)
                                Text(
                                    text = "$currentStr / $targetStr ${goal.unit}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                if (!goal.isCompleted) {
                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Quick add buttons
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        listOf(1.0, 5.0, 10.0).forEach { amount ->
                                            Button(
                                                onClick = {
                                                    viewModel.addProgress(goal.id, amount, "+$amount ${goal.unit}")
                                                },
                                                modifier = Modifier.weight(1f),
                                                shape = RoundedCornerShape(12.dp),
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = catColor.copy(alpha = 0.15f),
                                                    contentColor = catColor
                                                )
                                            ) {
                                                Text("+${amount.toInt()}", fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedButton(
                                        onClick = { showCustomProgressDialog = true },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Enregistrer une avancée sur mesure")
                                    }
                                }
                            }
                            GoalTargetType.CHECKLIST -> {
                                val doneCount = item.milestones.count { it.isCompleted }
                                Text(
                                    text = "$doneCount sur ${item.milestones.size} jalons validés",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            GoalTargetType.HABIT -> {
                                Text(
                                    text = "${item.habitCheckIns.size} / ${goal.targetValue.toInt()} ${goal.unit} complétés",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = AmberAccent.copy(alpha = 0.15f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.LocalFireDepartment,
                                            contentDescription = null,
                                            tint = AmberAccent
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Série actuelle : ${item.currentStreak} jours consécutifs !",
                                            fontWeight = FontWeight.Bold,
                                            color = AmberAccent
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Toggle completion button
                        Button(
                            onClick = { viewModel.toggleGoalCompletion(goal.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("toggle_completion_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (goal.isCompleted) MaterialTheme.colorScheme.surfaceVariant else SuccessGreen,
                                contentColor = if (goal.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else Color.White
                            )
                        ) {
                            Icon(
                                imageVector = if (goal.isCompleted) Icons.Default.History else Icons.Default.EmojiEvents,
                                contentDescription = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (goal.isCompleted) "Rouvrir cet objectif" else "Marquer comme accompli 🏆",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Habit 7-Day History Strip (if Habit goal)
            if (goal.targetType == GoalTargetType.HABIT) {
                item {
                    HabitWeekTrackerCard(
                        goalWithDetails = item,
                        onToggleDay = { epochDay ->
                            viewModel.toggleHabitToday(goal.id)
                        }
                    )
                }
            }

            // Milestones Section (for Checklist goals or milestones on any goal)
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Jalons & Étapes clés (${item.milestones.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${item.milestones.count { it.isCompleted }}/${item.milestones.size}",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = catColor
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (item.milestones.isEmpty()) {
                            Text(
                                text = "Aucun jalon pour le moment. Découpez votre objectif en étapes claires pour progresser plus sereinement !",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            item.milestones.forEach { milestone ->
                                MilestoneItemRow(
                                    milestone = milestone,
                                    catColor = catColor,
                                    onToggle = { viewModel.toggleMilestone(milestone) },
                                    onDelete = { viewModel.deleteMilestone(milestone) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Add milestone input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newMilestoneTitle,
                                onValueChange = { newMilestoneTitle = it },
                                placeholder = { Text("Ajouter un nouveau jalon...", fontSize = 13.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_milestone_input")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newMilestoneTitle.isNotBlank()) {
                                        viewModel.addMilestone(goal.id, newMilestoneTitle)
                                        newMilestoneTitle = ""
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                modifier = Modifier.testTag("add_milestone_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter")
                            }
                        }
                    }
                }
            }

            // Progression Logs History
            if (item.progressLogs.isNotEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Historique des avancées",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            item.progressLogs.take(8).forEach { log ->
                                val date = Instant.ofEpochMilli(log.timestamp)
                                    .atZone(ZoneId.systemDefault())
                                    .format(DateTimeFormatter.ofPattern("dd/MM à HH:mm"))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (log.note.isNotBlank()) log.note else "+${log.amount} ${goal.unit}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = date,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = "+${log.amount} ${goal.unit}",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = SuccessGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Supprimer cet objectif ?") },
            text = { Text("Cette action supprimera définitivement « ${goal.title} » ainsi que tous ses jalons et historiques.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteGoal(goal)
                        showDeleteConfirmDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Supprimer", color = DangerRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Annuler")
                }
            }
        )
    }

    // Custom Progress Dialog
    if (showCustomProgressDialog) {
        CustomProgressDialog(
            unit = goal.unit,
            onDismiss = { showCustomProgressDialog = false },
            onConfirm = { amount, note ->
                viewModel.addProgress(goal.id, amount, note)
                showCustomProgressDialog = false
            }
        )
    }
}

@Composable
fun MilestoneItemRow(
    milestone: Milestone,
    catColor: Color,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (milestone.isCompleted) SuccessGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onToggle() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = milestone.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = SuccessGreen,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = milestone.title,
                style = MaterialTheme.typography.bodyMedium,
                textDecoration = if (milestone.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
                color = if (milestone.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Supprimer jalon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun HabitWeekTrackerCard(
    goalWithDetails: GoalWithDetails,
    onToggleDay: (Long) -> Unit
) {
    val today = LocalDate.now()
    val days = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val dayFormatter = DateTimeFormatter.ofPattern("E")

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Suivi des 7 derniers jours",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                days.forEach { day ->
                    val isChecked = goalWithDetails.isCheckedInOn(day.toEpochDay())
                    val isToday = day == today
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (isToday) onToggleDay(day.toEpochDay())
                        }
                    ) {
                        Text(
                            text = day.format(dayFormatter).take(2).uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isChecked -> SuccessGreen
                                        isToday -> PrimaryIndigo.copy(alpha = 0.15f)
                                        else -> MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isChecked) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Text(
                                    text = "${day.dayOfMonth}",
                                    fontSize = 12.sp,
                                    color = if (isToday) PrimaryIndigo else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CustomProgressDialog(
    unit: String,
    onDismiss: () -> Unit,
    onConfirm: (Double, String) -> Unit
) {
    var amountText by remember { mutableStateOf("") }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Ajouter une progression") },
        text = {
            Column {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it },
                    label = { Text("Valeur ($unit)") },
                    placeholder = { Text("Ex: 2.5") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Note facultative") },
                    placeholder = { Text("Ex: Séance intensive, très fier !") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    if (amount > 0) {
                        onConfirm(amount, noteText)
                    }
                },
                enabled = (amountText.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Valider")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
