package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.CurrencyService;
import pe.edu.upeu.gestorfinanciero.service.EgresoService;
import pe.edu.upeu.gestorfinanciero.service.MovimientoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class EgresoController {

    @FXML private ComboBox<String> cbxCategoria;
    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMonto;
    @FXML private ComboBox<String> cmbMoneda;

    @FXML private Label lblSaldoEgreso;
    @FXML private Label lblPresupuestoRestante;
    @FXML private Label lblLimiteCategoria;

    @FXML private TableView<Egreso> tabla;
    @FXML private TableColumn<Egreso, String> colFecha;
    @FXML private TableColumn<Egreso, String> colDescripcion;
    @FXML private TableColumn<Egreso, Double> colMonto;
    @FXML private TableColumn<Egreso, Double> colSaldo;

    private final ObservableList<Egreso> lista = FXCollections.observableArrayList();

    private final UsuarioSesion usuarioSesion;
    private final EgresoService egresoService;
    private final MovimientoService movimientoService;
    private final CurrencyService currencyService;

    private Usuario usuarioActual;
    private final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        usuarioActual = usuarioSesion.getUsuarioActual();

        colFecha.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getFecha()));
        colDescripcion.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getDescripcion()));

        colMonto.setCellValueFactory(e -> {
            double pen = e.getValue().getMonto();
            double conv = currencyService.convertFromPen(pen, cmbMoneda.getValue());
            return new javafx.beans.property.SimpleDoubleProperty(conv).asObject();
        });

        colSaldo.setCellValueFactory(e -> {
            double pen = e.getValue().getSaldo();
            double conv = currencyService.convertFromPen(pen, cmbMoneda.getValue());
            return new javafx.beans.property.SimpleDoubleProperty(conv).asObject();
        });

        cmbMoneda.setItems(FXCollections.observableArrayList("PEN", "USD", "EUR"));
        cmbMoneda.getSelectionModel().select("PEN");
        cmbMoneda.setOnAction(e -> actualizarCategoriaSeleccionada());

        refrescarCategorias();
        actualizarCategoriaSeleccionada();

        cbxCategoria.setOnAction(e -> actualizarCategoriaSeleccionada());
    }

    private void refrescarCategorias() {
        cbxCategoria.setItems(FXCollections.observableArrayList(
                movimientoService.listarCategoriasUsuario(usuarioActual)
                        .stream()
                        .map(c -> c.getNombre())
                        .toList()
        ));
    }

    private void actualizarCategoriaSeleccionada() {
        String categoria = cbxCategoria.getValue();

        // Filtrar por categoría
        if (categoria == null) {
            lista.setAll(egresoService.listar(usuarioActual));
        } else {
            lista.setAll(egresoService.listar(usuarioActual).stream()
                    .filter(e -> e.getCategoria().equals(categoria))
                    .toList());
        }
        tabla.setItems(lista);

        // Actualizar saldo total de egresos
        double totalPen = lista.stream().mapToDouble(Egreso::getMonto).sum();
        double totalConv = currencyService.convertFromPen(totalPen, cmbMoneda.getValue());
        lblSaldoEgreso.setText("Total gastado: " + simbolo() + String.format("%.2f", totalConv));

        // Actualizar presupuesto y límite de la categoría
        if (categoria != null) {
            double saldoCat = movimientoService.obtenerSaldoCategoria(categoria, usuarioActual);
            double limite = movimientoService.obtenerLimiteCategoria(categoria, usuarioActual);

            double saldoConv = currencyService.convertFromPen(saldoCat, cmbMoneda.getValue());
            double limiteConv = currencyService.convertFromPen(limite, cmbMoneda.getValue());

            lblPresupuestoRestante.setText("Presupuesto restante: " + simbolo() + String.format("%.2f", saldoConv));
            lblLimiteCategoria.setText("Límite: " + simbolo() + String.format("%.2f", limiteConv));
        } else {
            lblPresupuestoRestante.setText("Presupuesto restante: -");
            lblLimiteCategoria.setText("Límite: -");
        }

        tabla.refresh();
    }

    private String simbolo() {
        return switch (cmbMoneda.getValue()) {
            case "USD" -> "$ ";
            case "EUR" -> "€ ";
            default -> "S/ ";
        };
    }

    @FXML
    public void registrar(ActionEvent e) {
        String categoria = cbxCategoria.getValue();
        String desc = txtDescripcion.getText().trim();
        String montoTxt = txtMonto.getText().trim();

        if (categoria == null || desc.isEmpty() || montoTxt.isEmpty()) {
            alert("Complete todos los campos.");
            return;
        }

        double montoEnMoneda;
        try { montoEnMoneda = Double.parseDouble(montoTxt); }
        catch (Exception ex) { alert("Monto inválido."); return; }

        double montoPen = currencyService.convertToPen(montoEnMoneda, cmbMoneda.getValue());

        double saldoCategoria = movimientoService.obtenerSaldoCategoria(categoria, usuarioActual);
        double limite = movimientoService.obtenerLimiteCategoria(categoria, usuarioActual);

        if ((saldoCategoria - montoPen) < -limite) {
            alert("Este gasto excede el límite de la categoría.");
            return;
        }

        double nuevoSaldo = saldoCategoria - montoPen;
        String fecha = LocalDate.now().format(F);

        Egreso eg = new Egreso(fecha, desc, montoPen, nuevoSaldo, categoria, usuarioActual);
        egresoService.guardar(eg);
        movimientoService.actualizarSaldoCategoria(categoria, nuevoSaldo, usuarioActual);

        limpiar();
        actualizarCategoriaSeleccionada();
    }

    @FXML
    public void eliminar(ActionEvent e) {
        Egreso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Seleccione un registro."); return; }

        double saldoCat = movimientoService.obtenerSaldoCategoria(sel.getCategoria(), usuarioActual);
        double nuevoSaldo = saldoCat + sel.getMonto();
        movimientoService.actualizarSaldoCategoria(sel.getCategoria(), nuevoSaldo, usuarioActual);

        egresoService.eliminar(sel);
        actualizarCategoriaSeleccionada();
    }

    @FXML
    public void editar(ActionEvent e) {
        Egreso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alert("Seleccione un registro."); return; }

        movimientoService.actualizarSaldoCategoria(sel.getCategoria(),
                movimientoService.obtenerSaldoCategoria(sel.getCategoria(), usuarioActual) + sel.getMonto(),
                usuarioActual);

        cbxCategoria.setValue(sel.getCategoria());
        txtDescripcion.setText(sel.getDescripcion());
        txtMonto.setText(String.format("%.2f",
                currencyService.convertFromPen(sel.getMonto(), cmbMoneda.getValue())));

        egresoService.eliminar(sel);
        actualizarCategoriaSeleccionada();
    }

    private void limpiar() {
        txtDescripcion.clear();
        txtMonto.clear();
        cbxCategoria.getSelectionModel().clearSelection();
    }

    private void alert(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(m);
        a.show();
    }
}
