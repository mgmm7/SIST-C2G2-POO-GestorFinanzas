package pe.edu.upeu.gestorfinanciero.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Controller
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;

    @FXML
    public void login(ActionEvent e) {
        try {
            // Cargar la ventana principal (maingui.fxml)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/main.fxml"));
            loader.setControllerFactory(pe.edu.upeu.gestorfinanciero.GestorFinancieroApplication.getContext()::getBean);
            Parent root = loader.load();

            // Cambiar la escena actual por la principal
            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestor Financiero");
            stage.show();

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void cerrar(ActionEvent e) {
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        stage.close();
    }
}