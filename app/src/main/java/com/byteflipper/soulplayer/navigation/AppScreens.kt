package com.byteflipper.soulplayer.navigation // Обновлено имя пакета

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.byteflipper.soulplayer.R // Обновлен импорт R

// Определяем все экраны приложения и их маршруты
sealed interface AppScreens {
    val route: String
    @get:StringRes val titleResId: Int
    @get:DrawableRes val icon: Int
    val arguments: List<NamedNavArgument> get() = emptyList()

    companion object {
        fun findScreenByRoute(route: String?): AppScreens? {
            if (route == null) return null
            return listOf(
                Home, Library, Songs, Artists, Albums, Playlists, Search, Settings, NowPlaying,
                ArtistDetails, AlbumDetails, PlaylistDetails
            ).find {
                val baseRoute = it.route.substringBefore("/{")
                val inputBaseRoute = route.substringBefore("/{")
                baseRoute == inputBaseRoute
            }
        }
    }

    data object Home : AppScreens {
        override val route: String = "home"
        override val titleResId: Int = R.string.screen_home
        override val icon: Int = R.drawable.home_24px
    }

    data object Library : AppScreens {
        override val route: String = "library"
        override val titleResId: Int = R.string.screen_library
        override val icon: Int = R.drawable.library_music_24px
    }

    data object Songs : AppScreens {
        override val route: String = "songs"
        override val titleResId: Int = R.string.screen_songs
        override val icon: Int = R.drawable.music_note_24px
    }

    data object Artists : AppScreens {
        override val route: String = "artists"
        override val titleResId: Int = R.string.screen_artists
        override val icon: Int = R.drawable.artist_24px
    }

    data object Albums : AppScreens {
        override val route: String = "albums"
        override val titleResId: Int = R.string.screen_albums
        override val icon: Int = R.drawable.album_24px
    }

    data object Playlists : AppScreens {
        override val route: String = "playlists"
        override val titleResId: Int = R.string.screen_playlists
        override val icon: Int = R.drawable.queue_music_24px
    }

    data object Search : AppScreens {
        override val route: String = "search"
        override val titleResId: Int = R.string.screen_search
        override val icon: Int = R.drawable.search_24px
    }

    data object Settings : AppScreens {
        override val route: String = "settings"
        override val titleResId: Int = R.string.screen_settings
        override val icon: Int = R.drawable.settings_24px
    }

    data object NowPlaying : AppScreens {
        override val route: String = "now_playing"
        override val titleResId: Int = R.string.screen_now_playing
        override val icon: Int = R.drawable.play_arrow_24px
    }

    data object ArtistDetails : AppScreens {
        const val ARG_ID = "id"
        override val route: String = "artist/{$ARG_ID}"
        override val titleResId: Int = R.string.screen_artist_details
        override val icon: Int = R.drawable.artist_24px
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(ARG_ID) { type = NavType.StringType }
        )
        fun createRoute(id: String) = "artist/$id"
    }

    data object AlbumDetails : AppScreens {
        const val ARG_ID = "id"
        override val route: String = "album/{$ARG_ID}"
        override val titleResId: Int = R.string.screen_album_details
        override val icon: Int = R.drawable.album_24px
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(ARG_ID) { type = NavType.StringType }
        )
        fun createRoute(id: String) = "album/$id"
    }

    data object PlaylistDetails : AppScreens {
        const val ARG_ID = "id"
        override val route: String = "playlist/{$ARG_ID}"
        override val titleResId: Int = R.string.screen_playlist_details
        override val icon: Int = R.drawable.queue_music_24px
        override val arguments: List<NamedNavArgument> = listOf(
            navArgument(ARG_ID) { type = NavType.StringType }
        )
        fun createRoute(id: String) = "playlist/$id"
    }
}

// Список экранов для нижней навигации или вкладок
// Теперь titleResId и icon берутся из самих объектов AppScreens
val mainNavigationItems = listOf(
    AppScreens.Home,
    AppScreens.Songs,
    AppScreens.Artists,
    AppScreens.Albums,
    AppScreens.Playlists
)