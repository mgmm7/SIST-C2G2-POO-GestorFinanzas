package pe.edu.upeu.gestorfinanciero.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.model.EditarUsuario;
import pe.edu.upeu.gestorfinanciero.model.Perfil;
import pe.edu.upeu.gestorfinanciero.service.IEditarUsuarioService;

import java.util.Optional;

@Controller
public class EditarUsuarioController {

    // 🧩 Componentes FXML
    @FXML private Label lblErrorUsuario;
    @FXML private Label lblErrorClave;
    @FXML private Label lblErrorEstado;
    @FXML private TextField txtBuscarUsuario;
    @FXML private Button btnBuscarUsuario;
    @FXML private Button btnGuardar;
    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContraseña;
    @FXML private ComboBox<String> cbEstado;
    @FXML private TableView<EditarUsuario> tableView;
    @FXML private TableColumn<EditarUsuario, String> userColum;
    @FXML private TableColumn<EditarUsuario, String> claveColum;
    @FXML private TableColumn<EditarUsuario, String> estadoColum;
    private TableColumn<EditarUsuario, Void> opcColum;

    // 🧠 Servicios
    @Autowired private IEditarUsuarioService usuarioService;

    // 🧾 Variables de control
    private ObservableList<EditarUsuario> listaEditarUsuarios;
    private EditarUsuario usuarioActual = null; // null = agregar, no null = editar

    // ===========================================================
    // 🔹 Inicialización
    // ===========================================================
    @FXML
    public void initialize() {
        btnBuscarUsuario.setOnAction(event -> buscarUsuario());
        definirColumnas();
        agregarAccionBotones();
        listarUsuarios();

        // Rellenar combos si los usas
        ObservableList<String> estados = FXCollections.observableArrayList("Activo", "Inactivo");
        cbEstado.setItems(estados);
        // Texto inicial del botón
        btnGuardar.setText("Registrar");
    }

    private void limpiarErrores() {
        lblErrorUsuario.setText("");
        lblErrorClave.setText("");
        lblErrorEstado.setText("");
        txtUsuario.getStyleClass().remove("text-field-error");
        txtContraseña.getStyleClass().remove("text-field-error");
        cbEstado.getStyleClass().remove("text-field-error");
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle("Validación");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }


    // ===========================================================
    // 🔹 Guardar / Actualizar usuario
    // ===========================================================
    @FXML
    public void guardarUsuario(javafx.event.ActionEvent event) {
        limpiarErrores(); // 🧼 limpiar errores anteriores

        boolean hayError = false;

        String usuario = txtUsuario.getText().trim();
        String clave = txtContraseña.getText().trim();
        String estado = cbEstado.getValue();

        // Validar usuario
        if (usuario.isEmpty()) {
            lblErrorUsuario.setText("El usuario es obligatorio");
            txtUsuario.getStyleClass().add("text-field-error");
            hayError = true;
        }

        // Validar contraseña
        if (clave.isEmpty()) {
            lblErrorClave.setText("La contraseña es obligatoria");
            txtContraseña.getStyleClass().add("text-field-error");
            hayError = true;
        } else if (clave.length() < 8) {
            lblErrorClave.setText("Debe tener al menos 8 caracteres");
            txtContraseña.getStyleClass().add("text-field-error");
            hayError = true;
        }

        // Validar estado
        if (estado == null || estado.isEmpty()) {
            lblErrorEstado.setText("Debe seleccionar un estado");
            cbEstado.getStyleClass().add("text-field-error");
            hayError = true;
        }

        if (hayError) return; // 🚫 Detiene el guardado si hay errores

        // 🧠 Si todo está correcto
        if (usuarioActual == null) {
            EditarUsuario nuevo = new EditarUsuario();
            nuevo.setUser(usuario);
            nuevo.setClave(clave);
            nuevo.setEstado(estado);

            Perfil perfil = new Perfil();
            perfil.setIdPerfil(1L);
            nuevo.setPerfil(perfil);

            usuarioService.save(nuevo);
            mostrarAlerta("✅ Usuario registrado correctamente!", Alert.AlertType.INFORMATION);
        } else {
            usuarioActual.setUser(usuario);
            usuarioActual.setClave(clave);
            usuarioActual.setEstado(estado);

            Perfil perfil = new Perfil();
            perfil.setIdPerfil(1L);
            usuarioActual.setPerfil(perfil);

            usuarioService.save(usuarioActual);
            mostrarAlerta("✏️ Usuario actualizado correctamente!", Alert.AlertType.INFORMATION);
        }

        limpiarFormulario();
        listarUsuarios();
    }



    // ===========================================================
    // 🔹 Métodos auxiliares
    // ===========================================================
    private void mostrarMensaje(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void limpiarFormulario() {
        txtUsuario.clear();
        txtContraseña.clear();
        cbEstado.setValue(null);
        usuarioActual = null;
        btnGuardar.setText("Registrar");
    }

    // ===========================================================
    // 🔹 Configuración de tabla
    // ===========================================================
    private void definirColumnas() {
        userColum = new TableColumn<>("Usuario");
        claveColum = new TableColumn<>("Contraseña");
        estadoColum = new TableColumn<>("Estado");
        opcColum = new TableColumn<>("Opciones");
        opcColum.setPrefWidth(200);

        tableView.getColumns().setAll(userColum, claveColum, estadoColum, opcColum);

        userColum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getUser()));
        claveColum.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getClave()));
        estadoColum.setCellValueFactory(c->new SimpleStringProperty(c.getValue().getEstado()));

    }

    private void agregarAccionBotones() {
        Callback<TableColumn<EditarUsuario, Void>, TableCell<EditarUsuario, Void>> cellFactory = param -> new TableCell<>() {
            private final Button editarBtn = new Button("Editar");
            private final Button eliminarBtn = new Button("Eliminar");

            {
                editarBtn.setOnAction(event -> {
                    EditarUsuario seleccionado = getTableView().getItems().get(getIndex());
                    editarDatos(seleccionado);
                });

                eliminarBtn.setOnAction(event -> {
                    EditarUsuario seleccionado = getTableView().getItems().get(getIndex());

                    // Crear alerta de confirmación
                    Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmacion.setTitle("Confirmar eliminación");
                    confirmacion.setHeaderText("¿Deseas eliminar este usuario?");
                    confirmacion.setContentText("Usuario: " + seleccionado.getUser());

                    // Mostrar y esperar respuesta
                    Optional<ButtonType> resultado = confirmacion.showAndWait();

                    if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                        // Si confirma, eliminamos el usuario
                        eliminarUsuario(seleccionado.getIdUsuario());

                        Alert exito = new Alert(Alert.AlertType.INFORMATION);
                        exito.setTitle("Usuario eliminado");
                        exito.setHeaderText(null);
                        exito.setContentText("El usuario fue eliminado correctamente.");
                        exito.showAndWait();
                    } else {
                        // Si cancela, mostramos aviso
                        Alert cancelado = new Alert(Alert.AlertType.INFORMATION);
                        cancelado.setTitle("Operación cancelada");
                        cancelado.setHeaderText(null);
                        cancelado.setContentText("La eliminación fue cancelada.");
                        cancelado.showAndWait();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(editarBtn, eliminarBtn);
                    box.setSpacing(10);
                    setGraphic(box);
                }
            }
        };
        opcColum.setCellFactory(cellFactory);
    }

    // ===========================================================
    // 🔹 Operaciones CRUD
    // ===========================================================
    private void listarUsuarios() {
        listaEditarUsuarios = FXCollections.observableArrayList(usuarioService.findAll());
        tableView.setItems(listaEditarUsuarios);
        agregarAccionBotones();
    }
    private void buscarUsuario() {
        String criterio = txtBuscarUsuario.getText().trim();

        if (criterio.isEmpty()) {
            // Si no se escribió nada, mostrar todos
            listarUsuarios();
            return;
        }

        // Buscar usuarios que coincidan (por ejemplo, con parte del nombre de usuario)
        listaEditarUsuarios = FXCollections.observableArrayList(
                    usuarioService.findByUserContainingIgnoreCase(criterio)
        );

        if (listaEditarUsuarios.isEmpty()) {
            mostrarMensaje("⚠️ No se encontró ningún usuario con ese criterio.");
        }

        tableView.setItems(listaEditarUsuarios);
        agregarAccionBotones();

    }


    private void eliminarUsuario(Long user) {
        usuarioService.delete(user);
        listarUsuarios();
    }

    private void editarDatos(EditarUsuario usuario) {
        usuarioActual = usuario;
        txtUsuario.setText(usuario.getUser());
        txtContraseña.setText(usuario.getClave());
        cbEstado.setValue(usuario.getEstado());
        btnGuardar.setText("Actualizar");
    }
}
