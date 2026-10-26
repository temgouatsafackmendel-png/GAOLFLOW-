package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalCategory
import com.example.data.model.GoalPriority
import com.example.data.model.GoalTargetType
import com.example.data.model.GoalTemplate
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryTeal
import com.example.ui.viewmodel.GoalViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditGoalScreen(
    goalId: Long?,
    initialTemplate: GoalTemplate?,
    viewModel: GoalViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val existingGoalFlow = remember(goalId) {
        if (goalId != null) viewModel.getGoalFlow(goalId) else null
    }
    val existingGoalWithDetails by existingGoalFlow?.collectAsState(initial = null) ?: remember { mutableStateOf(null) }

    var title by remember {
        mutableStateOf(initialTemplate?.title ?: "")
    }
    var description by remember {
        mutableStateOf(initialTemplate?.description ?: "")
    }
    var category by remember {
        mutableStateOf(initialTemplate?.category ?: GoalCategory.DEV_PERSO)
    }
    var targetType by remember {
        mutableStateOf(initialTemplate?.targetType ?: GoalTargetType.NUMERIC)
    }
    var targetValueText by remember {
        mutableStateOf(initialTemplate?.targetValue?.toInt()?.toString() ?: "100")
    }
    var unit by remember {
        mutableStateOf(initialTemplate?.unit ?: "%")
    }
    var deadlineEpochDay by remember {
        mutableStateOf(
            if (initialTemplate != null) {
                LocalDate.now().plusMonths(initialTemplate.durationMonths).toEpochDay()
            } else {
                LocalDate.now().plusMonths(1).toEpochDay()
            }
        )
    }
    var priority by remember {
        mutableStateOf(initialTemplate?.priority ?: GoalPriority.MEDIUM)
    }
    var shortTermSacrifice by remember {
        mutableStateOf(initialTemplate?.shortTermSacrifice ?: "")
    }
    var longTermReward by remember {
        mutableStateOf(initialTemplate?.longTermReward ?: "")
    }

    val milestones = remember {
        mutableStateListOf<String>().apply {
            if (initialTemplate != null) {
                addAll(initialTemplate.suggestedMilestones)
            }
        }
    }
    var newMilestoneInput by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var titleError by remember { mutableStateOf(false) }

    // Pre-populate if editing existing goal
    var isInitialized by remember { mutableStateOf(false) }
    if (existingGoalWithDetails != null && !isInitialized) {
        val existing = existingGoalWithDetails!!
        title = existing.goal.title
        description = existing.goal.description
        category = existing.goal.category
        targetType = existing.goal.targetType
        targetValueText = existing.goal.targetValue.toInt().toString()
        unit = existing.goal.unit
        deadlineEpochDay = existing.goal.deadlineEpochDay
        priority = existing.goal.priority
        shortTermSacrifice = existing.goal.shortTermSacrifice
        longTermReward = existing.goal.longTermReward
        milestones.clear()
        milestones.addAll(existing.milestones.map { it.title })
        isInitialized = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (goalId == null) "Nouvel Objectif" else "Modifier l'objectif",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("add_edit_back_button")) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
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
            // Title & Motivation Section
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
                            text = "Intitulé & Motivation",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = title,
                            onValueChange = {
                                title = it
                                if (it.isNotBlank()) titleError = false
                            },
                            label = { Text("Quel est votre objectif ? *") },
                            placeholder = { Text("Ex: Courir mon premier 10 km") },
                            isError = titleError,
                            supportingText = {
                                if (titleError) Text("L'intitulé est obligatoire", color = MaterialTheme.colorScheme.error)
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_title_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Pourquoi cet objectif compte-t-il ?") },
                            placeholder = { Text("Ex: Avoir plus d'énergie au quotidien et être fier de mon corps.") },
                            minLines = 2,
                            maxLines = 4,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_why_input")
                        )
                    }
                }
            }

            // L'Équation de la Gratification (Court Terme vs Long Terme)
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
                            text = "L'Équation de la Gratification",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Ce que vous sacrifiez maintenant détermine votre grandeur future.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = shortTermSacrifice,
                            onValueChange = { shortTermSacrifice = it },
                            label = { Text("Plaisir immédiat renoncé / Inconfort accepté") },
                            placeholder = { Text("Ex: Scroller moins, me lever tôt, limiter les sorties coûteuses...") },
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_sacrifice_input")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = longTermReward,
                            onValueChange = { longTermReward = it },
                            label = { Text("Gratification à Long Terme (Votre Victoire)") },
                            placeholder = { Text("Ex: Indépendance financière, corps d'athlète, respect de soi...") },
                            minLines = 2,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("goal_reward_input")
                        )
                    }
                }
            }

            // Category Picker
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
                            text = "Domaine de vie",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GoalCategory.values().forEach { cat ->
                                val isSelected = category == cat
                                val color = Color(cat.colorHex)
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) color.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        if (isSelected) 2.dp else 0.dp,
                                        if (isSelected) color else Color.Transparent
                                    ),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { category = cat }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = getCategoryIcon(cat),
                                            contentDescription = null,
                                            tint = color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = cat.title,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Target Type (Mesurable, Jalons, Habitude)
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
                            text = "Type de mesure",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GoalTargetType.values().forEach { type ->
                                val isSelected = targetType == type
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable {
                                            targetType = type
                                            if (type == GoalTargetType.HABIT && unit == "%") {
                                                unit = "jours"
                                                targetValueText = "30"
                                            }
                                        }
                                ) {
                                    Column(
                                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 6.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Text(
                                            text = type.label,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        when (targetType) {
                            GoalTargetType.NUMERIC -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    OutlinedTextField(
                                        value = targetValueText,
                                        onValueChange = { targetValueText = it },
                                        label = { Text("Cible chiffrée") },
                                        placeholder = { Text("Ex: 10") },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("target_value_input")
                                    )
                                    OutlinedTextField(
                                        value = unit,
                                        onValueChange = { unit = it },
                                        label = { Text("Unité") },
                                        placeholder = { Text("Ex: km, €, livres") },
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("target_unit_input")
                                    )
                                }
                            }
                            GoalTargetType.HABIT -> {
                                OutlinedTextField(
                                    value = targetValueText,
                                    onValueChange = { targetValueText = it },
                                    label = { Text("Nombre de jours ciblés") },
                                    placeholder = { Text("Ex: 30") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            GoalTargetType.CHECKLIST -> {
                                Text(
                                    text = "Vous définirez ci-dessous les jalons et étapes pour mesurer la réussite.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            // Milestones Builder
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
                            text = "Jalons & Étapes clés (${milestones.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        milestones.forEachIndexed { index, ms ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${index + 1}.",
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryIndigo,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = ms,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { milestones.removeAt(index) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Supprimer",
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = newMilestoneInput,
                                onValueChange = { newMilestoneInput = it },
                                placeholder = { Text("Ex: Lire les 3 premiers chapitres", fontSize = 13.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("new_milestone_field")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    if (newMilestoneInput.isNotBlank()) {
                                        milestones.add(newMilestoneInput.trim())
                                        newMilestoneInput = ""
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo),
                                modifier = Modifier.testTag("add_milestone_button")
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Ajouter")
                            }
                        }
                    }
                }
            }

            // Deadline & Priority
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
                            text = "Échéance & Priorité",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick deadline buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                1L to "1 mois",
                                3L to "3 mois",
                                6L to "6 mois",
                                12L to "1 an"
                            ).forEach { (months, label) ->
                                val targetDay = LocalDate.now().plusMonths(months).toEpochDay()
                                val isSelected = deadlineEpochDay == targetDay
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { deadlineEpochDay = targetDay }
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Custom Date picker trigger
                        val deadlineDate = LocalDate.ofEpochDay(deadlineEpochDay)
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { showDatePicker = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = PrimaryIndigo
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "Date limite fixée au",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = deadlineDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy")),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Priorité",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            GoalPriority.values().forEach { prio ->
                                val isSelected = priority == prio
                                val color = Color(prio.colorHex)
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { priority = prio }
                                ) {
                                    Text(
                                        text = prio.label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        modifier = Modifier.padding(vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Save / Submit Button
            item {
                Button(
                    onClick = {
                        if (title.isBlank()) {
                            titleError = true
                            return@Button
                        }
                        val targetVal = targetValueText.toDoubleOrNull() ?: 100.0
                        if (goalId == null) {
                            viewModel.createGoal(
                                title = title,
                                description = description,
                                category = category,
                                targetType = targetType,
                                targetValue = targetVal,
                                unit = unit,
                                deadlineEpochDay = deadlineEpochDay,
                                priority = priority,
                                milestones = milestones.toList(),
                                shortTermSacrifice = shortTermSacrifice.trim(),
                                longTermReward = longTermReward.trim()
                            )
                        } else {
                            existingGoalWithDetails?.let { current ->
                                viewModel.updateGoal(
                                    current.goal.copy(
                                        title = title.trim(),
                                        description = description.trim(),
                                        category = category,
                                        targetType = targetType,
                                        targetValue = targetVal,
                                        unit = unit.trim(),
                                        deadlineEpochDay = deadlineEpochDay,
                                        priority = priority,
                                        shortTermSacrifice = shortTermSacrifice.trim(),
                                        longTermReward = longTermReward.trim()
                                    )
                                )
                            }
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("save_goal_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryIndigo)
                ) {
                    Text(
                        text = if (goalId == null) "Créer et s'engager !" else "Enregistrer les modifications",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = deadlineEpochDay * 86400000L
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis
                        if (millis != null) {
                            deadlineEpochDay = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.systemDefault())
                                .toLocalDate()
                                .toEpochDay()
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
