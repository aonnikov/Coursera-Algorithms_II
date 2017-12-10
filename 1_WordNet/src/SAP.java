import edu.princeton.cs.algs4.BreadthFirstDirectedPaths;
import edu.princeton.cs.algs4.Digraph;

public class SAP {

    private final Digraph G;

    public SAP(Digraph G) {
        this.G = new Digraph(G);
    }

    // Length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        BreadthFirstDirectedPaths vP = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wP = new BreadthFirstDirectedPaths(this.G, w);

        int ancestor = ancestor(vP, wP);
        if (ancestor != -1) {
            return distanceTo(vP, wP, ancestor);
        }
        return -1;
    }

    // A common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        validateVertex(v);
        validateVertex(w);

        BreadthFirstDirectedPaths vP = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wP = new BreadthFirstDirectedPaths(this.G, w);

        return ancestor(vP, wP);
    }

    private int ancestor(BreadthFirstDirectedPaths v, BreadthFirstDirectedPaths w) {
        int ancestor = -1;
        int shortestPath = Integer.MAX_VALUE;

        for (int p = 0; p < this.G.V(); p++) {
            if (v.hasPathTo(p) && w.hasPathTo(p)) {
                int d = distanceTo(v, w, p);
                if (d < shortestPath) {
                    shortestPath = d;
                    ancestor = p;
                }
            }
        }
        return ancestor;
    }

    private int distanceTo(BreadthFirstDirectedPaths v, BreadthFirstDirectedPaths w, int p) {
        return v.distTo(p) + w.distTo(p);
    }

    // Length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        BreadthFirstDirectedPaths vP = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wP = new BreadthFirstDirectedPaths(this.G, w);

        int ancestor = ancestor(vP, wP);
        if (ancestor != -1) {
            return distanceTo(vP, wP, ancestor);
        }
        return -1;
    }

    // A common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new IllegalArgumentException();
        }

        BreadthFirstDirectedPaths vP = new BreadthFirstDirectedPaths(this.G, v);
        BreadthFirstDirectedPaths wP = new BreadthFirstDirectedPaths(this.G, w);

        return ancestor(vP, wP);
    }

    private void validateVertex(int v) {
        if (v < 0 || v > this.G.V() - 1) {
            throw new IllegalArgumentException("Invalid vertex index " + v);
        }
    }

    // Do unit testing of this class
    public static void main(String[] args) {
        Digraph G = new Digraph(13);
        G.addEdge(1, 0);
        G.addEdge(2, 0);
        G.addEdge(3, 1);
        G.addEdge(4, 1);
        G.addEdge(5, 1);
        G.addEdge(7, 3);
        G.addEdge(8, 3);
        G.addEdge(9, 5);
        G.addEdge(10, 5);
        G.addEdge(11, 10);
        G.addEdge(12, 10);

        SAP sap = new SAP(G);

        assert sap.ancestor(3, 11) == 1;
        assert sap.ancestor(11, 12) == 10;

        assert sap.length(3, 11) == 4;
    }
}