package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

public class VentanaPrincipal extends JFrame implements Observador {

    private final Controlador ctrl = Controlador.getInstance();

    private JLabel lblTotal, lblTiempo, lblCardio, lblFuerza, lblBaja, lblMedia, lblAlta, lblEstado;
    private JButton btnGenerar;

    public VentanaPrincipal() {
        super("Sistema de Rutinas de Entrenamiento");
        ctrl.suscribir(this);
        construir();
    }

    private void construir() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 24, 20, 24));
        panel.setBackground(new Color(245, 247, 250));
        setContentPane(panel);

        // Título
        JLabel titulo = new JLabel("Sistema de Rutinas de Entrenamiento", SwingConstants.CENTER);
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titulo.setForeground(new Color(40, 60, 100));
        panel.add(titulo, BorderLayout.NORTH);

        // Tarjetas de estadísticas
        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 14, 0));
        tarjetas.setOpaque(false);
        tarjetas.add(tarjetaGeneral());
        tarjetas.add(tarjetaTipo());
        tarjetas.add(tarjetaIntensidad());
        panel.add(tarjetas, BorderLayout.CENTER);

        // Área inferior
        JPanel inferior = new JPanel(new BorderLayout(6, 6));
        inferior.setOpaque(false);

        lblEstado = new JLabel("Cargue un archivo o base de datos para comenzar.", SwingConstants.CENTER);
        lblEstado.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblEstado.setForeground(Color.GRAY);

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        botones.setOpaque(false);

        JButton btnCargar = boton("Cargar Ejercicios", new Color(52, 120, 220));
        btnGenerar        = boton("Generar Rutina",    new Color(46, 160, 80));

        btnCargar.addActionListener(e -> new VentanaCarga(this).setVisible(true));
        btnGenerar.addActionListener(e -> {
            if (!ctrl.hayEjercicios()) {
                JOptionPane.showMessageDialog(this, "Primero cargue los ejercicios.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            new VentanaGeneracion(this).setVisible(true);
        });

        botones.add(btnCargar);
        botones.add(btnGenerar);
        inferior.add(lblEstado, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);
        panel.add(inferior, BorderLayout.SOUTH);

        btnGenerar.setEnabled(false);
        btnGenerar.setBackground(new Color(180, 180, 180));
    }

    private JPanel tarjetaGeneral() {
        JPanel card = tarjeta();
        lblTotal  = etiquetaGrande("—");
        lblTiempo = etiquetaChica("—");
        card.add(etiquetaTitulo("Total de ejercicios"));
        card.add(Box.createVerticalStrut(6));
        card.add(lblTotal);
        card.add(Box.createVerticalStrut(4));
        card.add(lblTiempo);
        return card;
    }

    private JPanel tarjetaTipo() {
        JPanel card = tarjeta();
        lblCardio = etiquetaGrande("Cardiovascular: —");
        lblFuerza = etiquetaGrande("Fuerza: —");
        card.add(etiquetaTitulo("Por tipo"));
        card.add(Box.createVerticalStrut(10));
        card.add(lblCardio);
        card.add(Box.createVerticalStrut(6));
        card.add(lblFuerza);
        return card;
    }

    private JPanel tarjetaIntensidad() {
        JPanel card = tarjeta();
        lblBaja  = etiquetaGrande("Baja: —");
        lblMedia = etiquetaGrande("Media: —");
        lblAlta  = etiquetaGrande("Alta: —");
        card.add(etiquetaTitulo("Por intensidad"));
        card.add(Box.createVerticalStrut(8));
        card.add(lblBaja);
        card.add(Box.createVerticalStrut(4));
        card.add(lblMedia);
        card.add(Box.createVerticalStrut(4));
        card.add(lblAlta);
        return card;
    }

    @Override
    public void alRecibir(Evento evento, Object datos) {
        if (evento == Evento.EJERCICIOS_CARGADOS) {
            actualizarEstadisticas();
            btnGenerar.setEnabled(true);
            btnGenerar.setBackground(new Color(46, 160, 80));
            lblEstado.setText("Ejercicios cargados desde: " + ctrl.getFuenteDatos());
            lblEstado.setForeground(new Color(40, 140, 60));
        } else if (evento == Evento.ERROR_CARGA) {
            lblEstado.setText("Error: " + datos);
            lblEstado.setForeground(new Color(180, 40, 40));
            JOptionPane.showMessageDialog(this, datos, "Error de carga", JOptionPane.ERROR_MESSAGE);
        } else if (evento == Evento.RUTINA_GENERADA) {
            new VentanaRevision(this, (Rutina) datos).setVisible(true);
        } else if (evento == Evento.ERROR_GENERACION) {
            JOptionPane.showMessageDialog(this, datos, "Error de generación", JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    private void actualizarEstadisticas() {
        Map<String, Object> stats = ctrl.estadisticas();
        int total  = (int) stats.get("total");
        int tiempo = (int) stats.get("tiempoTotal");
        Map<TipoEjercicio, Integer>   porTipo  = (Map<TipoEjercicio, Integer>)   stats.get("porTipo");
        Map<NivelIntensidad, Integer> porNivel = (Map<NivelIntensidad, Integer>)  stats.get("porNivel");

        lblTotal.setText(String.valueOf(total));
        lblTiempo.setText("Tiempo disponible: " + tiempo + " min");
        lblCardio.setText("Cardiovascular: " + porTipo.get(TipoEjercicio.CARDIOVASCULAR));
        lblFuerza.setText("Fuerza: "          + porTipo.get(TipoEjercicio.FUERZA));
        lblBaja.setText("Baja: "   + porNivel.get(NivelIntensidad.BAJA));
        lblMedia.setText("Media: " + porNivel.get(NivelIntensidad.MEDIA));
        lblAlta.setText("Alta: "   + porNivel.get(NivelIntensidad.ALTA));
    }

    // ── Helpers de UI ────────────────────────────────────────────────────────

    private JPanel tarjeta() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 230), 1, true),
            new EmptyBorder(16, 14, 16, 14)));
        return p;
    }

    private JLabel etiquetaTitulo(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JLabel etiquetaGrande(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setForeground(new Color(40, 80, 160));
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JLabel etiquetaChica(String texto) {
        JLabel l = new JLabel(texto, SwingConstants.CENTER);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        l.setForeground(Color.GRAY);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private JButton boton(String texto, Color color) {
        JButton b = new JButton(texto);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 22, 9, 22));
        b.setOpaque(true);
        return b;
    }
}
