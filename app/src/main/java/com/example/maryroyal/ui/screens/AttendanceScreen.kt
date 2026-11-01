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
import com.example.maryroyal.data.model.AttendanceRecord
import com.example.maryroyal.data.model.AttendanceStatus
import com.example.maryroyal.data.model.User
import com.example.maryroyal.data.repository.MaryRoyalRepository
import com.example.maryroyal.ui.components.BentoCard
import com.example.maryroyal.ui.components.HeroBanner
import com.example.maryroyal.ui.components.StatCard
import com.example.maryroyal.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AttendanceScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val today = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    val learners by repository.getAllLearners().collectAsState(initial = emptyList())
    val attendanceRecords by repository.getAttendanceByDate(today).collectAsState(initial = emptyList())

    val grades = listOf("All", "PP1", "PP2", "Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7", "Grade 8", "Grade 9")
    var selectedGrade by remember { mutableStateOf("Grade 4") }

    val filteredLearners = if (selectedGrade == "All") learners else learners.filter { it.grade == selectedGrade }

    val presentCount = attendanceRecords.count { it.status == AttendanceStatus.PRESENT }
    val absentCount = attendanceRecords.count { it.status == AttendanceStatus.ABSENT }
    val lateCount = attendanceRecords.count { it.status == AttendanceStatus.LATE }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeroBanner(
                tag = "Daily Register · $today (EAT)",
                title = "Schoolwide Attendance",
                subtitle = "Track daily presence, class register progress, and absentee follow-up across Mary Royal School.",
                actionButton = {
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Bulk mark all unrecorded in this grade as PRESENT
                                val newRecords = filteredLearners.map { l ->
                                    val existing = attendanceRecords.find { it.learnerId == l.id }
                                    existing ?: AttendanceRecord(
                                        learnerId = l.id,
                                        learnerName = l.name,
                                        grade = l.grade,
                                        date = today,
                                        status = AttendanceStatus.PRESENT
                                    )
                                }
                                repository.saveAttendance(newRecords)
                                repository.logAudit(
                                    actorName = currentUser.name,
                                    role = currentUser.role.name,
                                    action = "ATTENDANCE_BULK_MARKED",
                                    entity = "Attendance",
                                    entityId = selectedGrade
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_mark_all_present")
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = RoyalNavy,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Mark Remaining Present",
                            color = RoyalNavy,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            )
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Present",
                    value = "$presentCount",
                    subtitle = "Marked in class",
                    icon = Icons.Default.Check,
                    iconTint = AccentEmerald,
                    iconBg = AccentEmerald.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Absent",
                    value = "$absentCount",
                    subtitle = "Requires follow-up",
                    icon = Icons.Default.Close,
                    iconTint = AccentRose,
                    iconBg = AccentRose.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Late",
                    value = "$lateCount",
                    subtitle = "Tardy recorded",
                    icon = Icons.Default.Schedule,
                    iconTint = RoyalGold,
                    iconBg = RoyalGold.copy(alpha = 0.12f),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Grade selector
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
                text = "CLASS REGISTER: $selectedGrade (${filteredLearners.size} LEARNERS)",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        items(filteredLearners) { learner ->
            val existing = attendanceRecords.find { it.learnerId == learner.id }
            val currentStatus = existing?.status ?: AttendanceStatus.PRESENT

            BentoCard(modifier = Modifier.testTag("attendance_row_${learner.id}")) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = learner.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${learner.admissionNo} · Stream ${learner.stream}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    // Status Buttons (P, L, A)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        AttendanceButton(
                            label = "P",
                            selected = currentStatus == AttendanceStatus.PRESENT,
                            color = AccentEmerald,
                            onClick = {
                                coroutineScope.launch {
                                    repository.updateAttendanceRecord(
                                        AttendanceRecord(
                                            learnerId = learner.id,
                                            learnerName = learner.name,
                                            grade = learner.grade,
                                            date = today,
                                            status = AttendanceStatus.PRESENT
                                        )
                                    )
                                }
                            }
                        )
                        AttendanceButton(
                            label = "L",
                            selected = currentStatus == AttendanceStatus.LATE,
                            color = RoyalGold,
                            onClick = {
                                coroutineScope.launch {
                                    repository.updateAttendanceRecord(
                                        AttendanceRecord(
                                            learnerId = learner.id,
                                            learnerName = learner.name,
                                            grade = learner.grade,
                                            date = today,
                                            status = AttendanceStatus.LATE,
                                            note = "Recorded late"
                                        )
                                    )
                                }
                            }
                        )
                        AttendanceButton(
                            label = "A",
                            selected = currentStatus == AttendanceStatus.ABSENT,
                            color = AccentRose,
                            onClick = {
                                coroutineScope.launch {
                                    repository.updateAttendanceRecord(
                                        AttendanceRecord(
                                            learnerId = learner.id,
                                            learnerName = learner.name,
                                            grade = learner.grade,
                                            date = today,
                                            status = AttendanceStatus.ABSENT
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AttendanceButton(
    label: String,
    selected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (selected) color else color.copy(alpha = 0.12f))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            color = if (selected) Color.White else color
        )
    }
}
