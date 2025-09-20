// src/main/java/app/view/LibroForm.java
package app.view;

import app.dao.AutorDAO;
import app.dao.LibroDAO;
import app.model.Autor;
import app.model.ComboItem;
import app.model.Libro;
import app.model.LibroConAutor;
import app.model.Categoria;
import app.dao.CategoriaDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibroForm {
    public JPanel panelPrincipal;
    private JTextField txtNombre;
    private JTextField txtAnio;
    private JComboBox<ComboItem> cboAutor;
    private JComboBox<String> cboEstado;
    private JButton btnGuardar;
    private JButton btnActualizar;
    private JButton btnCargar;
    private JTable tblLibros;
    private JLabel Categoria;
    private JComboBox<ComboItem> cboCategoria;

    private final AutorDAO autorDAO = new AutorDAO();
    private final LibroDAO libroDAO = new LibroDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();   // <--- DAO de Categorías

    private final DefaultTableModel model = new DefaultTableModel(
            new Object[]{"ID", "Nombre", "Año", "Autor", "Categoría", "Estado"}, 0
    );

    private Integer selectedId = null;

    public LibroForm() {
        panelPrincipal.setPreferredSize(new Dimension(900, 600));
        tblLibros.setModel(model);
        tblLibros.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        cboEstado.addItem("1 - Activo");
        cboEstado.addItem("0 - Inactivo");

        cargarAutoresEnCombo();
        cargarCategoriasEnCombo();   // <--- carga de categorías

        btnGuardar.addActionListener(e -> onGuardar());
        btnActualizar.addActionListener(e -> onActualizar());
        btnCargar.addActionListener(e -> cargarTabla());

        tblLibros.getSelectionModel().addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int row = tblLibros.getSelectedRow();
            if (row == -1) { selectedId = null; return; }

            selectedId = Integer.parseInt(model.getValueAt(row, 0).toString());
            txtNombre.setText(model.getValueAt(row, 1).toString());
            txtAnio.setText(model.getValueAt(row, 2).toString());

            seleccionarAutorPorNombre(model.getValueAt(row, 3).toString());
            seleccionarCategoriaPorNombre(model.getValueAt(row, 4).toString());

            String estTxt = model.getValueAt(row, 5).toString();
            cboEstado.setSelectedIndex("Activo".equalsIgnoreCase(estTxt) ? 0 : 1);
        });
    }

    /** Carga autores en combo */
    private void cargarAutoresEnCombo() {
        try {
            cboAutor.removeAllItems();
            for (Autor a : autorDAO.listar()) {
                cboAutor.addItem(new ComboItem(a.getId(), a.getNombre()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /** Carga categorías en combo */
    private void cargarCategoriasEnCombo() {
        try {
            cboCategoria.removeAllItems();
            for (Categoria c : categoriaDAO.listar()) {
                cboCategoria.addItem(new ComboItem(c.getId(), c.getNombre()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void seleccionarAutorPorNombre(String nombre) {
        for (int i = 0; i < cboAutor.getItemCount(); i++) {
            ComboItem item = cboAutor.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboAutor.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarCategoriaPorNombre(String nombre) {
        for (int i = 0; i < cboCategoria.getItemCount(); i++) {
            ComboItem item = cboCategoria.getItemAt(i);
            if (item.getLabel().equalsIgnoreCase(nombre)) {
                cboCategoria.setSelectedIndex(i);
                return;
            }
        }
    }

    /** Guardar libro */
    private void onGuardar() {
        String nombre = txtNombre.getText().trim();
        String anioStr = txtAnio.getText().trim();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();
        ComboItem catItem = (ComboItem) cboCategoria.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        if (nombre.isEmpty() || anioStr.isEmpty() || autorItem == null || catItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Complete todos los campos obligatorios");
            return;
        }
        int anio;
        try { anio = Integer.parseInt(anioStr); }
        catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(panelPrincipal,"Año inválido"); return; }

        try {
            Libro l = new Libro(nombre, anio, autorItem.getId(), catItem.getId(), estado);
            libroDAO.insertar(l);
            limpiarFormulario();
            cargarTabla();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /** Actualizar libro */
    private void onActualizar() {
        if (selectedId == null) return;

        String nombre = txtNombre.getText().trim();
        String anioStr = txtAnio.getText().trim();
        ComboItem autorItem = (ComboItem) cboAutor.getSelectedItem();
        ComboItem catItem = (ComboItem) cboCategoria.getSelectedItem();
        int estado = (cboEstado.getSelectedIndex() == 0) ? 1 : 0;

        if (nombre.isEmpty() || anioStr.isEmpty() || autorItem == null || catItem == null) {
            JOptionPane.showMessageDialog(panelPrincipal, "Complete todos los campos obligatorios");
            return;
        }
        int anio;
        try { anio = Integer.parseInt(anioStr); }
        catch (NumberFormatException nfe) { JOptionPane.showMessageDialog(panelPrincipal,"Año inválido"); return; }

        try {
            Libro l = new Libro(selectedId, nombre, anio, autorItem.getId(), catItem.getId(), estado);
            if (libroDAO.actualizar(l)) {
                cargarTabla();
                seleccionarFilaPorId(selectedId);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /** Carga tabla con autor y categoría */
    private void cargarTabla() {
        try {
            List<LibroConAutor> lista = libroDAO.listarConAutor();
            model.setRowCount(0);
            for (LibroConAutor l : lista) {
                model.addRow(new Object[]{
                        l.getId(),
                        l.getNombre(),
                        l.getAnio(),
                        l.getAutorNombre(),
                        l.getCategoriaNombre(),   // <--- nueva columna
                        l.getEstado() == 1 ? "Activo" : "Inactivo"
                });
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtAnio.setText("");
        if (cboAutor.getItemCount() > 0) cboAutor.setSelectedIndex(0);
        if (cboCategoria.getItemCount() > 0) cboCategoria.setSelectedIndex(0);
        cboEstado.setSelectedIndex(0);
        tblLibros.clearSelection();
        selectedId = null;
    }

    private void seleccionarFilaPorId(Integer id) {
        if (id == null) return;
        for (int i = 0; i < model.getRowCount(); i++) {
            if (Integer.parseInt(model.getValueAt(i, 0).toString()) == id) {
                tblLibros.setRowSelectionInterval(i, i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Gestión de Libros");
            f.setContentPane(new LibroForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}
