package com.example.maryroyal.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
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
import com.example.maryroyal.ui.theme.*

@Composable
fun TimetableScreen(
    repository: MaryRoyalRepository,
    currentUser: User,
    onNavigateBack: () -> Unit
) {
    val timetable by repository.getAllTimetableEntries().collectAsState(initial = emptyList())
    val textbooks by repository.getAllTextbooks().collectAsState(initial = emptyList())

    var selectedDay by remember { mutableStateOf("Monday") }
    var selectedGrade by remember { mutableStateOf("Grade 4") }
    var activeTab by remember { mutableIntStateOf(0) } // 0: Timetable, 1: Textbooks / Library

    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")
    val grades = listOf("Grade 1", "Grade 2", "Grade 3", "Grade 4", "Grade 5", "Grade 6", "Grade 7")

    val filteredSchedule = timetable.filter { it.grade == selectedGrade && it.dayOfWeek == selectedDay }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            HeroBanner(
                tag = "Academic Schedules & Resource Hub",
                title = "Timetable & Library",
                subtitle = "Weekly 8-period class timetable and curriculum textbook allocations."
            )
        }

        item {
            TabRow(selectedTabIndex = activeTab) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = { Text("Class Timetable", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Textbooks & Library", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        if (activeTab == 0) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("SELECT CLASS:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(grades) { g ->
                            FilterChip(
                                selected = selectedGrade == g,
                                onClick = { selectedGrade = g },
                                label = { Text(g, fontSize = 11.sp) },
                                shape = CircleShape
                            )
                        }
                    }

                    Text("SELECT DAY OF WEEK:", style = MaterialTheme.typography.labelSmall)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(days) { d ->
                            FilterChip(
                                selected = selectedDay == d,
                                onClick = { selectedDay = d },
                                label = { Text(d, fontSize = 11.sp) },
                                shape = CircleShape
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "$selectedGrade · $selectedDay Lessons (${filteredSchedule.size})",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (filteredSchedule.isEmpty()) {
                item {
                    BentoCard {
                        Text(
                            text = "No lessons scheduled for $selectedGrade on $selectedDay yet.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(filteredSchedule) { lesson ->
                    BentoCard(modifier = Modifier.testTag("lesson_item_${lesson.id}")) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(RoyalNavy),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "P${lesson.period}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Column {
                                    Text(
                                        text = lesson.subject,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${lesson.timeRange} · Room: ${lesson.room}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            Text(
                                text = lesson.teacherName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = RoyalBlueDark,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        } else {
            // Textbooks
            item {
                Text(
                    text = "CURRICULUM TEXTBOOK INVENTORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(textbooks) { book ->
                BentoCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = book.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${book.grade} · ${book.subject} · Publisher: ${book.publisher}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${book.availableCopies} available",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = AccentEmerald
                            )
                            Text(
                                text = "${book.issuedCopies} issued / ${book.totalCopies} total",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
