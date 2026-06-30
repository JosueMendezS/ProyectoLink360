package link360.dao;

import link360.model.Cliente;
import link360.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    // ── SQL statements
    private static final String SQL_INSERT
            = "INSERT INTO Cliente (Cedula, Nombre, Apellidos, FechaIngreso, TipoCliente, Direccion, CodDistrito) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_SELECT_ALL
            = "SELECT Cedula, Nombre, Apellidos, FechaIngreso, TipoCliente, Direccion, CodDistrito "
            + "FROM Cliente ORDER BY Apellidos, Nombre";

    private static final String SQL_SELECT_BY_ID
            = "SELECT Cedula, Nombre, Apellidos, FechaIngreso, TipoCliente, Direccion, CodDistrito "
            + "FROM Cliente WHERE Cedula = ?";

    private static final String SQL_UPDATE
            = "UPDATE Cliente SET Nombre = ?, Apellidos = ?, FechaIngreso = ?, "
            + "TipoCliente = ?, Direccion = ?, CodDistrito = ? WHERE Cedula = ?";

    private static final String SQL_DELETE
            = "DELETE FROM Cliente WHERE Cedula = ?";

    // ── INSERT 
    public void insert(Cliente client) throws SQLException, ClassNotFoundException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
            ps.setString(1, client.getCedula());
            ps.setString(2, client.getNombre());
            ps.setString(3, client.getApellidos());
            ps.setDate(4, Date.valueOf(client.getFechaIngreso()));
            ps.setString(5, client.getTipoCliente());
            if (client.getDireccion() == null || client.getDireccion().trim().isEmpty()) {
                ps.setNull(6, Types.VARCHAR);
            } else {
                ps.setString(6, client.getDireccion());
            }
            ps.setInt(7, client.getCodDistrito());
            ps.executeUpdate();
        }
    }

    // ── SELECT ALL 
    public List<Cliente> findAll() throws SQLException, ClassNotFoundException {
        List<Cliente> list = new ArrayList<>();
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ── SELECT BY PK
    public Cliente findById(String cedula) throws SQLException, ClassNotFoundException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
            ps.setString(1, cedula);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    // ── UPDATE 
    public void update(Cliente client) throws SQLException, ClassNotFoundException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {
            ps.setString(1, client.getNombre());
            ps.setString(2, client.getApellidos());
            ps.setDate(3, Date.valueOf(client.getFechaIngreso()));
            ps.setString(4, client.getTipoCliente());
            if (client.getDireccion() == null || client.getDireccion().trim().isEmpty()) {
                ps.setNull(5, Types.VARCHAR);
            } else {
                ps.setString(5, client.getDireccion());
            }
            ps.setInt(6, client.getCodDistrito());
            ps.setString(7, client.getCedula());
            ps.executeUpdate();
        }
    }

    // ── DELETE 
    public void delete(String cedula) throws SQLException, ClassNotFoundException {
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {
            ps.setString(1, cedula);
            ps.executeUpdate();
        }
    }

    private Cliente mapRow(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setCedula(rs.getString("Cedula"));
        c.setNombre(rs.getString("Nombre"));
        c.setApellidos(rs.getString("Apellidos"));
        Date fi = rs.getDate("FechaIngreso");
        c.setFechaIngreso(fi != null ? fi.toString() : "");
        c.setTipoCliente(rs.getString("TipoCliente"));
        c.setDireccion(rs.getString("Direccion"));
        c.setCodDistrito(rs.getInt("CodDistrito"));
        return c;
    }
}
