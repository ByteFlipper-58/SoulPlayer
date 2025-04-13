package com.byteflipper.soulplayer.ui.search

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.byteflipper.soulplayer.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController
) {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    // Пример списка истории поиска
    val searchHistory = remember {
        mutableStateListOf("Android", "Compose", "Material 3")
    }

    Box(
        Modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 8.dp)
                .semantics { traversalIndex = -1f },
            query = query,
            onQueryChange = { query = it },
            onSearch = {
                active = false // Схлопываем SearchBar при поиске
                keyboardController?.hide()
                // TODO: Выполнить поиск по запросу 'it'
                if (it.isNotBlank() && !searchHistory.contains(it)) {
                    searchHistory.add(0, it)
                    if (searchHistory.size > 5) searchHistory.removeAt(searchHistory.lastIndex)
                }
            },
            active = active,
            onActiveChange = {
                active = it
                if (!active) { // Если деактивируется, убираем фокус
                    focusManager.clearFocus()
                }
             },
            placeholder = { Text(stringResource(R.string.search_hint)) },
            leadingIcon = {
                if (active) {
                    IconButton(onClick = {
                        // Если запрос пуст, деактивируем поиск (возвращаемся назад)
                        // Если не пуст, просто очищаем запрос
                        if (query.isEmpty()) {
                            active = false
                            // Можно добавить navController.popBackStack() если нужно явно вернуться
                        } else {
                            query = ""
                        }
                    }) {
                        Icon(painter = painterResource(id = R.drawable.skip_previous_24px), contentDescription = "Back / Clear") // TODO: String resource
                    }
                } else {
                    Icon(painter = painterResource(id = R.drawable.search_24px), contentDescription = null)
                }
            },
            trailingIcon = {
                if (active && query.isNotEmpty()) {
                    IconButton(onClick = { query = "" }) {
                        Icon(painter = painterResource(id = R.drawable.playlist_remove_24px), contentDescription = "Clear query") // TODO: String resource
                    }
                }
            },
            interactionSource = interactionSource
        ) {
            // Контент, отображаемый при активном SearchBar (например, история поиска)
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(searchHistory) { historyItem ->
                    Row(modifier = Modifier.padding(vertical = 14.dp)) {
                        Icon(
                            painter = painterResource(id = R.drawable.history_24px),
                            contentDescription = null,
                            modifier = Modifier.padding(end = 10.dp)
                        )
                        Text(text = historyItem, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }

        // Основной контент экрана (отображается, когда SearchBar не активен)
        // В этой версии SearchScreen сам является основным контентом,
        // поэтому дополнительный Column не нужен. Результаты поиска будут отображаться
        // внутри content лямбды SearchBar или под ней, когда active = false.
        if (!active) {
             Column(
                 modifier = Modifier
                     .fillMaxSize()
                     .padding(top = 80.dp, start = 16.dp, end = 16.dp, bottom = 16.dp),
                 horizontalAlignment = Alignment.CenterHorizontally
             ) {
                 // TODO: Отображение результатов поиска или другой контент по умолчанию
                 Text(
                     text = stringResource(R.string.search_placeholder_results),
                     style = MaterialTheme.typography.bodyMedium
                 )
             }
        }
    }
}
