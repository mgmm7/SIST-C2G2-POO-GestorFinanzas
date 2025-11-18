package pe.edu.upeu.gestorfinanciero.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.repository.UsuarioRepository;

@Controller
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioSesion usuarioSesion;
    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;

    @FXML private PasswordField txtPassActual;
    @FXML private PasswordField txtPassNueva;
    @FXML private PasswordField txtPassConfirm;

    @FXML private Button btnGuardarNombre;
    @FXML private Button btnGuardarCorreo;
    @FXML private Button btnGuardarPass;

    @FXML private Label iconPassActual;
    @FXML private Label iconConfirm;

    private Usuario user;

    @FXML
    public void initialize() {
        try {
            // Cargar la entidad JPA desde BD para que esté "managed"
            Long id = usuarioSesion.getUsuarioActual().getId();
            user = usuarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado en BD"));

            txtNombre.setText(user.getUsername());
            txtCorreo.setText(user.getEmail());

            // Detectar cambios en nombre
            txtNombre.textProperty().addListener((obs, old, nv) -> {
                boolean disabled = nv.trim().isEmpty() || nv.equals(user.getUsername());
                btnGuardarNombre.setDisable(disabled);
            });

            // Detectar cambios en correo
            txtCorreo.textProperty().addListener((obs, old, nv) -> {
                boolean disabled = nv.equals(user.getEmail());
                btnGuardarCorreo.setDisable(disabled);
            });

            // Validaciones de contraseña en vivo
            txtPassActual.textProperty().addListener((obs, old, nv) -> validarPassActual());
            txtPassNueva.textProperty().addListener((obs, old, nv) -> validarConfirmacion());
            txtPassConfirm.textProperty().addListener((obs, old, nv) -> validarConfirmacion());

            // inicialmente botones de contraseña deshabilitados
            btnGuardarPass.setDisable(true);

        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("Error al cargar datos del usuario: " + ex.getMessage());
        }
    }

    private void validarPassActual() {
        String ingresada = txtPassActual.getText();
        if (ingresada != null && !ingresada.isEmpty() && encoder.matches(ingresada, user.getPassword())) {
            iconPassActual.setText("✔");
            iconPassActual.setStyle("-fx-text-fill: #77ff77;");
            habilitarBotonContra();
        } else {
            iconPassActual.setText("✖");
            iconPassActual.setStyle("-fx-text-fill: #ff7777;");
            btnGuardarPass.setDisable(true);
        }
    }

    private void validarConfirmacion() {
        String nueva = txtPassNueva.getText();
        String confirm = txtPassConfirm.getText();

        if (confirm != null && !confirm.isEmpty() && confirm.equals(nueva)) {
            iconConfirm.setText("✔");
            iconConfirm.setStyle("-fx-text-fill: #77ff77;");
            habilitarBotonContra();
        } else {
            // Si confirm está vacío, no mostrar X hasta que el usuario teclee algo
            if (confirm == null || confirm.isEmpty()) {
                iconConfirm.setText("");
            } else {
                iconConfirm.setText("✖");
                iconConfirm.setStyle("-fx-text-fill: #ff7777;");
            }
            btnGuardarPass.setDisable(true);
        }
    }

    private void habilitarBotonContra() {
        boolean ok = encoder.matches(txtPassActual.getText(), user.getPassword())
                && txtPassNueva.getText().equals(txtPassConfirm.getText())
                && !txtPassNueva.getText().isEmpty();
        btnGuardarPass.setDisable(!ok);
    }

    // ----- ACCIONES invocadas desde FXML -----

    @FXML
    public void guardarNombre() {
        try {
            String nuevo = txtNombre.getText().trim();
            if (nuevo.isEmpty()) {
                mostrarError("El nombre no puede estar vacío.");
                return;
            }
            user.setUsername(nuevo);
            usuarioRepository.save(user);
            usuarioSesion.setUsuarioActual(user); // actualizar sesión
            btnGuardarNombre.setDisable(true);
            mostrarInfo("Nombre actualizado correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("No se pudo actualizar el nombre: " + ex.getMessage());
        }
    }

    @FXML
    public void guardarCorreo() {
        try {
            String nuevo = txtCorreo.getText().trim();
            if (nuevo.isEmpty()) {
                mostrarError("El correo no puede estar vacío.");
                return;
            }
            user.setEmail(nuevo);
            usuarioRepository.save(user);
            usuarioSesion.setUsuarioActual(user);
            btnGuardarCorreo.setDisable(true);
            mostrarInfo("Correo actualizado correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("No se pudo actualizar el correo: " + ex.getMessage());
        }
    }

    @FXML
    public void guardarPass() {
        try {
            String actual = txtPassActual.getText();
            String nueva = txtPassNueva.getText();
            String confirmar = txtPassConfirm.getText();

            if (!encoder.matches(actual, user.getPassword())) {
                mostrarError("La contraseña actual es incorrecta.");
                return;
            }
            if (nueva == null || nueva.length() < 6) {
                mostrarError("La nueva contraseña debe tener al menos 6 caracteres.");
                return;
            }
            if (!nueva.equals(confirmar)) {
                mostrarError("Las contraseñas no coinciden.");
                return;
            }

            user.setPassword(encoder.encode(nueva));
            usuarioRepository.save(user);
            usuarioSesion.setUsuarioActual(user);

            // limpiar campos
            txtPassActual.clear();
            txtPassNueva.clear();
            txtPassConfirm.clear();
            iconPassActual.setText("");
            iconConfirm.setText("");
            btnGuardarPass.setDisable(true);

            mostrarInfo("Contraseña actualizada correctamente.");
        } catch (Exception ex) {
            ex.printStackTrace();
            mostrarError("No se pudo actualizar la contraseña: " + ex.getMessage());
        }
    }

    // ----- utilidades UI -----
    private void mostrarError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private void mostrarInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Información");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
