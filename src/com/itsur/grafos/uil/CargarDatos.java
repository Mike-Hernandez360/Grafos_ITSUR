package com.itsur.grafos.uil;

import com.itsur.grafos.model.*;

/**
 * Grafo del campus ITSUR con distancias medidas (metros).
 */
public class CargarDatos {

    public static final int IMG_W = 1004;
    public static final int IMG_H = 638;

    public static void cargar(Grafo grafo) {
        Lugar entrada = new Lugar("Entrada", 420, 130);
        Lugar biblioteca = new Lugar("Biblioteca", 340, 185);
        Lugar palapas = new Lugar("Palapas", 320, 250);
        Lugar edificioA = new Lugar("Edificio A", 400, 255);
        Lugar edificioB = new Lugar("Edificio B", 520, 290);
        Lugar edificioVinc = new Lugar("Edificio, auditorio de vinculación y cocinas de gastronomía", 195, 230);
        Lugar cabana = new Lugar("Cabaña de gastronomía", 165, 330);
        Lugar auditorioPrin = new Lugar("Auditorio principal", 210, 460);
        Lugar canchasBasket = new Lugar("Canchas de básquetbol", 300, 480);
        Lugar cafeteria = new Lugar("Cafetería", 430, 470);
        Lugar edificioE = new Lugar("Edificio E", 380, 430);
        Lugar edificioTics = new Lugar("Edificio de Tic's", 460, 410);
        Lugar casetaEst = new Lugar("Caseta vigilancia estacionamiento", 530, 145);
        Lugar estacionamiento = new Lugar("Estacionamiento", 650, 185);
        Lugar canchaFutbol = new Lugar("Cancha de futbol", 720, 400);

        grafo.agregarLugar(entrada);
        grafo.agregarLugar(biblioteca);
        grafo.agregarLugar(palapas);
        grafo.agregarLugar(edificioA);
        grafo.agregarLugar(edificioB);
        grafo.agregarLugar(edificioVinc);
        grafo.agregarLugar(cabana);
        grafo.agregarLugar(auditorioPrin);
        grafo.agregarLugar(canchasBasket);
        grafo.agregarLugar(cafeteria);
        grafo.agregarLugar(edificioE);
        grafo.agregarLugar(edificioTics);
        grafo.agregarLugar(casetaEst);
        grafo.agregarLugar(estacionamiento);
        grafo.agregarLugar(canchaFutbol);

        // Aristas con medidas del usuario (metros)
        conectar(grafo, entrada, biblioteca, 27.37);
        conectar(grafo, entrada, edificioA, 83.58);
        conectar(grafo, entrada, edificioB, 100);
        conectar(grafo, biblioteca, palapas, 47.02);
        conectar(grafo, edificioA, palapas, 20.98);
        conectar(grafo, edificioA, edificioB, 69.37);
        // FALTA valor exacto: Palapas - Edificio, auditorio de vinculación y cocinas de gastronomía
        // Temporal: 50.0 hasta que confirmes el valor
        conectar(grafo, palapas, edificioVinc, 50.0);
        conectar(grafo, palapas, cabana, 78.28);
        conectar(grafo, edificioVinc, cabana, 42.05);
        conectar(grafo, cabana, auditorioPrin, 58.6);
        conectar(grafo, auditorioPrin, canchasBasket, 27.41);
        conectar(grafo, canchasBasket, cafeteria, 60.28);
        conectar(grafo, cafeteria, edificioE, 33.34);
        conectar(grafo, cafeteria, edificioTics, 45.42);
        conectar(grafo, edificioTics, edificioA, 80.02);
        conectar(grafo, edificioTics, edificioB, 62.95);
        conectar(grafo, edificioB, casetaEst, 94.85);
        conectar(grafo, edificioB, estacionamiento, 103.37);
        conectar(grafo, edificioB, canchaFutbol, 92.80);
    }

    private static void conectar(Grafo g, Lugar a, Lugar b, double peso) {
        g.conectar(a, b, peso);
        g.conectar(b, a, peso);
    }
}
