package app.view;

import app.dao.CategoriaDAO;
import app.model.Categoria;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CategoriaForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JComboBox<String> cboEstado;
    private JButton btnCargar;
    private JTable tblCategorias;
    private JButton btnActualizar;
    private JButton btnGuardar;


    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Estado"}, 0
    );

    private Integer selectedId = null;

    public CategoriaForm() {
        // Ajustes de la ventana principal
        panelPrincipal.setPreferredSize(new Dimension(600, 400));
        tblCategorias.setModel(model);
        tblCategorias.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Estado: 1=Activo, 0=Inactivo
        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());

        // Al seleccionar fila, cargar en campos
        tblCategorias.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblCategorias.getSelectedRow();
            if (row == -1) { selectedId = null; return; }

            selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
            txtNombre.setText(model.getValueAt(row, 1).toString());

            String estTxt = model.getValueAt(row, 2).toString();
            cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
        });

        // Cargar tabla inicial
        cargarTabla();
    }

    /** Guarda una nueva categoría */
    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        int estado = cboEstado.getSelectedIndex() == 0 ? 1 : 0;

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "El campo Nombre es obligatorio",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        try {
            Categoria c = new Categoria(nombre, estado);
            categoriaDAO.insertar(c);
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Categoría guardada con éxito");
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(panelPrincipal,
                    "Error al guardar: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Actualiza una categoría existente */
    private void onActualizar() {
        if (selectedId == null) return;

        String nombre = txtNombre.getText().trim();
        int estado = cboEstado.getSelectedIndex() == 0 ? 1 : 0;

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(panelPrincipal,
                    "El campo Nombre es obligatorio",
                    "Validación", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(panelPrincipal,
                "¿Desea actualizar esta categoría?",
                "Confirmación", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            Categoria c = new Categoria(selectedId, nombre, estado);
            if (categoriaDAO.actualizar(c)) {
                JOptionPane.showMessageDialog(panelPrincipal,
                        "Categoría actualizada");
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

    /** Carga la tabla con categorías (puedes filtrar solo activas si lo deseas) */
    private void cargarTabla() {
        try {
            List<Categoria> lista = categoriaDAO.listar();
            model.setRowCount(0);
            for (Categoria c : lista) {
                model.addRow(new Object[]{
                        c.getId(),
                        c.getNombre(),
                        c.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        cboEstado.setSelectedIndex(0);
        tblCategorias.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            Object val = model.getValueAt(i, 0);
            if (val != null && Integer.parseInt(val.toString()) == id) {
                tblCategorias.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Categorías");
            f.setContentPane(new CategoriaForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
