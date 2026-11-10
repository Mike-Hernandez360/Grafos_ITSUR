package djs;
import java.util.*;

public class Node {
    String name;
    List<Edge> edges;
    int distance;
    Node previous;
    public Node(String name) {
        this.name = name;
        this.edges = new ArrayList<>();
    }
    public void addEdge(Node destination, int weight) {
        edges.add(new Edge(destination, weight));
    }
    @Override
    public String toString() {
        return name;
    }
}  

