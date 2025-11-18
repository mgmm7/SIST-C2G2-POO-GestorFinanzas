package pe.edu.upeu.gestorfinanciero.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class MainController {

    @FXML private BorderPane mainPane;
    @FXML private AnchorPane contenidoCentral;

    @FXML private Label lblUsuario;
    @FXML private ImageView imgAvatar;

    private final ApplicationContext context;
    private final UsuarioSesion usuarioSesion;

    private void cargarVista(String nombreFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + nombreFXML));
            loader.setControllerFactory(context::getBean);
            Parent vista = loader.load();
            mainPane.setCenter(vista);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------- BOTONES DEL MENÚ -------------
    public void abrirUsuario(ActionEvent e){ cargarVista("usuario.fxml"); }
    public void abrirIngresos(ActionEvent e){ cargarVista("ingreso.fxml"); }
    public void abrirEgresos(ActionEvent e){ cargarVista("egreso.fxml"); }
    public void abrirMovimientos(ActionEvent e){ cargarVista("movimiento.fxml"); }
    public void abrirReporte(ActionEvent e){ cargarVista("reporte.fxml"); }
    public void abrirMeta(ActionEvent e){ cargarVista("meta.fxml"); }
    public void abrirConfiguracion(ActionEvent e){ cargarVista("configuracion.fxml"); }

    // ----------- CERRAR SESIÓN ----------------
    public void cerrarSesion(ActionEvent event) {
        usuarioSesion.logout(); // limpia el usuario actual

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/login.fxml"));
            loader.setControllerFactory(context::getBean);
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.setTitle("Iniciar Sesión");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    @FXML
    public void initialize() {

        // 1. Mostrar nombre del usuario logueado
        if (usuarioSesion.getUsuarioActual() != null) {
            lblUsuario.setText(usuarioSesion.getUsuarioActual().getUsername());
        }

        // 2. Avatar por defecto
        try {
            Image avatar = new Image(getClass().getResourceAsStream("/img/avatar.png"));
            imgAvatar.setImage(avatar);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el avatar por defecto");
        }

        // 3. Hacer el avatar circular
        imgAvatar.setFitWidth(70);
        imgAvatar.setFitHeight(70);

        javafx.scene.shape.Circle clip = new javafx.scene.shape.Circle(35, 35, 35);
        imgAvatar.setClip(clip);
    }
    public void actualizarNombre() {
        if (usuarioSesion.getUsuarioActual() != null) {
            lblUsuario.setText(usuarioSesion.getUsuarioActual().getUsername());
        }
    }



}
