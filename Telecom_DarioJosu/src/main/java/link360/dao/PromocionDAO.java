package link360.dao;

import link360.model.Promocion;

import link360.util.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

public class PromocionDAO {

    private static final String SQL_INSERT
            = "INSERT INTO Promocion (CodPromocion,Nombre,Descripcion,FechaInicio,FechaFin,PctDescuento,IdTipoPromo) "
            + "VALUES (?,?,?,?,?,?,?)";

    private static final String SQL_SELECT_ALL
            = "SELECT CodPromocion,Nombre,Descripcion,FechaInicio,FechaFin,PctDescuento,IdTipoPromo "
            + "FROM Promocion ORDER BY Nombre";

    private static final String SQL_SELECT_BY_ID
            = "SELECT CodPromocion,Nombre,Descripcion,FechaInicio,FechaFin,PctDescuento,IdTipoPromo "
            + "FROM Promocion WHERE CodPromocion=?";

    private static final String SQL_UPDATE
            = "UPDATE Promocion SET Nombre=?,Descripcion=?,FechaInicio=?,FechaFin=?,PctDescuento=?,IdTipoPromo=? "
            + "WHERE CodPromocion=?";

    private static final String SQL_DELETE = "DELETE FROM Promocion WHERE CodPromocion=?";

    public void insert(Promocion p) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, p.getCodPromocion());
            ps.setString(2, p.getNombre());

            ps.setString(3, p.getDescripcion());

            ps.setDate(4, Date.valueOf(p.getFechaInicio()));

            if (p.getFechaFin() == null || p.getFechaFin().trim().isEmpty()) {
                ps.setNull(5, Types.DATE);
            } else {
                ps.setDate(5, Date.valueOf(p.getFechaFin()));
            }

            ps.setDouble(6, p.getPctDescuento());
            ps.setInt(7, p.getIdTipoPromo());

            ps.executeUpdate();

        }

    }

    public List<Promocion> findAll() throws SQLException, ClassNotFoundException {

        List<Promocion> list = new ArrayList<>();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        }

        return list;

    }

    public Promocion findById(String cod) throws SQLException, ClassNotFoundException {

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

    public void update(Promocion p) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());

            ps.setDate(3, Date.valueOf(p.getFechaInicio()));

            if (p.getFechaFin() == null || p.getFechaFin().trim().isEmpty()) {
                ps.setNull(4, Types.DATE);
            } else {
                ps.setDate(4, Date.valueOf(p.getFechaFin()));
            }
            ps.setDouble(5, p.getPctDescuento());
            ps.setInt(6, p.getIdTipoPromo());

            ps.setString(7, p.getCodPromocion());

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

    private Promocion mapRow(ResultSet rs) throws SQLException {
        Date fi = rs.getDate("FechaInicio");
        Date ff = rs.getDate("FechaFin");
        return new Promocion(
                rs.getString("CodPromocion"),
                rs.getString("Nombre"),
                rs.getString("Descripcion"),
                fi != null ? fi.toString() : "",
                ff != null ? ff.toString() : null,
                rs.getDouble("PctDescuento"),
                rs.getInt("IdTipoPromo"));
    }

}
