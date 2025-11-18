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
import pe.edu.upeu.gestorfinanciero.service.IngresoService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequiredArgsConstructor
public class IngresoController {

    @FXML private TextField txtDescripcion;
    @FXML private TextField txtMonto;
    @FXML private Label lblSaldoIngreso;

    @FXML private TableView<Ingreso> tabla;
    @FXML private TableColumn<Ingreso, String> colFecha;
    @FXML private TableColumn<Ingreso, String> colDescripcion;
    @FXML private TableColumn<Ingreso, Double> colMonto;
    @FXML private TableColumn<Ingreso, Double> colSaldo;

    private final ObservableList<Ingreso> lista = FXCollections.observableArrayList();

    private final UsuarioSesion usuarioSesion;
    private final IngresoService ingresoService;

    private Usuario usuarioActual;

    @FXML
    public void initialize() {

        usuarioActual = usuarioSesion.getUsuarioActual();

        colFecha.setCellValueFactory(i -> new javafx.beans.property.SimpleStringProperty(i.getValue().getFecha()));
        colDescripcion.setCellValueFactory(i -> new javafx.beans.property.SimpleStringProperty(i.getValue().getDescripcion()));
        colMonto.setCellValueFactory(i -> new javafx.beans.property.SimpleDoubleProperty(i.getValue().getMonto()).asObject());
        colSaldo.setCellValueFactory(i -> new javafx.beans.property.SimpleDoubleProperty(i.getValue().getSaldo()).asObject());

        refrescarTabla();
    }

    private void refrescarTabla() {
        lista.setAll(ingresoService.listar(usuarioActual));
        tabla.setItems(lista);

        double total = lista.stream().mapToDouble(Ingreso::getMonto).sum();
        lblSaldoIngreso.setText("Total Ingresos: S/ " + String.format("%.2f", total));
    }

    @FXML
    public void registrar(ActionEvent e) {
        String desc = txtDescripcion.getText().trim();
        String montoTxt = txtMonto.getText().trim();

        if (desc.isEmpty() || montoTxt.isEmpty()) {
            alert("Complete todos los campos.");
            return;
        }

        double monto;
        try { monto = Double.parseDouble(montoTxt); }
        catch (Exception ex) {
            alert("Monto inválido.");
            return;
        }

        double saldoAnterior = ingresoService.total(usuarioActual);
        double nuevoSaldo = saldoAnterior + monto;

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Ingreso ingreso = new Ingreso(fecha, desc, monto, nuevoSaldo, usuarioActual);

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

        txtDescripcion.setText(sel.getDescripcion());
        txtMonto.setText(String.valueOf(sel.getMonto()));

        ingresoService.eliminar(sel);
        refrescarTabla();
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

