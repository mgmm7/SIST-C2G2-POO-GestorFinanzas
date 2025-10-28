package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.service.IngresoService;
import pe.edu.upeu.gestorfinanciero.service.ReporteService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class IngresoController {

    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMonto;
    @FXML private Label lblSaldoIngreso;
    @FXML private TableView<Ingreso> tablaIngresos;
    @FXML private TableColumn<Ingreso, String> colFecha;
    @FXML private TableColumn<Ingreso, String> colDescripcion;
    @FXML private TableColumn<Ingreso, Number> colMonto;
    @FXML private TableColumn<Ingreso, Number> colSaldo;

    private final ObservableList<Ingreso> listaIngresos = FXCollections.observableArrayList();
    private final IngresoService ingresoService;
    private final ReporteService reporteService;

    public IngresoController(IngresoService ingresoService, ReporteService reporteService) {
        this.ingresoService = ingresoService;
        this.reporteService = reporteService;
    }

    @FXML
    public void initialize() {
        colFecha.setCellValueFactory(data -> data.getValue().fechaProperty());
        colDescripcion.setCellValueFactory(data -> data.getValue().descripcionProperty());
        colMonto.setCellValueFactory(data -> data.getValue().montoProperty());
        colSaldo.setCellValueFactory(data -> data.getValue().saldoProperty());

        listaIngresos.clear(); // ✅ evita duplicados
        listaIngresos.addAll(ingresoService.listarIngresos());
        tablaIngresos.setItems(listaIngresos);

        actualizarSaldoIngreso();
    }

    @FXML
    public void registrarIngreso(ActionEvent e) {
        String descripcion = txtDescripcion.getText();
        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        double monto;

        if (descripcion.isEmpty() || txtMonto.getText().isEmpty()) {
            mostrarAlerta("Campos vacíos", "Debes llenar todos los campos.", Alert.AlertType.WARNING);
            return;
        }

        try {
            monto = Double.parseDouble(txtMonto.getText());
        } catch (NumberFormatException ex) {
            mostrarAlerta("Error", "Monto inválido.", Alert.AlertType.ERROR);
            return;
        }

        double saldoAnterior = reporteService.saldoActual();
        double nuevoSaldo = saldoAnterior + monto;

        Ingreso ingreso = new Ingreso(fecha, descripcion, monto, nuevoSaldo);
        ingresoService.guardarIngreso(ingreso);
        listaIngresos.add(ingreso);

        actualizarSaldoIngreso();
        limpiarCampos();
    }

    @FXML
    public void editarIngreso(ActionEvent e) {
        Ingreso seleccionado = tablaIngresos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un ingreso para editar.", Alert.AlertType.WARNING);
            return;
        }

        txtDescripcion.setText(seleccionado.getDescripcion());
        txtMonto.setText(String.valueOf(seleccionado.getMonto()));

        listaIngresos.remove(seleccionado);
        ingresoService.eliminarIngreso(seleccionado);
        actualizarSaldoIngreso();
    }

    @FXML
    public void eliminarIngreso(ActionEvent e) {
        Ingreso seleccionado = tablaIngresos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Advertencia", "Selecciona un ingreso para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        ingresoService.eliminarIngreso(seleccionado);
        listaIngresos.remove(seleccionado);
        actualizarSaldoIngreso();
    }

    private void actualizarSaldoIngreso() {
        double saldo = reporteService.saldoActual();
        lblSaldoIngreso.setText("Saldo actual: S/ " + String.format("%.2f", saldo));
    }

    private void limpiarCampos() {
        txtDescripcion.clear();
        txtMonto.clear();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
