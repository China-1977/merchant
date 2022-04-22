package work.onss.heroman.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import work.onss.heroman.data.repository.key.Key
import work.onss.heroman.data.repository.key.KeysDao
import work.onss.heroman.data.repository.score.Score
import work.onss.heroman.data.repository.score.ScoreDao
import work.onss.heroman.data.repository.site.Site
import work.onss.heroman.data.repository.site.SiteDao

@Database(entities = [Score::class, Site::class, Key::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {
    abstract fun ScoreDao(): ScoreDao
    abstract fun KeysDao(): KeysDao
    abstract fun SiteDao(): SiteDao
}