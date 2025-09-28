package com.compiler.parser.syntax;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.compiler.parser.grammar.Grammar;
import com.compiler.parser.grammar.Symbol;

/**
 * Calculates the FIRST and FOLLOW sets for a given grammar.
 * Main task of Practice 5.
 */
public class StaticAnalyzer {
    private final Grammar grammar;
    private final Map<Symbol, Set<Symbol>> firstSets;
    private final Map<Symbol, Set<Symbol>> followSets;

    public StaticAnalyzer(Grammar grammar) {
        this.grammar = grammar;
        this.firstSets = new HashMap<>();
        this.followSets = new HashMap<>();
    }

    /**
     * Calculates and returns the FIRST sets for all symbols.
     * @return A map from Symbol to its FIRST set.
     */
    public Map<Symbol, Set<Symbol>> getFirstSets() {
        // TODO: Implement the algorithm to calculate FIRST sets.
        /*
         * Pseudocode for FIRST set calculation:
         *
         * 1. For each symbol S in grammar:
         *      - If S is a terminal, FIRST(S) = {S}
         *      - If S is a non-terminal, FIRST(S) = {}
         *
         * 2. Repeat until no changes:
         *      For each production A -> X1 X2 ... Xn:
         *          - For each symbol Xi in the right-hand side:
         *              a. Add FIRST(Xi) - {ε} to FIRST(A)
         *              b. If ε is in FIRST(Xi), continue to next Xi
         *                 Otherwise, break
         *          - If ε is in FIRST(Xi) for all i, add ε to FIRST(A)
         *
         * 3. Return the map of FIRST sets for all symbols.
         */

         // 1. Initialize FIRST sets for terminals and non-terminals
         Map<Symbol, Set<Symbol>> firstSets = new HashMap<>();
         for (Symbol symbol : grammar.getTerminals()) { 
             firstSets.put(symbol, Set.of(symbol)); 
         }
         for (Symbol symbol : grammar.getNonTerminals()) { 
             firstSets.put(symbol, Set.of()); 
         }      
        // 2. Repeat until no changes:
        boolean changed;
        do {
            changed = false;
            // For each production A -> X1 X2 ... Xn:
            for (var production : grammar.getProductions()) {
                Symbol nonTerminal = production.getLeft();
                boolean allNullable = true;
                boolean haveEpsilon = false;
                //For each symbol Xi in the right-hand side:
                for (var symbol : production.getRight()) {
                    // Add FIRST(symbol) - {ε} to FIRST(nonTerminal)
                    for (var sym :firstSets.get(symbol)) {
                        if ( sym.name != "ε"   ) {
                            firstSets.get(nonTerminal).add(sym);
                            changed = true;
                        }else {
                            haveEpsilon = true;
                        }
                    }
                    // If ε is in FIRST(Xi), continue to next Xi Otherwise, break
                    if(!haveEpsilon) {
                        allNullable = false;
                        break;
                    }

                }
                //  If ε is in FIRST(Xi) for all i, add ε to FIRST(A)
                if(allNullable) {
                    Symbol epsilon = new Symbol("ε", SymbolType.TERMINAL);
                    firstSets.get(nonTerminal).add(epsilon);
                    changed = true;
                }
            } 
        } while (changed);

        return firstSets;
    }



    /**
     * Calculates and returns the FOLLOW sets for non-terminals.
     * @return A map from Symbol to its FOLLOW set.
     */
    public Map<Symbol, Set<Symbol>> getFollowSets() {
        // TODO: Implement the algorithm to calculate FOLLOW sets.
        /*
         * Pseudocode for FOLLOW set calculation:
         *
         * 1. For each non-terminal A, FOLLOW(A) = {}
         * 2. Add $ (end of input) to FOLLOW(S), where S is the start symbol
         *
         * 3. Repeat until no changes:
         *      For each production B -> X1 X2 ... Xn:
         *          For each Xi (where Xi is a non-terminal):
         *              a. For each symbol Xj after Xi (i < j <= n):
         *                  - Add FIRST(Xj) - {ε} to FOLLOW(Xi)
         *                  - If ε is in FIRST(Xj), continue to next Xj
         *                    Otherwise, break
         *              b. If ε is in FIRST(Xj) for all j > i, add FOLLOW(B) to FOLLOW(Xi)
         *
         * 4. Return the map of FOLLOW sets for all non-terminals.
         *
         * Note: This method should call getFirstSets() first to obtain FIRST sets.
         */
        throw new UnsupportedOperationException("Not implemented");
    }
}