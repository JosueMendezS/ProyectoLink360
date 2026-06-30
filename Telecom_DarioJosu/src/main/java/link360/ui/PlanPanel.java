package link360.ui;

import link360.dao.PlanTarifarioDAO;
import link360.model.PlanTarifario;
import link360.util.ErrorHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * CRUD panel for the Plan_Tarifario table. IdCateg FK is chosen via a JComboBox
 * showing category descriptions.
 *
 * @author Link360 Project
 */
public class PlanPanel extends JPanel {

    private final PlanTarifarioDAO planDAO = new PlanTarifarioDAO();

    private JTextField txtCodPlan, txtNombre, txtDescripcion, txtCuota,
            txtGb, txtMin, txtMsg, txtCostoExceso;
    private JComboBox<String> cmbCateg;  // values: "1-Basico", "2-Premium"

    private JTable table;
    private DefaultTableModel tableModel;
    private boolean editMode = false;

    // Category map (id -> description). Matches Categ_Plan seed data.
    private static final Object[][] CATEGORIAS = {
        {1, "Plan Básico (50 Mbps)"},
        {2, "Plan Premium (200 Mbps)"}
    };

    public PlanPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        loadTable();
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Datos del Plan Tarifario"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodPlan = new JTextField(8);
        txtNombre = new JTextField(18);
        txtDescripcion = new JTextField(28);
        txtCuota = new JTextField(10);
        txtGb = new JTextField(6);
        txtMin = new JTextField(6);
        txtMsg = new JTextField(6);
        txtCostoExceso = new JTextField(8);
        cmbCateg = new JComboBox<>(new String[]{"1 – Plan Básico (50 Mbps)", "2 – Plan Premium (200 Mbps)"});

        addField(p, gbc, "Código *", txtCodPlan, 0, 0);
        addField(p, gbc, "Nombre *", txtNombre, 0, 2);
        addField(p, gbc, "Categoría *", cmbCateg, 0, 4);
        addField(p, gbc, "Cuota Mensual (₡) *", txtCuota, 1, 0);
        addField(p, gbc, "GB Incluidos", txtGb, 1, 2);
        addField(p, gbc, "Min Incluidos", txtMin, 1, 4);
        addField(p, gbc, "MSG Incluidos", txtMsg, 2, 0);
        addField(p, gbc, "Costo Exceso", txtCostoExceso, 2, 2);
        gbc.gridx = 4;
        gbc.gridy = 2;
        gbc.weightx = 0;
        p.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 5;
        gbc.weightx = 1;
        p.add(txtDescripcion, gbc);
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
                new String[]{"Código", "Nombre", "Descripción", "Cuota (₡)", "GB", "Min", "MSG", "Costo Exceso", "Categ"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
       headerRenderer.setBackground(new Color(41, 128, 185)); 
        headerRenderer.setForeground(Color.WHITE);             
        headerRenderer.setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD)); 

        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        table.getTableHeader().setDefaultRenderer(headerRenderer);
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
        JButton btnNew = styledBtn("➕ Nuevo", new Color(39, 174, 96));
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

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            for (PlanTarifario pt : planDAO.findAll()) {
                tableModel.addRow(new Object[]{pt.getCodPlan(), pt.getNombre(), pt.getDescripcion(),
                    pt.getCuotaMensual(), pt.getGbIncluidos(), pt.getMinIncluidos(),
                    pt.getMsgIncluidos(), pt.getCostoExceso(), pt.getIdCateg()});
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar planes");
        }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtCodPlan.setText(str(tableModel.getValueAt(row, 0)));
        txtNombre.setText(str(tableModel.getValueAt(row, 1)));
        txtDescripcion.setText(str(tableModel.getValueAt(row, 2)));
        txtCuota.setText(str(tableModel.getValueAt(row, 3)));
        txtGb.setText(str(tableModel.getValueAt(row, 4)));
        txtMin.setText(str(tableModel.getValueAt(row, 5)));
        txtMsg.setText(str(tableModel.getValueAt(row, 6)));
        txtCostoExceso.setText(str(tableModel.getValueAt(row, 7)));
        int categ = (int) tableModel.getValueAt(row, 8);
        cmbCateg.setSelectedIndex(categ - 1);
        txtCodPlan.setEditable(false);
        editMode = true;
    }

    private void clearForm() {
        txtCodPlan.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCuota.setText("");
        txtGb.setText("0");
        txtMin.setText("0");
        txtMsg.setText("0");
        txtCostoExceso.setText("0");
        cmbCateg.setSelectedIndex(0);
        txtCodPlan.setEditable(true);
        editMode = false;
        table.clearSelection();
    }

    private void enableNewMode() {
        clearForm();
        txtCodPlan.requestFocus();
    }

    private void saveRecord() {
        PlanTarifario pt = buildFromForm();
        if (pt == null) {
            return;
        }
        try {
            if (editMode) {
                planDAO.update(pt);
                JOptionPane.showMessageDialog(this, "Plan actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                planDAO.insert(pt);
                JOptionPane.showMessageDialog(this, "Plan registrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, editMode ? "Actualizar plan" : "Registrar plan");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Guardar plan");
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un plan para eliminar.", "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cod = str(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ Al eliminar el plan '" + cod + "', el historial\nde líneas que usaron este plan puede verse afectado.\n\n¿Desea continuar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            planDAO.delete(cod);
            JOptionPane.showMessageDialog(this, "Plan eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, "Eliminar plan");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Eliminar plan");
        }
    }

    private PlanTarifario buildFromForm() {
        String cod = txtCodPlan.getText().trim();
        String nom = txtNombre.getText().trim();
        String cuotaStr = txtCuota.getText().trim();
        if (cod.isEmpty() || nom.isEmpty() || cuotaStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Código, Nombre y Cuota son obligatorios.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            double cuota = Double.parseDouble(cuotaStr);
            int gb = txtGb.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtGb.getText().trim());
            int min = txtMin.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtMin.getText().trim());
            int msg = txtMsg.getText().trim().isEmpty() ? 0 : Integer.parseInt(txtMsg.getText().trim());
            double exc = txtCostoExceso.getText().trim().isEmpty() ? 0 : Double.parseDouble(txtCostoExceso.getText().trim());
            int categ = cmbCateg.getSelectedIndex() + 1;
            return new PlanTarifario(cod, nom, txtDescripcion.getText().trim(), cuota, gb, min, msg, exc, categ);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠ Los campos numéricos deben contener solo números.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
