package com.compiler.lexer.regex;

import java.util.Stack;

import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;

/**
 * RegexParser
 * -----------
 * This class provides functionality to convert infix regular expressions into nondeterministic finite automata (NFA)
 * using Thompson's construction algorithm. It supports standard regex operators: concatenation (·), union (|),
 * Kleene star (*), optional (?), and plus (+). The conversion process uses the Shunting Yard algorithm to transform
 * infix regex into postfix notation, then builds the corresponding NFA.
 *
 * Features:
 * - Parses infix regular expressions and converts them to NFA.
 * - Supports regex operators: concatenation, union, Kleene star, optional, plus.
 * - Implements Thompson's construction rules for NFA generation.
 *
 * Example usage:
 * <pre>
 *     RegexParser parser = new RegexParser();
 *     NFA nfa = parser.parse("a(b|c)*");
 * </pre>
 */
/**
 * Parses regular expressions and constructs NFAs using Thompson's construction.
 */
public class RegexParser {

    /**
     * Default constructor for RegexParser.
     */
        public RegexParser() {
        }

    /**
     * Converts an infix regular expression to an NFA.
     *
     * @param infixRegex The regular expression in infix notation.
     * @return The constructed NFA.
     */
    public NFA parse(String infixRegex) {
        ShuntingYard shuntingYard = new ShuntingYard();
        String postfixRegex = shuntingYard.toPostfix(infixRegex);
        return buildNfaFromPostfix(postfixRegex);
    }

    /**
     * Builds an NFA from a postfix regular expression.
     *
     * @param postfixRegex The regular expression in postfix notation.
     * @return The constructed NFA.
     */
    private NFA buildNfaFromPostfix(String postfixRegex) {
        Stack<NFA> stack = new Stack<>();
        for (Character c : postfixRegex.toCharArray()) {
            if (isOperand(c)) {
                // Create an NFA for the operand and push it onto the stack
                NFA nfa = this.createNfaForCharacter(c);
                stack.push(nfa);
            } else {
                // Handle operators
                switch (c) {
                    case '|':
                        handleUnion(stack);
                        break;
                    case '*':
                        handleKleeneStar(stack);
                        break;
                    case '?':
                        handleOptional(stack);
                        break;
                    case '+':
                        handlePlus(stack);
                        break;
                    case '·':
                        handleConcatenation(stack);
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown operator: " + c);
                }
            }
        }
        // The final NFA is the only one left on the stack
        return stack.pop();
    }


 

    /**
     * Handles the '?' operator (zero or one occurrence).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or one occurrence.
     * @param stack The NFA stack.
     */
    private void handleOptional(Stack<NFA> stack) {
        // Pseudocode: Pop NFA,
        NFA nfa = stack.pop();
        // create new start/end, 
        State startState = new State();
        State endState = new State();
        endState.isFinal = true;

        // connection new startState   with the current nfa
        startState.addTransition(null, nfa.startState);
        // connection new endState with the current nfa
        nfa.endState.addTransition(null, endState);
        nfa.endState.isFinal = false;

        //add epsilon transitions for zero/one occurrence
        startState.addTransition(null, endState);
        stack.push(new NFA(startState, endState));
    }

    /**
     * Handles the '+' operator (one or more occurrences).
     * Pops an NFA from the stack and creates a new NFA that accepts one or more occurrences.
     * @param stack The NFA stack.
     */
    private void handlePlus(Stack<NFA> stack) {
        // Pseudocode: Pop NFA,
        NFA nfa = stack.pop();
        // create new start/end, add transitions for one or more occurrence
        State startState = new State();
        State endState = new State();
        endState.isFinal = true;

        // one or more repetitions
        nfa.endState.addTransition(null, nfa.startState);

        // connection new startState   with the current nfa
        startState.addTransition(null, nfa.startState);
        // connection new endState with the current nfa
        nfa.endState.addTransition(null, endState);
        nfa.endState.isFinal = false;

        NFA newNfa = new NFA(startState, endState);
        stack.push(newNfa);
    }
    
    /**
     * Creates an NFA for a single character.
     * @param c The character to create an NFA for.
     * @return The constructed NFA.
     */
    private NFA createNfaForCharacter(Character c) {
        State startState = new State();
        State endState = new State();
        endState.isFinal = true;
        startState.addTransition(c, endState);
        NFA nfa = new NFA(startState, endState);
        return nfa;
    }

    /**
     * Handles the concatenation operator (·).
     * Pops two NFAs from the stack and connects them in sequence.
     * @param stack The NFA stack.
     */
    private void handleConcatenation(Stack<NFA> stack) {
        // Pseudocode: Pop two NFAs,
        NFA nfa2 = stack.pop();
        NFA nfa1 = stack.pop();
        //connect end of first to start of second
        nfa1.endState.addTransition(null, nfa2.startState);
        nfa1.endState.isFinal = false;
        stack.push(new NFA(nfa1.startState, nfa2.endState));
    }

    /**
     * Handles the union operator (|).
     * Pops two NFAs from the stack and creates a new NFA that accepts either.
     * @param stack The NFA stack.
     */
    private void handleUnion(Stack<NFA> stack) {
        //Pop two NFAs,
        NFA nfa1  = stack.pop();
        NFA nfa2 = stack.pop();

        // create new start/end,
        State startState = new State();
        State endState = new State();
        endState.isFinal = true;

        // add epsilon transitions for union
        startState.addTransition(null, nfa1.startState);
        startState.addTransition(null, nfa2.startState);

        nfa1.endState.addTransition(null, endState);
        nfa1.endState.isFinal = false; 
        nfa2.endState.addTransition(null, endState);
        nfa2.endState.isFinal = false;

        NFA nfa = new NFA(startState, endState);
         
        stack.push(nfa);
    }



    /**
     * Handles the Kleene star operator (*).
     * Pops an NFA from the stack and creates a new NFA that accepts zero or more repetitions.
     * @param stack The NFA stack.
     */
    private void handleKleeneStar(Stack<NFA> stack) {
        // Pseudocode: Pop NFA,
        NFA nfa = stack.pop();
        // create new start/end, add transitions for zero or more repetitions
        State startState = new State();
        State endState = new State();
        endState.isFinal = true;

        // zero repetitions
        startState.addTransition(null, endState);

        // more repetitions
        nfa.endState.addTransition(null, nfa.startState);

        // connection new startState   with the current nfa
        startState.addTransition(null, nfa.startState);
        // connection new endState with the current nfa
        nfa.endState.addTransition(null, endState);
        nfa.endState.isFinal = false;

        NFA newNfa = new NFA(startState, endState);
        stack.push(newNfa);
    }

    /**
     * Checks if a character is an operand (not an operator).
     * @param c The character to check.
     * @return True if the character is an operand, false if it is an operator.
     */
    private boolean isOperand(Character c) {
        Character[] operators = {'|', '*', '?', '+', '(', ')', '·'};
        for(Character op :operators){
            if(c.equals(op)) return false;
        }
        return true;
    }
}