package com.itsur.grafos;

import com.itsur.grafos.ui.VentanaPrincipal;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Proyecto Unidad 4: Grafos ITSUR
 * Punto de entrada. Lanza la interfaz gráfica.
 */
public class Main {
    public static void main(String[] args) {
        // Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        });
    }
}
