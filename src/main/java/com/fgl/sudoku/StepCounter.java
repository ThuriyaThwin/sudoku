package com.fgl.sudoku;

import aima.core.search.csp.Assignment;
import aima.core.search.csp.CSP;
import aima.core.search.csp.CSPStateListener;
import aima.core.search.framework.Metrics;

/**
 * A simple CSP listener implementation which counts assignment changes and changes caused by
 * inference steps and provides some metrics.
 * @author Ruediger Lunde
 */
public class StepCounter implements CSPStateListener {
    private int assignmentCount = 0;
    private int inferenceCount = 0;

    @Override
    public void stateChanged(Assignment assignment, CSP csp) {
        if(assignment != null){
            ++ assignmentCount;
        }else{
            ++ inferenceCount;
        }
    }

    @Override
    public void stateChanged(CSP csp) {
        ++ inferenceCount;
    }

    public void reset() {
        assignmentCount = 0;
        inferenceCount = 0;
    }

    public Metrics getResults() {
        Metrics result = new Metrics();
        result.set("assignmentCount", assignmentCount);
        if (inferenceCount != 0)
            result.set("inferenceCount", inferenceCount);
        return result;
    }
}
