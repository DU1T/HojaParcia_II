package app.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;

public class Conexion
{

    private static final String URL  = "jdbc:sqlserver://localhost:1433;databaseName=BibliotecaDB;encrypt=false";
    private static final String USER = "sa";           // tu usuario
    private static final String PASS = "Dev2025!";  // tu contraseña
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Método de prueba
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("✅ Conexión exitosa a SQL Server");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
