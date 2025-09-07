package com.compiler.lexer;
import com.compiler.lexer.regex.*;
import com.compiler.lexer.nfa.*;
public class Test {
    public static void main(String[] args) {
        
        String example = "a|c*|d+";
        String postfix = ShuntingYard.toPostfix(example);
        System.out.println("Infix: " + example + "Postfix: " + postfix);

        example = "b|ced|a";
        postfix = ShuntingYard.toPostfix(example);
        System.out.println("Infix: " + example + "Postfix: " + postfix);

        example = "xyz*";
        postfix = ShuntingYard.toPostfix(example);
        System.out.println("Infix: " + example + "Postfix: " + postfix);

        example = "((ab)(cd)(ef)*(a|b)(e|a))";
        postfix = ShuntingYard.toPostfix(example);
        System.out.println("Infix: " + example + "Postfix: " + postfix);

        /** 
        String regex = "a+";
        RegexParser parser = new RegexParser();
        NFA nfa = parser.parse(regex);
        nfa.endState.isFinal = true;
        NfaSimulator nfaSimulator = new NfaSimulator();
        boolean result = nfaSimulator.simulate(nfa, "aaaa");
        System.out.println("Input: aaaa, Accepted: " + result);

        result = nfaSimulator.simulate(nfa, "b");
        System.out.println("Input: b, Accepted: " + result);
        result = nfaSimulator.simulate(nfa, "def");
        System.out.println("Input: def, Accepted: " + result);*/
    }
}