package app.dao;

import app.model.Prestamo;
import app.db.Conexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO
{
    // INSERT: crea un autor y devuelve el id generado
    public int insertar(Prestamo p) throws SQLException {
        String sql = "INSERT INTO Prestamo (idCliente, idLibro, fecha, estado) VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFecha());
            ps.setInt(4, p.getEstado());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);
                    return id;
                }
            }
        }
        return -1; // no se obtuvo id
    }


    public boolean devolver(int idPrestamo) throws SQLException {
        String sql = "UPDATE Prestamo SET estado=0 WHERE id=?";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPrestamo);
            return ps.executeUpdate() > 0;
        }
    }
    // UPDATE: devuelve true si actualizó al menos 1 fila
    public boolean actualizar(Prestamo p) throws SQLException {
        String sql = "UPDATE Prestamo SET idCliente = ?, idLibro = ?, fecha = ? WHERE id = ?";
        try (Connection con = Conexion.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getIdCliente());
            ps.setInt(2, p.getIdLibro());
            ps.setDate(3, p.getFecha());
            ps.setInt(4, p.getEstado());;
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean estaPrestado(int idLibro) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Prestamo WHERE idLibro=? AND estado=1";
        try (Connection con = Conexion.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()) return rs.getInt(1) > 0;
            }
        }
        return false;
    }

    public List<Prestamo> listarActivosPorCliente(int idCliente) throws SQLException {
        List<Prestamo> list = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE idCliente=? AND estado=1";
        try (Connection c = Conexion.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Prestamo(rs.getInt("id"), rs.getInt("idCliente"), rs.getInt("idLibro"), rs.getDate("fecha"), rs.getInt("estado")));
                }
            }
        }
        return list;
    }
    public List<Prestamo> listarActivosPorLibro(int idLibro) throws SQLException {
        List<Prestamo> list = new ArrayList<>();
        String sql = "SELECT * FROM Prestamo WHERE idLibro=? AND estado=1";
        try (Connection c = Conexion.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Prestamo(rs.getInt("id"), rs.getInt("idCliente"), rs.getInt("idLibro"), rs.getDate("fecha"), rs.getInt("estado")));
                }
            }
        }
        return list;
    }

}
