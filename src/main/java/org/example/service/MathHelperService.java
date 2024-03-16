package org.example.service;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.example.db.DataBase;
import org.example.interFace.HelperInterFace;

import java.util.List;
import java.util.Stack;

public class MathHelperService implements HelperInterFace {
    public String leftEquation = "";
    public String rightEquation = "";
    DataBase dataBase = new DataBase();
    public List<String> getFromDb(){
        return dataBase.getDataFromDatabase();
    }

    @Override
    public String saveToDb(String equation) {
        dataBase.save(equation);
        return "Saved Succesfully";
    }


    @Override
    public boolean divide(String equation) {
        leftEquation = "";
        rightEquation = "";
        int counter = 0;
        if (equation.contains("=")) {
            for (int i = 0; i < equation.length(); i++) {
                if (equation.charAt(i) == '=') {
                    counter++;

                    if (!copyRight(equation, i + 1, counter)){
                        return false;
                    };
                    if (!checkForBrackets(leftEquation) || !checkForOperationAndVariables(leftEquation)) {
                        System.out.println("LEFT Bracket Variable or Operational Error");
                        return false;
                    }
//                    System.out.println("Left: " + leftEquation + " Right: " + rightEquation);
//                    String FixedLeftEquation = fixNegativeNumbers(leftEquation);
//                    String FixedRightEquation = fixNegativeNumbers(rightEquation);
//                    System.out.println("Left: " + FixedLeftEquation + " Right: " + FixedRightEquation);
//                    Expression leftExpression = new ExpressionBuilder(FixedLeftEquation).variable("x").build().setVariable("x",2);
//                    var leftResult = leftExpression.evaluate();
//                    System.out.println("Left Result = "+leftResult);
//                    Expression rightExpression = new ExpressionBuilder(FixedRightEquation).variable("x").build().setVariable("x",2);
//                    var rightResult = rightExpression.evaluate();
//                    System.out.println("Right Result = "+rightResult);
                    return true;
                }
                leftEquation += equation.charAt(i);
            }
        } else return false;
        return true;

    }
    public Double calculateTheRoot(double x){
        String FixedLeftEquation = fixNegativeNumbers(leftEquation);
        String FixedRightEquation = fixNegativeNumbers(rightEquation);
        System.out.println("Left: " + FixedLeftEquation + " Right: " + FixedRightEquation);
        try {
            Expression leftExpression = new ExpressionBuilder(FixedLeftEquation).variable("x").build().setVariable("x", x);
            var leftResult = leftExpression.evaluate();
            System.out.println("Left Result = " + leftResult);
        } catch (Exception e) {

            e.printStackTrace();
        }
        Expression leftExpression = new ExpressionBuilder(FixedLeftEquation).variable("x").build().setVariable("x",x);
        var leftResult = leftExpression.evaluate();
        System.out.println("Left Result = "+leftResult);
        Expression rightExpression = new ExpressionBuilder(FixedRightEquation).variable("x").build().setVariable("x",x);
        var rightResult = rightExpression.evaluate();
        System.out.println("Right Result = "+rightResult);
        var finalResult = leftResult-rightResult;
        System.out.println("Final Result: "+finalResult);
        var less = 1e-9;
        if (Math.abs(finalResult)<less){
            return finalResult;
        }
        return (double) 0;
    }
    public String fixNegativeNumbers(String equation) {
        StringBuilder newEquation = new StringBuilder();
        for(int i = 0; i<equation.length(); i++){
            newEquation.append(equation.charAt(i));
            if(equation.charAt(i) == '*' && equation.charAt(i+1) == '-'){
                newEquation.append('(');
                newEquation.append('-');
                i+=2;
                while (Character.isDigit(equation.charAt(i)) || equation.charAt(i) =='x'){
                    newEquation.append(equation.charAt(i));
                    i++;
                    if (i==equation.length()){
                        break;
                    }
                }
                newEquation.append(')');
            }
        }
        return newEquation.toString();
    }

    public boolean copyRight(String equation, int index, int counter) {
        for (; index < equation.length(); index++) {
            if (equation.charAt(index) == '=') {
                counter++;
                if (counter > 1) {
                    System.out.println("Equals Error");
                    return false;
                }
            }
            rightEquation += equation.charAt(index);
        }
        if (!checkForBrackets(rightEquation) || !checkForOperationAndVariables(rightEquation)) {
            System.out.println("RIGHT Bracket Variable or Operational Error");
            return false;
        }
        return true;
    }


        @Override
        public boolean checkForBrackets (String equation){
            Stack<Character> stack = new Stack<>();
            for (char ch : equation.toCharArray()) {
                if (ch == '(') {
                    stack.push(ch);
                } else if (ch == ')') {
                    if (stack.isEmpty() || stack.pop() != '(') {
                        return false;
                    }
                }
            }


            if (!stack.isEmpty()) {
                return false;
            }
            return true;
        }

        @Override
        public boolean checkForOperationAndVariables (String equation){
            for (int i = 1; i < equation.length(); i++) {
                char curr = equation.charAt(i);
                char prev = equation.charAt(i - 1);
                if ((Character.isAlphabetic(curr) && curr != 'x') || (curr == 'x' && prev == 'x')) {
                    return false;
                }


                if (validOperation(curr) && validOperation(prev) && prev != '*' && prev != '=') {
                    return false;
                }
            }

            return true;
        }
        private static boolean validOperation ( char ch){
            return ch == '+' || ch == '-' || ch == '*' || ch == '/' || ch == '=';
        }

        @Override
        public boolean checkForVariable () {
            return false;
        }
    }
