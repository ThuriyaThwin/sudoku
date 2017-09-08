package com.fgl.sudoku;

import aima.core.search.csp.CSP;
import aima.core.search.csp.Domain;
import aima.core.search.csp.Variable;
import aima.core.search.csp.examples.NotEqualConstraint;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * This class constructing the sudoku problem as CSP problem,
 * set up variables, domain and constraints
 */
public class SudokuCSP extends CSP {

    private int SIZE;

    public SudokuCSP(int size){
        this.SIZE = size;
        init();
    }

    /**
     * Set up variables, domain and constraints
     */
    private void init(){
        // variables
        for (int i = 0; i < SIZE; i ++){
            for (int j = 0; j < SIZE; j ++){
                addVariable(new Variable(String.format("[%d,%d]", i, j)));
            }
        }

        // domain
        for (Variable variable: getVariables()){
            setDomain(variable, new Domain(IntStream.range(1, 10).boxed().toArray()));
        }

        // constraints
        for (int row = 0; row < SIZE; row ++){
            for (int col = 0; col < SIZE; col ++){

                Variable curr = getVariables().get(row * SIZE + col);
                for (int i = 0; i < SIZE; i ++){
                    // row
                    if(i != col){
                        addConstraint(new NotEqualConstraint(curr, getVariables().get(row * SIZE + i)));
                    }
                    // col
                    if(i != row){
                        addConstraint(new NotEqualConstraint(curr, getVariables().get(i * SIZE + col)));
                    }
                }
                // block
                for (int m = 3 * (row / 3); m < 3 * (row / 3) + 3; m ++){
                    for (int n = 3 * (col / 3); n < 3 * (col / 3) + 3; n ++){
                        if(m != row || n != col) {
                            addConstraint(new NotEqualConstraint(curr, getVariables().get(m * SIZE + n)));
                        }
                    }
                }
            }
        }
    }

    public void setAssignment(int row, int col, int val){
        Variable variable = getVariables().get(row * SIZE + col);
        setDomain(variable, new Domain(Arrays.asList(val)));
    }
}
