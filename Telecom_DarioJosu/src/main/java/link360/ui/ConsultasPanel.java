package link360.ui;

import link360.dao.ConsultasDAO;
import link360.util.ErrorHandler;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Panel that displays the 5 complex queries, the business view, and the audit
 * trail. All queries use JOINs. Some use SQL operators not covered in class
 * (UPPER, DATEDIFF, ISNULL).
 *
 * @author Link360 Project
 */
public class ConsultasPanel extends JPanel {

    private final ConsultasDAO dao = new ConsultasDAO();

    // Table for displaying query results
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblQueryTitle;
    private JTextArea txtQueryDescription;

    public ConsultasPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));

        add(buildSelectorPanel(), BorderLayout.NORTH);
        add(buildResultPanel(), BorderLayout.CENTER);
    }

    // ── Query selector ────────────────────────────────────────────────────────
    private JPanel buildSelectorPanel() {
        JPanel outer = new JPanel(new BorderLayout(5, 5));
        outer.setBackground(Color.WHITE);
        outer.setBorder(BorderFactory.createTitledBorder("Seleccione una consulta"));

        // Description area
        txtQueryDescription = new JTextArea(3, 60);
        txtQueryDescription.setEditable(false);
        txtQueryDescription.setLineWrap(true);
        txtQueryDescription.setWrapStyleWord(true);
        txtQueryDescription.setBackground(new Color(236, 240, 241));
        txtQueryDescription.setFont(new Font("SansSerif", Font.ITALIC, 12));
        txtQueryDescription.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        // Buttons for each query
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        btnPanel.setBackground(Color.WHITE);

        btnPanel.add(queryBtn("Q1 – Clientes y Planes",
                new Color(41, 128, 185), this::runQ1,
                "Q1: Clientes con sus líneas activas y plan tarifario vigente.\n"
                + "Usa INNER JOIN (Cliente + Linea_Movil + Historia_Plan + Plan_Tarifario).\n"
                + "Operador especial: UPPER() para mostrar nombres en mayúsculas, ISNULL() para FechaFin nula."));

        btnPanel.add(queryBtn("Q2 – Resumen Facturación",
                new Color(39, 174, 96), this::runQ2,
                "Q2: Resumen de facturación por cliente: total, promedio y facturas pendientes.\n"
                + "Usa LEFT JOIN (Cliente + Linea_Movil + Factura).\n"
                + "Operadores de clase: SUM(), AVG(), COUNT(), GROUP BY."));

        btnPanel.add(queryBtn("Q3 – Servicios por Línea",
                new Color(211, 84, 0), this::runQ3,
                "Q3: Servicios contratados por cada línea móvil con días activos.\n"
                + "Usa INNER JOIN (Linea_Movil + Contrat_Servicio + Servicio).\n"
                + "Operador especial: DATEDIFF(DAY, ...) para calcular antigüedad del contrato."));

        btnPanel.add(queryBtn("Q4 – Promociones Activas",
                new Color(142, 68, 173), this::runQ4,
                "Q4: Promociones vigentes con su tipo y máximo descuento permitido.\n"
                + "Usa INNER JOIN (Promocion + Tipo_Promo), filtro WHERE con GETDATE().\n"
                + "Operador especial: DATEDIFF(DAY, GETDATE(), FechaFin) para días restantes."));

        btnPanel.add(queryBtn("Q5 – Detalle Consumos",
                new Color(22, 160, 133), this::runQ5,
                "Q5: Registro de consumos de todas las líneas (llamadas, SMS, datos, roaming).\n"
                + "Usa CONVERT(VARCHAR, ..., 120) para formatear fechas.\n"
                + "Operador especial: ISNULL() para mostrar 'En curso' en llamadas sin FhFin."));

        btnPanel.add(queryBtn("🔍 Vista de Negocio",
                new Color(52, 73, 94), this::runView,
                "VISTA VW_HistorialPlanesServiciosActivos: consolida historia de planes\n"
                + "y servicios activos por línea móvil. Pregunta de negocio: ¿Qué plan tiene\ncada línea activa y qué servicios adicionales usa?"));

        btnPanel.add(queryBtn("📋 Pistas de Auditoría",
                new Color(192, 57, 43), this::runAudit,
                "AUDITORÍA: Muestra columnas de auditoría (creado por / fecha, modificado por / fecha)\n"
                + "sobre la tabla Cliente. Permite ver quién y cuándo creó o modificó cada registro.\n"
                + "Operador especial: ISNULL() para reemplazar nulls con '—'."));

        outer.add(btnPanel, BorderLayout.CENTER);
        outer.add(new JScrollPane(txtQueryDescription), BorderLayout.SOUTH);
        return outer;
    }

    private JButton queryBtn(String label, Color color,
            Runnable action, String description) {
        JButton b = new JButton(label);
        b.setBackground(color);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 11f));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> {
            txtQueryDescription.setText(description);
            action.run();
        });
        return b;
    }

    // ── Result table ──────────────────────────────────────────────────────────
    private JPanel buildResultPanel() {
        JPanel p = new JPanel(new BorderLayout(5, 5));
        p.setBackground(new Color(245, 247, 250));

        lblQueryTitle = new JLabel("← Seleccione una consulta del panel superior");
        lblQueryTitle.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblQueryTitle.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));

        tableModel = new DefaultTableModel(new String[]{"—"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(new Color(44, 62, 80)); 
        headerRenderer.setForeground(Color.WHITE);           
        headerRenderer.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD)); 
        
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        p.add(lblQueryTitle, BorderLayout.NORTH);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        // Row count label at the bottom
        JLabel lblCount = new JLabel(" ");
        lblCount.setName("rowCount");
        p.add(lblCount, BorderLayout.SOUTH);
        return p;
    }

    // ── Query runners ─────────────────────────────────────────────────────────
    private void runQ1() {
        runQuery("Q1 – Clientes con líneas activas y plan vigente",
                ConsultasDAO.HEADERS_Q1,
                () -> dao.queryClientsWithActivePlans());
    }

    private void runQ2() {
        runQuery("Q2 – Resumen de facturación por cliente",
                ConsultasDAO.HEADERS_Q2,
                () -> dao.queryBillingSummaryPerClient());
    }

    private void runQ3() {
        runQuery("Q3 – Servicios contratados por línea",
                ConsultasDAO.HEADERS_Q3,
                () -> dao.queryServicesPerLine());
    }

    private void runQ4() {
        runQuery("Q4 – Promociones activas con tipo y días restantes",
                ConsultasDAO.HEADERS_Q4,
                () -> dao.queryActivePromotions());
    }

    private void runQ5() {
        runQuery("Q5 – Detalle de consumos por línea",
                ConsultasDAO.HEADERS_Q5,
                () -> dao.queryConsumptionDetails());
    }

    private void runView() {
        runQuery("Vista: VW_HistorialPlanesServiciosActivos",
                ConsultasDAO.HEADERS_VIEW,
                () -> dao.queryView());
    }

    private void runAudit() {
        runQuery("Pistas de Auditoría – Tabla Cliente",
                ConsultasDAO.HEADERS_AUDIT,
                () -> dao.queryAuditTrail());
    }

    // ── Shared executor ───────────────────────────────────────────────────────
    @FunctionalInterface
    interface QuerySupplier {

        List<Object[]> get() throws Exception;
    }

    private void runQuery(String title, String[] headers, QuerySupplier supplier) {
        lblQueryTitle.setText(title);
        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);
        for (String h : headers) {
            tableModel.addColumn(h);
        }

        try {
            List<Object[]> rows = supplier.get();
            for (Object[] row : rows) {
                tableModel.addRow(row);
            }
            // Auto-fit columns
            for (int col = 0; col < table.getColumnCount(); col++) {
                table.getColumnModel().getColumn(col).setPreferredWidth(140);
            }
            // Show row count in the bottom label
            Component c = ((JPanel) getComponent(1)).getComponent(2);
            if (c instanceof JLabel) {
                ((JLabel) c).setText("  Total de registros: " + rows.size());
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Ejecutar consulta: " + title);
        }
    }
}
