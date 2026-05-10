package ropa.miragaya.sudokupremium.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import ropa.miragaya.sudokupremium.domain.generator.PuzzleGenerator
import ropa.miragaya.sudokupremium.domain.generator.SudokuGenerator
import ropa.miragaya.sudokupremium.domain.repository.GameRepository
import ropa.miragaya.sudokupremium.domain.repository.GameRepositoryImpl
import ropa.miragaya.sudokupremium.domain.solver.hints.HintGenerator
import ropa.miragaya.sudokupremium.domain.solver.hints.HintProvider
import ropa.miragaya.sudokupremium.domain.solver.utils.DebugBoardLoader
import ropa.miragaya.sudokupremium.domain.solver.utils.DebugBoardSource
import ropa.miragaya.sudokupremium.util.DefaultDispatcherProvider
import ropa.miragaya.sudokupremium.util.DispatcherProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGameRepository(gameRepositoryImpl: GameRepositoryImpl): GameRepository

    @Binds
    abstract fun bindPuzzleGenerator(sudokuGenerator: SudokuGenerator): PuzzleGenerator

    @Binds
    abstract fun bindHintProvider(hintGenerator: HintGenerator): HintProvider

    @Binds
    abstract fun bindDebugBoardSource(debugBoardLoader: DebugBoardLoader): DebugBoardSource

    @Binds
    abstract fun bindDispatcherProvider(defaultDispatcherProvider: DefaultDispatcherProvider): DispatcherProvider
}
