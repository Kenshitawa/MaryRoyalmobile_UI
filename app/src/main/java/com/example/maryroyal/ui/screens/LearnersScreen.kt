package com.example.maryroyal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maryroyal.data.model.Learner
import com.example.maryroyal.data.model.Role
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun LearnersScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val allLearners by repository.getAllLearners().collectAsState(initial = emptyList())
    var selectedGrade by remember { mutableStateOf("All") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedLearnerForDetail by remember { mutableStateOf<Learner?>(null) }

    val grades = listOf("All", "PP1", "PP2", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9")

    val isAccountant = currentUser.role == Role.ACCOUNTANT

    val filtered = allLearners.filter { learner ->
        val matchesGrade = if (selectedGrade == "All") true else learner.grade == selectedGrade
        val matchesSearch = learner.name.contains(searchQuery, ignoreCase = true) ||
                learner.admissionNo.contains(searchQuery, ignoreCase = true)
        matchesGrade && matchesSearch
    }

    if (selectedLearnerForDetail != null) {
        val l = selectedLearnerForDetail!!
        LearnerDetailDialog(
            learner = l,
            currentUser = currentUser,
            repository = repository,
            onDismiss = { selectedLearnerForDetail = null }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeroBanner(
                tag = "Student Information System",
                title = "Learner Directory",
                subtitle = if (isAccountant) {
                    "Learner identity and linked fee records. (Academic and health details restricted)."
                } else {
                    "Comprehensive learner profiles, CBE performance, health safeguarding, and guardian links."
                }
            )
        }

        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search learners...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_learners_input"),
                shape = RoundedCornerShape(14.dp)
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(grades) { g ->
                    FilterChip(
                        selected = selectedGrade == g,
                        onClick = { selectedGrade = g },
                        label = { Text(g, fontSize = 12.sp) },
                        shape = CircleShape
                    )
                }
            }
        }

        item {
            Text(
                text = "ACTIVE LEARNERS (${filtered.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(filtered) { learner ->
            BentoCard(
                onClick = {
                    selectedLearnerForDetail = learner
                    coroutineScope.launch {
                        repository.logAudit(
                            actorName = currentUser.name,
                            role = currentUser.role.name,
                            action = "PROFILE_VIEW",
                            entity = "Learner",
                            entityId = learner.id
                        )
                    }
                },
                modifier = Modifier.testTag("learner_row_${learner.id}")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(RoyalBlue.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = learner.name.take(1),
                                color = RoyalBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                        Column {
                            Text(
                                text = learner.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${learner.admissionNo} · ${learner.grade} (${learner.stream})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LearnerDetailDialog(
    learner: Learner,
    currentUser: User,
    repository: MaryRoyalRepository,
    onDismiss: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var selectedTab by remember { mutableIntStateOf(0) }
    val isAccountant = currentUser.role == Role.ACCOUNTANT
    val tabs = if (isAccountant) listOf("Overview", "Finance") else listOf("Overview", "CBC Marks", "Health & Welfare", "Guardians")

    val assessments by repository.getAssessmentsForLearner(learner.id).collectAsState(initial = emptyList())
    val invoices by repository.getInvoicesForLearner(learner.id).collectAsState(initial = emptyList())
    val payments by repository.getPaymentsForLearner(learner.id).collectAsState(initial = emptyList())

    val totalBilled = invoices.sumOf { it.totalAmount }
    val totalPaid = payments.filter { it.status == "RECORDED" }.sumOf { it.amount }
    val balance = totalBilled - totalPaid

    // Log health view if health tab is opened by authorized user
    LaunchedEffect(selectedTab) {
        if (!isAccountant && selectedTab == 2) {
            repository.logAudit(
                actorName = currentUser.name,
                role = currentUser.role.name,
                action = "HEALTH_VIEW",
                entity = "LearnerProfile",
                entityId = learner.id
            )
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = learner.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "${learner.admissionNo} · ${learner.grade} (${learner.stream})",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusBadge(
                        text = learner.admissionStatus,
                        containerColor = AccentEmeraldLight,
                        contentColor = AccentEmerald
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                TabRow(selectedTabIndex = selectedTab) {
                    tabs.forEachIndexed { index, tabTitle ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(tabTitle, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                when {
                    selectedTab == 0 -> {
                        // Overview
                        DetailRow("UPI Number", learner.upiNumber)
                        DetailRow("Date of Birth", learner.dateOfBirth)
                        DetailRow("Gender", learner.gender)
                        DetailRow("Residence", "${learner.village}, ${learner.physicalAddress}")
                        DetailRow("County / Ward", "${learner.county} · ${learner.ward}")
                        DetailRow("Current Fee Balance", "KES ${balance.toInt()}", valueColor = if (balance > 0) AccentRose else AccentEmerald)
                    }
                    isAccountant && selectedTab == 1 -> {
                        // Finance for Accountant
                        DetailRow("Total Invoiced", "KES ${totalBilled.toInt()}")
                        DetailRow("Total Paid", "KES ${totalPaid.toInt()}")
                        DetailRow("Outstanding Fee Balance", "KES ${balance.toInt()}", valueColor = if (balance > 0) AccentRose else AccentEmerald)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Recent Payments:", style = MaterialTheme.typography.labelSmall)
                        if (payments.isEmpty()) {
                            Text("No recorded payments yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            payments.take(3).forEach { p ->
                                Text("• ${p.receiptNo}: KES ${p.amount.toInt()} via ${p.method.label} (${p.date})", fontSize = 12.sp)
                            }
                        }
                    }
                    !isAccountant && selectedTab == 1 -> {
                        // CBC Marks
                        if (assessments.isEmpty()) {
                            Text("No released CBE assessments recorded yet.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            assessments.forEach { a ->
                                BentoCard(backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(a.subject, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${a.score}% (${a.descriptor.code})", fontWeight = FontWeight.Black, color = RoyalBlue)
                                    }
                                    Text(a.competency, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    if (a.teacherComment.isNotBlank()) {
                                        Text("Teacher Note: \"${a.teacherComment}\"", fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                                    }
                                }
                            }
                        }
                    }
                    !isAccountant && selectedTab == 2 -> {
                        // Health & Safeguarding
                        Text("CONFIDENTIAL HEALTH SAFEGUARDING RECORD", style = MaterialTheme.typography.labelSmall, color = AccentRose)
                        DetailRow("Blood Group", learner.bloodGroup)
                        DetailRow("Known Medical Conditions", learner.knownConditions)
                        DetailRow("Allergies", learner.allergies)
                        DetailRow("Special Learning Needs", learner.specialNeeds)
                        Text(
                            text = "Access to this health record has been appended to the immutable school audit trail.",
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    !isAccountant && selectedTab == 3 -> {
                        // Guardians
                        DetailRow("Guardian Name", learner.guardianName)
                        DetailRow("Relationship", learner.guardianRelation)
                        DetailRow("Primary Phone", learner.guardianPhone)
                        DetailRow("Emergency Contact", learner.emergencyContactName)
                        DetailRow("Emergency Phone", learner.emergencyContactPhone)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Composable
fun DetailRow(label: String, value: String, valueColor: Color = MaterialTheme.colorScheme.onSurface) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp
        )
        Text(
            text = value.ifBlank { "—" },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            fontSize = 12.sp
        )
    }
}
