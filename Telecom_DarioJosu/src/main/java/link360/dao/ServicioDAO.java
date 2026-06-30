package link360.dao;

import link360.model.Servicio;

import link360.util.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

public class ServicioDAO {

    private static final String SQL_INSERT
            = "INSERT INTO Servicio (CodServicio, Nombre, Descripcion, CostoMensual, Categoria) VALUES (?,?,?,?,?)";

    private static final String SQL_SELECT_ALL
            = "SELECT CodServicio, Nombre, Descripcion, CostoMensual, Categoria FROM Servicio ORDER BY Nombre";

    private static final String SQL_SELECT_BY_ID
            = "SELECT CodServicio, Nombre, Descripcion, CostoMensual, Categoria FROM Servicio WHERE CodServicio=?";

    private static final String SQL_UPDATE
            = "UPDATE Servicio SET Nombre=?, Descripcion=?, CostoMensual=?, Categoria=? WHERE CodServicio=?";

    private static final String SQL_DELETE
            = "DELETE FROM Servicio WHERE CodServicio=?";

    public void insert(Servicio s) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, s.getCodServicio());

            ps.setString(2, s.getNombre());

            ps.setString(3, s.getDescripcion());

            ps.setDouble(4, s.getCostoMensual());

            ps.setString(5, s.getCategoria());

            ps.executeUpdate();

        }

    }

    public List<Servicio> findAll() throws SQLException, ClassNotFoundException {

        List<Servicio> list = new ArrayList<>();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        }

        return list;

    }

    public Servicio findById(String cod) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setString(1, cod);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRow(rs);
                }

            }

        }

        return null;

    }

    public void update(Servicio s) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, s.getNombre());

            ps.setString(2, s.getDescripcion());

            ps.setDouble(3, s.getCostoMensual());

            ps.setString(4, s.getCategoria());

            ps.setString(5, s.getCodServicio());

            ps.executeUpdate();

        }

    }

    public void delete(String cod) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setString(1, cod);

            ps.executeUpdate();

        }

    }

    private Servicio mapRow(ResultSet rs) throws SQLException {

        return new Servicio(
                rs.getString("CodServicio"), rs.getString("Nombre"),
                rs.getString("Descripcion"), rs.getDouble("CostoMensual"),
                rs.getString("Categoria"));

    }

}
