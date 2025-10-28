package pe.edu.upeu.gestorfinanciero.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.dto.MovimientoDto;
import pe.edu.upeu.gestorfinanciero.service.ReporteService;

@Controller
public class ReporteController {

    @FXML private TableView<MovimientoDto> tablaMovimientos;
    @FXML private Label lblTotalIngresos;
    @FXML private Label lblTotalEgresos;
    @FXML private Label lblSaldoActual;
    @FXML private TextArea txtDetalles;

    private final ReporteService reporteService;

    public ReporteController(ReporteService reporteService) {
        this.reporteService = reporteService;
    }

    @FXML
    public void initialize() {
        configurarTabla();
        cargarDatos();
    }

    private void configurarTabla() {
        TableColumn<MovimientoDto, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));

        TableColumn<MovimientoDto, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<MovimientoDto, String> colDesc = new TableColumn<>("Descripción");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<MovimientoDto, Double> colMonto = new TableColumn<>("Monto");
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));

        TableColumn<MovimientoDto, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tablaMovimientos.getColumns().setAll(colTipo, colCategoria, colDesc, colMonto, colFecha);

        tablaMovimientos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, nuevo) -> {
            if (nuevo != null) {
                txtDetalles.setText(
                        "Tipo: " + nuevo.getTipo() + "\n" +
                                "Categoría: " + nuevo.getCategoria() + "\n" +
                                "Descripción: " + nuevo.getDescripcion() + "\n" +
                                "Monto: S/ " + nuevo.getMonto() + "\n" +
                                "Fecha: " + nuevo.getFecha()
                );
            }
        });
    }

    private void cargarDatos() {
        var movimientos = reporteService.obtenerTodosLosMovimientos();
        tablaMovimientos.getItems().setAll(movimientos);

        lblTotalIngresos.setText("Total Ingresos: S/ " + String.format("%.2f", reporteService.totalIngresos()));
        lblTotalEgresos.setText("Total Egresos: S/ " + String.format("%.2f", reporteService.totalEgresos()));
        lblSaldoActual.setText("Saldo Actual: S/ " + String.format("%.2f", reporteService.saldoActual()));
    }
}
