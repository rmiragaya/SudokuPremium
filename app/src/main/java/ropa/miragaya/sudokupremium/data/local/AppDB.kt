package ropa.miragaya.sudokupremium.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [GameEntity::class, GameHistoryEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE saved_game ADD COLUMN hintsUsed INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE saved_game ADD COLUMN rewardedHintsAvailable INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}
