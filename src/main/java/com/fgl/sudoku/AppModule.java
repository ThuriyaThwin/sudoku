package com.fgl.sudoku;

import aima.core.search.csp.ImprovedBacktrackingStrategy;
import aima.core.search.csp.MinConflictsStrategy;
import aima.core.search.csp.SolutionStrategy;
import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

/**
 * This class provides instantiation of
 * SolutionStrategy beans and StepCounter bean
 * for dependency injection.
 */
@Module
public class AppModule {

    @Provides @IntoMap
    @BKStrategyKey(BKStrategy.FC)
    SolutionStrategy solverFC(){
        SolutionStrategy solver = new ImprovedBacktrackingStrategy();
        ((ImprovedBacktrackingStrategy)solver).setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
        return solver;
    }

    @Provides @IntoMap
    @BKStrategyKey(BKStrategy.FC_MRV)
    SolutionStrategy solverFC_MRV(){
        SolutionStrategy solver = new ImprovedBacktrackingStrategy(true, false, false, false);
        ((ImprovedBacktrackingStrategy)solver).setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
        return solver;
    }

    @Provides @IntoMap
    @BKStrategyKey(BKStrategy.FC_LCV)
    SolutionStrategy solverFCL_CV(){
        SolutionStrategy solver = new ImprovedBacktrackingStrategy(false, false, false, true);
        ((ImprovedBacktrackingStrategy)solver).setInference(ImprovedBacktrackingStrategy.Inference.FORWARD_CHECKING);
        return solver;
    }

    @Provides @IntoMap
    @BKStrategyKey(BKStrategy.AC3)
    SolutionStrategy solverAC3(){
        SolutionStrategy solver = new ImprovedBacktrackingStrategy(false, false, true, false);
        return solver;
    }

    @Provides @IntoMap
    @BKStrategyKey(BKStrategy.MRV_DEG_AC3_LCV)
    SolutionStrategy solverMRV_DEG_AC3_LCV(){
        SolutionStrategy solver = new ImprovedBacktrackingStrategy(true, true, true, true);
        return solver;
    }

    @Provides @IntoMap
    @BKStrategyKey(BKStrategy.MIN_CONFLICT)
    SolutionStrategy solverMIN_CONFLICT(){
        SolutionStrategy solver = new MinConflictsStrategy(5_000);
        return solver;
    }

    @Provides
    StepCounter stepCounter(){
        return new StepCounter();
    }

}

@MapKey
@interface BKStrategyKey{
    BKStrategy value();
}
