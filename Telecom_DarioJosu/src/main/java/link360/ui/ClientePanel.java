package link360.ui;

import link360.dao.ClienteDAO;
import link360.dao.DistritoDAO;
import link360.model.Cliente;
import link360.model.Distrito;
import link360.util.ErrorHandler;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class ClientePanel extends JPanel {

    private final ClienteDAO clienteDAO = new ClienteDAO();
    private final DistritoDAO distritoDAO = new DistritoDAO();

    private JTextField txtCedula, txtNombre, txtApellidos, txtFechaIngreso, txtDireccion;
    private JComboBox<String> cmbTipoCliente;
    private JComboBox<Distrito> cmbDistrito;

    private JTable table;
    private DefaultTableModel tableModel;

    private boolean editMode = false;

    public ClientePanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 247, 250));
        add(buildForm(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        add(buildButtons(), BorderLayout.SOUTH);
        loadDistritos();
        loadTable();
    }

    private JPanel buildForm() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createTitledBorder("Datos del Cliente"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 8, 5, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtCedula = new JTextField(12);
        txtNombre = new JTextField(18);
        txtApellidos = new JTextField(18);
        txtFechaIngreso = new JTextField(10);
        txtDireccion = new JTextField(30);
        cmbTipoCliente = new JComboBox<>(new String[]{"Bronce", "Plata", "Oro", "Platino"});
        cmbDistrito = new JComboBox<>();

        addField(p, gbc, "Cédula *", txtCedula, 0, 0);
        addField(p, gbc, "Nombre *", txtNombre, 0, 2);
        addField(p, gbc, "Apellidos *", txtApellidos, 0, 4);
        addField(p, gbc, "Fecha Ingreso *\n(YYYY-MM-DD)", txtFechaIngreso, 1, 0);
        addField(p, gbc, "Tipo Cliente *", cmbTipoCliente, 1, 2);
        addField(p, gbc, "Distrito *", cmbDistrito, 1, 4);
        gbc.gridx = 0;
        gbc.gridy = 2;
        p.add(new JLabel("Dirección:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 5;
        p.add(txtDireccion, gbc);
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
                new String[]{"Cédula", "Nombre", "Apellidos", "Fecha Ingreso",
                    "Tipo", "Dirección", "Cód Distrito"}, 0) {
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

    private void loadDistritos() {
        try {
            List<Distrito> distritos = distritoDAO.findAll();
            cmbDistrito.removeAllItems();
            for (Distrito d : distritos) {
                cmbDistrito.addItem(d);
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar distritos");
        }
    }

    private void loadTable() {
        try {
            tableModel.setRowCount(0);
            List<Cliente> clientes = clienteDAO.findAll();
            for (Cliente c : clientes) {
                tableModel.addRow(new Object[]{
                    c.getCedula(), c.getNombre(), c.getApellidos(),
                    c.getFechaIngreso(), c.getTipoCliente(),
                    c.getDireccion(), c.getCodDistrito()
                });
            }
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Cargar clientes");
        }
    }

    private void populateFormFromTable() {
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        txtCedula.setText(str(tableModel.getValueAt(row, 0)));
        txtNombre.setText(str(tableModel.getValueAt(row, 1)));
        txtApellidos.setText(str(tableModel.getValueAt(row, 2)));
        txtFechaIngreso.setText(str(tableModel.getValueAt(row, 3)));
        cmbTipoCliente.setSelectedItem(str(tableModel.getValueAt(row, 4)));
        txtDireccion.setText(str(tableModel.getValueAt(row, 5)));
        int codDist = (int) tableModel.getValueAt(row, 6);
        for (int i = 0; i < cmbDistrito.getItemCount(); i++) {
            if (cmbDistrito.getItemAt(i).getCodDistrito() == codDist) {
                cmbDistrito.setSelectedIndex(i);
                break;
            }
        }
        txtCedula.setEditable(false);
        editMode = true;
    }

    private void clearForm() {
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellidos.setText("");
        txtFechaIngreso.setText("");
        txtDireccion.setText("");
        cmbTipoCliente.setSelectedIndex(0);
        if (cmbDistrito.getItemCount() > 0) {
            cmbDistrito.setSelectedIndex(0);
        }
        txtCedula.setEditable(true);
        editMode = false;
        table.clearSelection();
    }

    private void enableNewMode() {
        clearForm();
        txtCedula.requestFocus();
    }

    private void saveRecord() {
        Cliente c = buildClienteFromForm();
        if (c == null) {
            return;
        }
        try {
            if (editMode) {
                clienteDAO.update(c);
                JOptionPane.showMessageDialog(this, "Cliente actualizado exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                clienteDAO.insert(c);
                JOptionPane.showMessageDialog(this, "Cliente registrado exitosamente.",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, editMode ? "Actualizar cliente" : "Registrar cliente");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Guardar cliente");
        }
    }

    private void deleteRecord() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un cliente de la tabla para eliminar.",
                    "Selección requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String cedula = str(tableModel.getValueAt(row, 0));
        String nombre = str(tableModel.getValueAt(row, 1)) + " " + str(tableModel.getValueAt(row, 2));

        int confirm = JOptionPane.showConfirmDialog(this,
                "⚠ ADVERTENCIA DE BORRADO EN CASCADA\n\n"
                + "Está por eliminar al cliente: " + nombre + " (" + cedula + ")\n\n"
                + "Esta acción también afectará:\n"
                + "  • Sus líneas móviles registradas\n"
                + "  • Números de contacto y correos electrónicos\n"
                + "  • Historial de puntos asociados\n\n"
                + "¿Desea continuar con la eliminación?",
                "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            clienteDAO.delete(cedula);
            JOptionPane.showMessageDialog(this, "Cliente eliminado.", "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadTable();
        } catch (SQLException ex) {
            ErrorHandler.handle(this, ex, "Eliminar cliente");
        } catch (Exception ex) {
            ErrorHandler.handle(this, ex, "Eliminar cliente");
        }
    }

    private Cliente buildClienteFromForm() {
        String cedula = txtCedula.getText().trim();
        String nombre = txtNombre.getText().trim();
        String apellidos = txtApellidos.getText().trim();
        String fecha = txtFechaIngreso.getText().trim();
        String tipo = (String) cmbTipoCliente.getSelectedItem();
        String dir = txtDireccion.getText().trim();
        Distrito dist = (Distrito) cmbDistrito.getSelectedItem();

        if (cedula.isEmpty() || nombre.isEmpty() || apellidos.isEmpty() || fecha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "⚠ Los campos Cédula, Nombre, Apellidos y Fecha de Ingreso son obligatorios.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!fecha.matches("\\d{4}-\\d{2}-\\d{2}")) {
            JOptionPane.showMessageDialog(this,
                    "⚠ La fecha debe tener el formato YYYY-MM-DD (ejemplo: 2025-06-15).",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        try {
            LocalDate.parse(fecha);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "⚠ La fecha ingresada no es válida (por ejemplo 2025-02-30 no existe).",
                    "Fecha inválida", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (dist == null) {
            JOptionPane.showMessageDialog(this, "⚠ Seleccione un distrito.",
                    "Campos requeridos", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        if (!cedula.matches("\\d+")) {
            JOptionPane.showMessageDialog(this,
                    "⚠ La cédula debe contener solo dígitos numéricos (sin guiones ni espacios).",
                    "Formato inválido", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        return new Cliente(cedula, nombre, apellidos, fecha, tipo,
                dir.isEmpty() ? null : dir, dist.getCodDistrito());

    }

    private String str(Object o) {
        return o == null ? "" : o.toString();
    }
}
