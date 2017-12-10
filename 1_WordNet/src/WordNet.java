import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by aonnikov on 26/11/2017.
 */
public class WordNet {

    private int synsetCount; // Count of synsets
    private final Map<Integer, String> synsetsMap; // Map synset Id to its name
    private final Map<String, Bag<Integer>> nounsMap; // Map noun to synset Id
    private final Digraph hypernymsDigraph; // Digraph of hypernyms
    private final SAP sap;

    // Constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null) {
            throw new IllegalArgumentException("The synsets parameter must be not null!");
        }
        if (hypernyms == null) {
            throw new IllegalArgumentException("The hypernyms parameter must be not null!");
        }

        this.synsetsMap = new HashMap<>();
        this.nounsMap = new HashMap<>();

        In in = new In(synsets);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] fields = line.split(",");
            String[] nouns = fields[1].split(" ");

            this.synsetsMap.put(this.synsetCount, fields[1]);
            for (String noun : nouns) {
                if (!this.nounsMap.containsKey(noun)) {
                    this.nounsMap.put(noun, new Bag<>());
                }
                this.nounsMap.get(noun).add(Integer.valueOf(fields[0]));
            }
            this.synsetCount++;
        }

        this.hypernymsDigraph = new Digraph(this.synsetCount);

        in = new In(hypernyms);
        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] fields = line.split(",");

            int synsetId = Integer.parseInt(fields[0]);
            for (int i = 1; i < fields.length; i++) {
                int hypernymId = Integer.parseInt(fields[i]);
                this.hypernymsDigraph.addEdge(synsetId, hypernymId);
            }
        }

        if (!isDAG()) {
            throw new IllegalArgumentException("Input is not a DAG!");
        }

        this.sap = new SAP(hypernymsDigraph);
    }

    private boolean isDAG() {
        // Determine roots count
        int rootCount = 0;
        for (int i = 0; i < this.hypernymsDigraph.V(); i++) {
            Iterable<Integer> adj = this.hypernymsDigraph.adj(i);
            if (!adj.iterator().hasNext()) {
                rootCount++;
            }
        }
        if (rootCount > 1) {
            return false;
        }

        // Find out cycles
        DirectedCycle directedCycle = new DirectedCycle(this.hypernymsDigraph);
        if (directedCycle.hasCycle()) {
            return false;
        }

        return true;
    }

    // Returns all WordNet nouns
    public Iterable<String> nouns() {
        return this.nounsMap.keySet();
    }

    // Is the word a WordNet noun?
    public boolean isNoun(String word) {
        if (word == null) {
            throw new IllegalArgumentException("The word parameter must be not null!");
        }
        return this.nounsMap.containsKey(word);
    }

    // Distance between nounA and nounB
    public int distance(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new IllegalArgumentException("The nounA is not a WordNet noun!");
        }
        if (!isNoun(nounB)) {
            throw new IllegalArgumentException("The nounB is not a WordNet noun!");
        }

        return this.sap.length(this.nounsMap.get(nounA), this.nounsMap.get(nounB));
    }

    // A synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        if (!isNoun(nounA)) {
            throw new IllegalArgumentException("The nounA is not a WordNet noun!");
        }
        if (!isNoun(nounB)) {
            throw new IllegalArgumentException("The nounB is not a WordNet noun!");
        }

        int ancestor = this.sap.ancestor(this.nounsMap.get(nounA), this.nounsMap.get(nounB));
        return this.synsetsMap.get(ancestor);
    }

    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        System.out.println("Synsets: " + wordNet.synsetCount);
        System.out.println("Nouns: " + wordNet.nounsMap.size());
        System.out.println(wordNet.hypernymsDigraph.V());
        System.out.println(wordNet.hypernymsDigraph.E());

        assert wordNet.sap("unit", "benzyl_radical").equals("unit building_block");
    }
}
