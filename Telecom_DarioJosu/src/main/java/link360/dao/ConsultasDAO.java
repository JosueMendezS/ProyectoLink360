package link360.dao;

import link360.util.DatabaseConnection;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

/**
 *
 * DAO for complex SELECT queries (JOINs, aggregates, VIEW and audit trail).
 *
 * Each method returns a List of Object[] rows for display in a JTable.
 *
 *
 *
 * SQL operators used:
 *
 * Studied in class : SUM, AVG, COUNT, INNER JOIN, LEFT JOIN, GROUP BY, ORDER BY
 *
 * NOT studied (extra): UPPER() / UCASE equivalent via UPPER(), DATEDIFF(),
 * ISNULL()
 *
 *
 *
 * @author Link360 Project
 *
 */
public class ConsultasDAO {

    /**
     *
     * Q1 – Clients with their active mobile lines and current plan.
     *
     * Uses INNER JOIN across Cliente, Linea_Movil, Historia_Plan and
     * Plan_Tarifario.
     *
     * Uses ISNULL() (operator not covered in class) to handle nullable
     * FechaFin.
     *
     */
    public static final String[] HEADERS_Q1
            = {"Cédula", "Nombre", "Apellidos", "Tipo Cliente", "Teléfono", "Tipo Línea",
                "Tecnología", "Plan Actual", "Cuota Mensual (₡)"};

    public List<Object[]> queryClientsWithActivePlans() throws SQLException, ClassNotFoundException {

        String sql
                = "SELECT c.Cedula, UPPER(c.Nombre) AS Nombre, UPPER(c.Apellidos) AS Apellidos, "
                + "c.TipoCliente, lm.NumTelefonico, lm.TipoLinea, lm.Tecnologia, "
                + "pt.Nombre AS Plan, pt.CuotaMensual "
                + "FROM Cliente c "
                + "INNER JOIN Linea_Movil lm ON c.Cedula = lm.Cedula "
                + "INNER JOIN Historia_Plan hp ON lm.NumTelefonico = hp.NumTelefonico "
                + "INNER JOIN Plan_Tarifario pt ON hp.CodPlan = pt.CodPlan "
                + "WHERE lm.Estado_Linea = 'Activa' "
                + "AND ISNULL(hp.FechaFin, '9999-12-31') = '9999-12-31' "
                + "ORDER BY c.Apellidos";

        return executeQuery(sql);

    }

    /**
     *
     * Q2 – Billing summary per client: total billed, average invoice, pending
     * count.
     *
     * Uses SUM, AVG, COUNT (operators studied in class).
     *
     */
    public static final String[] HEADERS_Q2
            = {"Cédula", "Nombre", "Cant. Facturas", "Total Facturado (₡)", "Promedio Factura (₡)",
                "Facturas Pendientes"};

    public List<Object[]> queryBillingSummaryPerClient() throws SQLException, ClassNotFoundException {

        String sql
                = "SELECT c.Cedula, c.Nombre + ' ' + c.Apellidos AS Nombre, "
                + "COUNT(f.NumFactura) AS CantFacturas, "
                + "SUM(f.MontoFinal) AS TotalFacturado, "
                + "CAST(AVG(f.MontoFinal) AS DECIMAL(12,2)) AS Promedio, "
                + "SUM(CASE WHEN f.EstadoPago = 'Pendiente' THEN 1 ELSE 0 END) AS Pendientes "
                + "FROM Cliente c "
                + "LEFT JOIN Linea_Movil lm ON c.Cedula = lm.Cedula "
                + "LEFT JOIN Factura f ON lm.NumTelefonico = f.NumTelefonico "
                + "GROUP BY c.Cedula, c.Nombre, c.Apellidos "
                + "ORDER BY TotalFacturado DESC";

        return executeQuery(sql);

    }

    /**
     *
     * Q3 – Services contracted per line with their status and monthly cost.
     *
     * Uses INNER JOIN across Linea_Movil, Contrat_Servicio, Servicio.
     *
     * Uses DATEDIFF() (operator not covered in class) to show days since
     * contract.
     *
     */
    public static final String[] HEADERS_Q3
            = {"Teléfono", "Cédula Cliente", "Servicio", "Categoría", "Costo (₡)",
                "Estado Contrato", "Días Contratado"};

    public List<Object[]> queryServicesPerLine() throws SQLException, ClassNotFoundException {

        String sql
                = "SELECT lm.NumTelefonico, lm.Cedula, s.Nombre AS Servicio, s.Categoria, "
                + "s.CostoMensual, cs.Estado_Contrato, "
                + "DATEDIFF(DAY, cs.FechaContratacion, GETDATE()) AS DiasContratado "
                + "FROM Linea_Movil lm "
                + "INNER JOIN Contrat_Servicio cs ON lm.NumTelefonico = cs.NumTelefonico "
                + "INNER JOIN Servicio s ON cs.CodServicio = s.CodServicio "
                + "ORDER BY lm.NumTelefonico, s.Nombre";

        return executeQuery(sql);

    }

    /**
     *
     * Q4 – Active promotions with their type and maximum allowed discount.
     *
     * Uses INNER JOIN between Promocion and Tipo_Promo.
     *
     * Uses GETDATE() to filter currently active promotions.
     *
     */
    public static final String[] HEADERS_Q4
            = {"Código", "Promoción", "Tipo", "% Descuento", "% Máx Permitido",
                "Inicio", "Fin", "Días Restantes"};

    public List<Object[]> queryActivePromotions() throws SQLException, ClassNotFoundException {

        String sql
                = "SELECT p.CodPromocion, p.Nombre, tp.TipoConcepto, p.PctDescuento, "
                + "tp.PorcentajeMaximo, p.FechaInicio, p.FechaFin, "
                + "DATEDIFF(DAY, GETDATE(), p.FechaFin) AS DiasRestantes "
                + "FROM Promocion p "
                + "INNER JOIN Tipo_Promo tp ON p.IdTipoPromo = tp.IdTipoPromo "
                + "WHERE p.FechaFin >= GETDATE() "
                + "ORDER BY p.FechaFin";

        return executeQuery(sql);

    }

    /**
     *
     * Q5 – Consumption details per line (calls, SMS, data, roaming).
     *
     * Uses LEFT JOIN to include all consumption types.
     *
     * Uses ISNULL to replace NULL end-times with a label.
     *
     */
    public static final String[] HEADERS_Q5
            = {"Teléfono", "Tipo Consumo", "Ámbito", "Inicio", "Fin", "Cantidad", "Costo (₡)"};

    public List<Object[]> queryConsumptionDetails() throws SQLException, ClassNotFoundException {

        String sql
                = "SELECT con.NumTelefonico, con.TipoConsumo, con.Ambito, "
                + "CONVERT(VARCHAR, con.FhInicio, 120) AS Inicio, "
                + "ISNULL(CONVERT(VARCHAR, con.FhFin, 120), 'En curso') AS Fin, "
                + "con.Cantidad, con.CostoEvaluado "
                + "FROM Consumo con "
                + "ORDER BY con.FhInicio DESC";

        return executeQuery(sql);

    }

    /**
     *
     * VIEW – Queries the view VW_HistorialPlanesServiciosActivos created in the
     * physical model.
     *
     * Shows consolidated history of plans and active services per line.
     *
     */
    public static final String[] HEADERS_VIEW
            = {"Teléfono", "Cédula", "Tipo Línea", "Plan", "Cuota (₡)", "Inicio Plan",
                "Servicio", "Categoría", "Costo Servicio (₡)", "Estado Contrato"};

    public List<Object[]> queryView() throws SQLException, ClassNotFoundException {

        String sql = "SELECT * FROM VW_HistorialPlanesServiciosActivos ORDER BY NumTelefonico";

        return executeQuery(sql);

    }

    /**
     *
     * AUDIT – Reads audit trail columns (CreatedBy, CreatedAt, UpdatedBy,
     * UpdatedAt, Estado)
     *
     * from the Cliente table as a sample of the audit mechanism.
     *
     */
    public static final String[] HEADERS_AUDIT
            = {"Cédula", "Nombre", "Tipo", "Creado Por", "Fecha Creación",
                "Modificado Por", "Fecha Modificación", "Estado Registro"};

    public List<Object[]> queryAuditTrail() throws SQLException, ClassNotFoundException {

        String sql
                = "SELECT c.Cedula, c.Nombre + ' ' + c.Apellidos AS Nombre, c.TipoCliente, "
                + "c.CreatedBy, CONVERT(VARCHAR,c.CreatedAt,120) AS FechaCreacion, "
                + "ISNULL(c.UpdatedBy,'—') AS UpdatedBy, "
                + "ISNULL(CONVERT(VARCHAR,c.UpdatedAt,120),'—') AS FechaModificacion, "
                + "c.EstadoRegistro "
                + "FROM Cliente c "
                + "ORDER BY c.CreatedAt DESC";

        return executeQuery(sql);

    }

    // ── Shared query executor ──────────────────────────────────────────────────
    /**
     *
     * Executes any SELECT and returns rows as Object[] lists (suitable for
     * JTable).
     *
     */
    private List<Object[]> executeQuery(String sql) throws SQLException, ClassNotFoundException {

        List<Object[]> rows = new ArrayList<>();

        Connection conn = DatabaseConnection.getInstance().getConnection();

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            int colCount = rs.getMetaData().getColumnCount();

            while (rs.next()) {

                Object[] row = new Object[colCount];

                for (int i = 0; i < colCount; i++) {
                    row[i] = rs.getObject(i + 1);
                }

                rows.add(row);

            }

        }

        return rows;

    }

}
