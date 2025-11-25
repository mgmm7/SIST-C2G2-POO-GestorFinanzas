package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Aporte;
import pe.edu.upeu.gestorfinanciero.model.Meta;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.AporteService;
import pe.edu.upeu.gestorfinanciero.service.MetaService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MetaController {

    // ==== MONEDA ====
    @FXML private ComboBox<String> cbMetaMoneda;
    private String currentCurrency = "S/";

    private double convert(double monto) {
        return switch (currentCurrency) {
            case "$" -> monto * 0.27;
            case "€" -> monto * 0.25;
            default -> monto;
        };
    }

    // ==== FORMULARIO META ====
    @FXML private TextField txtNombreMeta;
    @FXML private TextField txtObjetivo;
    @FXML private DatePicker dpFechaLimite;

    @FXML private Button btnRegistrar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;
    @FXML private Button btnAportar;

    // ==== TABLA METAS ====
    @FXML private TableView<Meta> tablaMetas;
    @FXML private TableColumn<Meta, String> colNombre;
    @FXML private TableColumn<Meta, Double> colObjetivo;
    @FXML private TableColumn<Meta, Double> colAportado;
    @FXML private TableColumn<Meta, Double> colProgreso;

    @FXML private Label lblMetaNombre;
    @FXML private Label lblMetaObjetivo;
    @FXML private Label lblMetaAportado;
    @FXML private ProgressBar pbMetaGrande;

    // ==== TABLA APORTES ====
    @FXML private TableView<Aporte> tablaAportes;
    @FXML private TableColumn<Aporte, String> colApFecha;
    @FXML private TableColumn<Aporte, String> colApDesc;
    @FXML private TableColumn<Aporte, Double> colApMonto;
    @FXML private TableColumn<Aporte, String> colApEvidencia;

    private final MetaService metaService;
    private final AporteService aporteService;
    private final UsuarioSesion usuarioSesion;

    private Usuario usuarioActual;
    private final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Meta editingMeta = null;
    private Aporte editingAporte = null;

    @FXML
    public void initialize() {
        usuarioActual = usuarioSesion.getUsuarioActual();

        // ==== MONEDA ====
        cbMetaMoneda.setItems(FXCollections.observableArrayList("S/", "$", "€"));
        cbMetaMoneda.setValue("S/");
        cbMetaMoneda.valueProperty().addListener((o,v,n)->{
            currentCurrency = n;
            tablaMetas.refresh();
            tablaAportes.refresh();
            Meta sel = tablaMetas.getSelectionModel().getSelectedItem();
            actualizarDetalleMeta(sel);
        });

        // ==== COLUMNAS TABLA METAS ====
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colProgreso.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getProgreso()).asObject());
        colProgreso.setCellFactory(ProgressBarTableCell.forTableColumn());

        colObjetivo.setCellFactory(col -> new TableCell<Meta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Meta m = (Meta) getTableRow().getItem();
                    setText(currentCurrency + " " + String.format("%.2f", convert(m.getObjetivo())));
                }
            }
        });

        colAportado.setCellFactory(col -> new TableCell<Meta, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Meta m = (Meta) getTableRow().getItem();
                    setText(currentCurrency + " " + String.format("%.2f", convert(m.getAportado())));
                }
            }
        });

        // ==== COLUMNAS TABLA APORTES ====
        colApFecha.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(
                a.getValue().getFecha() == null ? "" : a.getValue().getFecha().format(F)
        ));
        colApDesc.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(a.getValue().getDescripcion()));
        colApMonto.setCellFactory(col -> new TableCell<Aporte, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if(empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setText(null);
                } else {
                    Aporte a = (Aporte) getTableRow().getItem();
                    setText(currentCurrency + " " + String.format("%.2f", convert(a.getMonto())));
                }
            }
        });
        colApEvidencia.setCellValueFactory(a -> new javafx.beans.property.SimpleStringProperty(
                a.getValue().getEvidencia() == null ? "" : a.getValue().getEvidencia()
        ));

        // ==== SELECCIÓN ====
        tablaMetas.getSelectionModel().selectedItemProperty().addListener((obs,o,n)->{
            actualizarDetalleMeta(n);
            editingAporte = null;
        });

        refrescarMetas();
        btnAportar.setDisable(true);
    }

    private void refrescarMetas() {
        List<Meta> metas = metaService.listarMetasUsuario(usuarioActual);
        tablaMetas.setItems(FXCollections.observableArrayList(metas));
    }

    private void actualizarDetalleMeta(Meta m) {
        if (m == null) {
            lblMetaNombre.setText("");
            lblMetaObjetivo.setText("");
            lblMetaAportado.setText("");
            pbMetaGrande.setProgress(0);
            tablaAportes.setItems(FXCollections.emptyObservableList());
            btnAportar.setDisable(true);
            return;
        }

        lblMetaNombre.setText(m.getNombre());
        lblMetaObjetivo.setText("Objetivo: " + currentCurrency + " " + String.format("%.2f", convert(m.getObjetivo())));
        lblMetaAportado.setText("Aportado: " + currentCurrency + " " + String.format("%.2f", convert(m.getAportado())));
        pbMetaGrande.setProgress(m.getProgreso());

        List<Aporte> aps = metaService.listarAportesMeta(m);
        tablaAportes.setItems(FXCollections.observableArrayList(aps));

        btnAportar.setDisable(false);
    }

    // ==== REGISTRAR / EDITAR / ELIMINAR / APORTAR ====
    @FXML
    public void registrar() {
        if(editingMeta != null) {
            editarMeta();
            return;
        }

        String nombre = txtNombreMeta.getText().trim();
        String objTxt = txtObjetivo.getText().trim();
        LocalDate fechaLim = dpFechaLimite.getValue();

        if(nombre.isEmpty() || objTxt.isEmpty()) {
            mostrarAlerta("Complete todos los campos de la meta.");
            return;
        }

        double objetivo;
        try { objetivo = Double.parseDouble(objTxt); } catch(Exception e) { mostrarAlerta("Objetivo inválido."); return; }

        metaService.crearMeta(nombre, objetivo, fechaLim, usuarioActual);

        txtNombreMeta.clear();
        txtObjetivo.clear();
        dpFechaLimite.setValue(null);
        refrescarMetas();
    }

    private void editarMeta() {
        String nombre = txtNombreMeta.getText().trim();
        String objTxt = txtObjetivo.getText().trim();
        LocalDate fechaLim = dpFechaLimite.getValue();

        if(nombre.isEmpty() || objTxt.isEmpty()) {
            mostrarAlerta("Complete todos los campos de la meta.");
            return;
        }

        double objetivo;
        try { objetivo = Double.parseDouble(objTxt); } catch(Exception e) { mostrarAlerta("Objetivo inválido."); return; }

        metaService.editarMeta(editingMeta.getId(), nombre, objetivo, fechaLim, usuarioActual);

        editingMeta = null;
        btnRegistrar.setText("Registrar");

        txtNombreMeta.clear();
        txtObjetivo.clear();
        dpFechaLimite.setValue(null);
        refrescarMetas();
    }

    @FXML
    public void editar() {
        Meta sel = tablaMetas.getSelectionModel().getSelectedItem();
        if(sel == null) return;

        txtNombreMeta.setText(sel.getNombre());
        txtObjetivo.setText(String.valueOf(sel.getObjetivo()));
        dpFechaLimite.setValue(sel.getFechaLimite());

        editingMeta = sel;
        btnRegistrar.setText("Guardar cambios");
    }

    @FXML
    public void eliminar() {
        Meta sel = tablaMetas.getSelectionModel().getSelectedItem();
        if(sel != null) {
            metaService.eliminarMeta(sel.getId(), usuarioActual);
            actualizarDetalleMeta(null);
            refrescarMetas();
        }
    }

    @FXML
    public void aportar() {
        Meta sel = tablaMetas.getSelectionModel().getSelectedItem();
        if(sel == null) return;

        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Aportar a meta: " + sel.getNombre());
        dlg.setHeaderText("Ingrese monto a aportar");
        dlg.setContentText("Monto:");
        Optional<String> maybe = dlg.showAndWait();
        if(maybe.isEmpty()) return;

        double monto;
        try { monto = Double.parseDouble(maybe.get().trim()); } catch(Exception ex){ return; }

        TextInputDialog dlg2 = new TextInputDialog();
        dlg2.setTitle("Descripción");
        dlg2.setHeaderText("Descripción corta");
        dlg2.setContentText("Descripción:");
        Optional<String> descOpt = dlg2.showAndWait();
        String desc = descOpt.orElse("").trim();

        Aporte ap = new Aporte();
        ap.setMeta(sel);
        ap.setUsuario(usuarioActual);
        ap.setMonto(monto);
        ap.setDescripcion(desc);
        ap.setEvidencia(null);
        ap.setFecha(LocalDate.now());

        aporteService.registrar(ap);

        refrescarMetas();
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
