package com.itsur.grafos.ui;

import com.itsur.grafos.model.*;
import com.itsur.grafos.uil.CargarDatos;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

/**
 * Panel que dibuja el grafo sobre la imagen del campus ITSUR.
 * Las coordenadas de los nodos están en píxeles de la imagen original;
 * aquí se transforman al tamaño/posición real del fondo escalado.
 */
public class PanelGrafo extends JPanel {

    private Grafo grafo;
    private List<Lugar> rutaResaltada;
    private Set<String> aristasRuta;

    private Lugar lugarOrigen;
    private Lugar lugarDestino;

    private BufferedImage imagenFondo;
    private Consumer<Lugar> onNodoClick;

    // Transformación imagen → panel (se calcula en cada paint)
    private double scale = 1.0;
    private int offsetX = 0;
    private int offsetY = 0;

    private static final int RADIO = 18;
    private static final Color COLOR_NODO = new Color(33, 150, 243, 235);
    private static final Color COLOR_NODO_RUTA = new Color(76, 175, 80, 240);
    private static final Color COLOR_NODO_ORIGEN = new Color(255, 152, 0, 245);
    private static final Color COLOR_NODO_DESTINO = new Color(244, 67, 54, 245);
    private static final Color COLOR_ARISTA = new Color(50, 50, 50, 200);
    private static final Color COLOR_ARISTA_RUTA = new Color(46, 160, 50);
    private static final Color COLOR_HOVER = new Color(255, 235, 59, 230);

    private Lugar nodoHover;

    public PanelGrafo() {
        setPreferredSize(new Dimension(780, 500));
        setBackground(new Color(30, 40, 50));
        setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 180)),
            " Mapa del Campus ITSUR · Clic en un lugar para elegir origen/destino "));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        cargarImagenFondo();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Lugar clic = encontrarNodo(e.getX(), e.getY());
                if (clic != null && onNodoClick != null) {
                    onNodoClick.accept(clic);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (nodoHover != null) {
                    nodoHover = null;
                    repaint();
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                Lugar bajo = encontrarNodo(e.getX(), e.getY());
                if (bajo != nodoHover) {
                    nodoHover = bajo;
                    repaint();
                }
            }
        });
    }

    private void cargarImagenFondo() {
        String[] rutas = {
            "/com/itsur/grafos/resources/img-itsur.png",
            "/img-itsur.png"
        };
        for (String ruta : rutas) {
            try {
                InputStream is = getClass().getResourceAsStream(ruta);
                if (is != null) {
                    imagenFondo = ImageIO.read(is);
                    is.close();
                    if (imagenFondo != null) return;
                }
            } catch (IOException ignored) {}
        }
        try {
            java.io.File f = new java.io.File("docs/img-itsur.png");
            if (f.exists()) {
                imagenFondo = ImageIO.read(f);
            } else {
                f = new java.io.File("src/com/itsur/grafos/resources/img-itsur.png");
                if (f.exists()) imagenFondo = ImageIO.read(f);
            }
        } catch (IOException ignored) {}
    }

    /** Convierte coordenada de imagen → coordenada de panel */
    private int toPanelX(int imgX) {
        return offsetX + (int) Math.round(imgX * scale);
    }

    private int toPanelY(int imgY) {
        return offsetY + (int) Math.round(imgY * scale);
    }

    /** Calcula scale y offsets para cubrir el panel (cover) */
    private void calcularTransformacion(int panelW, int panelH) {
        int imgW = imagenFondo != null ? imagenFondo.getWidth() : CargarDatos.IMG_W;
        int imgH = imagenFondo != null ? imagenFondo.getHeight() : CargarDatos.IMG_H;

        // Cover: la imagen cubre todo el panel
        scale = Math.max((double) panelW / imgW, (double) panelH / imgH);
        int iw = (int) Math.round(imgW * scale);
        int ih = (int) Math.round(imgH * scale);
        offsetX = (panelW - iw) / 2;
        offsetY = (panelH - ih) / 2;
    }

    public void setGrafo(Grafo grafo) {
        this.grafo = grafo;
        this.rutaResaltada = null;
        this.aristasRuta = null;
        this.lugarOrigen = null;
        this.lugarDestino = null;
        repaint();
    }

    public void setOnNodoClick(Consumer<Lugar> callback) {
        this.onNodoClick = callback;
    }

    public void setOrigenDestino(Lugar origen, Lugar destino) {
        this.lugarOrigen = origen;
        this.lugarDestino = destino;
        repaint();
    }

    public void setRuta(List<Lugar> ruta) {
        this.rutaResaltada = ruta;
        this.aristasRuta = new HashSet<>();
        if (ruta != null && ruta.size() > 1) {
            for (int i = 0; i < ruta.size() - 1; i++) {
                String a = ruta.get(i).getNombre();
                String b = ruta.get(i + 1).getNombre();
                aristasRuta.add(a + "|" + b);
                aristasRuta.add(b + "|" + a);
            }
        }
        repaint();
    }

    public void limpiarRuta() {
        this.rutaResaltada = null;
        this.aristasRuta = null;
        repaint();
    }

    private Lugar encontrarNodo(int mx, int my) {
        if (grafo == null) return null;
        // Usar la última transformación calculada
        for (Lugar l : grafo.getLugares()) {
            int px = toPanelX(l.getX());
            int py = toPanelY(l.getY());
            int dx = mx - px;
            int dy = my - py;
            if (dx * dx + dy * dy <= RADIO * RADIO) {
                return l;
            }
        }
        return null;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth();
        int h = getHeight();
        calcularTransformacion(w, h);

        // === Fondo ===
        if (imagenFondo != null) {
            int iw = (int) Math.round(imagenFondo.getWidth() * scale);
            int ih = (int) Math.round(imagenFondo.getHeight() * scale);
            g2.drawImage(imagenFondo, offsetX, offsetY, iw, ih, null);
            // Capa suave para legibilidad
            g2.setColor(new Color(255, 255, 255, 70));
            g2.fillRect(0, 0, w, h);
        } else {
            g2.setColor(new Color(245, 248, 250));
            g2.fillRect(0, 0, w, h);
        }

        if (grafo == null) {
            g2.dispose();
            return;
        }

        // === Aristas ===
        Set<String> dibujadas = new HashSet<>();
        for (Lugar lugar : grafo.getLugares()) {
            for (Arista arista : lugar.getAristas()) {
                Lugar dest = arista.getDestino();
                String key1 = lugar.getNombre() + "|" + dest.getNombre();
                String key2 = dest.getNombre() + "|" + lugar.getNombre();
                if (dibujadas.contains(key1) || dibujadas.contains(key2)) continue;
                dibujadas.add(key1);

                boolean enRuta = aristasRuta != null && aristasRuta.contains(key1);

                int x1 = toPanelX(lugar.getX());
                int y1 = toPanelY(lugar.getY());
                int x2 = toPanelX(dest.getX());
                int y2 = toPanelY(dest.getY());

                if (enRuta) {
                    g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2.setColor(COLOR_ARISTA_RUTA);
                } else {
                    g2.setStroke(new BasicStroke(2.2f));
                    g2.setColor(COLOR_ARISTA);
                }
                g2.drawLine(x1, y1, x2, y2);

                // Peso
                int mx = (x1 + x2) / 2;
                int my = (y1 + y2) / 2;
                String peso = (arista.getPeso() == Math.rint(arista.getPeso()))
                        ? ((int) arista.getPeso()) + " m"
                        : String.format(java.util.Locale.US, "%.2f m", arista.getPeso());
                g2.setFont(new Font("SansSerif", Font.BOLD, 11));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(peso);
                g2.setColor(new Color(255, 255, 255, 230));
                g2.fillRoundRect(mx - tw / 2 - 4, my - 11, tw + 8, 16, 8, 8);
                g2.setColor(enRuta ? new Color(27, 94, 32) : new Color(30, 30, 30));
                g2.drawString(peso, mx - tw / 2, my + 2);
            }
        }

        // === Nodos ===
        for (Lugar lugar : grafo.getLugares()) {
            int px = toPanelX(lugar.getX());
            int py = toPanelY(lugar.getY());

            Color colorNodo = COLOR_NODO;
            if (lugar == lugarOrigen) {
                colorNodo = COLOR_NODO_ORIGEN;
            } else if (lugar == lugarDestino) {
                colorNodo = COLOR_NODO_DESTINO;
            } else if (rutaResaltada != null && rutaResaltada.contains(lugar)) {
                colorNodo = COLOR_NODO_RUTA;
            }

            if (lugar == nodoHover) {
                g2.setColor(COLOR_HOVER);
                g2.fill(new Ellipse2D.Double(px - RADIO - 6, py - RADIO - 6,
                        (RADIO + 6) * 2.0, (RADIO + 6) * 2.0));
            }

            // Sombra
            g2.setColor(new Color(0, 0, 0, 55));
            g2.fill(new Ellipse2D.Double(px - RADIO + 3, py - RADIO + 3, RADIO * 2.0, RADIO * 2.0));

            // Círculo
            g2.setColor(colorNodo);
            g2.fill(new Ellipse2D.Double(px - RADIO, py - RADIO, RADIO * 2.0, RADIO * 2.0));
            g2.setStroke(new BasicStroke(2.5f));
            g2.setColor(colorNodo.darker());
            g2.draw(new Ellipse2D.Double(px - RADIO, py - RADIO, RADIO * 2.0, RADIO * 2.0));

            // Inicial
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("SansSerif", Font.BOLD, 13));
            String inicial = obtenerInicial(lugar.getNombre());
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(inicial, px - fm.stringWidth(inicial) / 2, py + 5);

            // Nombre
            g2.setFont(new Font("SansSerif", Font.BOLD, 12));
            fm = g2.getFontMetrics();
            String nombre = lugar.getNombre();
            int tw = fm.stringWidth(nombre);
            g2.setColor(new Color(255, 255, 255, 210));
            g2.fillRoundRect(px - tw / 2 - 4, py + RADIO + 2, tw + 8, 16, 6, 6);
            g2.setColor(new Color(20, 20, 20));
            g2.drawString(nombre, px - tw / 2, py + RADIO + 14);
        }

        dibujarLeyenda(g2);
        g2.dispose();
    }

    private String obtenerInicial(String nombre) {
        if (nombre == null || nombre.isEmpty()) return "?";
        String n = nombre.toLowerCase();
        if (n.equals("entrada") || n.contains("entrada principal")) return "EN";
        if (n.contains("caseta") && n.contains("estacionamiento")) return "CE";
        if (n.contains("caseta") && n.contains("puerta")) return "CP";
        if (n.contains("edificio a")) return "A";
        if (n.contains("edificio b")) return "B";
        if (n.contains("edificio e")) return "E";
        if (n.contains("tics") || n.contains("tic")) return "TI";
        if (n.contains("vinculación") || n.contains("vinculacion")) return "VI";
        if (n.contains("auditorio") && n.contains("principal")) return "AP";
        if (n.contains("auditorio")) return "AU";
        if (n.contains("biblioteca")) return "BI";
        if (n.contains("cafetería") || n.contains("cafeteria")) return "CA";
        if (n.contains("dirección") || n.contains("direccion")) return "DI";
        if (n.contains("palapas")) return "PA";
        if (n.contains("estacionamiento") && !n.contains("caseta")) return "ES";
        if (n.contains("futbol") || n.contains("fútbol")) return "CF";
        if (n.contains("basket") || n.contains("básquet") || n.contains("basquet")) return "BK";
        if (n.contains("cocinas")) return "CG";
        if (n.contains("cabaña") || n.contains("cabana")) return "CB";
        String[] partes = nombre.split(" ");
        if (partes.length >= 2) {
            return ("" + partes[0].charAt(0) + partes[1].charAt(0)).toUpperCase();
        }
        return nombre.substring(0, Math.min(2, nombre.length())).toUpperCase();
    }

    private void dibujarLeyenda(Graphics2D g2) {
        int lx = 12;
        int ly = getHeight() - 58;

        g2.setColor(new Color(255, 255, 255, 220));
        g2.fillRoundRect(lx - 6, ly - 8, 390, 48, 10, 10);

        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(COLOR_NODO_ORIGEN);
        g2.fillOval(lx, ly, 14, 14);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Origen", lx + 18, ly + 12);

        g2.setColor(COLOR_NODO_DESTINO);
        g2.fillOval(lx + 75, ly, 14, 14);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Destino", lx + 93, ly + 12);

        g2.setColor(COLOR_NODO_RUTA);
        g2.fillOval(lx + 155, ly, 14, 14);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("En la ruta", lx + 173, ly + 12);

        g2.setColor(COLOR_NODO);
        g2.fillOval(lx + 250, ly, 14, 14);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString("Otros", lx + 268, ly + 12);

        g2.setFont(new Font("SansSerif", Font.ITALIC, 10));
        g2.setColor(new Color(80, 80, 80));
        g2.drawString("Clic en un nodo para seleccionar", lx, ly + 32);
    }
}
