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
import com.example.maryroyal.data.model.Role
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatCard
import com.example.maryroyal.ui.components.StatusBadge
import com.example.maryroyal.ui.theme.*

@Composable
fun DashboardScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onSwitchUser: (User) -> Unit,
    onNavigate: (String) -> Unit
) {
    val learners by repository.getAllLearners().collectAsState(initial = emptyList())
    val users by repository.getAllUsers().collectAsState(initial = emptyList())
    val invoices by repository.getAllInvoices().collectAsState(initial = emptyList())
    val payments by repository.getAllPayments().collectAsState(initial = emptyList())
    val assessments by repository.getAllAssessments().collectAsState(initial = emptyList())

    val totalLearners = learners.size
    val totalInvoiced = invoices.sumOf { it.totalAmount }
    val totalCollected = payments.filter { it.status == "RECORDED" }.sumOf { it.amount }
    val collectionRate = if (totalInvoiced > 0) ((totalCollected / totalInvoiced) * 100).toInt() else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Role switcher strip
        item {
            Column {
                Text(
                    text = "ACTIVE VIEWPOINT / ROLE",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(users) { u ->
                        val isSelected = u.id == currentUser.id
                        FilterChip(
                            selected = isSelected,
                            onClick = { onSwitchUser(u) },
                            label = {
                                Text(
                                    text = "${u.role.title} (${u.name.split(" ").first()})",
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalNavy,
                                selectedLabelColor = Color.White
                            ),
                            shape = CircleShape
                        )
                    }
                }
            }
        }

        // 2. Hero Banner
        item {
            HeroBanner(
                tag = "Mary Royal School · Obunga, Kisumu",
                title = "Karibu, ${currentUser.name.split(" ").first()}",
                subtitle = when (currentUser.role) {
                    Role.DIRECTOR -> "Executive overview of CBE learning, admissions, and school operations."
                    Role.HEAD_TEACHER -> "Academic leadership, teacher reviews, and school attendance."
                    Role.SECRETARY -> "Admissions desk, student records, and learner enrollment."
                    Role.ACCOUNTANT -> "Fee invoices, M-PESA & bank collections, and receipts."
                    Role.TEACHER -> "Competency assessments, daily class register, and timetable."
                    Role.PARENT -> "Your linked children's CBC evaluations, attendance, and fee balance."
                    Role.LEARNER -> "Explore your learning tasks, released grades, and study resources."
                    else -> "School management portal."
                },
                actionButton = {
                    Button(
                        onClick = { onNavigate("admissions") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("action_quick_admissions")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Admissions Desk",
                            color = RoyalNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        // 3. Bento KPI Cards Grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Learners",
                    value = "$totalLearners",
                    subtitle = "Enrolled across PP1-Gr9",
                    icon = Icons.Default.School,
                    iconTint = RoyalBlue,
                    modifier = Modifier.weight(1f),
                    testTag = "stat_learners"
                )
                StatCard(
                    title = "Collection",
                    value = "$collectionRate%",
                    subtitle = "KES ${(totalCollected / 1000).toInt()}k of ${(totalInvoiced / 1000).toInt()}k",
                    icon = Icons.Default.AccountBalanceWallet,
                    iconTint = AccentEmerald,
                    iconBg = AccentEmerald.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f),
                    testTag = "stat_finance"
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Assessments",
                    value = "${assessments.size}",
                    subtitle = "CBE competency marks",
                    icon = Icons.Default.AssignmentTurnedIn,
                    iconTint = AccentPurple,
                    iconBg = AccentPurple.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f),
                    testTag = "stat_assessments"
                )
                StatCard(
                    title = "Attendance",
                    value = "94%",
                    subtitle = "Today's daily presence",
                    icon = Icons.Default.CheckCircle,
                    iconTint = RoyalGold,
                    iconBg = RoyalGold.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f),
                    testTag = "stat_attendance"
                )
            }
        }

        // 4. Quick Action Modules
        item {
            Text(
                text = "SCHOOL WORKSPACES",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(10.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                WorkspaceTile(
                    title = "Learner Directory & Profiles",
                    description = "Access grades PP1 through Grade 9, medical info, and emergency contacts.",
                    icon = Icons.Default.People,
                    tag = "Directory",
                    tagColor = RoyalBlue,
                    onClick = { onNavigate("learners") }
                )
                WorkspaceTile(
                    title = "CBE Assessment & Grading",
                    description = "Record CBC levels (EE, ME, AE, BE), term scores, and rubrics.",
                    icon = Icons.Default.MenuBook,
                    tag = "CBC/CBE",
                    tagColor = AccentPurple,
                    onClick = { onNavigate("assessments") }
                )
                WorkspaceTile(
                    title = "Daily Attendance Register",
                    description = "Fast mark learners present, late, or absent with notes.",
                    icon = Icons.Default.CalendarToday,
                    tag = "Roll Call",
                    tagColor = AccentEmerald,
                    onClick = { onNavigate("attendance") }
                )
                WorkspaceTile(
                    title = "Fee Invoices & Payments",
                    description = "Track balances, record M-PESA & Bank fees, and generate receipts.",
                    icon = Icons.Default.ReceiptLong,
                    tag = "Accounts",
                    tagColor = RoyalGold,
                    onClick = { onNavigate("finance") }
                )
                WorkspaceTile(
                    title = "School Timetable",
                    description = "Master 8-period weekly schedule across grades and teaching staff.",
                    icon = Icons.Default.Schedule,
                    tag = "Schedule",
                    tagColor = RoyalNavy,
                    onClick = { onNavigate("timetable") }
                )
                WorkspaceTile(
                    title = "Executive Reports & Audit Log",
                    description = "MOE compliance data, fee collection analytics, and tamper-proof trail.",
                    icon = Icons.Default.Analytics,
                    tag = "Governance",
                    tagColor = RoyalBlueDark,
                    onClick = { onNavigate("reports") }
                )
            }
        }
    }
}

@Composable
private fun WorkspaceTile(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tag: String,
    tagColor: Color,
    onClick: () -> Unit
) {
    BentoCard(
        onClick = onClick,
        modifier = Modifier.testTag("workspace_${tag.lowercase()}")
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(tagColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = tagColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        maxLines = 2
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
