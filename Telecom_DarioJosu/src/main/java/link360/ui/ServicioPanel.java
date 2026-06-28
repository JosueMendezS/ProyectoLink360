package link360.ui;

import link360.dao.ServicioDAO;
import link360.model.Servicio;
import link360.util.ErrorHandler;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

/**
 * CRUD panel for the Servicio table.
 *
 * @author Link360 Project
 */
public class ServicioPanel extends JPanel {

    private final ServicioDAO servicioDAO = new ServicioDAO();

    private JTextField txtCodServicio, txtNombre, txtDescripcion, txtCosto;
    private JComboBox<String> cmbCategoria;

    private JTable table;
    private DefaultTableModel tableModel;
    private boolean editMode = false;

    public ServicioPanel() {
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
        p.setBorder(BorderFactory.createTitledBorder("Datos del Servicio"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodServicio = new JTextField(10);
        txtNombre = new JTextField(20);
        txtDescripcion = new JTextField(30);
        txtCosto = new JTextField(10);
        cmbCategoria = new JComboBox<>(new String[]{"Voz", "Datos", "Seguridad", "Entretenimiento"});

        addField(p, gbc, "Código *", txtCodServicio, 0, 0);
        addField(p, gbc, "Nombre *", txtNombre, 0, 2);
        addField(p, gbc, "Categoría *", cmbCategoria, 0, 4);
        addField(p, gbc, "Costo Mensual (₡) *", txtCosto, 1, 0);
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.weightx = 0;
        p.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 3;
        gbc.weightx = 1;
        gbc.gridwidth = 3;
        p.add(txtDescripcion, gbc);
        gbc.gridwidth = 1;
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
                new String[]{"Código", "Nombre", "Descripción", "Costo Mensual (₡)", "Categoría"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.getTableHeader().setBackground(new Color(211, 84, 0));
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
            for (Servicio s : servicioDAO.findAll()) {
                tableModel.addRow(new Object[]{s.getCodServicio(), s.getNombre(),
                    s.getDescripcion(), s.getCostoMensual(), s.getCategoria()});
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar servicios");
        }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtCodServicio.setText(str(tableModel.getValueAt(row, 0)));
        txtNombre.setText(str(tableModel.getValueAt(row, 1)));
        txtDescripcion.setText(str(tableModel.getValueAt(row, 2)));
        txtCosto.setText(str(tableModel.getValueAt(row, 3)));
        cmbCategoria.setSelectedItem(tableModel.getValueAt(row, 4));
        txtCodServicio.setEditable(false);
        editMode = true;
    }

    private void clearForm() {
        txtCodServicio.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtCosto.setText("");
        cmbCategoria.setSelectedIndex(0);
        txtCodServicio.setEditable(true);
        editMode = false;
        table.clearSelection();
    }

    private void enableNewMode() {
        clearForm();
        txtCodServicio.requestFocus();
    }

    private void saveRecord() {
        Servicio s = buildFromForm();
        if (s == null) {
            return;
        }
        try {
            if (editMode) {
                servicioDAO.update(s);
                JOptionPane.showMessageDialog(this, "Servicio actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                servicioDAO.insert(s);
                JOptionPane.showMessageDialog(this, "Servicio registrado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, editMode ? "Actualizar servicio" : "Registrar servicio");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Guardar servicio");
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un servicio para eliminar.", "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cod = str(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ Al eliminar el servicio '" + cod + "', se eliminarán\ntambién los contratos asociados a líneas móviles.\n\n¿Desea continuar?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            servicioDAO.delete(cod);
            JOptionPane.showMessageDialog(this, "Servicio eliminado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, "Eliminar servicio");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Eliminar servicio");
        }
    }

    private Servicio buildFromForm() {
        String cod = txtCodServicio.getText().trim();
        String nom = txtNombre.getText().trim();
        String desc = txtDescripcion.getText().trim();
        String costoStr = txtCosto.getText().trim();
        if (cod.isEmpty() || nom.isEmpty() || costoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Código, Nombre y Costo son obligatorios.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        double costo;
        try {
            costo = Double.parseDouble(costoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠ El costo debe ser un número válido (ej: 3500.00)", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new Servicio(cod, nom, desc, costo, (String) cmbCategoria.getSelectedItem());
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
