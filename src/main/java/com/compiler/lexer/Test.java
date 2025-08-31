package com.compiler.lexer;
import com.compiler.lexer.regex.*;

public class Test {
    public static void main(String[] args) {
        
        String example = "((a|b)+)|(def)*";
        String postfix = ShuntingYard.toPostfix(example);
        System.out.println("Infix: " + example + "Postfix: " + postfix);
    }
}