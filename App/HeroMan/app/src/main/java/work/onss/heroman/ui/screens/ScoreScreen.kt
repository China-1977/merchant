package work.onss.heroman.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import work.onss.heroman.R


@Composable
fun ScoreScreen() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = null
        )
        Column {
            Text(
                text = "驿站",
            )
        }
    }
}

@Preview
@Composable
fun TaskScreenPreview() {
    ScoreScreen()
}

