package frontend;

import backend.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;

public class VentanaCarga extends JDialog implements Observador {

    private final Controlador ctrl = Controlador.getInstance();

    private JComboBox<String> cmbFuente;
    private JTextField txtRuta;
    private JButton btnCargar;
    private JLabel lblMensaje;
    private JProgressBar barra;

    public VentanaCarga(Frame padre) {
        super(padre, "Cargar Ejercicios", true);
        ctrl.suscribir(this);
        construir();
    }

    private void construir() {
        setSize(520, 300);
        setLocationRelativeTo(getOwner());
        setResizable(false);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(18, 22, 16, 22));
        panel.setBackground(new Color(245, 247, 250));
        setContentPane(panel);

        JLabel titulo = new JLabel("Carga de Ejercicios");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titulo.setForeground(new Color(40, 60, 100));
        panel.add(titulo, BorderLayout.NORTH);

        // Formulario central
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 4, 6, 4);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; g.weightx = 0;
        form.add(new JLabel("Fuente:"), g);
        g.gridx = 1; g.weightx = 1;
        cmbFuente = new JComboBox<>(new String[]{"Archivo CSV", "Base de datos SQLite"});
        form.add(cmbFuente, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0;
        form.add(new JLabel("Ruta:"), g);
        g.gridx = 1; g.weightx = 1;
        JPanel filaPuta = new JPanel(new BorderLayout(6, 0));
        filaPuta.setOpaque(false);
        txtRuta = new JTextField();
        JButton btnExaminar = new JButton("Examinar...");
        btnExaminar.addActionListener(e -> examinar());
        filaPuta.add(txtRuta, BorderLayout.CENTER);
        filaPuta.add(btnExaminar, BorderLayout.EAST);
        form.add(filaPuta, g);

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2;
        barra = new JProgressBar();
        barra.setIndeterminate(false);
        barra.setVisible(false);
        form.add(barra, g);

        g.gridy = 3;
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        form.add(lblMensaje, g);

        panel.add(form, BorderLayout.CENTER);

        // Botones
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botones.setOpaque(false);
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(e -> cerrar());
        btnCargar = new JButton("Cargar");
        btnCargar.setBackground(new Color(52, 120, 220));
        btnCargar.setForeground(Color.WHITE);
        btnCargar.setOpaque(true);
        btnCargar.setFocusPainted(false);
        btnCargar.addActionListener(e -> cargar());
        botones.add(btnCancelar);
        botones.add(btnCargar);
        panel.add(botones, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnCargar);
    }

    private void examinar() {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        boolean esCSV = cmbFuente.getSelectedIndex() == 0;
        chooser.setFileFilter(esCSV
            ? new FileNameExtensionFilter("Archivos CSV (*.csv)", "csv")
            : new FileNameExtensionFilter("Base de datos SQLite (*.db, *.sqlite)", "db", "sqlite"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            txtRuta.setText(chooser.getSelectedFile().getAbsolutePath());
    }

    private void cargar() {
        String ruta = txtRuta.getText().trim();
        if (ruta.isEmpty()) { mensaje("Ingrese la ruta del archivo.", false); return; }

        habilitarControles(false);
        barra.setVisible(true);
        barra.setIndeterminate(true);
        mensaje("Cargando...", null);

        if (cmbFuente.getSelectedIndex() == 0) ctrl.cargarDesdeArchivo(ruta);
        else                                    ctrl.cargarDesdeBaseDeDatos(ruta);
    }

    @Override
    public void alRecibir(Evento evento, Object datos) {
        barra.setIndeterminate(false);
        barra.setVisible(false);

        if (evento == Evento.EJERCICIOS_CARGADOS) {
            mensaje("Ejercicios cargados correctamente.", true);
            ctrl.desuscribir(this);
            Timer t = new Timer(700, e -> dispose());
            t.setRepeats(false);
            t.start();
        } else if (evento == Evento.ERROR_CARGA) {
            habilitarControles(true);
            mensaje((String) datos, false);
        }
    }

    private void habilitarControles(boolean estado) {
        btnCargar.setEnabled(estado);
        txtRuta.setEnabled(estado);
        cmbFuente.setEnabled(estado);
    }

    private void mensaje(String texto, Boolean exito) {
        lblMensaje.setText(texto);
        if (exito == null)       lblMensaje.setForeground(new Color(80, 80, 200));
        else if (exito)          lblMensaje.setForeground(new Color(30, 130, 60));
        else                     lblMensaje.setForeground(new Color(180, 40, 40));
    }

    private void cerrar() {
        ctrl.desuscribir(this);
        dispose();
    }
}
