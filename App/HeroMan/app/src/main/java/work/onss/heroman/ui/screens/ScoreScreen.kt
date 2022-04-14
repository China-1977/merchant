package work.onss.heroman.ui.screens


import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview


@Composable
fun ScoreScreen() {
    TopAppBar(
        title = {
            Text(
                text = "订单",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview
@Composable
fun TaskScreenPreview() {
    ScoreScreen()
}

