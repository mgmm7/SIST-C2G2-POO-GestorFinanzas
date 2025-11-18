package pe.edu.upeu.gestorfinanciero.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.GestorFinancieroApplication;
import pe.edu.upeu.gestorfinanciero.service.UsuarioService;

@Controller
public class RegistroController {

    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    @FXML private PasswordField txtPassword;

    private final UsuarioService usuarioService;

    public RegistroController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @FXML
    public void registrar(ActionEvent event) {
        String username = txtUsername.getText();
        String email = txtEmail.getText();
        String password = txtPassword.getText();

        try {
            usuarioService.registrar(username, email, password);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Cuenta creada");
            alert.setHeaderText(null);
            alert.setContentText("Cuenta creada correctamente. Serás redirigido al inicio de sesión.");
            alert.showAndWait();

            // Redirigir al login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loader.setControllerFactory(GestorFinancieroApplication.getContext()::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (IllegalArgumentException iae) {
            mostrarAlerta(Alert.AlertType.WARNING, "Datos inválidos", iae.getMessage());
        } catch (RuntimeException re) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", re.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error inesperado.");
        }
    }

    @FXML
    public void volver(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loader.setControllerFactory(GestorFinancieroApplication.getContext()::getBean);
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
