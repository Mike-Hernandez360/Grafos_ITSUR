package com.itsur.grafos.model;
import java.util.*;

public class Grafo {
    List<Lugar> lugares;
    public Grafo() {
        this.lugares = new ArrayList<>();
    }
    public void agregarLugar(Lugar lug) {
        lugares.add(lug);
    }
    public List<Lugar> getLugares() {
        return lugares;
    }
    public Lugar getLugar(String name) {
        for (Lugar l : lugares) {
            if (l.getNombre().equalsIgnoreCase(name))return l;
        }
        return null;
    }
    public void conectar(Lugar origen, Lugar destino, int peso) {
        origen.agregarArista(destino, peso);
    }
    
}
