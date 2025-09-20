package app.view;

import app.dao.LibroDAO;
import app.dao.PrestamoDAO;
import app.dao.ClienteDAO;
import app.model.Cliente;
import app.model.ComboItem;
import app.model.Libro;
import app.model.Prestamo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PrestamoForm
{

    public JPanel panelPrincipal;
    private JTextField txtTituloLibro;
    private JButton btnVerificarDisp;
    private JButton btnConfirmar;
    private JTable tblLibrosPrestamos;
    private JTextField txtCliente;
    private JTextField txtNit;
    private JButton btnValidarCliente;
    private JComboBox cboAutor;
    private JComboBox cboLibro;

    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final DefaultTableModel modelLibros = new DefaultTableModel(
            new Object[]{"ID", "Título", "Autor", "Estado"}, 0
    );

    private Integer selectedLibroId = null;
    private Integer clienteId = null;

    public PrestamoForm() {
        panelPrincipal.setPreferredSize(new Dimension(900, 500));

        tblLibrosPrestamos.setModel(modelLibros);
        tblLibrosPrestamos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Inicialmente campos cliente y confirmar deshabilitados
        txtCliente.setEnabled(false);
        txtNit.setEnabled(false);
        btnValidarCliente.setEnabled(false);
        btnConfirmar.setEnabled(false);

        cargarAutoresEnCombo();
        cargarLibrosEnCombo();

        btnVerificarDisp.addActionListener(e -> verificarDisponibilidad());
        btnValidarCliente.addActionListener(e -> validarCliente());
        btnConfirmar.addActionListener(e -> confirmarPrestamo());

        tblLibrosPrestamos.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblLibrosPrestamos.getSelectedRow();
            if (row == -1) { selectedLibroId = null; return; }

            selectedLibroId = Integer.parseInt(modelLibros.getValueAt(row, 0).toString());
        });
    }

    /** Carga autores en combo */
    private void cargarAutoresEnCombo() {
        try {
            cboAutor.removeAllItems();
            List<ComboItem> autores = libroDAO.listarAutores();
            for (ComboItem a : autores) {
                cboAutor.addItem(a);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    /** Carga autores en combo */
    private void cargarLibrosEnCombo() {
        try {
            cboLibro.removeAllItems();
            List<ComboItem> libros = libroDAO.listarLibros();
            for (ComboItem a : libros) {
                cboLibro.addItem(a);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /** Verifica disponibilidad del libro */
    private void verificarDisponibilidad() {
        ComboItem libroItem = (ComboItem) cboLibro.getSelectedItem();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();

        if (libroItem == null || autorItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione título y seleccione autor");
            return;
        }

        try {
            List<Libro> libros = libroDAO.buscarPorTituloYAutor(libroItem.getLabel(), autorItem.getId());

            modelLibros.setRowCount(0);
            boolean libroDisponible = true;

            for (Libro l : libros) {
                boolean prestado = prestamoDAO.estaPrestado(l.getId());
                modelLibros.addRow(new Object[]{
                        l.getId(),
                        l.getNombre(),
                        autorItem.getLabel(),
                        prestado ? "Prestado" : "Disponible"
                });

                if (prestado) libroDisponible = false;
            }

            if (!libroDisponible) {
                JOptionPane.showMessageDialog(panelPrincipal, "El libro está en préstamo. Revise la tabla");
            } else if (libros.size() > 0) {
                JOptionPane.showMessageDialog(panelPrincipal, "Libro disponible para préstamo");
                txtCliente.setEnabled(true);
                txtNit.setEnabled(true);
                btnValidarCliente.setEnabled(true);
            } else {
                JOptionPane.showMessageDialog(panelPrincipal, "No se encontraron libros con ese título y autor");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Valida que el cliente exista */
    private void validarCliente() {
        String nombre = txtCliente.getText().trim();
        String nit = txtNit.getText().trim();

        if (nombre.isEmpty() || nit.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal, "Ingrese Nombre y NIT del cliente");
            return;
        }

        try {
            Cliente c = clienteDAO.buscarPorNitYNombre(nit, nombre);
            if (c != null) {
                clienteId = c.getId();
                JOptionPane.showMessageDialog(panelPrincipal, "Cliente válido. Puede confirmar préstamo");
                btnConfirmar.setEnabled(true);
            } else {
                int resp = JOptionPane.showConfirmDialog(panelPrincipal,
                        "Cliente no existe. ¿Desea registrar uno nuevo?",
                        "Cliente no encontrado", JOptionPane.YES_NO_OPTION);
                if (resp == JOptionPane.YES_OPTION) {
                    ClienteForm cf = new ClienteForm();
                    JFrame f = new JFrame("Registrar Cliente");
                    f.setContentPane(cf.panelPrincipal);
                    f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    f.pack();
                    f.setLocationRelativeTo(null);
                    f.setVisible(true);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Confirma el préstamo */
    private void confirmarPrestamo() {
        if (selectedLibroId == null || clienteId == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Seleccione libro y valide cliente");
            return;
        }

        try {
            Prestamo p = new Prestamo(clienteId, selectedLibroId, new java.sql.Date(System.currentTimeMillis()), 1);
            prestamoDAO.insertar(p);
            JOptionPane.showMessageDialog(panelPrincipal, "Préstamo confirmado");
            limpiarFormulario();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtTituloLibro.setText("");
        cboAutor.setSelectedIndex(0);
        cboLibro.setSelectedIndex(0);
        modelLibros.setRowCount(0);
        txtCliente.setText("");
        txtNit.setText("");
        txtCliente.setEnabled(false);
        txtNit.setEnabled(false);
        btnValidarCliente.setEnabled(false);
        btnConfirmar.setEnabled(false);
        selectedLibroId = null;
        clienteId = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Préstamos");
            f.setContentPane(new PrestamoForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

}
