package app.view;

import javax.swing.*;
import app.dao.PrestamoDAO;
import app.model.Prestamo;
import app.dao.ClienteDAO;
import app.dao.LibroDAO;
import app.model.Cliente;
import app.model.Libro;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DevolucionForm {
    public JPanel panelPrincipal;
    private JTextField txtCliente;
    private JButton btnDevolver;
    private JTable tblPrestamos;
    private JTextField txtLibro;
    private JTextField txtEstado;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final LibroDAO libroDAO = new LibroDAO();

    // ✅ Ahora incluye la columna Fecha
    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Cliente", "Libro", "Fecha", "Estado"}, 0
    );

    private Integer selectedPrestamoId = null;

    public DevolucionForm() {
        panelPrincipal.setPreferredSize(new Dimension(750, 420));
        tblPrestamos.setModel(model);
        tblPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        btnDevolver.addActionListener(e -> onDevolver());

        tblPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblPrestamos.getSelectedRow();
            if (row == -1) {
                limpiarCampos();
                return;
            }

            selectedPrestamoId = Integer.parseInt(model.getValueAt(row, 0).toString());
            txtCliente.setText(model.getValueAt(row, 1).toString());
            txtLibro.setText(model.getValueAt(row, 2).toString());
            // el Estado ahora es la columna 4 (índice 4)
            txtEstado.setText(model.getValueAt(row, 4).toString());
        });

        cargarTabla();
    }

    /** Carga la tabla con préstamos activos */
    private void cargarTabla() {
        try {
            model.setRowCount(0);
            String sql = "SELECT p.id, c.nombre AS clienteNombre, l.nombre AS libroNombre, " +
                    "p.fecha, p.estado " +
                    "FROM Prestamo p " +
                    "JOIN Cliente c ON c.id = p.idCliente " +
                    "JOIN Libro l ON l.id = p.idLibro " +
                    "WHERE p.estado = 1 " +
                    "ORDER BY p.id DESC";

            try (var con = app.db.Conexion.getConnection();
                 var ps = con.prepareStatement(sql);
                 var rs = ps.executeQuery()) {

                while (rs.next()) {
                    model.addRow(new Object[]{
                            rs.getInt("id"),
                            rs.getString("clienteNombre"),
                            rs.getString("libroNombre"),
                            rs.getDate("fecha"),                      // ✅ fecha
                            rs.getInt("estado") == 1 ? "Activo" : "Inactivo"
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al cargar préstamos: " + ex.getMessage());
        }
    }

    /** Marca un préstamo como devuelto (estado=0) */
    private void onDevolver() {
        if (selectedPrestamoId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione un préstamo para devolver");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Desea devolver el libro seleccionado?",
                "Confirmación", JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            if (prestamoDAO.devolver(selectedPrestamoId)) {
                JOptionPane.showMessageDialog(panelPrincipal, "Devolución exitosa");
                cargarTabla();
                limpiarCampos();
            } else {
                JOptionPane.showMessageDialog(panelPrincipal, "No se pudo devolver el préstamo");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal, "Error al devolver: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        txtCliente.setText("");
        txtLibro.setText("");
        txtEstado.setText("");
        selectedPrestamoId = null;
        tblPrestamos.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Devolución de Préstamos");
            f.setContentPane(new DevolucionForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

}
