package com.fgl.sudoku;

import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {AppModule.class})
public interface AppComponent {
    void inject(SudokuSolver sudokuSolver);
}
