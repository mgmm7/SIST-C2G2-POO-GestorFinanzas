package pe.edu.upeu.gestorfinanciero.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class MainController {

    @FXML private BorderPane mainPane;
    @FXML private AnchorPane contenidoCentral;
    private final ApplicationContext context;

    public MainController(ApplicationContext context) {
        this.context = context;
    }

    private void cargarVista(String nombreFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + nombreFXML));
            loader.setControllerFactory(context::getBean);
            AnchorPane vista = loader.load();
            mainPane.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void abrirUsuario(ActionEvent e){ cargarVista("usuario.fxml"); }
    public void abrirIngresos(ActionEvent e){ cargarVista("ingreso.fxml"); }
    public void abrirEgresos(ActionEvent e){ cargarVista("egreso.fxml"); }
    public void abrirMovimientos(ActionEvent e){ cargarVista("movimiento.fxml"); }
    public void abrirReporte(ActionEvent e){ cargarVista("reporte.fxml"); }
    public void abrirMeta(ActionEvent e){ cargarVista("meta.fxml"); }
    public void abrirConfiguracion(ActionEvent e){ cargarVista("configuracion.fxml"); }
}