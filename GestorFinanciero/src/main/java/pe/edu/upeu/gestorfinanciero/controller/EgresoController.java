package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.service.EgresoService;
import pe.edu.upeu.gestorfinanciero.service.MovimientoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class EgresoController {

    @FXML private ComboBox<String> cbxCategoria;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMonto;
    @FXML private Label lblSaldoEgreso;
    @FXML private TableView<Egreso> tablaEgresos;
    @FXML private TableColumn<Egreso, String> colFecha;
    @FXML private TableColumn<Egreso, String> colDescripcion;
    @FXML private TableColumn<Egreso, Number> colMonto;
    @FXML private TableColumn<Egreso, Number> colSaldo;

    private final ObservableList<Egreso> listaEgresos = FXCollections.observableArrayList();
    private final EgresoService egresoService;
    private final MovimientoService movimientoService;

    public EgresoController(EgresoService egresoService, MovimientoService movimientoService) {
        this.egresoService = egresoService;
        this.movimientoService = movimientoService;
    }

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(data -> data.getValue().fechaProperty());
        colDescripcion.setCellValueFactory(data -> data.getValue().descripcionProperty());
        colMonto.setCellValueFactory(data -> data.getValue().montoProperty());
        colSaldo.setCellValueFactory(data -> data.getValue().saldoProperty());

        cbxCategoria.setItems(FXCollections.observableArrayList(movimientoService.obtenerCategorias()));

        listaEgresos.clear(); // ✅ evita duplicados
        listaEgresos.addAll(egresoService.listarEgresos());
        tablaEgresos.setItems(listaEgresos);

        actualizarSaldoEgreso();
    }

    @FXML
    public void registrarEgreso(ActionEvent e) {
        String categoria = cbxCategoria.getValue();
        String descripcion = txtDescripcion.getText();
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double monto;

        if (categoria == null || descripcion.isEmpty() || txtMonto.getText().isEmpty()) {
            mostrarAlerta("Error", "Debes llenar todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Monto inválido.", Alert.AlertType.ERROR);
            return;
        }

        double saldoCategoria = movimientoService.obtenerSaldoCategoria(categoria);
        double limite = movimientoService.obtenerLimiteCategoria(categoria);

        if (saldoCategoria - monto < -limite) {
            mostrarAlerta("Límite excedido",
                    "No puedes registrar este gasto. Excede el límite asignado.",
                    Alert.AlertType.ERROR);
            return;
        }

        double nuevoSaldo = saldoCategoria - monto;

        Egreso egreso = new Egreso(fecha, descripcion, monto, nuevoSaldo, categoria);
        egresoService.guardarEgreso(egreso);
        movimientoService.actualizarSaldoCategoria(categoria, nuevoSaldo);

        listaEgresos.add(egreso);
        actualizarSaldoEgreso();

        limpiarCampos();
    }

    @FXML
    public void eliminarEgreso(ActionEvent e) {
        Egreso seleccionado = tablaEgresos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un registro para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        egresoService.eliminarEgreso(seleccionado);
        listaEgresos.remove(seleccionado);
        actualizarSaldoEgreso();
    }

    @FXML
    public void editarEgreso(ActionEvent e) {
        Egreso seleccionado = tablaEgresos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un registro para editar.", Alert.AlertType.WARNING);
            return;
        }

        txtDescripcion.setText(seleccionado.getDescripcion());
        txtMonto.setText(String.valueOf(seleccionado.getMonto()));
        cbxCategoria.setValue(seleccionado.getCategoria());

        listaEgresos.remove(seleccionado);
        egresoService.eliminarEgreso(seleccionado);
        actualizarSaldoEgreso();
    }

    private void actualizarSaldoEgreso() {
        double total = listaEgresos.stream().mapToDouble(Egreso::getSaldo).sum();
        lblSaldoEgreso.setText("Saldo restante: S/ " + String.format("%.2f", total));
    }

    private void limpiarCampos() {
        txtDescripcion.clear();
        txtMonto.clear();
        cbxCategoria.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}