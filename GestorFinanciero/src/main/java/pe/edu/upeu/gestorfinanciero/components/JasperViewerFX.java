package pe.edu.upeu.gestorfinanciero.components;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;

public class JasperViewerFX {

    public void viewReport(String title, JasperPrint jasperPrint) {
        try {
            JasperViewer viewer = new JasperViewer(jasperPrint, false);
            viewer.setTitle(title);
            viewer.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}