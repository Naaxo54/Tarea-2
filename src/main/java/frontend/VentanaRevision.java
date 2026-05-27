package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class VentanaRevision extends JFrame {

    private final Rutina rutina;
    private int indice = 0;

    private JLabel lblIndicador, lblNombre, lblTipo, lblIntensidad, lblTiempo;
    private JTextArea txtDescripcion;
    private JButton btnVolver, btnSiguiente;
    private JProgressBar barra;

    public VentanaRevision(Frame padre, Rutina rutina) {
        super("Revisión de Rutina");
        this.rutina = rutina;
        construir();
        actualizar();
    }

    private void construir() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(660, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(18, 24, 16, 24));
        panel.setBackground(new Color(245, 247, 250));
        setContentPane(panel);

        // Encabezado
        JPanel header = new JPanel(new BorderLayout(2, 4));
        header.setOpaque(false);
        JLabel titulo = new JLabel("Revisión de Rutina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(40, 60, 100));
        lblIndicador = new JLabel("", SwingConstants.RIGHT);
        lblIndicador.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblIndicador.setForeground(Color.GRAY);
        barra = new JProgressBar(0, rutina.total());
        barra.setForeground(new Color(52, 120, 220));
        barra.setBackground(new Color(220, 228, 240));
        barra.setPreferredSize(new Dimension(100, 6));

        JPanel headerTop = new JPanel(new BorderLayout());
        headerTop.setOpaque(false);
        headerTop.add(titulo, BorderLayout.WEST);
        headerTop.add(lblIndicador, BorderLayout.EAST);
        header.add(headerTop, BorderLayout.NORTH);
        header.add(barra, BorderLayout.SOUTH);
        panel.add(header, BorderLayout.NORTH);

        // Tarjeta del ejercicio
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 215, 240), 1, true),
            new EmptyBorder(18, 20, 18, 20)));

        lblNombre = new JLabel("—");
        lblNombre.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblNombre.setForeground(new Color(30, 50, 100));
        lblNombre.setAlignmentX(LEFT_ALIGNMENT);

        JPanel etiquetas = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        etiquetas.setOpaque(false);
        etiquetas.setAlignmentX(LEFT_ALIGNMENT);
        lblTipo       = badge("—", new Color(52, 120, 220));
        lblIntensidad = badge("—", new Color(46, 160, 80));
        lblTiempo     = badge("—", new Color(170, 100, 20));
        etiquetas.add(lblTipo); etiquetas.add(lblIntensidad); etiquetas.add(lblTiempo);

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(new Color(220, 228, 245));
        sep.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lDesc = new JLabel("Descripción:");
        lDesc.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lDesc.setForeground(new Color(80, 100, 140));
        lDesc.setAlignmentX(LEFT_ALIGNMENT);

        txtDescripcion = new JTextArea();
        txtDescripcion.setEditable(false);
        txtDescripcion.setWrapStyleWord(true);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescripcion.setBackground(new Color(248, 250, 255));
        txtDescripcion.setBorder(new EmptyBorder(8, 10, 8, 10));

        JScrollPane scroll = new JScrollPane(txtDescripcion);
        scroll.setAlignmentX(LEFT_ALIGNMENT);
        scroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(210, 220, 240)));

        card.add(lblNombre);
        card.add(Box.createVerticalStrut(6));
        card.add(etiquetas);
        card.add(Box.createVerticalStrut(8));
        card.add(sep);
        card.add(Box.createVerticalStrut(8));
        card.add(lDesc);
        card.add(Box.createVerticalStrut(4));
        card.add(scroll);
        panel.add(card, BorderLayout.CENTER);

        // Botones de navegación
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setOpaque(false);
        btnVolver    = boton("Volver",    new Color(130, 140, 160));
        btnSiguiente = boton("Siguiente", new Color(52, 120, 220));
        btnVolver.addActionListener(e -> { if (indice > 0) { indice--; actualizar(); } });
        btnSiguiente.addActionListener(e -> {
            if (indice < rutina.total() - 1) { indice++; actualizar(); }
            else new VentanaResumen(this, rutina).setVisible(true);
        });
        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        izq.setOpaque(false); izq.add(btnVolver);
        JPanel der = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        der.setOpaque(false); der.add(btnSiguiente);
        navPanel.add(izq, BorderLayout.WEST);
        navPanel.add(der, BorderLayout.EAST);
        panel.add(navPanel, BorderLayout.SOUTH);
    }

    private void actualizar() {
        Ejercicio e = rutina.get(indice);
        int pos     = indice + 1;
        int total   = rutina.total();

        lblIndicador.setText("Ejercicio " + pos + " de " + total);
        barra.setValue(pos);
        lblNombre.setText(e.getNombre());
        lblTipo.setText("  " + capitalizar(e.getTipo().getNombre()) + "  ");
        lblIntensidad.setText("  Intensidad " + capitalizar(e.getIntensidad().getNombre()) + "  ");
        lblTiempo.setText("  " + e.getTiempoMin() + " min  ");
        txtDescripcion.setText(e.getDescripcion());
        txtDescripcion.setCaretPosition(0);

        btnVolver.setEnabled(indice > 0);
        btnVolver.setBackground(indice > 0 ? new Color(130, 140, 160) : new Color(200, 205, 215));

        boolean esUltimo = (indice == total - 1);
        btnSiguiente.setText(esUltimo ? "Resumen de la rutina" : "Siguiente");
        btnSiguiente.setBackground(esUltimo ? new Color(46, 160, 80) : new Color(52, 120, 220));
    }

    private JLabel badge(String texto, Color color) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 11));
        l.setForeground(Color.WHITE);
        l.setBackground(color);
        l.setOpaque(true);
        l.setBorder(new EmptyBorder(3, 8, 3, 8));
        return l;
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setOpaque(true);
        return b;
    }

    private String capitalizar(String s) {
        return s == null || s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }
}
