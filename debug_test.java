// Test simple para debugear el algoritmo FOLLOW
import java.util.Map;
import java.util.Set;

public class DebugTest {
    public static void main(String[] args) {
        String grammarDef = "S -> A B\nA -> a\nB -> b";
        Grammar grammar = new Grammar(grammarDef);
        
        System.out.println("Grammar productions:");
        for (var prod : grammar.getProductions()) {
            System.out.println("  " + prod.getLeft().name + " -> " + 
                prod.getRight().stream().map(s -> s.name).collect(java.util.stream.Collectors.joining(" ")));
        }
        
        System.out.println("\nNon-terminals:");
        for (var nt : grammar.getNonTerminals()) {
            System.out.println("  " + nt.name);
        }
        
        System.out.println("\nTerminals:");
        for (var t : grammar.getTerminals()) {
            System.out.println("  " + t.name);
        }
        
        StaticAnalyzer analyzer = new StaticAnalyzer(grammar);
        
        Map<Symbol, Set<Symbol>> firstSets = analyzer.getFirstSets();
        System.out.println("\nFIRST sets:");
        for (var entry : firstSets.entrySet()) {
            System.out.println("  FIRST(" + entry.getKey().name + ") = {" + 
                entry.getValue().stream().map(s -> s.name).collect(java.util.stream.Collectors.joining(", ")) + "}");
        }
        
        Map<Symbol, Set<Symbol>> followSets = analyzer.getFollowSets();
        System.out.println("\nFOLLOW sets:");
        for (var entry : followSets.entrySet()) {
            System.out.println("  FOLLOW(" + entry.getKey().name + ") = {" + 
                entry.getValue().stream().map(s -> s.name).collect(java.util.stream.Collectors.joining(", ")) + "}");
        }
    }
}