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
            boolean current_isOperand = isOperand(regex.charAt(i));

            if(i<regex.length()-1){ // If not at end of string

                //Check if a and next character form an implicit concatenation
                boolean next_isOperand = isOperand( regex.charAt(i+1));

                // Case 1: current (c1) is operand and next (c2) is operand, then insert '·'
                // examble : ( ab → a· b). 
                if(current_isOperand && next_isOperand){
                    output.append('·');
                }
                // Case 2: current (c1) is operand and next (c2) is `(`
                // example: ( a( → a· (.
                if(current_isOperand && regex.charAt(i+1) == '('){
                    output.append('·');
                }
                // Case 3: current (c1) is `)` and next (c2) is operand
                // example: ( )a → )· a).
                if(regex.charAt(i) == ')' && next_isOperand){
                    output.append('·');
                }
                // Case 4: current (c1) is unary and next (c2) is operand
                // example: ( *a → *· a).
                if(isUnaryOperator(regex.charAt(i)) && next_isOperand){
                    output.append('·');
                }
                // Case 5: current (c1) is `)` and next (c2) is `(`
                // example: ( )( → )· ().
                if(regex.charAt(i) == ')' && regex.charAt(i+1) == '('){
                    output.append('·');
                }
            }
        }
        //System.out.println(output + "\n " + output.toString());
        return output.toString();
    }

    /**
     * Determines if the given character is a unary operator (*, +, ?).
     *
     * @param c Character to evaluate.
     * @return true if it is a unary operator, false otherwise.
     */
    private static boolean isUnaryOperator(char c) {
        char[] unaryOperators = {'*', '+', '?'};
        for(char op : unaryOperators){
            if(c==op) return true;
        }
        return false;
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
            //System.out.println(i);
            char a = infixRegex.charAt(i);

            if (isOperand(a)) {
                output.append(a);
            } else if (a == '(') {
                operators.push(a);
            } else if (a == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    output.append(operators.pop());
                }
                operators.pop(); // Pop'('
            } else { // Case : 'a' is a operator
                // compare precedence: true if the top has more precedence
                //System.out.println(a);
                boolean stack_more_precedence = !operators.isEmpty() && precedence.get(operators.peek()) >= precedence.get(a);
                while(!operators.isEmpty() && operators.peek() != '(' && stack_more_precedence) {
                    output.append(operators.pop());
                }
                operators.push(a);
            }
        }
        while(!operators.isEmpty()){
            output.append(operators.pop());
        }

        return output.toString();
    }
}
        


