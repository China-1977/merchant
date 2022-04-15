package work.onss.heroman.ui.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import work.onss.heroman.R


@Composable
fun ScoreScreen() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.Green)
            .height(150.dp)
            .width(150.dp)
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.favicon),
            contentDescription = null,
            modifier = Modifier.size(100.dp,200.dp)
        )
        Column {
            Text(
                text = "星美城市广场",
            )
            Text(
                text = "当代国际大厦",
            )
        }
    }
}

@Preview
@Composable
fun TaskScreenPreview() {
    ScoreScreen()
}

