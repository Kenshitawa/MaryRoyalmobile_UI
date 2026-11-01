package com.example.maryroyal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Security
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
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*

@Composable
fun ReportsAndAuditScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val learners by repository.getAllLearners().collectAsState(initial = emptyList())
    val invoices by repository.getAllInvoices().collectAsState(initial = emptyList())
    val payments by repository.getAllPayments().collectAsState(initial = emptyList())
    val assessments by repository.getAllAssessments().collectAsState(initial = emptyList())
    val auditEvents by repository.getAuditEvents().collectAsState(initial = emptyList())

    var activeTab by remember { mutableIntStateOf(0) } // 0: Executive Analytics, 1: Immutable Audit Trail

    // Enrollment Bands
    val ecdCount = learners.count { it.grade.startsWith("PP") }
    val lowerPrimaryCount = learners.count { it.grade in listOf("Grade 1", "Grade 2", "Grade 3") }
    val upperPrimaryCount = learners.count { it.grade in listOf("Grade 4", "Grade 5", "Grade 6") }
    val jssCount = learners.count { it.grade in listOf("Grade 7", "Grade 8", "Grade 9") }
    val totalLearners = learners.size.coerceAtLeast(1)

    // Finance Analytics
    val totalInvoiced = invoices.sumOf { it.totalAmount }
    val totalCollected = payments.filter { it.status == "RECORDED" }.sumOf { it.amount }
    val collectionRate = if (totalInvoiced > 0) ((totalCollected / totalInvoiced) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeroBanner(
                tag = "Governance & Strategic Intelligence",
                title = "School Analytics & Audit",
                subtitle = "Live executive statistics for MOE compliance, fee analytics, and tamper-proof security log."
            )
        }

        item {
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Executive Reports", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Security Audit Trail", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        if (activeTab == 0) {
            // Enrollment by Curriculum Band
            item {
                BentoCard {
                    Text(
                        text = "ENROLLMENT BY CURRICULUM BAND",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    EnrollmentBar("Early Childhood (PP1 - PP2)", ecdCount, totalLearners, RoyalBlue)
                    EnrollmentBar("Lower Primary (Grade 1 - 3)", lowerPrimaryCount, totalLearners, AccentEmerald)
                    EnrollmentBar("Upper Primary (Grade 4 - 6)", upperPrimaryCount, totalLearners, RoyalGold)
                    EnrollmentBar("Junior Secondary (Grade 7 - 9)", jssCount, totalLearners, AccentPurple)
                }
            }

            // Fee Collection Breakdown
            item {
                BentoCard {
                    Text(
                        text = "TERM 1 2026 FEE REVENUE SUMMARY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Total Billed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("KES ${(totalInvoiced / 1000).toInt()}k", fontWeight = FontWeight.Black, fontSize = 16.sp)
                        }
                        Column {
                            Text("Total Collected", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("KES ${(totalCollected / 1000).toInt()}k", fontWeight = FontWeight.Black, fontSize = 16.sp, color = AccentEmerald)
                        }
                        Column {
                            Text("Collection Rate", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$collectionRate%", fontWeight = FontWeight.Black, fontSize = 16.sp, color = RoyalBlue)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LinearProgressIndicator(
                        progress = { (collectionRate / 100f).coerceIn(0f, 1f) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(CircleShape),
                        color = AccentEmerald,
                        trackColor = AccentEmerald.copy(alpha = 0.2f)
                    )
                }
            }

            // CBE Grade Performance
            item {
                BentoCard {
                    Text(
                        text = "OVERALL CBC PERFORMANCE QUALITY",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val avgScore = if (assessments.isNotEmpty()) assessments.map { it.score }.average().toInt() else 0
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Mean Competency Score", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$avgScore% on Rubric Scales", fontWeight = FontWeight.Black, fontSize = 20.sp, color = RoyalNavy)
                        }
                        StatusBadge(
                            text = if (avgScore >= 80) "Exceeding Expectations" else "Meeting Expectations",
                            containerColor = AccentEmeraldLight,
                            contentColor = AccentEmerald
                        )
                    }
                }
            }
        } else {
            // Security Audit Trail
            item {
                Text(
                    text = "IMMUTABLE AUDIT TRAIL (${auditEvents.size} EVENTS)",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(auditEvents) { event ->
                BentoCard(modifier = Modifier.testTag("audit_row_${event.id}")) {
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
                                    text = event.action.replace("_", " "),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                StatusBadge(
                                    text = event.actorRole,
                                    containerColor = RoyalNavy.copy(alpha = 0.1f),
                                    contentColor = RoyalNavy
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Actor: ${event.actorName} · Entity: ${event.entity} (${event.entityId})",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                        Text(
                            text = event.timestamp,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnrollmentBar(band: String, count: Int, total: Int, color: Color) {
    val pct = (count.toFloat() / total).coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(band, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Text("$count learners (${(pct * 100).toInt()}%)", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { pct },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(CircleShape),
            color = color,
            trackColor = color.copy(alpha = 0.15f)
        )
    }
}
