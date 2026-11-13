package com.itsur.grafos.ui;

import com.itsur.grafos.algoritmo.Dijkstra;
import com.itsur.grafos.model.*;
import com.itsur.grafos.uil.CargarDatos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Ventana principal de la aplicación Grafos ITSUR.
 * Interfaz gráfica para calcular caminos más cortos con Dijkstra.
 * Soporta selección por combo o por clic en el mapa.
 */
public class VentanaPrincipal extends JFrame {

    private final Grafo grafo;
    private final Dijkstra dijkstra;
    private final PanelGrafo panelGrafo;

    private JComboBox<Lugar> comboOrigen;
    private JComboBox<Lugar> comboDestino;
    private JLabel lblResultado;
    private JLabel lblDistancia;
    private JTextArea areaRuta;
    private JLabel lblInstruccion;

    /** true = próximo clic será origen, false = destino */
    private boolean seleccionandoOrigen = true;

    public VentanaPrincipal() {
        super("Grafos ITSUR - Algoritmo de Dijkstra");

        grafo = new Grafo();
        CargarDatos.cargar(grafo);
        dijkstra = new Dijkstra(grafo);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 680));
        setLocationRelativeTo(null);

        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(new EmptyBorder(12, 12, 12, 12));
        content.setBackground(new Color(240, 242, 245));
        setContentPane(content);

        content.add(crearHeader(), BorderLayout.NORTH);

        panelGrafo = new PanelGrafo();
        panelGrafo.setGrafo(grafo);
        // Callback cuando el usuario hace clic en un nodo
        panelGrafo.setOnNodoClick(this::manejarClicNodo);
        content.add(panelGrafo, BorderLayout.CENTER);

        content.add(crearPanelControles(), BorderLayout.EAST);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setOpaque(false);
        JLabel lblInfo = new JLabel("Proyecto Unidad 4 · ITSUR · Dijkstra · Clic en el mapa para elegir origen y destino");
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblInfo.setForeground(new Color(100, 100, 100));
        footer.add(lblInfo);
        content.add(footer, BorderLayout.SOUTH);

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    SwingUtilities.updateComponentTreeUI(this);
                    break;
                }
            }
        } catch (Exception ignored) {}
    }

    private JPanel crearHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel titulo = new JLabel("🗺️  Caminos más cortos en el campus ITSUR");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        titulo.setForeground(new Color(25, 55, 95));
        header.add(titulo, BorderLayout.WEST);

        lblInstruccion = new JLabel("Haz clic en un lugar del mapa para elegir el ORIGEN");
        lblInstruccion.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblInstruccion.setForeground(new Color(230, 126, 34));
        header.add(lblInstruccion, BorderLayout.SOUTH);

        return header;
    }

    private JPanel crearPanelControles() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(270, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 210, 210)),
            new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel lblTitulo = new JLabel("Calcular ruta");
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 15));
        lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTitulo);
        panel.add(Box.createVerticalStrut(6));

        JLabel lblAyuda = new JLabel("<html><small>También puedes usar los combos<br>o hacer clic en el mapa.</small></html>");
        lblAyuda.setForeground(new Color(110, 110, 110));
        lblAyuda.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblAyuda);
        panel.add(Box.createVerticalStrut(12));

        // Origen
        JLabel lblOrigen = new JLabel("Origen:");
        lblOrigen.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblOrigen);
        panel.add(Box.createVerticalStrut(4));

        comboOrigen = new JComboBox<>();
        comboOrigen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboOrigen.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Lugar l : grafo.getLugares()) {
            comboOrigen.addItem(l);
        }
        comboOrigen.addActionListener(e -> sincronizarDesdeCombos());
        panel.add(comboOrigen);
        panel.add(Box.createVerticalStrut(12));

        // Destino
        JLabel lblDestino = new JLabel("Destino:");
        lblDestino.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDestino);
        panel.add(Box.createVerticalStrut(4));

        comboDestino = new JComboBox<>();
        comboDestino.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        comboDestino.setAlignmentX(Component.LEFT_ALIGNMENT);
        for (Lugar l : grafo.getLugares()) {
            comboDestino.addItem(l);
        }
        if (comboDestino.getItemCount() > 1) {
            comboDestino.setSelectedIndex(1);
        }
        comboDestino.addActionListener(e -> sincronizarDesdeCombos());
        panel.add(comboDestino);
        panel.add(Box.createVerticalStrut(16));

        // Botón calcular
        JButton btnCalcular = new JButton("Calcular camino");
        btnCalcular.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnCalcular.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        btnCalcular.setBackground(new Color(33, 150, 243));
        btnCalcular.setForeground(Color.WHITE);
        btnCalcular.setFocusPainted(false);
        btnCalcular.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnCalcular.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCalcular.addActionListener(e -> calcularRuta());
        panel.add(btnCalcular);
        panel.add(Box.createVerticalStrut(8));

        // Botón limpiar
        JButton btnLimpiar = new JButton("Limpiar selección");
        btnLimpiar.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLimpiar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        btnLimpiar.addActionListener(e -> limpiarTodo());
        panel.add(btnLimpiar);
        panel.add(Box.createVerticalStrut(18));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sep);
        panel.add(Box.createVerticalStrut(14));

        // Resultados
        JLabel lblResTitle = new JLabel("Resultado");
        lblResTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblResTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblResTitle);
        panel.add(Box.createVerticalStrut(10));

        JLabel lblR = new JLabel("Ruta:");
        lblR.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblR);

        areaRuta = new JTextArea(4, 20);
        areaRuta.setEditable(false);
        areaRuta.setLineWrap(true);
        areaRuta.setWrapStyleWord(true);
        areaRuta.setFont(new Font("SansSerif", Font.PLAIN, 12));
        areaRuta.setBackground(new Color(248, 250, 252));
        JScrollPane scroll = new JScrollPane(areaRuta);
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        panel.add(scroll);
        panel.add(Box.createVerticalStrut(10));

        JLabel lblD = new JLabel("Distancia total:");
        lblD.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblD);

        lblDistancia = new JLabel("—");
        lblDistancia.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblDistancia.setForeground(new Color(46, 125, 50));
        lblDistancia.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblDistancia);

        panel.add(Box.createVerticalStrut(12));
        lblResultado = new JLabel(" ");
        lblResultado.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblResultado.setForeground(new Color(100, 100, 100));
        lblResultado.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblResultado);

        panel.add(Box.createVerticalGlue());

        // Sincronizar selección inicial de combos con el mapa
        SwingUtilities.invokeLater(this::sincronizarDesdeCombos);

        return panel;
    }

    /** Maneja el clic en un nodo del mapa */
    private void manejarClicNodo(Lugar lugar) {
        if (seleccionandoOrigen) {
            comboOrigen.setSelectedItem(lugar);
            seleccionandoOrigen = false;
            lblInstruccion.setText("Ahora haz clic en el DESTINO");
            lblInstruccion.setForeground(new Color(198, 40, 40));
        } else {
            comboDestino.setSelectedItem(lugar);
            seleccionandoOrigen = true;
            lblInstruccion.setText("Haz clic en un lugar del mapa para elegir el ORIGEN");
            lblInstruccion.setForeground(new Color(230, 126, 34));
            // Calcular automáticamente al elegir destino
            calcularRuta();
        }
        actualizarColoresMapa();
    }

    private void sincronizarDesdeCombos() {
        actualizarColoresMapa();
    }

    private void actualizarColoresMapa() {
        Lugar origen = (Lugar) comboOrigen.getSelectedItem();
        Lugar destino = (Lugar) comboDestino.getSelectedItem();
        panelGrafo.setOrigenDestino(origen, destino);
    }

    private void calcularRuta() {
        Lugar origen = (Lugar) comboOrigen.getSelectedItem();
        Lugar destino = (Lugar) comboDestino.getSelectedItem();

        if (origen == null || destino == null) {
            JOptionPane.showMessageDialog(this, "Selecciona origen y destino.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (origen == destino) {
            JOptionPane.showMessageDialog(this, "El origen y el destino deben ser diferentes.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Dijkstra.Resultado res = dijkstra.calcular(origen, destino);

        if (res == null) {
            panelGrafo.limpiarRuta();
            areaRuta.setText("No existe un camino entre estos lugares.");
            lblDistancia.setText("—");
            lblResultado.setText("Sin ruta");
            return;
        }

        panelGrafo.setRuta(res.getRuta());
        panelGrafo.setOrigenDestino(origen, destino);

        areaRuta.setText(res.getRutaComoTexto());
        lblDistancia.setText(res.getDistanciaFormateada());
        lblResultado.setText(res.getRuta().size() + " lugares en la ruta");
    }

    private void limpiarTodo() {
        panelGrafo.limpiarRuta();
        panelGrafo.setOrigenDestino(null, null);
        areaRuta.setText("");
        lblDistancia.setText("—");
        lblResultado.setText(" ");
        seleccionandoOrigen = true;
        lblInstruccion.setText("Haz clic en un lugar del mapa para elegir el ORIGEN");
        lblInstruccion.setForeground(new Color(230, 126, 34));
        if (comboOrigen.getItemCount() > 0) comboOrigen.setSelectedIndex(0);
        if (comboDestino.getItemCount() > 1) comboDestino.setSelectedIndex(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal v = new VentanaPrincipal();
            v.setVisible(true);
        });
    }
}
