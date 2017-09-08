package com.fgl.sudoku;

import aima.core.search.csp.*;
import javafx.application.Platform;
import javafx.concurrent.Task;

import javax.inject.Inject;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import java.util.stream.IntStream;

/**
 * This class defines a task for solving the sudoku as a CSP problem,
 * It defines CSP problem by calling SudokuCSP class, then solve it
 * using one backtracking strategy.
 * It accepts a puzzle, represented as a string of length 81,
 * a mutex indicating whether there is another executing task,
 * and a variable indicating which backtracking strategy
 * should be used.
 */
public class SudokuSolver extends Task<Optional<String>> {

    private String board;
    private AtomicBoolean solving;
    private BKStrategy selectBKStrategy;
    private SolutionStrategy solver;

    @Inject StepCounter stepCounter;
    @Inject Map<BKStrategy, SolutionStrategy> solvers;

    private static final int SIZE = 9;
    private static final Logger LOGGER = Logger.getLogger(SudokuSolver.class.getName());

    SudokuSolver(String board, AtomicBoolean solving, BKStrategy selectBKStrategy){
        this.board = board;
        this.solving = solving;
        this.selectBKStrategy = selectBKStrategy;
        SudokuApplication.getAppComponent().inject(this);

        // get SolutionStrategy from multi-bound map
        solver = solvers.get(selectBKStrategy);
        // add listener to track assignment/inference counts
        stepCounter.reset();
        solver.addCSPStateListener(stepCounter);
    }

    @Override
    public Optional<String>
    call() throws Exception {
        LOGGER.info("Board: " + board);
        LOGGER.info("Strategy: " + selectBKStrategy);

        if(board == null || board.length() != SIZE * SIZE){
            updateMessage("Invalid Board. ");
            return Optional.empty();
        }

        try{
            return solve();
        }finally {
            Platform.runLater(() -> {
                solving.set(false);
            });
        }
    }

    public Optional<String>
    solve(){
        // construct CSP problem
        CSP csp = new SudokuCSP(SIZE);

        // get input and fix value for pre-fixed cells
        for (int i = 0; i < board.length(); i++) {
            char ch = board.charAt(i);
            if(ch != '.'){
                ((SudokuCSP)csp).setAssignment(i / SIZE, i % SIZE,ch - '0');
            }
        }

        // solve CSP
        Assignment assignment = solver.solve(csp.copyDomains());
        if(assignment == null){
            updateMessage("No Solution found: " + stepCounter.getResults().toString());
            return Optional.empty();
        }

        // prepare returning solution
        updateMessage(stepCounter.getResults().toString());
        LOGGER.info(stepCounter.getResults().toString());
        StringBuilder solution = new StringBuilder();
        IntStream.range(0, SIZE * SIZE).boxed().mapToInt(i -> (int)assignment.getAssignment(csp.getVariables().get(i))).forEach(solution::append);
        LOGGER.info("Solution: " + solution.toString());

        return Optional.of(solution.toString());
    }
}
