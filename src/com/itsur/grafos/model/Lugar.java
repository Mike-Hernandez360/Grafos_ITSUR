package com.itsur.grafos.model;
import java.util.*;

public class Lugar {
    String nombre;
    List<Arista> aristas;
    int distancias;
    Lugar anterior;
    public Lugar(String nombre) {
        this.nombre = nombre;
        this.aristas = new ArrayList<>();
        this.distancias = Integer.MAX_VALUE;
        this.anterior = null;
    }
    public void agregarArista(Lugar destino, int peso) {
        aristas.add(new Arista(destino, peso));
    }
    public String getNombre() {
        return nombre;
    }
    public List<Arista> getAristas() {
        return aristas;
    }
    public int getDistancia() {
        return distancias;
    }
    public Lugar getAnterior() {
        return anterior;
    }
    public void setDistancia(int distancia) {
        this.distancias = distancia;
    }
    public void setAnterior(Lugar anterior) {
        this.anterior = anterior;
    }

    @Override
    public String toString() {
        return nombre;
    }
}  

