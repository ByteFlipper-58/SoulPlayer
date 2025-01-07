package com.byteflipper.soulplayer.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.Layout
import com.byteflipper.soulplayer.core.MusicTrack
import androidx.compose.runtime.getValue

@Composable
fun SongListItem(track: MusicTrack, onClick:(MusicTrack) -> Unit) {
    Card(
        modifier = Modifier
            .padding(4.dp)
            .clickable { onClick(track) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            track.cover?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Cover",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                ScrollingText(text = track.title, color = Color.Black)
                Text(text = track.artist, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = formatDuration(track.duration), color = Color.Gray)
        }
    }
}
@Composable
fun ScrollingText(text: String, color: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "text_scroll_animation")
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = "text_scroll_animation"
    )
    Box(modifier = Modifier.fillMaxWidth()) {
        Layout(content = {
            Text(text = text, color = color, maxLines = 1, overflow = TextOverflow.Clip)
        }) { measurables, constraints ->
            val textPlaceable = measurables.first().measure(constraints)
            val textWidth = textPlaceable.width
            val textHeight = textPlaceable.height
            val containerWidth = constraints.maxWidth

            layout(containerWidth, textHeight) {
                if (textWidth > containerWidth) {
                    textPlaceable.placeRelative(x = (offset * textWidth).toInt() % (containerWidth + textWidth), y = 0)
                } else {
                    textPlaceable.placeRelative(x = 0, y = 0)
                }
            }
        }
    }
}