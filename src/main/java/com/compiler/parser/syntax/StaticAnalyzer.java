package com.compiler.parser.syntax;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.compiler.parser.grammar.Grammar;
import com.compiler.parser.grammar.Symbol;
import com.compiler.parser.grammar.SymbolType;

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
             Set<Symbol> terminalSet = new HashSet<>();
             terminalSet.add(symbol);
             firstSets.put(symbol, terminalSet); 
         }
         for (Symbol symbol : grammar.getNonTerminals()) { 
             firstSets.put(symbol, new HashSet<>()); 
         }
         
         // Ensure epsilon symbol has its own FIRST set if it exists in the grammar
         Symbol epsilon = new Symbol("ε", SymbolType.TERMINAL);
         if (!firstSets.containsKey(epsilon)) {
             Set<Symbol> epsilonSet = new HashSet<>();
             epsilonSet.add(epsilon);
             firstSets.put(epsilon, epsilonSet);
         }      
        // 2. Repeat until no changes:
        boolean changed;
        do {
            changed = false;
            // For each production A -> X1 X2 ... Xn:
            for (var production : grammar.getProductions()) {
                Symbol nonTerminal = production.getLeft();
                boolean allHaveEpsilon = true;
                
                // Handle empty production (epsilon production)
                if (production.getRight().isEmpty()) {
                    if (firstSets.get(nonTerminal).add(epsilon)) {
                        changed = true;
                    }
                    continue;
                }
                
                //For each symbol Xi in the right-hand side:
                for (var symbol : production.getRight()) {
                    // Ensure the symbol has a FIRST set
                    if (!firstSets.containsKey(symbol)) {
                        firstSets.put(symbol, new HashSet<>());
                    }
                    
                    boolean symbolHasEpsilon = false;
                    // Add FIRST(symbol) - {ε} to FIRST(nonTerminal)
                    for (var sym : firstSets.get(symbol)) {
                        if (!sym.name.equals("ε")) {
                            if (firstSets.get(nonTerminal).add(sym)) {
                                changed = true;
                            }
                        } else {
                            symbolHasEpsilon = true;
                        }
                    }
                    
                    // If ε is not in FIRST(Xi), stop processing further symbols
                    if (!symbolHasEpsilon) {
                        allHaveEpsilon = false;
                        break;
                    }
                }
                
                //  If ε is in FIRST(Xi) for all i, add ε to FIRST(A)
                if (allHaveEpsilon) {
                    if (firstSets.get(nonTerminal).add(epsilon)) {
                        changed = true;
                    }
                }
            } 
        } while (changed);

        // Store in instance variable for use by getFollowSets()
        this.firstSets.clear();
        this.firstSets.putAll(firstSets);
        
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
        // First, ensure we have the FIRST sets calculated
        if (this.firstSets.isEmpty()) {
            getFirstSets();
        }
        
        // Initialize FOLLOW sets for non-terminals
        Map<Symbol, Set<Symbol>> followSets = new HashMap<>();
        for (Symbol symbol : grammar.getNonTerminals()) {
            followSets.put(symbol, new HashSet<>());
        }
        // Add $ (end of input) to FOLLOW(S), where S is the start symbol
        Symbol startSymbol = grammar.getStartSymbol();
        followSets.get(startSymbol).add(new Symbol("$", SymbolType.TERMINAL));

        // 3. Repeat until no changes:
        boolean changed;
        do {
            changed = false;
            // For each production B -> X1 X2 ... Xn:
            for (var production : grammar.getProductions()) {
                Symbol nonTerminal = production.getLeft();
                // For each Xi (where Xi is a non-terminal):
                for (int i = 0; i < production.getRight().size(); i++) {
                    Symbol symbol = production.getRight().get(i);
                    if (symbol.type == SymbolType.NON_TERMINAL) {
                        // a. For each symbol Xj after Xi (i < j <= n):
                        for (int j = i + 1; j < production.getRight().size(); j++) {
                            Symbol nextSymbol = production.getRight().get(j);
                            // Add FIRST(Xj) - {ε} to FOLLOW(Xi)
                            Set<Symbol> nextSymbolFirstSet = this.firstSets.get(nextSymbol);
                            if (nextSymbolFirstSet != null) {
                                for (var first : nextSymbolFirstSet) {
                                    if (!first.name.equals("ε")) {
                                        if (followSets.get(symbol).add(first)) {
                                            changed = true;
                                        }
                                    }
                                }
                            }
                            // If ε is in FIRST(Xj), continue to next Xj
                            Set<Symbol> nextSymbolFirstSet2 = this.firstSets.get(nextSymbol);
                            if (nextSymbolFirstSet2 != null && nextSymbolFirstSet2.contains(new Symbol("ε", SymbolType.TERMINAL))) {
                                continue;
                            } else {
                                break;
                            }
                        }
                        // b. If ε is in FIRST(Xj) for all j > i, add FOLLOW(B) to FOLLOW(Xi)
                        if (production.getRight().subList(i + 1, production.getRight().size()).stream()
                                .allMatch(sym -> {
                                    Set<Symbol> symFirstSet = this.firstSets.get(sym);
                                    return symFirstSet != null && symFirstSet.contains(new Symbol("ε", SymbolType.TERMINAL));
                                })) {
                            if (followSets.get(symbol).addAll(followSets.get(nonTerminal))) {
                                changed = true;
                            }
                        }
                    }
                }
            }
        } while (changed);

        return followSets;      
    }
}