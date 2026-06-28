package link360.dao;

import link360.model.LineaMovil;

import link360.util.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

/**
 *
 * DAO for the Linea_Movil table. Full CRUD operations.
 *
 * Cascade note: deleting a Linea_Movil may affect Consumo, Contrat_Servicio,
 *
 * Historia_Plan, Adquis_Paquete and Factura
 *
 *
 *
 * @author Link360 Project
 *
 */
public class LineaMovilDAO {

    private static final String SQL_INSERT
            = "INSERT INTO Linea_Movil (NumTelefonico, Cedula, TipoLinea, Tecnologia, "
            + "FechaActivacion, Estado_Linea, TipoSIM) VALUES (?,?,?,?,?,?,?)";

    private static final String SQL_SELECT_ALL
            = "SELECT NumTelefonico, Cedula, TipoLinea, Tecnologia, "
            + "FechaActivacion, Estado_Linea, TipoSIM FROM Linea_Movil ORDER BY NumTelefonico";

    private static final String SQL_SELECT_BY_ID
            = "SELECT NumTelefonico, Cedula, TipoLinea, Tecnologia, "
            + "FechaActivacion, Estado_Linea, TipoSIM FROM Linea_Movil WHERE NumTelefonico = ?";

    private static final String SQL_UPDATE
            = "UPDATE Linea_Movil SET Cedula=?, TipoLinea=?, Tecnologia=?, "
            + "FechaActivacion=?, Estado_Linea=?, TipoSIM=? WHERE NumTelefonico=?";

    private static final String SQL_DELETE
            = "DELETE FROM Linea_Movil WHERE NumTelefonico=?";

    public void insert(LineaMovil lm) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, lm.getNumTelefonico());

            ps.setString(2, lm.getCedula());

            ps.setString(3, lm.getTipoLinea());

            ps.setString(4, lm.getTecnologia());

            ps.setDate(5, Date.valueOf(lm.getFechaActivacion()));

            ps.setString(6, lm.getEstadoLinea());

            ps.setString(7, lm.getTipoSIM());

            ps.executeUpdate();

        }

    }

    public List<LineaMovil> findAll() throws SQLException, ClassNotFoundException {

        List<LineaMovil> list = new ArrayList<>();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        }

        return list;

    }

    public LineaMovil findById(String numTel) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setString(1, numTel);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRow(rs);
                }

            }

        }

        return null;

    }

    public void update(LineaMovil lm) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, lm.getCedula());

            ps.setString(2, lm.getTipoLinea());

            ps.setString(3, lm.getTecnologia());

            ps.setDate(4, Date.valueOf(lm.getFechaActivacion()));

            ps.setString(5, lm.getEstadoLinea());

            ps.setString(6, lm.getTipoSIM());

            ps.setString(7, lm.getNumTelefonico());

            ps.executeUpdate();

        }

    }

    public void delete(String numTel) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setString(1, numTel);

            ps.executeUpdate();

        }

    }

    private LineaMovil mapRow(ResultSet rs) throws SQLException {

        LineaMovil lm = new LineaMovil();

        lm.setNumTelefonico(rs.getString("NumTelefonico"));

        lm.setCedula(rs.getString("Cedula"));

        lm.setTipoLinea(rs.getString("TipoLinea"));

        lm.setTecnologia(rs.getString("Tecnologia"));

        lm.setFechaActivacion(rs.getString("FechaActivacion"));

        lm.setEstadoLinea(rs.getString("Estado_Linea"));

        lm.setTipoSIM(rs.getString("TipoSIM"));

        return lm;

    }

}
