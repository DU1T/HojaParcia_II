package app.view;

import app.core.Sesion;

import javax.swing.*;
import java.awt.*;

public class MainMenuForm {
    public JPanel panelPrincipal;
    private JLabel lblUsuario;
    private JButton btnAutores;
    private JButton btnLibros;
    private JButton btnRegistrarUsuario; // abre RegistroUsuario
    private JButton btnSalir;
    private JButton btnCategorias;
    private JButton btnClientes;
    private JButton btnPrestamo;

    public MainMenuForm() {
        panelPrincipal.setPreferredSize(new Dimension(420, 260));

        if (Sesion.isLogged() && lblUsuario != null) {
            lblUsuario.setText("Usuario: " + Sesion.getUsuario().getNombre()
                    + " (" + Sesion.getUsuario().getRol() + ")");
        }
        //Evaluamos si es admin para acceso a botones
        boolean esAdmin = Sesion.hasRole("ADMIN");
        if (btnRegistrarUsuario != null) {
            btnRegistrarUsuario.setEnabled(esAdmin);
            btnRegistrarUsuario.setVisible(esAdmin);
            // si prefieres ocultarlo para OPERADOR:
            // btnRegistrarUsuario.setVisible(esAdmin);
        }
        if(btnCategorias != null)
        {
            btnCategorias.setEnabled(esAdmin);
            btnCategorias.setVisible(esAdmin);
        }


        if (btnAutores != null)  btnAutores.addActionListener(e -> abrirAutores());
        if (btnLibros  != null)  btnLibros.addActionListener(e -> abrirLibros());
        if (btnRegistrarUsuario != null) btnRegistrarUsuario.addActionListener(e -> abrirRegistroUsuario());
        if (btnCategorias != null) btnCategorias.addActionListener(e -> abrirCategorias());
        if (btnClientes != null) btnClientes.addActionListener(e -> abrirClientes());
        if (btnPrestamo != null) btnPrestamo.addActionListener(e-> abrirMenuPrestamo());
        if (btnSalir   != null)  btnSalir.addActionListener(e -> {
            Sesion.logout();
            System.exit(0);
        });
    }

    private void abrirAutores() {
        JFrame f = new JFrame("Gestión de Autores");
        f.setContentPane(new AutorForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }

    private void abrirLibros() {
        JFrame f = new JFrame("Gestión de Libros");
        f.setContentPane(new LibroForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }
    private void abrirClientes() {
        JFrame f = new JFrame("Gestión de Clientes");
        f.setContentPane(new ClienteForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }
    private void abrirMenuPrestamo() {
        JFrame f = new JFrame("Menu Prestamo");
        f.setContentPane(new PrestamoMenu().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }

    private void abrirRegistroUsuario() {
        if (!Sesion.hasRole("ADMIN")) return;
        JFrame f = new JFrame("Registro de Usuario");
        f.setContentPane(new RegistroUsuarioForm().panelPrincipal); // <- NOMBRE CORRECTO
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }
    private void abrirCategorias() {
        if (!Sesion.hasRole("ADMIN")) return;
        JFrame f = new JFrame("Categorias");
        f.setContentPane(new CategoriaForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
    }

    // launcher opcional
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Menú Principal – Librería");
            f.setContentPane(new MainMenuForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack(); f.setLocationRelativeTo(null); f.setVisible(true);
        });
    }
}