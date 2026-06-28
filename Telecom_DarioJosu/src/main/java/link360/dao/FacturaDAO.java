package link360.dao;

import link360.model.Factura;

import link360.util.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

/**
 *
 * DAO for the Factura table. Full CRUD.
 *
 * NumFactura is IDENTITY — the DB generates it on INSERT.
 *
 * @author Link360 Project
 *
 */
public class FacturaDAO {

    private static final String SQL_INSERT
            = "INSERT INTO Factura (NumTelefonico, Fecha, FechaVencimiento, Monto, Impuestos, "
            + "DescuentosAplicados, PuntosRedimidos, MontoFinal, FechaPago, EstadoPago) "
            + "VALUES (?,?,?,?,?,?,?,?,?,?)";

    private static final String SQL_SELECT_ALL
            = "SELECT NumFactura, NumTelefonico, Fecha, FechaVencimiento, Monto, Impuestos, "
            + "DescuentosAplicados, PuntosRedimidos, MontoFinal, FechaPago, EstadoPago "
            + "FROM Factura ORDER BY Fecha DESC";

    private static final String SQL_SELECT_BY_ID
            = "SELECT NumFactura, NumTelefonico, Fecha, FechaVencimiento, Monto, Impuestos, "
            + "DescuentosAplicados, PuntosRedimidos, MontoFinal, FechaPago, EstadoPago "
            + "FROM Factura WHERE NumFactura=?";

    private static final String SQL_UPDATE
            = "UPDATE Factura SET NumTelefonico=?, Fecha=?, FechaVencimiento=?, Monto=?, Impuestos=?, "
            + "DescuentosAplicados=?, PuntosRedimidos=?, MontoFinal=?, FechaPago=?, EstadoPago=? "
            + "WHERE NumFactura=?";

    private static final String SQL_DELETE
            = "DELETE FROM Factura WHERE NumFactura=?";

    public void insert(Factura f) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, f.getNumTelefonico());

            ps.setDate(2, Date.valueOf(f.getFecha()));

            ps.setDate(3, Date.valueOf(f.getFechaVencimiento()));

            ps.setDouble(4, f.getMonto());

            ps.setDouble(5, f.getImpuestos());

            ps.setDouble(6, f.getDescuentosAplicados());

            ps.setInt(7, f.getPuntosRedimidos());

            ps.setDouble(8, f.getMontoFinal());

            if (f.getFechaPago() == null || f.getFechaPago().trim().isEmpty()) {
                ps.setNull(9, Types.DATE);
            } else {
                ps.setDate(9, Date.valueOf(f.getFechaPago()));
            }

            ps.setString(10, f.getEstadoPago());

            ps.executeUpdate();

        }

    }

    public List<Factura> findAll() throws SQLException, ClassNotFoundException {

        List<Factura> list = new ArrayList<>();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        }

        return list;

    }

    public Factura findById(int id) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapRow(rs);
                }

            }

        }

        return null;

    }

    public void update(Factura f) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, f.getNumTelefonico());

            ps.setDate(2, Date.valueOf(f.getFecha()));

            ps.setDate(3, Date.valueOf(f.getFechaVencimiento()));

            ps.setDouble(4, f.getMonto());

            ps.setDouble(5, f.getImpuestos());

            ps.setDouble(6, f.getDescuentosAplicados());

            ps.setInt(7, f.getPuntosRedimidos());

            ps.setDouble(8, f.getMontoFinal());

            if (f.getFechaPago() == null || f.getFechaPago().trim().isEmpty()) {
                ps.setNull(9, Types.DATE);
            } else {
                ps.setDate(9, Date.valueOf(f.getFechaPago()));
            }

            ps.setString(10, f.getEstadoPago());

            ps.setInt(11, f.getNumFactura());

            ps.executeUpdate();

        }

    }

    public void delete(int id) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);

            ps.executeUpdate();

        }

    }

    private Factura mapRow(ResultSet rs) throws SQLException {

        Factura f = new Factura();

        f.setNumFactura(rs.getInt("NumFactura"));

        f.setNumTelefonico(rs.getString("NumTelefonico"));

        f.setFecha(rs.getString("Fecha"));

        f.setFechaVencimiento(rs.getString("FechaVencimiento"));

        f.setMonto(rs.getDouble("Monto"));

        f.setImpuestos(rs.getDouble("Impuestos"));

        f.setDescuentosAplicados(rs.getDouble("DescuentosAplicados"));

        f.setPuntosRedimidos(rs.getInt("PuntosRedimidos"));

        f.setMontoFinal(rs.getDouble("MontoFinal"));

        f.setFechaPago(rs.getString("FechaPago"));

        f.setEstadoPago(rs.getString("EstadoPago"));

        return f;

    }

}
