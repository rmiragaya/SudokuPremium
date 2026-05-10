package ropa.miragaya.sudokupremium.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.analytics.AnalyticsTracker
import ropa.miragaya.sudokupremium.analytics.FirebaseAnalyticsTracker
import ropa.miragaya.sudokupremium.auth.AuthSessionManager
import ropa.miragaya.sudokupremium.auth.FirebaseAuthSessionManager
import ropa.miragaya.sudokupremium.config.FirebaseRemoteConfigProvider
import ropa.miragaya.sudokupremium.config.RemoteConfigProvider
import ropa.miragaya.sudokupremium.crash.CrashReporter
import ropa.miragaya.sudokupremium.crash.FirebaseCrashReporter
import ropa.miragaya.sudokupremium.domain.generator.PuzzleGenerator
import ropa.miragaya.sudokupremium.domain.generator.SudokuGenerator
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import ropa.miragaya.sudokupremium.domain.repository.GameRepositoryImpl
import ropa.miragaya.sudokupremium.domain.solver.hints.HintGenerator
import ropa.miragaya.sudokupremium.domain.solver.hints.HintProvider
import ropa.miragaya.sudokupremium.domain.solver.utils.DebugBoardLoader
import ropa.miragaya.sudokupremium.domain.solver.utils.DebugBoardSource
import ropa.miragaya.sudokupremium.domain.stats.FirebaseUserStatsRepository
import ropa.miragaya.sudokupremium.domain.stats.UserStatsRepository
import ropa.miragaya.sudokupremium.util.DefaultDispatcherProvider
import ropa.miragaya.sudokupremium.util.DispatcherProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    @Singleton
    abstract fun bindUserStatsRepository(firebaseUserStatsRepository: FirebaseUserStatsRepository): UserStatsRepository

    @Binds
    abstract fun bindPuzzleGenerator(sudokuGenerator: SudokuGenerator): PuzzleGenerator

    @Binds
    abstract fun bindHintProvider(hintGenerator: HintGenerator): HintProvider

    @Binds
    abstract fun bindDebugBoardSource(debugBoardLoader: DebugBoardLoader): DebugBoardSource

    @Binds
    abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(firebaseAnalyticsTracker: FirebaseAnalyticsTracker): AnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindAuthSessionManager(firebaseAuthSessionManager: FirebaseAuthSessionManager): AuthSessionManager

    @Binds
    @Singleton
    abstract fun bindCrashReporter(firebaseCrashReporter: FirebaseCrashReporter): CrashReporter

    @Binds
    @Singleton
    abstract fun bindRemoteConfigProvider(
        firebaseRemoteConfigProvider: FirebaseRemoteConfigProvider
    ): RemoteConfigProvider
}
