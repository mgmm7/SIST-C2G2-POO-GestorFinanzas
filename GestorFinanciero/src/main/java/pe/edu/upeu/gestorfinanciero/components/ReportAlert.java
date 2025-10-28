package pe.edu.upeu.gestorfinanciero.components;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class ReportAlert {

    private final JasperPrint jasperPrint;

    public ReportAlert(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
    }

    public void show() {
        try {
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle("Visualizar Reporte");
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}