package djs;
import java.util.*;

public class Graph {
    List<Node> nodes = new ArrayList<>();
    public void addNode(Node n) {
        nodes.add(n);
    }
    public void dijkstra(Node start, Node end) {
        // Inicializar todos los nodos
        for (Node n : nodes) {
            n.distance = Integer.MAX_VALUE;
            n.previous = null;
        }
        start.distance = 0;
        // Crear PriorityQueue
        PriorityQueue<Node> pq = new PriorityQueue<>(
        Comparator.comparingInt(n -> n.distance)
        );
        pq.add(start);
        // Procesar nodos
        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (current == end) {
                break;
            }
            // Revisar vecinos
            for (Edge edge : current.edges) {
                Node neighbor = edge.destination;
                int newDistance = current.distance + edge.weight;
                // ¿Encontramos un camino más corto?
                if (newDistance < neighbor.distance) {
                    neighbor.distance = newDistance;
                    neighbor.previous = current;
                    pq.add(neighbor);
                }
            }
        }
        // Mostrar resultado
        if (end.distance == Integer.MAX_VALUE) {
            System.out.println("No existe un camino.");
            return;
        }
        // Reconstruir camino
        Stack<Node> path = new Stack<>();
        Node current = end;
        while (current != null) {
            path.push(current);
            current = current.previous;
        }
        System.out.println("Camino más corto:");
        while (!path.isEmpty()) {
            System.out.print(path.pop());
            if (!path.isEmpty()) {
                System.out.print(" -> ");
            }
        }
        System.out.println("\nDistancia total: " + end.distance);
    }
}
