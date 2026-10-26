package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrackChanges
import com.example.ui.viewmodel.DisciplineProfile
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GoalCategory
import com.example.data.model.GoalTargetType
import com.example.data.model.GoalWithDetails
import com.example.ui.components.AnimatedProgressBar
import com.example.ui.components.CategoryBadge
import com.example.ui.components.DeadlineBadge
import com.example.ui.components.PriorityBadge
import com.example.ui.components.getCategoryIcon
import com.example.ui.theme.AmberAccent
import com.example.ui.theme.PrimaryIndigo
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.GoalSortOption
import com.example.ui.viewmodel.GoalStatusFilter
import com.example.ui.viewmodel.GoalViewModel
import java.time.LocalDate

@Composable
fun HomeScreen(
    viewModel: GoalViewModel,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToAddGoal: () -> Unit,
    onOpenTemplates: () -> Unit,
    onOpenStats: () -> Unit,
    onOpenSmartGuide: () -> Unit,
    onOpenDisciplineJourney: () -> Unit,
    onOpenAntiTemptation: () -> Unit,
    onOpenGratificationPhilosophy: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val goals by viewModel.goalsList.collectAsState()
    val stats by viewModel.stats.collectAsState()
    val todayHabits by viewModel.todayHabits.collectAsState()
    val profile by viewModel.disciplineProfile.collectAsState()

    var showSortMenu by remember { mutableStateOf(false) }
    var isSearchExpanded by remember { mutableStateOf(false) }

    val currentQuote = viewModel.quotes[uiState.currentQuoteIndex]

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Free and Gratification Banner
            item {
                FreeAndGratificationBanner(
                    onOpenPhilosophy = onOpenGratificationPhilosophy
                )
            }

            // Header Top Bar
            item {
                HomeHeader(
                    bestStreak = stats.bestStreak,
                    onOpenStats = onOpenStats,
                    onOpenSmartGuide = onOpenSmartGuide,
                    onOpenDisciplineJourney = onOpenDisciplineJourney,
                    onOpenGratificationPhilosophy = onOpenGratificationPhilosophy
                )
            }

            // Anti-Temptation Shield Quick Action Card
            item {
                AntiTemptationBannerCard(
                    onOpenAntiTemptation = onOpenAntiTemptation
                )
            }

            // Discipline Level & Journey Card
            item {
                DisciplineLevelCard(
                    profile = profile,
                    onOpenDisciplineJourney = onOpenDisciplineJourney
                )
            }

            // Daily Motivation Quote Card
            item {
                QuoteBannerCard(
                    quote = currentQuote.first,
                    author = currentQuote.second,
                    onRefreshQuote = { viewModel.nextQuote() }
                )
            }

            // Stats Summary Strip
            item {
                StatsSummaryCard(
                    total = stats.totalCount,
                    inProgress = stats.inProgressCount,
                    completed = stats.completedCount,
                    overallPercent = stats.overallPercentage,
                    onOpenStats = onOpenStats
                )
            }

            // Today's Habits Section (if any habit goals exist)
            if (todayHabits.isNotEmpty()) {
                item {
                    TodayHabitsSection(
                        habits = todayHabits,
                        onToggleHabit = { viewModel.toggleHabitToday(it) },
                        onNavigateToDetail = onNavigateToDetail
                    )
                }
            }

            // Search Bar & Filter Controls
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.setSearchQuery(it) },
                            placeholder = { Text("Rechercher un objectif...", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Recherche",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (uiState.searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Effacer"
                                        )
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryIndigo,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_text_field")
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        // Sort Menu Button
                        Box {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { showSortMenu = true }
                                    .testTag("sort_button")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FilterList,
                                        contentDescription = "Trier",
                                        tint = PrimaryIndigo,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                GoalSortOption.values().forEach { option ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = option.label,
                                                fontWeight = if (uiState.sortOption == option) FontWeight.Bold else FontWeight.Normal,
                                                color = if (uiState.sortOption == option) PrimaryIndigo else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            viewModel.setSortOption(option)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Status Filter Tabs (Tous, En cours, Atteints)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GoalStatusFilter.values().forEach { filter ->
                            val isSelected = uiState.statusFilter == filter
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) PrimaryIndigo else MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setStatusFilter(filter) }
                                    .testTag("filter_${filter.name.lowercase()}")
                            ) {
                                Text(
                                    text = filter.label,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // "Toutes catégories" chip
                        val isAllSelected = uiState.selectedCategory == null
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isAllSelected) SecondaryTeal.copy(alpha = 0.15f) else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isAllSelected) SecondaryTeal else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { viewModel.setCategoryFilter(null) }
                        ) {
                            Text(
                                text = "Toutes",
                                fontSize = 12.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isAllSelected) SecondaryTeal else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        GoalCategory.values().forEach { category ->
                            val isCatSelected = uiState.selectedCategory == category
                            val catColor = Color(category.colorHex)
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isCatSelected) catColor.copy(alpha = 0.2f) else Color.Transparent,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (isCatSelected) catColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setCategoryFilter(if (isCatSelected) null else category) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = getCategoryIcon(category),
                                        contentDescription = null,
                                        tint = if (isCatSelected) catColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = category.title,
                                        fontSize = 12.sp,
                                        fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCatSelected) catColor else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Goals List Header
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mes Objectifs (${goals.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = uiState.sortOption.label,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Empty state
            if (goals.isEmpty()) {
                item {
                    EmptyGoalsCard(
                        onAddGoal = onNavigateToAddGoal,
                        onOpenTemplates = onOpenTemplates
                    )
                }
            } else {
                items(goals, key = { it.goal.id }) { goalItem ->
                    GoalCardItem(
                        goalWithDetails = goalItem,
                        onClick = { onNavigateToDetail(goalItem.goal.id) },
                        onQuickProgress = { amount ->
                            viewModel.addProgress(goalItem.goal.id, amount, "Progression rapide")
                        },
                        onToggleHabit = {
                            viewModel.toggleHabitToday(goalItem.goal.id)
                        }
                    )
                }
            }
        }

        // Floating Action Buttons (Add Goal & Templates)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onOpenTemplates() }
                    .testTag("templates_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Idées",
                        tint = AmberAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Idées d'objectifs",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FloatingActionButton(
                onClick = onNavigateToAddGoal,
                containerColor = PrimaryIndigo,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.testTag("add_goal_fab")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Nouvel objectif")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nouvel Objectif", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun FreeAndGratificationBanner(
    onOpenPhilosophy: () -> Unit
) {
    Surface(
        color = PrimaryIndigo.copy(alpha = 0.07f),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenPhilosophy() }
            .testTag("free_gratification_banner")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = null,
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "100% GRATUIT • SANS ABONNEMENT • DÉDIÉ À VOTRE SUCCÈS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo,
                    letterSpacing = 0.5.sp
                )
            }
            Text(
                text = "Philosophie →",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryIndigo
            )
        }
    }
}

@Composable
fun AntiTemptationBannerCard(
    onOpenAntiTemptation: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.25f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenAntiTemptation() }
            .testTag("anti_temptation_card")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEF4444).copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = Color(0xFFEF4444),
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Bouclier Anti-Gratification Immédiate",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Face à une envie de procrastiner ou de céder ? Cliquez ici pour bloquer la tentation (+35 XP).",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }
            Icon(
                imageVector = Icons.Default.CompareArrows,
                contentDescription = null,
                tint = Color(0xFFEF4444),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HomeHeader(
    bestStreak: Int,
    onOpenStats: () -> Unit,
    onOpenSmartGuide: () -> Unit,
    onOpenDisciplineJourney: () -> Unit,
    onOpenGratificationPhilosophy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "DISCIPLINE",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = PrimaryIndigo,
                letterSpacing = 1.sp
            )
            Text(
                text = "Bâtir l'Excellence & l'Auto-Discipline",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Streak badge
            if (bestStreak > 0) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = AmberAccent.copy(alpha = 0.15f),
                    modifier = Modifier.padding(end = 4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Série",
                            tint = AmberAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${bestStreak}j",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AmberAccent
                        )
                    }
                }
            }

            // Parcours Discipline par Niveaux
            IconButton(
                onClick = onOpenDisciplineJourney,
                modifier = Modifier.testTag("discipline_journey_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Niveaux de Discipline",
                    tint = PrimaryIndigo
                )
            }

            // Loi de la Gratification
            IconButton(
                onClick = onOpenGratificationPhilosophy,
                modifier = Modifier.testTag("gratification_philosophy_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.CompareArrows,
                    contentDescription = "Loi de la Gratification",
                    tint = PrimaryIndigo
                )
            }

            // SMART method tips
            IconButton(
                onClick = onOpenSmartGuide,
                modifier = Modifier.testTag("smart_guide_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Méthode SMART",
                    tint = SecondaryTeal
                )
            }

            // Trophies / Stats button
            IconButton(
                onClick = onOpenStats,
                modifier = Modifier.testTag("stats_nav_button")
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "Statistiques & Succès",
                    tint = AmberAccent
                )
            }
        }
    }
}

@Composable
fun DisciplineLevelCard(
    profile: DisciplineProfile,
    onOpenDisciplineJourney: () -> Unit
) {
    val levelColor = Color(profile.level.colorHex)
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, levelColor.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenDisciplineJourney() }
            .testTag("discipline_level_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(levelColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = levelColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "${profile.level.name} : ${profile.level.title}",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Chemin de l'Auto-Discipline",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = levelColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${profile.totalXp} XP",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = levelColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            AnimatedProgressBar(
                progress = profile.progressFraction,
                categoryColor = levelColor,
                height = 6.dp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Progression du niveau : ${(profile.progressFraction * 100).toInt()}%",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Explorer les 5 Niveaux →",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryIndigo
                )
            }
        }
    }
}

@Composable
fun QuoteBannerCard(
    quote: String,
    author: String,
    onRefreshQuote: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.FormatQuote,
                contentDescription = null,
                tint = PrimaryIndigo,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "« $quote »",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "— $author",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryIndigo
                )
            }
            IconButton(
                onClick = onRefreshQuote,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Changer la citation",
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun StatsSummaryCard(
    total: Int,
    inProgress: Int,
    completed: Int,
    overallPercent: Int,
    onOpenStats: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onOpenStats() }
            .testTag("stats_summary_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tableau de Bord Global",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$overallPercent% atteint",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryIndigo
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedProgressBar(
                progress = overallPercent / 100f,
                categoryColor = PrimaryIndigo,
                height = 6.dp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(label = "Actifs", value = "$inProgress", color = PrimaryIndigo)
                StatItem(label = "Atteints 🏆", value = "$completed", color = SuccessGreen)
                StatItem(label = "Total", value = "$total", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TodayHabitsSection(
    habits: List<GoalWithDetails>,
    onToggleHabit: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val today = LocalDate.now().toEpochDay()
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = AmberAccent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Habitudes d'aujourd'hui",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(habits) { habitItem ->
                val isDoneToday = habitItem.isCheckedInOn(today)
                val catColor = Color(habitItem.goal.category.colorHex)

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isDoneToday) SuccessGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isDoneToday) SuccessGreen else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigateToDetail(habitItem.goal.id) }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onToggleHabit(habitItem.goal.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = if (isDoneToday) Icons.Default.CheckCircle else Icons.Default.TrackChanges,
                                contentDescription = "Pointer",
                                tint = if (isDoneToday) SuccessGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = habitItem.goal.title,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Série : ${habitItem.currentStreak} jours 🔥",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (habitItem.currentStreak > 0) AmberAccent else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GoalCardItem(
    goalWithDetails: GoalWithDetails,
    onClick: () -> Unit,
    onQuickProgress: (Double) -> Unit,
    onToggleHabit: () -> Unit
) {
    val goal = goalWithDetails.goal
    val catColor = Color(goal.category.colorHex)
    val progress = goalWithDetails.progressFraction
    val percent = goalWithDetails.progressPercent

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (goal.isCompleted) SuccessGreen.copy(alpha = 0.45f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .testTag("goal_card_${goal.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Priority row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryBadge(category = goal.category)
                    Spacer(modifier = Modifier.width(6.dp))
                    PriorityBadge(priority = goal.priority)
                }

                DeadlineBadge(
                    daysRemaining = goalWithDetails.daysRemaining,
                    isCompleted = goal.isCompleted
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = goal.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // Description / "Mon Pourquoi"
            if (goal.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Pourquoi : ${goal.description}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar & Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val valueText = when (goal.targetType) {
                    GoalTargetType.NUMERIC -> {
                        val currentStr = if (goal.currentValue % 1.0 == 0.0) goal.currentValue.toInt().toString() else "%.1f".format(goal.currentValue)
                        val targetStr = if (goal.targetValue % 1.0 == 0.0) goal.targetValue.toInt().toString() else "%.1f".format(goal.targetValue)
                        "$currentStr / $targetStr ${goal.unit}"
                    }
                    GoalTargetType.CHECKLIST -> {
                        val done = goalWithDetails.milestones.count { it.isCompleted }
                        "$done / ${goalWithDetails.milestones.size} jalons"
                    }
                    GoalTargetType.HABIT -> {
                        "${goalWithDetails.habitCheckIns.size} / ${goal.targetValue.toInt()} ${goal.unit} • Série : ${goalWithDetails.currentStreak}j 🔥"
                    }
                }

                Text(
                    text = valueText,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (goal.isCompleted) SuccessGreen else catColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            AnimatedProgressBar(
                progress = progress,
                categoryColor = if (goal.isCompleted) SuccessGreen else catColor
            )

            // Quick actions for numeric goals or habits
            if (!goal.isCompleted) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (goal.targetType) {
                        GoalTargetType.NUMERIC -> {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = catColor.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onQuickProgress(1.0) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "+1",
                                        tint = catColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "+1 ${goal.unit}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = catColor
                                    )
                                }
                            }
                        }
                        GoalTargetType.HABIT -> {
                            val today = LocalDate.now().toEpochDay()
                            val doneToday = goalWithDetails.isCheckedInOn(today)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (doneToday) SuccessGreen.copy(alpha = 0.15f) else catColor.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { onToggleHabit() }
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (doneToday) Icons.Default.Check else Icons.Default.TrackChanges,
                                        contentDescription = "Pointer",
                                        tint = if (doneToday) SuccessGreen else catColor,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (doneToday) "Fait aujourd'hui !" else "Pointer aujourd'hui",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (doneToday) SuccessGreen else catColor
                                    )
                                }
                            }
                        }
                        GoalTargetType.CHECKLIST -> {
                            val remainingMilestones = goalWithDetails.milestones.count { !it.isCompleted }
                            Text(
                                text = if (remainingMilestones == 0) "Prêt à valider !" else "$remainingMilestones jalon(s) restant(s)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyGoalsCard(
    onAddGoal: () -> Unit,
    onOpenTemplates: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryIndigo.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.TrackChanges,
                    contentDescription = null,
                    tint = PrimaryIndigo,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Aucun objectif trouvé",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "« Un voyage de mille lieues commence toujours par un premier pas. » Définissez votre premier cap dès maintenant !",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryIndigo,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onAddGoal() }
                ) {
                    Text(
                        text = "Créer un objectif",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenTemplates() }
                ) {
                    Text(
                        text = "Voir les modèles",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}
