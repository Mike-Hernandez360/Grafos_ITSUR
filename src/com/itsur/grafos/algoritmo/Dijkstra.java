package algoritmo;
import java.util.*;
import model.*;

public class Dijkstra {
    public void dijkstra(Lugar start, Lugar end) {
        // Inicializar todos los nodos
        for (Lugar l : grafo.getLugares()) {
            l.setDistancia(Integer.MAX_VALUE);
            l.setAnterior(null);
        }
        start.setDistancia(0);
        // Crear PriorityQueue
        PriorityQueue<Lugar> pq = new PriorityQueue<>(
            Comparator.comparingInt(Lugar::getDistancia)
        );
        pq.add(start);
        // Procesar nodos
        while (!pq.isEmpty()) {
            Lugar current = pq.poll();
            if (current == end)break;
            // Revisar vecinos
            for (Arista arista : actual.getAristas()) {
                Lugar vecino = arista.getDestino();
                int nuevaDistancia = current.getDistancia() + arista.getPeso();
                // ¿Encontramos un camino más corto?
                if (nuevaDistancia < vecino.getDistancia()) {
                    vecino.setDistancia(nuevaDistancia);
                    vecino.setAnterior(current);
                    pq.add(neighbor);
                }
            }
        }
        // Mostrar resultado
        if (end.getDistancia() == Integer.MAX_VALUE) {
            System.out.println("No existe un camino de " + start.getNombre() + " a " + end.getNombre());
            return;
        }
        // Reconstruir camino
        List<Lugar> ruta = new ArrayList<>();
        Lugar actual = end;
        while (actual != null) {
            ruta.add(actual);
            actual = actual.getAnterior();
        }
        System.out.println("Camino más corto:");
        // while (!ruta.isEmpty()) {
        //     System.out.print(ruta.remove(ruta.size() - 1));
        //     if (!ruta.isEmpty()) {
        //         System.out.print(" -> ");
        //     }
        // }
        for(int i=0; i < ruta.size(); i++){
            System.out.print(ruta.get(i));
            if(i < ruta.size()-1)System.out.print(" -> ");
        }
        System.out.println("\nDistancia total: " + end.getDistancia()+" metros");
    }
}
