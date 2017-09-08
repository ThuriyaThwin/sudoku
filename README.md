# Solve Sudoku As Constraint Satisfaction Problem

# Environment

* Oracle JDK8

# Installation
  
```sh
$ bash mvnw clean
$ bash mvnw install
$ java -jar target/sudoku-0.0.1-SNAPSHOT-jar-with-dependencies.jar
```
   
# About

* This project is managed by `Maven`. The `mvnw` files for running the Maven project are included in the project root directory (`mvnw` for linux and `mvnw.cmd` for Windows). 
* `AIMA Java Core` library by Ravi Mohan etc. provides the implementation of backtrack searching algorithms. 
* `JavaFX`, provided by Oracle, is used as the UI library. 
* Dependency library `Dagger`, provided by Google, is used for resolving class dependencies at the compile time. 
* `SudokuApplication` class sets up main UI, which provides option for selecting four pre-defined sudoku puzzles or entering your own puzzles, and option for selecting backtracking strategy. It also provides option for solving or cancelling the puzzle. 
* `SudokuSolver` class defines a callable task for solving the sudoku as a CSP problem. It defines CSP problem by calling `SudokuCSP` class, then solve it using one backtracking strategy. 
