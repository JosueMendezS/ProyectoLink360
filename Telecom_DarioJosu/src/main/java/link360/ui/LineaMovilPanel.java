package link360.ui;

import link360.dao.ClienteDAO;
import link360.dao.LineaMovilDAO;
import link360.model.Cliente;
import link360.model.LineaMovil;
import link360.util.ErrorHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * CRUD panel for the Linea_Movil table. FK to Cliente (Cedula) is resolved via
 * a dropdown.
 *
 * @author Link360 Project
 */
public class LineaMovilPanel extends JPanel {

    private final LineaMovilDAO lineaDAO = new LineaMovilDAO();
    private final ClienteDAO clienteDAO = new ClienteDAO();

    private JTextField txtNumTel, txtFechaActivacion;
    private JComboBox<Cliente> cmbCliente;
    private JComboBox<String> cmbTipoLinea, cmbTecnologia, cmbEstado, cmbTipoSIM;

    private JTable table;
    private DefaultTableModel tableModel;
    private boolean editMode = false;

    public LineaMovilPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        loadClientes();
        loadTable();
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Datos de la Línea Móvil"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNumTel = new JTextField(12);
        txtFechaActivacion = new JTextField(10);
        cmbCliente = new JComboBox<>();
        cmbTipoLinea = new JComboBox<>(new String[]{"Postpago", "Prepago", "Empresarial"});
        cmbTecnologia = new JComboBox<>(new String[]{"4G", "5G", "3G"});
        cmbEstado = new JComboBox<>(new String[]{"Activa", "Suspendida", "Cancelada"});
        cmbTipoSIM = new JComboBox<>(new String[]{"eSIM", "Fisica"});

        addField(p, gbc, "Número Telefónico *", txtNumTel, 0, 0);
        addField(p, gbc, "Cliente *", cmbCliente, 0, 2);
        addField(p, gbc, "Tipo Línea *", cmbTipoLinea, 0, 4);
        addField(p, gbc, "Tecnología *", cmbTecnologia, 1, 0);
        addField(p, gbc, "Fecha Activación *\n(YYYY-MM-DD)", txtFechaActivacion, 1, 2);
        addField(p, gbc, "Estado *", cmbEstado, 1, 4);
        addField(p, gbc, "Tipo SIM *", cmbTipoSIM, 2, 0);
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
                new String[]{"Número", "Cédula Cliente", "Tipo", "Tecnología",
                    "Fecha Activación", "Estado", "SIM"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(new Color(22, 160, 133));
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
        JButton btnNew = styledBtn("➕ Nueva Línea", new Color(39, 174, 96));
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

    private void loadClientes() {
        try {
            cmbCliente.removeAllItems();
            for (Cliente c : clienteDAO.findAll()) {
                cmbCliente.addItem(c);
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar clientes");
        }
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            for (LineaMovil lm : lineaDAO.findAll()) {
                tableModel.addRow(new Object[]{
                    lm.getNumTelefonico(), lm.getCedula(), lm.getTipoLinea(),
                    lm.getTecnologia(), lm.getFechaActivacion(),
                    lm.getEstadoLinea(), lm.getTipoSIM()
                });
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar líneas móviles");
        }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtNumTel.setText(str(tableModel.getValueAt(row, 0)));
        String cedula = str(tableModel.getValueAt(row, 1));
        for (int i = 0; i < cmbCliente.getItemCount(); i++) {
            if (cmbCliente.getItemAt(i).getCedula().equals(cedula)) {
                cmbCliente.setSelectedIndex(i);
                break;
            }
        }
        cmbTipoLinea.setSelectedItem(tableModel.getValueAt(row, 2));
        cmbTecnologia.setSelectedItem(tableModel.getValueAt(row, 3));
        txtFechaActivacion.setText(str(tableModel.getValueAt(row, 4)));
        cmbEstado.setSelectedItem(tableModel.getValueAt(row, 5));
        cmbTipoSIM.setSelectedItem(tableModel.getValueAt(row, 6));
        txtNumTel.setEditable(false);
        editMode = true;
    }

    private void clearForm() {
        txtNumTel.setText("");
        txtFechaActivacion.setText("");
        cmbTipoLinea.setSelectedIndex(0);
        cmbTecnologia.setSelectedIndex(0);
        cmbEstado.setSelectedIndex(0);
        cmbTipoSIM.setSelectedIndex(0);
        if (cmbCliente.getItemCount() > 0) {
            cmbCliente.setSelectedIndex(0);
        }
        txtNumTel.setEditable(true);
        editMode = false;
        table.clearSelection();
    }

    private void enableNewMode() {
        clearForm();
        txtNumTel.requestFocus();
    }

    private void saveRecord() {
        LineaMovil lm = buildFromForm();
        if (lm == null) {
            return;
        }
        try {
            if (editMode) {
                lineaDAO.update(lm);
                JOptionPane.showMessageDialog(this, "Línea actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                lineaDAO.insert(lm);
                JOptionPane.showMessageDialog(this, "Línea registrada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, editMode ? "Actualizar línea" : "Registrar línea");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Guardar línea");
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una línea para eliminar.",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String numTel = str(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ ADVERTENCIA DE BORRADO EN CASCADA\n\n"
                + "Está por eliminar la línea: " + numTel + "\n\n"
                + "Esta acción también eliminará:\n"
                + "  • Historial de consumos (llamadas, SMS, datos, roaming)\n"
                + "  • Contratos de servicio asociados\n"
                + "  • Historial de planes\n"
                + "  • Facturas vinculadas a este número\n\n"
                + "¿Desea continuar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            lineaDAO.delete(numTel);
            JOptionPane.showMessageDialog(this, "Línea eliminada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, "Eliminar línea");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Eliminar línea");
        }
    }

    private LineaMovil buildFromForm() {
        String num = txtNumTel.getText().trim();
        String fecha = txtFechaActivacion.getText().trim();
        Cliente cli = (Cliente) cmbCliente.getSelectedItem();
        if (num.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Número telefónico y fecha de activación son obligatorios.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "⚠ La fecha debe tener formato YYYY-MM-DD.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new LineaMovil(num, cli != null ? cli.getCedula() : "",
                (String) cmbTipoLinea.getSelectedItem(),
                (String) cmbTecnologia.getSelectedItem(),
                fecha,
                (String) cmbEstado.getSelectedItem(),
                (String) cmbTipoSIM.getSelectedItem());
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
