package com.compiler.lexer.regex;

import java.util.HashMap;
import java.util.Map;

import java.util.Stack;

/**
 * Utility class for regular expression parsing using the Shunting Yard
 * algorithm.
 * <p>
 * Provides methods to preprocess regular expressions by inserting explicit
 * concatenation operators, and to convert infix regular expressions to postfix
 * notation for easier parsing and NFA construction.
 */
/**
 * Utility class for regular expression parsing using the Shunting Yard
 * algorithm.
 */
public class ShuntingYard {

    /**
     * Default constructor for ShuntingYard.
     */
    public ShuntingYard() {
        // TODO: Implement constructor if needed
    }

    /**
     * Inserts the explicit concatenation operator ('·') into the regular
     * expression according to standard rules. This makes implicit
     * concatenations explicit, simplifying later parsing.
     *
     * @param regex Input regular expression (may have implicit concatenation).
     * @return Regular expression with explicit concatenation operators.
     */
    public static String insertConcatenationOperator(String regex) {
        /*
            Pseudocode:
            For each character in regex:
                - Append a character to output
                - If not at end of string:
                        - Check if a and next character form an implicit concatenation
                        - If so, append '·' to output
            Return output as string
         */
        StringBuilder output = new StringBuilder();

        for(int i = 0; i< regex.length(); i++ ){
            output.append(regex.charAt(i)); // Append a character to output
            if(i<regex.length()-1){ // If not at end of string
                //Check if a and next character form an implicit concatenation
                if(isOperand( regex.charAt(i+1))){
                    output.append('·');
                }
            }
        }
        return output.toString();
    }

    /**
     * Determines if the given character is an operand (not an operator or
     * parenthesis).
     *
     * @param c Character to evaluate.
     * @return true if it is an operand, false otherwise.
     */
    private static boolean isOperand(char c) {
        // TODO: Implement isOperand
        /*
        Pseudocode:
        Return true if c is not one of: '|', '*', '?', '+', '(', ')', '·'
         */
        char[] operators = {'|', '*', '?', '+', '(', ')', '·'};
        for(char op :operators){
            if(c==op) return false;
        }
        return true;
        //throw new UnsupportedOperationException("Not implemented");
    }

    /**
     * Converts an infix regular expression to postfix notation using the
     * Shunting Yard algorithm. This is useful for constructing NFAs from
     * regular expressions.
     *
     * @param infixRegex Regular expression in infix notation.
     * @return Regular expression in postfix notation.
     */
    public static String toPostfix(String infixRegex) {
        /*
        Pseudocode:
        1. Define operator precedence map
        2. Preprocess regex to insert explicit concatenation operators
        3. For each character in regex:
            - If operand: append to output
            - If '(': push to stack
            - If ')': pop operators to output until '(' is found
            - If operator: pop operators with higher/equal precedence, then push a operator
        4. After loop, pop remaining operators to output
        5. Return output as string
         */


        Map<Character, Integer> precedence = new HashMap<>();
        precedence.put('|', 1);
        precedence.put('·', 2);
        precedence.put('*', 3);
        precedence.put('+', 4);
        precedence.put('?', 3);
        precedence.put('(', 0); 
        precedence.put(')', 0); 

        //insert explicit concatenation operators
        infixRegex = insertConcatenationOperator(infixRegex);

        StringBuilder output = new StringBuilder();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < infixRegex.length(); i++) {
            char a = infixRegex.charAt(i);

            if (isOperand(a)) {
                output.append(a);
            }
            if (a == '(') {
                operators.push(a);
            }
            if (a == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.append(operators.pop());
                }
                operators.pop(); // Pop'('
            } else { // Case : 'a' is a operator
                // compare precedence: true if the top has more precedence
                boolean stack_more_precedence = precedence.get(operators.peek()) >= precedence.get(a);
                while(!operators.isEmpty() && operators.peek() != '(' && stack_more_precedence) {
                    output.append(operators.pop());
                }
                operators.push(a);
            }
        }

        return output.toString();
    }
}
        


