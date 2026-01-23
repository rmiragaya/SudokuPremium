package ropa.miragaya.sudokupremium.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ropa.miragaya.sudokupremium.domain.generator.SudokuGenerator
import ropa.miragaya.sudokupremium.domain.solver.Solver
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {

    @Provides
    @Singleton
    fun provideSolver(): Solver {
        return Solver()
    }

    @Provides
    @Singleton
    fun provideSudokuGenerator(solver: Solver): SudokuGenerator {
        return SudokuGenerator(solver)
    }

}