package app.view;
import app.dao.ClienteDAO;
import app.model.Cliente;

import javax.swing.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.regex.Pattern;

public class ClienteForm {
    private JTextField txtNombre;
    private JComboBox<String>  cboEstado;
    private JButton btnCargar;
    private JTable tblClientes;
    private JButton btnActualizar;
    private JButton btnGuardar;
    private JTextField txtNit;
    private JTextField txtTelefono;
    public JPanel panelPrincipal;

    private final ClienteDAO clienteDAO = new ClienteDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "NIT", "Teléfono", "Estado"}, 0
    );

    private Integer selectedId = null;

    public ClienteForm() {
        panelPrincipal.setPreferredSize(new Dimension(700, 450));
        tblClientes.setModel(model);
        tblClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Estado 1 = Activo, 0 = Inactivo
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());

        tblClientes.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblClientes.getSelectedRow();
            if (row == -1) { selectedId = null; return; }

            selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
            txtNombre.setText(model.getValueAt(row, 1).toString());
            txtNit.setText(model.getValueAt(row, 2) != null ? model.getValueAt(row, 2).toString() : "");
            txtTelefono.setText(model.getValueAt(row, 3) != null ? model.getValueAt(row, 3).toString() : "");
            String estTxt = model.getValueAt(row, 4).toString();
            cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
        });

        cargarTabla();
    }

    /** Guarda un nuevo cliente */
    private void onGuardar() {
        if (!validarCampos()) return;

        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();
        int estado = cboEstado.getSelectedIndex() == 0 ? 1 : 0;

        try {
            Cliente c = new Cliente(nombre, nit, telefono, estado);
            clienteDAO.insertar(c);
            JOptionPane.showMessageDialog(panelPrincipal, "Cliente guardado correctamente");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Actualiza cliente existente */
    private void onActualizar() {
        if (selectedId == null) return;
        if (!validarCampos()) return;

        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();
        int estado = cboEstado.getSelectedIndex() == 0 ? 1 : 0;

        int confirm = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Desea actualizar este cliente?",
                "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Cliente c = new Cliente(selectedId, nombre, nit, telefono, estado);
            if (clienteDAO.actualizar(c)) {
                JOptionPane.showMessageDialog(panelPrincipal, "Cliente actualizado");
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al actualizar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Validaciones de nombre obligatorio y NIT/teléfono numéricos si no están vacíos */
    private boolean validarCampos() {
        String nombre = txtNombre.getText().trim();
        String nit = txtNit.getText().trim();
        String telefono = txtTelefono.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "El campo Nombre es obligatorio",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        //Verificacion nit alfanumerico sin espacios
        Pattern soloDigitos = Pattern.compile("^\\d+$");
        Pattern nitPattern = Pattern.compile("^[A-Za-z0-9\\-]+$");
        if (!nit.isEmpty() && !nitPattern.matcher(nit).matches()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "El NIT no puede contener espacios en blanco",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            txtNit.requestFocus();
            return false;
        }
        if (!telefono.isEmpty() && !soloDigitos.matcher(telefono).matches()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "El Teléfono debe ser numérico",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            txtTelefono.requestFocus();
            return false;
        }
        return true;
    }

    private void cargarTabla() {
        try {
            List<Cliente> lista = clienteDAO.listar();
            model.setRowCount(0);
            for (Cliente c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getNit(),
                        c.getTelefono(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtNit.setText("");
        txtTelefono.setText("");
        cboEstado.setSelectedIndex(0);
        tblClientes.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblClientes.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    /** Para pruebas independientes */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Clientes");
            f.setContentPane(new ClienteForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }

}
