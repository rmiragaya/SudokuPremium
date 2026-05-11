package ropa.miragaya.sudokupremium.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.BuildConfig
import ropa.miragaya.sudokupremium.data.local.AppDatabase
import ropa.miragaya.sudokupremium.data.local.GameDao

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "sudoku_db"
        ).addMigrations(AppDatabase.MIGRATION_3_4)

        if (BuildConfig.DEBUG) {
            builder.fallbackToDestructiveMigration(true)
        }

        return builder.build()
    }

    @Provides
    fun provideGameDao(database: AppDatabase): GameDao {
        return database.gameDao()
    }
}
