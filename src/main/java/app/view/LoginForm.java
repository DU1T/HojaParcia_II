package app.view;

import app.core.Sesion;
import app.dao.UsuarioDAO;
import app.model.Usuario;

import javax.swing.*;
import java.awt.*;

public class LoginForm {
    public JPanel panelPrincipal;
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private JButton btnEntrar;
    private JLabel lblStatus;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginForm() {
        panelPrincipal.setPreferredSize(new Dimension(360, 200));
        btnEntrar.addActionListener(e -> onEntrar());
    }

    private void onEntrar() {
        String user = txtUsuario.getText().trim();
        String pass = new String(txtPassword.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            if (lblStatus != null) lblStatus.setText("Ingrese usuario y contraseña");
            return;
        }
        try {
            Usuario u = usuarioDAO.validarLogin(user, pass);
            if (u == null) {
                if (lblStatus != null) lblStatus.setText("Credenciales inválidas");
                return;
            }
            Sesion.login(u);
            abrirMenu();
            SwingUtilities.getWindowAncestor(panelPrincipal).dispose(); // cierra el login
        } catch (Exception ex) {
            if (lblStatus != null) lblStatus.setText("Error de conexión");
            ex.printStackTrace();
        }
    }

    private void abrirMenu() {
        JFrame f = new JFrame("Menú Principal – Biblioteca");
        f.setContentPane(new MainMenuForm().panelPrincipal);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    // Launcher
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame f = new JFrame("Login Biblioteca UNIVERSALES 🌟");
            f.setContentPane(new LoginForm().panelPrincipal);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);
        });
    }
}