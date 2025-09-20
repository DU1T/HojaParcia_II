package app.dao;

import app.model.Cliente;
import app.db.Conexion;

import java.sql.PreparedStatement;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO
{
    //Crete
    public int insertar(Cliente cte) throws SQLException {
        String sql = "INSERT INTO Cliente (nombre, nit, telefono, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, cte.getNombre());
            ps.setString(2, cte.getNit());
            ps.setString(3, cte.getTelefono());
            ps.setInt(4, cte.getEstado());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    cte.setId(id);
                    return id;
                }
            }
        }
        return -1;
    }
    public boolean actualizar(Cliente cte) throws SQLException {
        String sql = "UPDATE Cliente SET nombre=?, nit=?, telefono=?, estado=? WHERE id=?";
        try (Connection c = Conexion.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cte.getNombre());
            ps.setString(2, cte.getNit());
            ps.setString(3, cte.getTelefono());
            ps.setInt(4, cte.getEstado());
            ps.setInt(5, cte.getId());
            return ps.executeUpdate() > 0;
        }
    }

    public List<Cliente> listar() throws SQLException {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM Cliente ORDER BY id DESC";
        List<Cliente> lista = new ArrayList<>();

        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapCliente(rs));
            }
        }
        return lista;
    }

    public boolean eliminarLogico(int id) throws SQLException {
        String sql = "UPDATE Cliente SET estado=0 WHERE id=?";
        try (Connection c = Conexion.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
    // --- Helper: mapea un ResultSet a Cliente
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        return new Cliente(
                rs.getInt("id"),
                rs.getString("nombre"),
                rs.getString("nit"),
                rs.getString("telefono"),
                rs.getInt("estado") // BIT en SQL Server ↔ boolean en Java
        );
    }
    /** Busca un cliente por NIT y nombre */
    public Cliente buscarPorNitYNombre(String nit, String nombre) {
        String sql = "SELECT id, nombre, nit, telefono, estado FROM Cliente WHERE nit=? AND nombre=? AND estado=1";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nit);
            ps.setString(2, nombre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Cliente(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("nit"),
                            rs.getString("telefono"),
                            rs.getInt("estado")
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // no existe
    }
}
