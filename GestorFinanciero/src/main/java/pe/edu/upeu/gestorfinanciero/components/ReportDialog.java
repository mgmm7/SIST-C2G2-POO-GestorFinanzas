package pe.edu.upeu.gestorfinanciero.components;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.awt.*;

public class ReportDialog {

    private final JasperPrint jasperPrint;

    public ReportDialog(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    public void show() {
        SwingUtilities.invokeLater(() -> {
            try {
                JasperViewer viewer = new JasperViewer(jasperPrint, false);
                viewer.setTitle("Visualizar Reporte");

                JFrame frame = (JFrame) viewer.getComponent(0).getParent().getParent().getParent();
                frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}