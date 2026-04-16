package com.mohamedabdelazeim.islamicapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mohamedabdelazeim.islamicapp.data.AdhkarItem
import com.mohamedabdelazeim.islamicapp.data.ZekrData
import com.mohamedabdelazeim.islamicapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AdhkarScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    val gold = Gold
    val scope = rememberCoroutineScope()

    var tabIndex by remember { mutableIntStateOf(0) }
    val morningList = remember { ZekrData.loadMorningAdhkar(ctx) }
    val eveningList = remember { ZekrData.loadEveningAdhkar(ctx) }
    val currentList = if (tabIndex == 0) morningList else eveningList

    val pagerState = rememberPagerState(pageCount = { currentList.size })

    val gradient = Brush.verticalGradient(listOf(DeepBlue, SurfaceBlue))

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "الأذكار الصباحية والمسائية",
                        color = gold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "رجوع", tint = gold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0D1B2A))
            )
        },
        containerColor = DeepBlue
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(gradient)
        ) {
            // ── Tabs ───────────────────────────────────────────────────────────
            TabRow(
                selectedTabIndex = tabIndex,
                containerColor = Color(0xFF0F2133),
                contentColor = gold,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[tabIndex]),
                        color = gold
                    )
                }
            ) {
                Tab(
                    selected = tabIndex == 0,
                    onClick = {
                        tabIndex = 0
                        scope.launch { pagerState.scrollToPage(0) }
                    },
                    selectedContentColor = gold,
                    unselectedContentColor = Color.Gray
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.WbSunny, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("أذكار الصباح", fontSize = 14.sp)
                    }
                }
                Tab(
                    selected = tabIndex == 1,
                    onClick = {
                        tabIndex = 1
                        scope.launch { pagerState.scrollToPage(0) }
                    },
                    selectedContentColor = gold,
                    unselectedContentColor = Color.Gray
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 12.dp)
                    ) {
                        Icon(Icons.Default.NightsStay, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("أذكار المساء", fontSize = 14.sp)
                    }
                }
            }

            if (currentList.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("لا توجد أذكار", color = Color.Gray)
                }
            } else {
                // ── Page Counter ───────────────────────────────────────────────
                Text(
                    text = "ذكر ${pagerState.currentPage + 1} / ${currentList.size}",
                    color = Color.Gray,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    textAlign = TextAlign.Center
                )

                // ── Pager ──────────────────────────────────────────────────────
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) { page ->
                    AdhkarCard(item = currentList[page], gold = gold)
                }

                // ── Navigation Row ─────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage > 0)
                                    pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        enabled = pagerState.currentPage > 0
                    ) {
                        Icon(
                            Icons.Default.ArrowForward,
                            "السابق",
                            tint = if (pagerState.currentPage > 0) gold else Color.Gray
                        )
                    }

                    Text(
                        "${pagerState.currentPage + 1} / ${currentList.size}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = {
                            scope.launch {
                                if (pagerState.currentPage < currentList.size - 1)
                                    pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        enabled = pagerState.currentPage < currentList.size - 1
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            "التالي",
                            tint = if (pagerState.currentPage < currentList.size - 1) gold else Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdhkarCard(item: AdhkarItem, gold: Color) {
    var count by rememberSaveable(item.id) { mutableIntStateOf(0) }
    val done = count >= item.repeat

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Title
        Text(
            text = item.title,
            color = gold,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        // Zekr Text Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F2133))
        ) {
            Text(
                text = item.text,
                color = Color.White,
                fontSize = 19.sp,
                textAlign = TextAlign.Center,
                lineHeight = 34.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(20.dp)
            )
        }

        // Tasbeeh Counter
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    if (done)
                        Brush.radialGradient(listOf(Gold, Color(0xFFFFFF8C00)))
                    else
                        Brush.radialGradient(listOf(Color(0xFF1B5E20), Color(0xFF0F2133)))
                )
                .clickable { if (!done) count++ },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedContent(targetState = count, label = "count") { c ->
                    Text(
                        "$c",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (done) Color.Black else Color.White
                    )
                }
                Text(
                    "من ${item.repeat}",
                    color = if (done) Color.Black else Color.Gray,
                    fontSize = 13.sp
                )
            }
        }

        // Reset Button
        OutlinedButton(
            onClick = { count = 0 },
            colors = ButtonDefaults.outlinedButtonColors(contentColor = gold),
            border = BorderStroke(1.dp, gold)
        ) {
            Icon(Icons.Default.Refresh, null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (done) "أعد التسبيح ✓" else "إعادة العد")
        }

        // Benefit
        if (item.benefit.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF003300))
            ) {
                Text(
                    "💡 ${item.benefit}",
                    color = Color(0xFFAAFFAA),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Extension needed for tab indicator
@OptIn(ExperimentalFoundationApi::class)
private fun androidx.compose.ui.Modifier.tabIndicatorOffset(
    currentTabPosition: TabPosition
): androidx.compose.ui.Modifier = this.then(
    androidx.compose.ui.layout.layout { measurable, constraints ->
        val tabWidth = currentTabPosition.width.roundToPx()
        val placeable = measurable.measure(constraints.copy(minWidth = tabWidth, maxWidth = tabWidth))
        layout(placeable.width, placeable.height) {
            placeable.place(currentTabPosition.left.roundToPx(), 0)
        }
    }
)
