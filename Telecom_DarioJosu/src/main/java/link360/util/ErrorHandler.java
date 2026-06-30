package link360.util;

import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ErrorHandler {

    private static final int ERR_UNIQUE_VIOLATION = 2627;  // PK / UNIQUE constraint
    private static final int ERR_UNIQUE_VIOLATION_ALT = 2601;  // duplicate key
    private static final int ERR_FK_VIOLATION = 547;   // FK / reference constraint
    private static final int ERR_NULL_VIOLATION = 515;   // NOT NULL constraint
    private static final int ERR_CHECK_VIOLATION = 547;   // CHECK constraint (same as FK in SS)
    private static final int ERR_CHECK_CONSTRAINT = 8115;  // arithmetic / overflow

    public static void handle(java.awt.Component parent, SQLException e, String context) {
        String userMessage;
        String logMessage = "[ErrorHandler] SQL error " + e.getErrorCode()
                + " during '" + context + "': " + e.getMessage();
        System.err.println(logMessage);

        int code = e.getErrorCode();

        if (code == ERR_UNIQUE_VIOLATION || code == ERR_UNIQUE_VIOLATION_ALT) {
            userMessage = "Error de integridad: ya existe un registro con esa llave primaria o valor único.\n"
                    + "Verifique que el identificador ingresado no esté duplicado.";
        } else if (code == ERR_FK_VIOLATION) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("delete") || msg.contains("reference")) {
                userMessage = "No se puede eliminar este registro porque otros registros dependen de él.\n"
                        + "Primero elimine o reasigne los registros relacionados.";
            } else {
                userMessage = "Error de llave foránea: el valor ingresado no existe en la tabla relacionada.\n"
                        + "Seleccione un valor válido del listado.";
            }
        } else if (code == ERR_NULL_VIOLATION) {
            userMessage = "Campo obligatorio vacío: uno o más campos requeridos no contienen valor.\n"
                    + "Complete todos los campos marcados como obligatorios.";
        } else if (e.getMessage() != null && e.getMessage().contains("CHECK constraint")) {
            userMessage = "Valor no permitido: un campo contiene un dato fuera del rango o formato aceptado.\n"
                    + "Revise los valores ingresados (ej.: estado, tipo, porcentaje).";
        } else {
            userMessage = "Error de base de datos durante: " + context + ".\n"
                    + "Detalle técnico: " + e.getMessage();
        }

        JOptionPane.showMessageDialog(
                parent,
                userMessage,
                "Error — " + context,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void handle(java.awt.Component parent, Exception e, String context) {
        System.err.println("[ErrorHandler] Exception during '" + context + "': " + e.getMessage());
        JOptionPane.showMessageDialog(
                parent,
                "Error inesperado durante: " + context + ".\nDetalle: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }
}
