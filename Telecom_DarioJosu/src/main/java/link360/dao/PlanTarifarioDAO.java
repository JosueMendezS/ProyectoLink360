package link360.dao;

import link360.model.PlanTarifario;

import link360.util.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

public class PlanTarifarioDAO {

    private static final String SQL_INSERT
            = "INSERT INTO Plan_Tarifario (CodPlan, Nombre, Descripcion, CuotaMensual, "
            + "GbIncluidos, MinIncluidos, MsgIncluidos, CostoExceso, IdCateg) VALUES (?,?,?,?,?,?,?,?,?)";

    private static final String SQL_SELECT_ALL
            = "SELECT CodPlan, Nombre, Descripcion, CuotaMensual, GbIncluidos, "
            + "MinIncluidos, MsgIncluidos, CostoExceso, IdCateg FROM Plan_Tarifario ORDER BY Nombre";

    private static final String SQL_SELECT_BY_ID
            = "SELECT CodPlan, Nombre, Descripcion, CuotaMensual, GbIncluidos, "
            + "MinIncluidos, MsgIncluidos, CostoExceso, IdCateg FROM Plan_Tarifario WHERE CodPlan=?";

    private static final String SQL_UPDATE
            = "UPDATE Plan_Tarifario SET Nombre=?, Descripcion=?, CuotaMensual=?, GbIncluidos=?, "
            + "MinIncluidos=?, MsgIncluidos=?, CostoExceso=?, IdCateg=? WHERE CodPlan=?";

    private static final String SQL_DELETE
            = "DELETE FROM Plan_Tarifario WHERE CodPlan=?";

    public void insert(PlanTarifario p) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {

            ps.setString(1, p.getCodPlan());
            ps.setString(2, p.getNombre());

            ps.setString(3, p.getDescripcion());
            ps.setDouble(4, p.getCuotaMensual());

            ps.setInt(5, p.getGbIncluidos());
            ps.setInt(6, p.getMinIncluidos());

            ps.setInt(7, p.getMsgIncluidos());
            ps.setDouble(8, p.getCostoExceso());

            ps.setInt(9, p.getIdCateg());

            ps.executeUpdate();

        }

    }

    public List<PlanTarifario> findAll() throws SQLException, ClassNotFoundException {

        List<PlanTarifario> list = new ArrayList<>();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }

        }

        return list;

    }

    public PlanTarifario findById(String cod) throws SQLException, ClassNotFoundException {

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

    public void update(PlanTarifario p) throws SQLException, ClassNotFoundException {

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());

            ps.setDouble(3, p.getCuotaMensual());
            ps.setInt(4, p.getGbIncluidos());

            ps.setInt(5, p.getMinIncluidos());
            ps.setInt(6, p.getMsgIncluidos());

            ps.setDouble(7, p.getCostoExceso());
            ps.setInt(8, p.getIdCateg());

            ps.setString(9, p.getCodPlan());

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

    private PlanTarifario mapRow(ResultSet rs) throws SQLException {

        return new PlanTarifario(
                rs.getString("CodPlan"), rs.getString("Nombre"), rs.getString("Descripcion"),
                rs.getDouble("CuotaMensual"), rs.getInt("GbIncluidos"), rs.getInt("MinIncluidos"),
                rs.getInt("MsgIncluidos"), rs.getDouble("CostoExceso"), rs.getInt("IdCateg"));

    }

}
