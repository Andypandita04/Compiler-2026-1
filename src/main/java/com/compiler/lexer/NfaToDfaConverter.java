package com.compiler.lexer;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import com.compiler.lexer.dfa.DFA;
import com.compiler.lexer.dfa.DfaState;
import com.compiler.lexer.nfa.NFA;
import com.compiler.lexer.nfa.State;

/**
 * NfaToDfaConverter
 * -----------------
 * This class provides a static method to convert a Non-deterministic Finite Automaton (NFA)
 * into a Deterministic Finite Automaton (DFA) using the standard subset construction algorithm.
 */
/**
 * Utility class for converting NFAs to DFAs using the subset construction algorithm.
 */
public class NfaToDfaConverter {
	/**
	 * Default constructor for NfaToDfaConverter.
	 */
		public NfaToDfaConverter() {
			// TODO: Implement constructor if needed
		}

	/**
	 * Converts an NFA to a DFA using the subset construction algorithm.
	 * Each DFA state represents a set of NFA states. Final states are marked if any NFA state in the set is final.
	 *
	 * @param nfa The input NFA
	 * @param alphabet The input alphabet (set of characters)
	 * @return The resulting DFA
	 */
	public static DFA convertNfaToDfa(NFA nfa, Set<Character> alphabet) {
		/*
		 Pseudocode:
		 1. Create initial DFA state from epsilon-closure of NFA start state
		 2. While there are unmarked DFA states:
			  - For each symbol in alphabet:
				  - Compute move and epsilon-closure for current DFA state
				  - If target set is new, create new DFA state and add to list/queue
				  - Add transition from current to target DFA state
		 3. Mark DFA states as final if any NFA state in their set is final
		 4. Return DFA with start state and all DFA states
		*/
		//1. Create initial DFA state from epsilon-closure of NFA start state
		State startStateNFA = nfa.startState;
		Set<State> startClosure = epsilonClosure(Set.of(startStateNFA));
		DfaState startStateDFA = new DfaState(startClosure);

		/**
		 *  2. While there are unmarked DFA states:
			  - For each symbol in alphabet:
				  - Compute move and epsilon-closure for current DFA state
				  - If target set is new, create new DFA state and add to list/queue
				  - Add transition from current to target DFA state
		 */
		List<DfaState> dfaStates = new ArrayList<>(); // to final DFA 
		dfaStates.add(startStateDFA);

		List<DfaState> unmarkedStates = new ArrayList<>();
		unmarkedStates.add(startStateDFA);

		while (!unmarkedStates.isEmpty()) {
			DfaState currentDFAState = unmarkedStates.remove(0);
			// For each symbol in alphabet:
			for (char symbol : alphabet) {
				// Compute move and epsilon-closure for current DFA state
				Set<State> movedStates = move(currentDFAState.getNfaStates(), symbol);
				Set<State> targetClosure = epsilonClosure(movedStates);
				// If target set is new, create new DFA state and add to list/queue
				targetDFAState = findDfaState(dfaStates, targetClosure);
				if (targetDFAState == null) {
					targetDFAState = new DfaState(targetClosure);
					dfaStates.add(targetDFAState);
					unmarkedStates.add(targetDFAState);
				}

				// Add transition from current to target DFA state
				currentDFAState.addTransition(symbol, targetDFAState);
			}
		}

		// 3. Mark DFA states as final if any NFA state in their set is final
		for (DfaState dfaState : dfaStates) {
			for( State a: dfaState.getNfaStates()) {
				if (a.isFinal) {
					dfaState.setFinal(true);
					break;
				}
			}
		}

		// 4. Return DFA with start state and all DFA states
		return new DFA(startStateDFA, dfaStates);
	}

	/**
	 * Computes the epsilon-closure of a set of NFA states.
	 * The epsilon-closure is the set of states reachable by epsilon (null) transitions.
	 *
	 * @param states The set of NFA states.
	 * @return The epsilon-closure of the input states.
	 */
	private static Set<State> epsilonClosure(Set<State> states) {
		// 1. Initialize closure with input states
		Set<State> closure = new HashSet<>(states);
		// 2. Use stack to process states
		Stack<State> stack = new Stack<>();
		stack.addAll(states);
		//3. For each state, add all reachable states via epsilon transitions
	    //4. Return closure set
		while (!stack.isEmpty()) {
			State state = stack.pop();
			for (State epsilonState : state.getEpsilonTransitions()) {
				if (closure.add(epsilonState)) {
					stack.push(epsilonState);
				}
			}
		}
		return closure;
	}



	/**
	 * Returns the set of states reachable from a set of NFA states by a given symbol.
	 *
	 * @param states The set of NFA states.
	 * @param symbol The input symbol.
	 * @return The set of reachable states.
	 */
	private static Set<State> move(Set<State> states, char symbol) {
		/*
		 Pseudocode:
		 1. For each state in input set:
			  - For each transition with given symbol:
				  - Add destination state to result set
		 2. Return result set
		*/
		Set<State> result = new HashSet<>();
		for (State state : states) {
			List<State> nextStates = state.getTransitions(symbol);
			result.addAll(nextStates);
		}
		return result;
	}


	/**
	 * Finds an existing DFA state representing a given set of NFA states.
	 *
	 * @param dfaStates The list of DFA states.
	 * @param targetNfaStates The set of NFA states to search for.
	 * @return The matching DFA state, or null if not found.
	 */
	private static DfaState findDfaState(List<DfaState> dfaStates, Set<State> targetNfaStates) {
	   /*
	    Pseudocode:
	    1. For each DFA state in list:
		    - If its NFA state set equals target set, return DFA state
	    2. If not found, return null
	   */
	    for(DfaState dfaState : dfaStates) {
			if(dfaState.equals(targetNfaStates)) return dfaState;
 		} 
		return null;
	}
}
