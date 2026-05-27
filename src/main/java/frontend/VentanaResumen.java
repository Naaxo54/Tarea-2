package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.Map;

public class VentanaResumen extends JDialog {

    private final Rutina rutina;

    public VentanaResumen(Frame padre, Rutina rutina) {
        super(padre, "Resumen de la Rutina", true);
        this.rutina = rutina;
        construir();
    }

    private void construir() {
        setSize(440, 420);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 12));
        panel.setBorder(new EmptyBorder(20, 24, 16, 24));
        panel.setBackground(new Color(245, 247, 250));
        setContentPane(panel);

        JLabel titulo = new JLabel("Resumen de la Rutina");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titulo.setForeground(new Color(40, 60, 100));
        panel.add(titulo, BorderLayout.NORTH);

        // Tarjetas
        Map<TipoEjercicio, Integer>   porTipo  = rutina.porTipo();
        Map<NivelIntensidad, Integer> porNivel = rutina.porIntensidad();

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setOpaque(false);

        contenido.add(tarjeta("Resumen general", new Color(52, 100, 200), new String[]{
            "Total de ejercicios:    " + rutina.total(),
            "Tiempo total estimado:  " + rutina.tiempoTotal() + " minutos"
        }));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(tarjeta("Por tipo de ejercicio", new Color(46, 140, 80), new String[]{
            "Cardiovascular:  " + porTipo.get(TipoEjercicio.CARDIOVASCULAR) + " ejercicio(s)",
            "Fuerza:          " + porTipo.get(TipoEjercicio.FUERZA)          + " ejercicio(s)"
        }));
        contenido.add(Box.createVerticalStrut(10));
        contenido.add(tarjeta("Por nivel de intensidad", new Color(200, 100, 30), new String[]{
            "Baja:   " + porNivel.get(NivelIntensidad.BAJA)  + " ejercicio(s)",
            "Media:  " + porNivel.get(NivelIntensidad.MEDIA) + " ejercicio(s)",
            "Alta:   " + porNivel.get(NivelIntensidad.ALTA)  + " ejercicio(s)"
        }));

        panel.add(contenido, BorderLayout.CENTER);

        // Botón cerrar
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.setOpaque(false);
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.setBackground(new Color(100, 110, 130));
        btnCerrar.setForeground(Color.WHITE);
        btnCerrar.setOpaque(true);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBorder(new EmptyBorder(8, 22, 8, 22));
        btnCerrar.addActionListener(e -> dispose());
        botones.add(btnCerrar);
        panel.add(botones, BorderLayout.SOUTH);
    }

    private JPanel tarjeta(String titulo, Color color, String[] lineas) {
        JPanel card = new JPanel(new BorderLayout(6, 6));
        card.setBackground(Color.WHITE);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 200));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(210, 220, 240), 1, true),
            new EmptyBorder(10, 14, 10, 14)));

        JPanel barra = new JPanel();
        barra.setBackground(color);
        barra.setPreferredSize(new Dimension(5, 1));

        JPanel cuerpo = new JPanel();
        cuerpo.setLayout(new BoxLayout(cuerpo, BoxLayout.Y_AXIS));
        cuerpo.setOpaque(false);

        JLabel lTitulo = new JLabel(titulo);
        lTitulo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lTitulo.setForeground(color);
        cuerpo.add(lTitulo);
        cuerpo.add(Box.createVerticalStrut(6));

        for (String linea : lineas) {
            JLabel l = new JLabel(linea);
            l.setFont(new Font("Monospaced", Font.PLAIN, 13));
            l.setForeground(new Color(50, 60, 80));
            cuerpo.add(l);
            cuerpo.add(Box.createVerticalStrut(2));
        }

        card.add(barra, BorderLayout.WEST);
        card.add(cuerpo, BorderLayout.CENTER);
        return card;
    }
}
