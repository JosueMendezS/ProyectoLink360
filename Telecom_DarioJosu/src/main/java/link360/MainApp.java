package link360;

import link360.ui.ClientePanel;
import link360.ui.ConsultasPanel;
import link360.ui.FacturaPanel;
import link360.ui.LineaMovilPanel;
import link360.ui.PlanPanel;
import link360.ui.PromocionPanel;
import link360.ui.ServicioPanel;

import javax.swing.*;
import java.awt.*;

public class MainApp {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("No se pudo cargar el look and feel del sistema: " + e.getMessage());
        }

        SwingUtilities.invokeLater(MainApp::buildAndShow);
    }

    private static void buildAndShow() {
        JFrame frame = new JFrame("Link360 Telecom - Gestion de Base de Datos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Clientes", new ClientePanel());
        tabs.addTab("Lineas Moviles", new LineaMovilPanel());
        tabs.addTab("Planes", new PlanPanel());
        tabs.addTab("Servicios", new ServicioPanel());
        tabs.addTab("Promociones", new PromocionPanel());
        tabs.addTab("Facturas", new FacturaPanel());
        tabs.addTab("Consultas", new ConsultasPanel());

        frame.add(tabs, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
