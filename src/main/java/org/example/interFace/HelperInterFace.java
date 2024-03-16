package org.example.interFace;

public interface HelperInterFace {
    String saveToDb(String equation);
    boolean divide(String equation);
    boolean checkForBrackets(String equation);
    boolean checkForOperationAndVariables(String equation);
    boolean checkForVariable();
}
