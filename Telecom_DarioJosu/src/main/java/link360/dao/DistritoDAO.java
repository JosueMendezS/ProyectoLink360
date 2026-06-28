package link360.dao;

import link360.model.Distrito;
import link360.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class DistritoDAO {
    public List<Distrito> findAll() throws SQLException, ClassNotFoundException {
        List<Distrito> list = new ArrayList<>();
        String sql = "SELECT CodDistrito, NombreDistrito, CodCanton FROM Distrito ORDER BY NombreDistrito";
        Connection conn = DatabaseConnection.getInstance().getConnection();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Distrito(rs.getInt("CodDistrito"),
                                      rs.getString("NombreDistrito"),
                                      rs.getInt("CodCanton")));
            }
        }
        return list;
    }
}