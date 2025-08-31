package com.compiler.lexer.nfa;

/**
 * Represents a Non-deterministic Finite Automaton (NFA) with a start and end state.
 * <p>
 * An NFA is used in lexical analysis to model regular expressions and pattern matching.
 * This class encapsulates the start and end states of the automaton.
 */

public class NFA {
    /**
     * The initial (start) state of the NFA.
     */
    public final State startState;

    /**
     * The final (accepting) state of the NFA.
     */
    public final State endState;

    /**
     * The states of NFA
     */
    public List<State> states;

    /**
     * The transitions of NFA
     */
    public List<Transition> transitions;

    /**
     * Constructs a new NFA with the given start and end states.
     * @param start The initial state.
     * @param end The final (accepting) state.
     */
    public NFA(State start, State end) {
        this.startState = start;
        this.endState = end;
        this.states = new ArrayList<>();
        this.transitions = new ArrayList<>();
    }

    /**
     * Returns the initial (start) state of the NFA.
     * @return the start state
     */
    public State getStartState() {
        return this.startState;
    }

    /**
     * Add state to NFA
     * @param e the state
     */
    public void addState(State e){
        this.states.add(e);
    }

    /**
     * Add transitions to NFA
     * @param t
     */
    public void addTransition(Transition t){
        this.transitions(t);
    }
}