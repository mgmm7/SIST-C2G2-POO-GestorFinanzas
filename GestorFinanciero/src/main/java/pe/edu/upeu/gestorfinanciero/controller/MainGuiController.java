package pe.edu.upeu.gestorfinanciero.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Controller
public class MainGuiController implements Initializable {

    @FXML
    private BorderPane bp;

    @FXML
    private MenuBar menuBarFx;

    @FXML
    private TabPane tabPaneFx;

    @Autowired
    private ApplicationContext context;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        MenuPrincipal();
    }
    private void MenuPrincipal() {
        Menu menuPrincipal = new Menu("Principal");

        MenuItem Usuario = new MenuItem("Usuario");
        Usuario.setOnAction(e -> abrirPestaña("usuario", "/view/main_editar.fxml","Gestión de Usuarios"));

    }

    private void abrirPestaña(String id, String rutaFXML, String titulo) {
        // Si la pestaña ya está abierta, solo seleccionarla
        for (Tab tab : tabPaneFx.getTabs()) {
            if (tab.getId() != null && tab.getId().equals(id)) {
                tabPaneFx.getSelectionModel().select(tab);
                return;
            }
        }

        Tab nueva = new Tab(titulo);
        nueva.setId(id);
        nueva.setClosable(true);

        if (rutaFXML != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                loader.setControllerFactory(context::getBean);
                nueva.setContent(loader.load());
            } catch (IOException ex) {
                nueva.setContent(new StackPane(new Label(" Error al cargar: " + rutaFXML)));
                ex.printStackTrace();
            }
        } else {
            nueva.setContent(new StackPane(new Label(" Bienvenido al Gestor Financiero")));
        }

        tabPaneFx.getTabs().add(nueva);
        tabPaneFx.getSelectionModel().select(nueva);
    }

    @FXML
    private void cerrarSesion() {
        try {
            Stage stage = (Stage) bp.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loader.setControllerFactory(context::getBean);

            Scene nuevaEscena = new Scene(loader.load());
            stage.setScene(nuevaEscena);

        } catch (IOException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al regresar al login").showAndWait();
        }
    }

    @FXML
    private void salir() {
        Stage stage = (Stage) bp.getScene().getWindow();
        stage.close();
        javafx.application.Platform.exit();
        System.exit(0);
    }

    @FXML
    private void abrirUsuario() {

    }
    @FXML
    private void abrirGestionUsuarios() {
        abrirPestaña("usuarios", "/view/main_editar.fxml", "Gestión de Usuarios varios");
    }


}
