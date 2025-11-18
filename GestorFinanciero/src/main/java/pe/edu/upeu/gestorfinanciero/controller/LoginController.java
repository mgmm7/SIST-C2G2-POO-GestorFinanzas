package pe.edu.upeu.gestorfinanciero.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.GestorFinancieroApplication;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.UsuarioService;

@Controller
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtClave;

    private final UsuarioService usuarioService;
    private final UsuarioSesion usuarioSesion;

    public LoginController(UsuarioService usuarioService, UsuarioSesion usuarioSesion) {
        this.usuarioService = usuarioService;
        this.usuarioSesion = usuarioSesion;
    }


    @FXML
    public void login(ActionEvent event) {
        String username = txtUsuario.getText().trim();
        String password = txtClave.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos",
                    "Debe ingresar un usuario y una contraseña.");
            return;
        }

        try {
            Usuario u = usuarioService.login(username, password);

            // Guardar usuario en sesión
            usuarioSesion.setUsuarioActual(u);

            // Redirigir a Main
            cargarVista(event, "/view/main.fxml", "Gestor Financiero");

        } catch (Exception ex) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación", ex.getMessage());
        }
    }


    @FXML
    public void irRegistro(ActionEvent event) {
        cargarVista(event, "/view/registro.fxml", "Crear cuenta");
    }


    private void cargarVista(ActionEvent event, String ruta, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ruta));
            loader.setControllerFactory(GestorFinancieroApplication.getContext()::getBean);
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(titulo);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR,
                    "Error interno", "No se pudo cargar la vista solicitada.");
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
