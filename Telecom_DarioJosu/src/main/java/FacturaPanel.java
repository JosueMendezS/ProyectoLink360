package link360.ui;

import link360.dao.FacturaDAO;
import link360.dao.LineaMovilDAO;
import link360.model.Factura;
import link360.model.LineaMovil;
import link360.util.ErrorHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;

/**
 * CRUD panel for the Factura table. FK NumTelefonico is resolved via JComboBox
 * from Linea_Movil.
 *
 * @author Link360 Project
 */
public class FacturaPanel extends JPanel {

    private final FacturaDAO facturaDAO = new FacturaDAO();
    private final LineaMovilDAO lineaDAO = new LineaMovilDAO();

    private JTextField txtNumFactura, txtFecha, txtFechaVenc, txtMonto,
            txtImpuestos, txtDescuentos, txtPuntos, txtMontoFinal, txtFechaPago;
    private JComboBox<LineaMovil> cmbLinea;
    private JComboBox<String> cmbEstadoPago;

    private JTable table;
    private DefaultTableModel tableModel;
    private boolean editMode = false;

    public FacturaPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        loadLineas();
        loadTable();
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Datos de la Factura"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 8, 4, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumFactura = new JTextField(6);
        txtNumFactura.setEditable(false);
        txtNumFactura.setBackground(new Color(220, 220, 220));
        txtFecha = new JTextField(10);
        txtFechaVenc = new JTextField(10);
        txtMonto = new JTextField(10);
        txtImpuestos = new JTextField(10);
        txtDescuentos = new JTextField(10);
        txtPuntos = new JTextField(8);
        txtMontoFinal = new JTextField(10);
        txtFechaPago = new JTextField(10);
        cmbLinea = new JComboBox<>();
        cmbEstadoPago = new JComboBox<>(new String[]{"Pendiente", "Pagada", "Vencida"});

        addField(p, gbc, "N° Factura (auto)", txtNumFactura, 0, 0);
        addField(p, gbc, "Línea Móvil *", cmbLinea, 0, 2);
        addField(p, gbc, "Estado Pago *", cmbEstadoPago, 0, 4);
        addField(p, gbc, "Fecha Factura *\n(YYYY-MM-DD)", txtFecha, 1, 0);
        addField(p, gbc, "Fecha Vencimiento *\n(YYYY-MM-DD)", txtFechaVenc, 1, 2);
        addField(p, gbc, "Fecha Pago\n(YYYY-MM-DD o vacío)", txtFechaPago, 1, 4);
        addField(p, gbc, "Monto (₡) *", txtMonto, 2, 0);
        addField(p, gbc, "Impuestos (₡) *", txtImpuestos, 2, 2);
        addField(p, gbc, "Descuentos (₡)", txtDescuentos, 2, 4);
        addField(p, gbc, "Puntos Redimidos", txtPuntos, 3, 0);
        addField(p, gbc, "Monto Final (₡) *", txtMontoFinal, 3, 2);
        return p;
    }

    private void addField(JPanel p, GridBagConstraints gbc,
            String label, JComponent field, int row, int col) {
        gbc.gridx = col;
        gbc.gridy = row;
        gbc.weightx = 0;
        p.add(new JLabel(label + ":"), gbc);
        gbc.gridx = col + 1;
        gbc.weightx = 1;
        p.add(field, gbc);
    }

    private JScrollPane buildTable() {
        tableModel = new DefaultTableModel(
                new String[]{"N° Factura", "Teléfono", "Fecha", "Vencimiento", "Monto", "Impuestos",
                    "Descuentos", "Puntos", "Monto Final", "Fecha Pago", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(new Color(142, 68, 173));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateFormFromTable();
            }
        });
        return new JScrollPane(table);
    }

    private JPanel buildButtons() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        p.setBackground(new Color(245, 247, 250));
        JButton btnNew = styledBtn("➕ Nueva", new Color(39, 174, 96));
        JButton btnSave = styledBtn("💾 Guardar", new Color(41, 128, 185));
        JButton btnDelete = styledBtn("🗑 Eliminar", new Color(192, 57, 43));
        JButton btnClear = styledBtn("✖ Limpiar", new Color(127, 140, 141));
        JButton btnRefresh = styledBtn("🔄 Refrescar", new Color(142, 68, 173));
        btnNew.addActionListener(e -> enableNewMode());
        btnSave.addActionListener(e -> saveRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadTable());
        p.add(btnNew);
        p.add(btnSave);
        p.add(btnDelete);
        p.add(btnClear);
        p.add(btnRefresh);
        return p;
    }

    private JButton styledBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void loadLineas() {
        try {
            cmbLinea.removeAllItems();
            for (LineaMovil lm : lineaDAO.findAll()) {
                cmbLinea.addItem(lm);
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar líneas");
        }
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            for (Factura f : facturaDAO.findAll()) {
                tableModel.addRow(new Object[]{f.getNumFactura(), f.getNumTelefonico(),
                    f.getFecha(), f.getFechaVencimiento(), f.getMonto(), f.getImpuestos(),
                    f.getDescuentosAplicados(), f.getPuntosRedimidos(), f.getMontoFinal(),
                    f.getFechaPago(), f.getEstadoPago()});
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar facturas");
        }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtNumFactura.setText(str(tableModel.getValueAt(row, 0)));
        String tel = str(tableModel.getValueAt(row, 1));
        for (int i = 0; i < cmbLinea.getItemCount(); i++) {
            if (cmbLinea.getItemAt(i).getNumTelefonico().equals(tel)) {
                cmbLinea.setSelectedIndex(i);
                break;
            }
        }
        txtFecha.setText(str(tableModel.getValueAt(row, 2)));
        txtFechaVenc.setText(str(tableModel.getValueAt(row, 3)));
        txtMonto.setText(str(tableModel.getValueAt(row, 4)));
        txtImpuestos.setText(str(tableModel.getValueAt(row, 5)));
        txtDescuentos.setText(str(tableModel.getValueAt(row, 6)));
        txtPuntos.setText(str(tableModel.getValueAt(row, 7)));
        txtMontoFinal.setText(str(tableModel.getValueAt(row, 8)));
        txtFechaPago.setText(str(tableModel.getValueAt(row, 9)));
        cmbEstadoPago.setSelectedItem(tableModel.getValueAt(row, 10));
        editMode = true;
    }

    private void clearForm() {
        txtNumFactura.setText("");
        txtFecha.setText("");
        txtFechaVenc.setText("");
        txtMonto.setText("");
        txtImpuestos.setText("");
        txtDescuentos.setText("0");
        txtPuntos.setText("0");
        txtMontoFinal.setText("");
        txtFechaPago.setText("");
        cmbEstadoPago.setSelectedIndex(0);
        if (cmbLinea.getItemCount() > 0) {
            cmbLinea.setSelectedIndex(0);
        }
        editMode = false;
        table.clearSelection();
    }

    private void enableNewMode() {
        clearForm();
    }

    private void saveRecord() {
        Factura f = buildFromForm();
        if (f == null) {
            return;
        }
        try {
            if (editMode) {
                facturaDAO.update(f);
                JOptionPane.showMessageDialog(this, "Factura actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                facturaDAO.insert(f);
                JOptionPane.showMessageDialog(this, "Factura registrada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, editMode ? "Actualizar factura" : "Registrar factura");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Guardar factura");
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una factura para eliminar.", "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String num = str(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ Al eliminar la factura N°" + num + ", también se eliminarán\nlos conceptos de cobro asociados.\n\n¿Desea continuar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            facturaDAO.delete(Integer.parseInt(num));
            JOptionPane.showMessageDialog(this, "Factura eliminada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, "Eliminar factura");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Eliminar factura");
        }
    }

    private Factura buildFromForm() {
        LineaMovil lm = (LineaMovil) cmbLinea.getSelectedItem();
        String fecha = txtFecha.getText().trim();
        String fechaV = txtFechaVenc.getText().trim();
        String montoS = txtMonto.getText().trim();
        String impS = txtImpuestos.getText().trim();
        String finalS = txtMontoFinal.getText().trim();
        if (lm == null || fecha.isEmpty() || fechaV.isEmpty() || montoS.isEmpty() || impS.isEmpty() || finalS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Línea, Fechas, Monto, Impuestos y Monto Final son obligatorios.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        for (String d : new String[]{fecha, fechaV}) {
            if (!d.matches("\\d{4}-\\d{2}-\\d{2}")) {
                JOptionPane.showMessageDialog(this, "⚠ Las fechas deben tener formato YYYY-MM-DD.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        try {
            double monto = Double.parseDouble(montoS);
            double imp = Double.parseDouble(impS);
            double desc = txtDescuentos.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtDescuentos.getText().trim());
            int puntos = txtPuntos.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtPuntos.getText().trim());
            double montoFinal = Double.parseDouble(finalS);
            String fechaPago = txtFechaPago.getText().trim();
            int numFact = txtNumFactura.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtNumFactura.getText().trim());
            return new Factura(numFact, lm.getNumTelefonico(), fecha, fechaV, monto, imp, desc, puntos, montoFinal,
                    fechaPago.isEmpty() ? null : fechaPago, (String) cmbEstadoPago.getSelectedItem());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠ Los montos y puntos deben ser números válidos.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
