package pe.edu.upeu.gestorfinanciero.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.components.ColumnInfo;
import pe.edu.upeu.gestorfinanciero.components.TableViewHelper;
import pe.edu.upeu.gestorfinanciero.components.Toast;
import pe.edu.upeu.gestorfinanciero.enums.Estado;
import pe.edu.upeu.gestorfinanciero.model.Meta;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.MetaIService;
import pe.edu.upeu.gestorfinanciero.service.UsuarioIService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Controller
public class MetaController {
    @FXML
    TextField txtNombreMeta, txtMObjetivo,
            txtMActual, txtFiltroDato;
    @FXML
    DatePicker dpFRegistro, dpFLimite;
    @FXML
    private TableView<Meta> tableView;
    @FXML
    Label lbnMsg, idPrueba;
    @FXML
    private AnchorPane miContenedor;
    Stage stage;
    @Autowired
    UsuarioIService usuarioIService;
    @Autowired
    MetaIService ms;
    private Validator validator;
    ObservableList<Meta> listarMeta;
    Meta formulario;
    Long idMetaCE = 0L;

    private void filtrarMetas(String filtro) {
        if (filtro == null || filtro.isEmpty()) {
            tableView.getItems().clear();
            tableView.getItems().addAll(listarMeta);
        } else {
            String lowerCaseFilter = filtro.toLowerCase();
            List<Meta> metasFiltradas = listarMeta.stream()
                    .filter(meta -> {
                        if (meta.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(meta.getMobjetivo()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(meta.getMactual()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(meta.getFcreacion()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(meta.getFlimite()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        if (String.valueOf(meta.getEstado()).contains(lowerCaseFilter)) {
                            return true;
                        }
                        return false; // Si no coincide con ningún campo
                    })
                    .collect(Collectors.toList());
            tableView.getItems().clear();
            tableView.getItems().addAll(metasFiltradas);
        }
    }

    public void listar() {
        try {
            tableView.getItems().clear();
            listarMeta = FXCollections.observableArrayList(ms.findAll());
            tableView.getItems().addAll(listarMeta);
            txtFiltroDato.textProperty().addListener((observable, oldValue,
                                                      newValue) -> {
                filtrarMetas(newValue);
            });
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        dpFRegistro.setValue(LocalDate.now());
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000),
                event -> {
                    stage = (Stage) miContenedor.getScene().getWindow();
                    if (stage != null) {
                        System.out.println("El título del stage es: " +
                                stage.getTitle());
                    } else {
                        System.out.println("Stage aún no disponible.");
                    }
                }));
        timeline.setCycleCount(1);
        timeline.play();

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        TableViewHelper<Meta> tableViewHelper = new TableViewHelper<>();
        LinkedHashMap<String, ColumnInfo> columns = new LinkedHashMap<>();
        columns.put("ID Meta", new ColumnInfo("idmeta", 60.0));
        columns.put("Nombre Meta", new ColumnInfo("nombre", 150.0));
        columns.put("Monto Objetivo", new ColumnInfo("mobjetivo", 100.0));
        columns.put("Monto Actual", new ColumnInfo("mactual", 100.0));
        columns.put("F. Registro", new ColumnInfo("fcreacion", 100.0));
        columns.put("F. Límite", new ColumnInfo("flimite", 100.0));
        columns.put("Estado", new ColumnInfo("estado", 100.0));
        Consumer<Meta> updateAction = (Meta meta) -> {
            System.out.println("Actualizar: " + meta);
            editForm(meta);
        };
        Consumer<Meta> deleteAction = (Meta meta) ->
        {
            System.out.println("Actualizar: " + meta);
            ms.delete(meta.getIdmeta());
            double with = stage.getWidth() / 1.5;
            double h = stage.getHeight() / 2;
            Toast.showToast(stage, "Se eliminó correctamente!!", 2000, with,
                    h);
            listar();
        };
        tableViewHelper.addColumnsInOrderWithSize(tableView,
                columns, updateAction, deleteAction);
        tableView.setTableMenuButtonVisible(true);
        listar();
    }

    public void limpiarError() {
        List<Control> controles = List.of(
                txtNombreMeta, txtMObjetivo, txtMActual,
                dpFRegistro, dpFLimite
        );
        controles.forEach(c -> c.getStyleClass().remove("text-field-error"));
    }

    public void clearForm() {
        txtNombreMeta.clear();
        txtMObjetivo.clear();
        txtMActual.clear();
        dpFRegistro.setValue(LocalDate.now());
        dpFLimite.setValue(null);
        idMetaCE = 0L;
        limpiarError();
    }

    public void editForm(Meta meta) {
        txtNombreMeta.setText(meta.getNombre());
        txtMObjetivo.setText(meta.getMobjetivo().toString());
        txtMActual.setText(meta.getMactual().toString());
        dpFRegistro.setValue(meta.getFcreacion());
        dpFLimite.setValue(meta.getFlimite());

        idMetaCE = meta.getIdmeta();
        limpiarError();
    }

    private void mostrarErroresValidacion(List<ConstraintViolation<Meta>> violaciones) {
        limpiarError();
        //Mantiene el orden de los campos del formulario
        Map<String, Control> campos = new LinkedHashMap<>();
        campos.put("nombre", txtNombreMeta);
        campos.put("mobjetivo", txtMObjetivo);
        campos.put("mactual", txtMActual);
        campos.put("fcreacion", dpFRegistro);
        campos.put("flimite", dpFLimite);
        //Guarda los errores siguiendo el orden del formulario
        LinkedHashMap<String, String> erroresOrdenados = new LinkedHashMap<>();
        final Control[] primerControlConError = {null};
        for (String campo : campos.keySet()) {
            violaciones.stream()
                    .filter(v -> v.getPropertyPath().toString().equals(campo))
                    .findFirst()
                    .ifPresent(v -> {
                        erroresOrdenados.put(campo, v.getMessage());
                        Control control = campos.get(campo);
                        //Aplica el estilo de error si no lo tiene
                        if (control != null && !control.getStyleClass().contains("text-field-error")) {
                            control.getStyleClass().add("text-field-error");
                        }
                        //Guarda el primer control con error para enfocar después
                        if (primerControlConError[0] == null) {
                            primerControlConError[0] = control;
                        }
                    });
        }
        //Muestra el primer mensaje de error y enfoca el control correspondiente
        if (!erroresOrdenados.isEmpty()) {
            var primerError = erroresOrdenados.entrySet().iterator().next();
            lbnMsg.setText(primerError.getValue());
            lbnMsg.setStyle("-fx-text-fill: red; -fx-font-size: 16px;");
            //Enfocar el primer campo con error
            if (primerControlConError[0] != null) {
                Control finalPrimerControl = primerControlConError[0];
                Platform.runLater(finalPrimerControl::requestFocus);
            }
        }
    }

    private void procesarFormulario() {
        lbnMsg.setText("Formulario válido");
        lbnMsg.setStyle("-fx-text-fill: green; -fx-font-size: 16px;");
        limpiarError();
        double width = stage.getWidth() / 1.5;
        double height = stage.getHeight() / 2;
        if (idMetaCE > 0L) {
            formulario.setIdmeta(idMetaCE);
            ms.update(formulario);
            Toast.showToast(stage, "Se actualizó correctamente!!", 2000, width, height);
        } else {
            ms.save(formulario);
            Toast.showToast(stage, "Se guardó correctamente!!", 2000, width, height);
        }
        clearForm();
        listar();
    }

    @FXML
    public void validarFormulario() {
        formulario = new Meta();
        formulario.setNombre(txtNombreMeta.getText());
        formulario.setMobjetivo(parseBigDecimalSafe(txtMObjetivo.getText()));
        formulario.setMactual(parseBigDecimalSafe(txtMActual.getText()));
        formulario.setFcreacion(dpFRegistro.getValue());
        formulario.setFlimite(dpFLimite.getValue());
        formulario.setFactualizacion(LocalDate.now());
        formulario.setEstado(Estado.ENPROGRESO);

        Usuario usuarioActual = usuarioIService.findById(1L);
        formulario.setUsuario(usuarioActual);

        Set<ConstraintViolation<Meta>> violaciones = validator.validate(formulario);
        List<ConstraintViolation<Meta>> violacionesOrdenadas = violaciones.stream()
                .sorted(Comparator.comparing(v -> v.getPropertyPath().toString()))
                .toList();
        if (violacionesOrdenadas.isEmpty()) {
            procesarFormulario();
        } else {
            mostrarErroresValidacion(violacionesOrdenadas);
        }
    }

    // método para el BigDecimal
    private BigDecimal parseBigDecimalSafe(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO; // o puedes manejarlo de otra forma
        }
    }

    private double parseDoubleSafe(String value) {
        if (value == null || value.trim().isEmpty()) return 0.0;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
