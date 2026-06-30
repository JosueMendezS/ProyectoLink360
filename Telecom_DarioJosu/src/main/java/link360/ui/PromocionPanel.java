package link360.ui;

import link360.dao.PromocionDAO;
import link360.model.Promocion;
import link360.util.ErrorHandler;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * CRUD panel for the Promocion table. IdTipoPromo FK resolved via JComboBox.
 *
 * @author Link360 Project
 */
public class PromocionPanel extends JPanel {

    private final PromocionDAO promoDAO = new PromocionDAO();

    private JTextField txtCodPromo, txtNombre, txtDescripcion,
            txtFechaInicio, txtFechaFin, txtPctDescuento;
    private JComboBox<String> cmbTipoPromo;

    private JTable table;
    private DefaultTableModel tableModel;
    private boolean editMode = false;

    private static final String[] TIPOS_PROMO = {
        "1 – Promoción por volumen", "2 – Promoción de temporada"
    };

    public PromocionPanel() {
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
        p.setBorder(BorderFactory.createTitledBorder("Datos de la Promoción"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCodPromo = new JTextField(10);
        txtNombre = new JTextField(20);
        txtDescripcion = new JTextField(30);
        txtFechaInicio = new JTextField(10);
        txtFechaFin = new JTextField(10);
        txtPctDescuento = new JTextField(8);
        cmbTipoPromo = new JComboBox<>(TIPOS_PROMO);

        addField(p, gbc, "Código *", txtCodPromo, 0, 0);
        addField(p, gbc, "Nombre *", txtNombre, 0, 2);
        addField(p, gbc, "Tipo Promo *", cmbTipoPromo, 0, 4);
        addField(p, gbc, "Fecha Inicio *\n(YYYY-MM-DD)", txtFechaInicio, 1, 0);
        addField(p, gbc, "Fecha Fin\n(YYYY-MM-DD, opcional)", txtFechaFin, 1, 2);
        addField(p, gbc, "% Descuento *", txtPctDescuento, 1, 4);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        p.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.gridwidth = 5;
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
                new String[]{"Código", "Nombre", "Descripción", "Inicio", "Fin", "% Descuento", "Tipo Promo"}, 0) {
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

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            for (Promocion pr : promoDAO.findAll()) {
                tableModel.addRow(new Object[]{pr.getCodPromocion(), pr.getNombre(),
                    pr.getDescripcion(), pr.getFechaInicio(), pr.getFechaFin(),
                    pr.getPctDescuento(), pr.getIdTipoPromo()});
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar promociones");
        }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtCodPromo.setText(str(tableModel.getValueAt(row, 0)));
        txtNombre.setText(str(tableModel.getValueAt(row, 1)));
        txtDescripcion.setText(str(tableModel.getValueAt(row, 2)));
        txtFechaInicio.setText(str(tableModel.getValueAt(row, 3)));
        Object ff = tableModel.getValueAt(row, 4);
        txtFechaFin.setText(ff != null ? ff.toString() : "");
        txtPctDescuento.setText(str(tableModel.getValueAt(row, 5)));
        int tipo = (int) tableModel.getValueAt(row, 6);
        cmbTipoPromo.setSelectedIndex(tipo - 1);
        txtCodPromo.setEditable(false);
        editMode = true;
    }

    private void clearForm() {
        txtCodPromo.setText("");
        txtNombre.setText("");
        txtDescripcion.setText("");
        txtFechaInicio.setText("");
        txtFechaFin.setText("");
        txtPctDescuento.setText("");
        cmbTipoPromo.setSelectedIndex(0);
        txtCodPromo.setEditable(true);
        editMode = false;
        table.clearSelection();
    }

    private void enableNewMode() {
        clearForm();
        txtCodPromo.requestFocus();
    }

    private void saveRecord() {
        Promocion pr = buildFromForm();
        if (pr == null) {
            return;
        }
        try {
            if (editMode) {
                promoDAO.update(pr);
                JOptionPane.showMessageDialog(this, "Promoción actualizada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                promoDAO.insert(pr);
                JOptionPane.showMessageDialog(this, "Promoción registrada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, editMode ? "Actualizar promoción" : "Registrar promoción");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Guardar promoción");
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione una promoción para eliminar.", "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cod = str(tableModel.getValueAt(row, 0));
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar la promoción '" + cod + "'?\n"
                + "Las incompatibilidades asociadas también serán eliminadas.",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            promoDAO.delete(cod);
            JOptionPane.showMessageDialog(this, "Promoción eliminada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, "Eliminar promoción");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Eliminar promoción");
        }
    }

    private Promocion buildFromForm() {
        String cod = txtCodPromo.getText().trim();
        String nom = txtNombre.getText().trim();
        String fi = txtFechaInicio.getText().trim();
        String ff = txtFechaFin.getText().trim();
        String pctS = txtPctDescuento.getText().trim();
        if (cod.isEmpty() || nom.isEmpty() || fi.isEmpty() || pctS.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠ Todos los campos marcados con * son obligatorios.", "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        LocalDate ldFi;
        try {
            ldFi = LocalDate.parse(fi);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "⚠ La Fecha de Inicio no es válida. Use formato YYYY-MM-DD.",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String ffFinal = null;
        if (!ff.isEmpty()) {
            try {
                LocalDate ldFf = LocalDate.parse(ff);
                if (ldFf.isBefore(ldFi)) {
                    JOptionPane.showMessageDialog(this,
                            "⚠ La Fecha de Fin no puede ser anterior a la Fecha de Inicio.",
                            "Fechas inválidas", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
                ffFinal = ff;
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "⚠ La Fecha de Fin no es válida. Use YYYY-MM-DD o déjela vacía.",
                        "Fecha inválida", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        }
        try {
            double pct = Double.parseDouble(pctS);
            if (pct < 0 || pct > 100) {
                JOptionPane.showMessageDialog(this, "⚠ El porcentaje debe estar entre 0 y 100.", "Valor inválido", JOptionPane.WARNING_MESSAGE);
                return null;
            }
            int tipo = cmbTipoPromo.getSelectedIndex() + 1;
            return new Promocion(cod, nom, txtDescripcion.getText().trim(), fi, ffFinal, pct, tipo);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "⚠ El porcentaje debe ser un número válido.", "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
