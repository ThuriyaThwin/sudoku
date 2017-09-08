package com.fgl.sudoku;

public enum BKStrategy {
    FC("FC"),
    FC_MRV("FC+MRV"),
    FC_LCV("FC+LCV"),
    AC3("AC3"),
    MRV_DEG_AC3_LCV("MRV+DEG+AC3+LCV"),
    MIN_CONFLICT("MIN CONFLICT");

    String value;

    BKStrategy(String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
