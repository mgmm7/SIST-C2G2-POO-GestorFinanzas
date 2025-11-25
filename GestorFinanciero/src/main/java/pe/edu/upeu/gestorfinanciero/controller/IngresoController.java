package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.CurrencyService;
import pe.edu.upeu.gestorfinanciero.service.IngresoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class IngresoController {

    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMonto;
    @FXML private Label lblSaldoIngreso;
    @FXML private ComboBox<String> cmbMoneda;

    @FXML private TableView<Ingreso> tabla;
    @FXML private TableColumn<Ingreso, String> colFecha;
    @FXML private TableColumn<Ingreso, String> colDescripcion;
    @FXML private TableColumn<Ingreso, Double> colMonto;
    @FXML private TableColumn<Ingreso, Double> colSaldo;

    private final ObservableList<Ingreso> lista = FXCollections.observableArrayList();

    private final UsuarioSesion usuarioSesion;
    private final IngresoService ingresoService;
    private final CurrencyService currencyService;

    private Usuario usuarioActual;
    private final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        usuarioActual = usuarioSesion.getUsuarioActual();

        colFecha.setCellValueFactory(i -> new javafx.beans.property.SimpleStringProperty(i.getValue().getFecha()));
        colDescripcion.setCellValueFactory(i -> new javafx.beans.property.SimpleStringProperty(i.getValue().getDescripcion()));

        colMonto.setCellValueFactory(i -> {
            double pen = i.getValue().getMonto();
            double conv = currencyService.convertFromPen(pen, cmbMoneda.getValue());
            return new javafx.beans.property.SimpleDoubleProperty(conv).asObject();
        });

        colSaldo.setCellValueFactory(i -> {
            double pen = i.getValue().getSaldo();
            double conv = currencyService.convertFromPen(pen, cmbMoneda.getValue());
            return new javafx.beans.property.SimpleDoubleProperty(conv).asObject();
        });

        cmbMoneda.setItems(FXCollections.observableArrayList("PEN", "USD", "EUR"));
        cmbMoneda.getSelectionModel().select("PEN");
        cmbMoneda.setOnAction(e -> refrescarTabla());

        refrescarTabla();
    }

    @FXML
    public void registrar(ActionEvent e) {

        if (txtDescripcion.getText().isEmpty() || txtMonto.getText().isEmpty()) {
            alert("Complete todos los campos.");
            return;
        }

        double montoEnMoneda;
        try { montoEnMoneda = Double.parseDouble(txtMonto.getText()); }
        catch (Exception ex) {
            alert("Monto inválido.");
            return;
        }

        String moneda = cmbMoneda.getValue();

        double montoPen = currencyService.convertToPen(montoEnMoneda, moneda);

        double saldoAnterior = ingresoService.total(usuarioActual);
        double nuevoSaldo = saldoAnterior + montoPen;

        String fecha = LocalDate.now().format(F);

        Ingreso ingreso = new Ingreso(fecha, txtDescripcion.getText(), montoPen, nuevoSaldo, usuarioActual);

        ingresoService.guardar(ingreso);

        limpiar();
        refrescarTabla();
    }

    @FXML
    public void eliminar(ActionEvent e) {
        Ingreso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("Seleccione un registro.");
            return;
        }

        ingresoService.eliminar(sel);
        refrescarTabla();
    }

    @FXML
    public void editar(ActionEvent e) {
        Ingreso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("Seleccione un registro.");
            return;
        }

        String moneda = cmbMoneda.getValue();
        double montoConvertido = currencyService.convertFromPen(sel.getMonto(), moneda);

        txtDescripcion.setText(sel.getDescripcion());
        txtMonto.setText(String.format("%.2f", montoConvertido));

        ingresoService.eliminar(sel);
        refrescarTabla();
    }

    private void refrescarTabla() {
        lista.setAll(ingresoService.listar(usuarioActual));
        tabla.setItems(lista);

        double totalPen = lista.stream().mapToDouble(Ingreso::getMonto).sum();
        double totalConv = currencyService.convertFromPen(totalPen, cmbMoneda.getValue());

        lblSaldoIngreso.setText("Total Ingresos: " + simbolo() + String.format("%.2f", totalConv));

        tabla.refresh();
    }

    private String simbolo() {
        return switch (cmbMoneda.getValue()) {
            case "USD" -> "$ ";
            case "EUR" -> "€ ";
            default -> "S/ ";
        };
    }

    private void limpiar() {
        txtDescripcion.clear();
        txtMonto.clear();
    }

    private void alert(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(m);
        a.show();
    }
}
