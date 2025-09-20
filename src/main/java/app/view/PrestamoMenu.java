package app.view;

import javax.swing.*;
import java.awt.*;

public class PrestamoMenu {
    private JButton btnPrestamo;
    private JButton btnDevolver;
    public JPanel panelPrincipal;

    public PrestamoMenu() {

        panelPrincipal.setPreferredSize(new Dimension(320, 260));

        // Acciones de botones
        btnPrestamo.addActionListener(e -> abrirPrestamoForm());
        btnDevolver.addActionListener(e -> abrirDevolucionForm());
    }

    private void abrirPrestamoForm() {
        JFrame f = new JFrame("Gestión de Préstamos");
        f.setContentPane(new PrestamoForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void abrirDevolucionForm() {
        JFrame f = new JFrame("Devolución de Préstamos");
        f.setContentPane(new DevolucionForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Main para probar el menú
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Menú de Préstamos");
            f.setContentPane(new PrestamoMenu().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

}
