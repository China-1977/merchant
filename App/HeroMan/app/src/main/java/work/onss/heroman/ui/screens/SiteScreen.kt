package work.onss.heroman.ui.screens


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun SiteScreen() {
    LazyColumn(){
        items(20){
            Card() {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                ) {
                    Text(
                        text = "马沙窝村",
                    )
                    Text(
                        text = "聊城市茌平区一零五国道",
                    )
                }
            }
        }
    }

}

@Preview
@Composable
fun StudyScreenPreview() {
    SiteScreen()
}

