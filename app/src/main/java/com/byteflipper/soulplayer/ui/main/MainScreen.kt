package com.byteflipper.soulplayer.ui.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.byteflipper.soulplayer.R
import com.byteflipper.soulplayer.data.NavigationType
import com.byteflipper.soulplayer.navigation.AppNavHost
import com.byteflipper.soulplayer.navigation.AppScreens
import com.byteflipper.soulplayer.navigation.mainNavigationItems

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navigationType by viewModel.navigationType.collectAsState()
    val scope = rememberCoroutineScope()

    var query by rememberSaveable { mutableStateOf("") }
    var searchActive by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    val searchHistory = remember {
        mutableStateListOf("Android", "Compose", "Material 3")
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.zIndex(if (searchActive) 0f else 1f),
            topBar = {
                AppBar(
                    navController = navController,
                    onSearchClick = { searchActive = true },
                    onSettingsClick = { navController.navigate(AppScreens.Settings.route) }
                )
            },
            bottomBar = {
                if (navigationType == NavigationType.BOTTOM) {
                    AppBottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                if (navigationType == NavigationType.TABS) {
                    AppTabRowNavigation(navController = navController)
                }
                AppNavHost(
                    navController = navController,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // SearchBar на переднем плане, когда активен
        if (searchActive) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
                    .semantics { isTraversalGroup = true }
            ) {
                SearchBar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopStart),
                    query = query,
                    onQueryChange = { query = it },
                    onSearch = { currentQuery ->
                        // Выполнить поиск по запросу
                        searchActive = false
                        keyboardController?.hide()
                        focusManager.clearFocus()

                        if (currentQuery.isNotBlank() && !searchHistory.contains(currentQuery)) {
                            searchHistory.add(0, currentQuery)
                            if (searchHistory.size > 5) searchHistory.removeAt(searchHistory.lastIndex)
                        }
                        // TODO: выполнить поиск
                    },
                    active = true,
                    onActiveChange = {
                        searchActive = it
                    },
                    placeholder = { Text(stringResource(R.string.search_hint)) },
                    leadingIcon = {
                        IconButton(onClick = {
                            searchActive = false
                            query = ""
                        }) {
                            Icon(
                                painter = painterResource(id = R.drawable.arrow_back_24px),
                                contentDescription = "Back"
                            )
                        }
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.close_24px),
                                    contentDescription = "Clear query"
                                )
                            }
                        }
                    }
                ) {
                    // Отображение истории поиска
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(searchHistory) { historyItem ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        query = historyItem
                                        // Опционально: выполняем поиск сразу или оставляем поиск активным для редактирования
                                        // searchActive = false
                                        // keyboardController?.hide()
                                        // focusManager.clearFocus()
                                        // TODO: Выполнить поиск
                                    }
                                    .padding(vertical = 14.dp)
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.history_24px),
                                    contentDescription = null,
                                    modifier = Modifier.padding(end = 10.dp)
                                )
                                Text(text = historyItem)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    navController: NavHostController,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentScreen = mainNavigationItems.find { it.route == currentDestination?.route }
        ?: AppScreens.findScreenByRoute(currentDestination?.route)

    TopAppBar(
        title = {
            Text(text = currentScreen?.let { stringResource(id = it.titleResId) } ?: "SoulPlayer")
        },
        modifier = modifier,
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    painter = painterResource(id = R.drawable.search_24px),
                    contentDescription = stringResource(R.string.action_search)
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(
                    painter = painterResource(id = R.drawable.settings_24px),
                    contentDescription = stringResource(R.string.action_settings)
                )
            }
        },
    )
}

@Composable
fun AppBottomNavigationBar(navController: NavHostController, modifier: Modifier = Modifier) {
    NavigationBar(modifier = modifier) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        mainNavigationItems.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(painter = painterResource(id = screen.icon), contentDescription = stringResource(id = screen.titleResId)) },
                label = { Text(stringResource(id = screen.titleResId)) },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

@Composable
fun AppTabRowNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val currentTabIndex = mainNavigationItems.indexOfFirst { it.route == currentDestination?.route }.coerceAtLeast(0)

    ScrollableTabRow(selectedTabIndex = currentTabIndex, modifier = modifier, edgePadding = 0.dp) {
        mainNavigationItems.forEachIndexed { index, screen ->
            Tab(
                selected = currentTabIndex == index,
                onClick = {
                    if (currentTabIndex != index) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                text = { Text(stringResource(id = screen.titleResId)) },
                icon = { Icon(painter = painterResource(id = screen.icon), contentDescription = stringResource(id = screen.titleResId)) }
            )
        }
    }
}
