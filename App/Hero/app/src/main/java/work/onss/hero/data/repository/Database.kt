package work.onss.hero.data.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import work.onss.hero.data.repository.score.Score
import work.onss.hero.data.repository.score.ScoreDao
import work.onss.hero.data.repository.site.Site
import work.onss.hero.data.repository.site.SiteDao

@Database(entities = [Score::class, Site::class], version = 1, exportSchema = true)
abstract class Database : RoomDatabase() {
    abstract fun ScoreDao(): ScoreDao
    abstract fun SiteDao(): SiteDao
}