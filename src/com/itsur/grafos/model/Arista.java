package com.itsur.grafos.model;

public class Arista {
    Lugar destino;
    int peso;
    public Arista(Lugar destino, int peso) {
        this.destino = destino;
        this.peso = peso;
    }
    public Lugar getDestino() {
        return destino;
    }
    public int getPeso() {
        return peso;
    }
}
