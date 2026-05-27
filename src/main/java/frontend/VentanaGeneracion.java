package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;

public class VentanaGeneracion extends JDialog implements Observador {

    private final Controlador ctrl = Controlador.getInstance();
    private final Map<TipoEjercicio, Map<NivelIntensidad, JSpinner>> spinners = new LinkedHashMap<>();

    private JButton btnGenerar;
    private JLabel lblMensaje;
    private JProgressBar barra;

    public VentanaGeneracion(Frame padre) {
        super(padre, "Generar Rutina de Entrenamiento", true);
        ctrl.suscribir(this);
        construir();
    }

    private void construir() {
        setSize(540, 420);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(18, 22, 14, 22));
        panel.setBackground(new Color(245, 247, 250));
        setContentPane(panel);

        JLabel titulo = new JLabel("Configurar Rutina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(new Color(40, 60, 100));
        panel.add(titulo, BorderLayout.NORTH);

        // Tarjetas por tipo
        JPanel contenido = new JPanel(new GridLayout(0, 1, 0, 10));
        contenido.setOpaque(false);
        for (TipoEjercicio tipo : TipoEjercicio.values())
            contenido.add(crearTarjeta(tipo));
        panel.add(new JScrollPane(contenido) {{ setBorder(null); setOpaque(false); getViewport().setOpaque(false); }}, BorderLayout.CENTER);

        // Inferior
        JPanel inferior = new JPanel(new BorderLayout(4, 4));
        inferior.setOpaque(false);

        barra = new JProgressBar();
        barra.setVisible(false);
        barra.setIndeterminate(false);

        lblMensaje = new JLabel(" ", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cerrar());
        btnGenerar = new JButton("Generar Rutina");
        btnGenerar.setBackground(new Color(46, 160, 80));
        btnGenerar.setForeground(Color.WHITE);
        btnGenerar.setOpaque(true);
        btnGenerar.setFocusPainted(false);
        btnGenerar.addActionListener(e -> generar());
        botones.add(btnCancelar);
        botones.add(btnGenerar);

        inferior.add(barra, BorderLayout.NORTH);
        inferior.add(lblMensaje, BorderLayout.CENTER);
        inferior.add(botones, BorderLayout.SOUTH);
        panel.add(inferior, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnGenerar);
    }

    private JPanel crearTarjeta(TipoEjercicio tipo) {
        JPanel card = new JPanel(new BorderLayout(8, 6));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 218, 230), 1, true),
            new EmptyBorder(10, 14, 10, 14)));

        JLabel lTipo = new JLabel(capitalizar(tipo.getNombre()));
        lTipo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTipo.setForeground(new Color(40, 70, 140));
        card.add(lTipo, BorderLayout.NORTH);

        JPanel filaSpinners = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 2));
        filaSpinners.setOpaque(false);

        Map<NivelIntensidad, JSpinner> mapaNivel = new LinkedHashMap<>();
        spinners.put(tipo, mapaNivel);

        Color[] colores = {new Color(60, 180, 100), new Color(250, 170, 40), new Color(220, 60, 60)};
        int i = 0;
        for (NivelIntensidad nivel : NivelIntensidad.values()) {
            JPanel bloque = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
            bloque.setOpaque(false);
            JLabel dot = new JLabel("●");
            dot.setForeground(colores[i++]);
            JLabel lNivel = new JLabel(capitalizar(nivel.getNombre()) + ":");
            JSpinner sp = new JSpinner(new SpinnerNumberModel(0, 0, 20, 1));
            sp.setPreferredSize(new Dimension(55, 26));
            mapaNivel.put(nivel, sp);
            bloque.add(dot); bloque.add(lNivel); bloque.add(sp);
            filaSpinners.add(bloque);
        }

        card.add(filaSpinners, BorderLayout.CENTER);
        return card;
    }

    private void generar() {
        Map<TipoEjercicio, Map<NivelIntensidad, Integer>> requisitos = new LinkedHashMap<>();
        int total = 0;

        for (Map.Entry<TipoEjercicio, Map<NivelIntensidad, JSpinner>> e : spinners.entrySet()) {
            Map<NivelIntensidad, Integer> porNivel = new LinkedHashMap<>();
            for (Map.Entry<NivelIntensidad, JSpinner> n : e.getValue().entrySet()) {
                int val = (int) n.getValue().getValue();
                porNivel.put(n.getKey(), val);
                total += val;
            }
            requisitos.put(e.getKey(), porNivel);
        }

        if (total == 0) { mensaje("Seleccione al menos un ejercicio.", false); return; }

        btnGenerar.setEnabled(false);
        barra.setVisible(true);
        barra.setIndeterminate(true);
        mensaje("Generando rutina...", null);
        ctrl.generarRutina(requisitos);
    }

    @Override
    public void alRecibir(Evento evento, Object datos) {
        barra.setIndeterminate(false);
        barra.setVisible(false);

        if (evento == Evento.RUTINA_GENERADA) {
            ctrl.desuscribir(this);
            dispose();
        } else if (evento == Evento.ERROR_GENERACION) {
            btnGenerar.setEnabled(true);
            mensaje((String) datos, false);
        }
    }

    private void mensaje(String texto, Boolean exito) {
        lblMensaje.setText(texto);
        if (exito == null)  lblMensaje.setForeground(new Color(80, 80, 200));
        else if (exito)     lblMensaje.setForeground(new Color(30, 130, 60));
        else                lblMensaje.setForeground(new Color(180, 40, 40));
    }

    private String capitalizar(String s) {
        return s == null || s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private void cerrar() { ctrl.desuscribir(this); dispose(); }
}
