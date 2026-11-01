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
import com.example.maryroyal.data.model.AssessmentRecord
import com.example.maryroyal.data.model.CbcDescriptor
import com.example.maryroyal.data.model.Role
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun AssessmentsScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val learners by repository.getAllLearners().collectAsState(initial = emptyList())
    val assessments by repository.getAllAssessments().collectAsState(initial = emptyList())
    var showAddDialog by remember { mutableStateOf(false) }

    val canCreate = currentUser.role in listOf(Role.TEACHER, Role.HEAD_TEACHER, Role.DIRECTOR)
    val canApprove = currentUser.role in listOf(Role.HEAD_TEACHER, Role.DIRECTOR)

    if (showAddDialog) {
        AddAssessmentDialog(
            learners = learners,
            onDismiss = { showAddDialog = false },
            onSave = { assessment ->
                coroutineScope.launch {
                    repository.recordAssessment(assessment)
                    repository.logAudit(
                        actorName = currentUser.name,
                        role = currentUser.role.name,
                        action = "ASSESSMENT_RECORDED",
                        entity = "Assessment",
                        entityId = assessment.learnerId
                    )
                    showAddDialog = false
                }
            }
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
                tag = "Competency-Based Education (CBE)",
                title = "Assessments & Rubrics",
                subtitle = "Record formative competency rubrics (EE, ME, AE, BE) aligned with KICD CBC guidelines.",
                actionButton = if (canCreate) {
                    {
                        Button(
                            onClick = { showAddDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("btn_new_assessment")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = RoyalNavy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Record CBE Mark",
                                color = RoyalNavy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                } else null
            )
        }

        // Summary descriptors bar
        item {
            val eeCount = assessments.count { it.descriptor == CbcDescriptor.EE }
            val meCount = assessments.count { it.descriptor == CbcDescriptor.ME }
            val aeCount = assessments.count { it.descriptor == CbcDescriptor.AE }
            val beCount = assessments.count { it.descriptor == CbcDescriptor.BE }

            BentoCard {
                Text(
                    text = "CBC PERFORMANCE DISTRIBUTION",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    DescriptorPill("EE (80-100%)", "$eeCount", AccentEmerald)
                    DescriptorPill("ME (60-79%)", "$meCount", RoyalBlue)
                    DescriptorPill("AE (40-59%)", "$aeCount", RoyalGold)
                    DescriptorPill("BE (<40%)", "$beCount", AccentRose)
                }
            }
        }

        item {
            Text(
                text = "RELEASED & VERIFIED CBC MARKS (${assessments.size})",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(assessments) { record ->
            BentoCard(modifier = Modifier.testTag("assessment_card_${record.id}")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = record.learnerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            StatusBadge(
                                text = record.descriptor.code,
                                containerColor = when (record.descriptor) {
                                    CbcDescriptor.EE -> AccentEmeraldLight
                                    CbcDescriptor.ME -> Color(0xFFDBEAFE)
                                    CbcDescriptor.AE -> RoyalGoldLight
                                    CbcDescriptor.BE -> AccentRoseLight
                                },
                                contentColor = when (record.descriptor) {
                                    CbcDescriptor.EE -> AccentEmerald
                                    CbcDescriptor.ME -> RoyalBlue
                                    CbcDescriptor.AE -> RoyalGold
                                    CbcDescriptor.BE -> AccentRose
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${record.grade} · ${record.subject} · ${record.term}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalBlueDark,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Strand: ${record.strand} · Competency: ${record.competency}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                        if (record.teacherComment.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "“${record.teacherComment}”",
                                style = MaterialTheme.typography.bodyMedium,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${record.score}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = RoyalNavy
                        )
                        Text(
                            text = record.status,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = AccentEmerald
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DescriptorPill(label: String, count: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Black,
            color = color
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun AddAssessmentDialog(
    learners: List<com.example.maryroyal.data.model.Learner>,
    onDismiss: () -> Unit,
    onSave: (AssessmentRecord) -> Unit
) {
    var selectedLearner by remember { mutableStateOf(learners.firstOrNull()) }
    var subject by remember { mutableStateOf("Mathematics") }
    var strand by remember { mutableStateOf("Numbers & Operations") }
    var competency by remember { mutableStateOf("Fractions & Division") }
    var scoreText by remember { mutableStateOf("85") }
    var comment by remember { mutableStateOf("Excellent problem-solving approach.") }

    val subjects = listOf("Mathematics", "English", "Kiswahili", "Science & Technology", "Social Studies", "Creative Arts", "Pre-Technical Studies")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record CBE Assessment", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text("Select Learner:", style = MaterialTheme.typography.labelSmall)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(learners) { l ->
                        FilterChip(
                            selected = selectedLearner?.id == l.id,
                            onClick = { selectedLearner = l },
                            label = { Text(l.name, fontSize = 11.sp) }
                        )
                    }
                }

                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Learning Area / Subject") },
                    modifier = Modifier.fillMaxWidth().testTag("assessment_input_subject")
                )

                OutlinedTextField(
                    value = strand,
                    onValueChange = { strand = it },
                    label = { Text("Strand") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = competency,
                    onValueChange = { competency = it },
                    label = { Text("Specific Learning Outcome / Competency") },
                    modifier = Modifier.fillMaxWidth().testTag("assessment_input_competency")
                )

                OutlinedTextField(
                    value = scoreText,
                    onValueChange = { scoreText = it },
                    label = { Text("Score (0 - 100)") },
                    modifier = Modifier.fillMaxWidth().testTag("assessment_input_score")
                )

                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("Teacher Observation Remarks") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val s = scoreText.toIntOrNull() ?: 75
                    val learner = selectedLearner ?: learners.first()
                    val record = AssessmentRecord(
                        learnerId = learner.id,
                        learnerName = learner.name,
                        grade = learner.grade,
                        subject = subject,
                        term = "Term 1 2026",
                        strand = strand,
                        competency = competency,
                        score = s,
                        teacherComment = comment,
                        status = "RELEASED"
                    )
                    onSave(record)
                },
                modifier = Modifier.testTag("btn_save_assessment")
            ) {
                Text("Save Assessment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
