package com.byteflipper.soulplayer.ui.artistdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.byteflipper.soulplayer.R

@Composable
fun ArtistDetailsScreen(artistId: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "${stringResource(id = R.string.screen_artist_details)}: $artistId")
    }
}
