package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
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
    @FXML private Label lblSaldoEgreso;

    @FXML private TableView<Egreso> tabla;
    @FXML private TableColumn<Egreso, String> colFecha;
    @FXML private TableColumn<Egreso, String> colDescripcion;
    @FXML private TableColumn<Egreso, Double> colMonto;
    @FXML private TableColumn<Egreso, Double> colSaldo;

    private final ObservableList<Egreso> lista = FXCollections.observableArrayList();

    private final UsuarioSesion usuarioSesion;
    private final EgresoService egresoService;
    private final MovimientoService movimientoService;

    private Usuario usuarioActual;

    @FXML
    public void initialize() {

        usuarioActual = usuarioSesion.getUsuarioActual();

        colFecha.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getFecha()));
        colDescripcion.setCellValueFactory(e -> new javafx.beans.property.SimpleStringProperty(e.getValue().getDescripcion()));
        colMonto.setCellValueFactory(e -> new javafx.beans.property.SimpleDoubleProperty(e.getValue().getMonto()).asObject());
        colSaldo.setCellValueFactory(e -> new javafx.beans.property.SimpleDoubleProperty(e.getValue().getSaldo()).asObject());

        refrescarCategorias();
        refrescarTabla();
    }

    private void refrescarCategorias() {
        cbxCategoria.setItems(FXCollections.observableArrayList(
                movimientoService.listarCategoriasUsuario(usuarioActual)
                        .stream()
                        .map(Categoria::getNombre)
                        .toList()
        ));
    }

    private void refrescarTabla() {
        lista.setAll(egresoService.listar(usuarioActual));
        tabla.setItems(lista);

        double total = lista.stream().mapToDouble(Egreso::getMonto).sum();
        lblSaldoEgreso.setText("Total gastado: S/ " + String.format("%.2f", total));
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

        double monto;
        try { monto = Double.parseDouble(montoTxt); }
        catch (Exception ex) {
            alert("Monto inválido.");
            return;
        }

        double saldoCategoria = movimientoService.obtenerSaldoCategoria(categoria, usuarioActual);
        double limite = movimientoService.obtenerLimiteCategoria(categoria, usuarioActual);

        if ((saldoCategoria - monto) < -limite) {
            alert("Este gasto excede el límite de la categoría.");
            return;
        }

        double nuevoSaldo = saldoCategoria - monto;

        String fecha = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        Egreso eg = new Egreso(fecha, desc, monto, nuevoSaldo, categoria, usuarioActual);
        egresoService.guardar(eg);

        // actualizar saldo de categoría
        movimientoService.actualizarSaldoCategoria(categoria, nuevoSaldo, usuarioActual);

        limpiar();
        refrescarTabla();
    }

    @FXML
    public void eliminar(ActionEvent e) {
        Egreso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("Seleccione un registro.");
            return;
        }

        // devolver saldo a la categoría
        double saldoCat = movimientoService.obtenerSaldoCategoria(sel.getCategoria(), usuarioActual);
        double nuevoSaldo = saldoCat + sel.getMonto();
        movimientoService.actualizarSaldoCategoria(sel.getCategoria(), nuevoSaldo, usuarioActual);

        egresoService.eliminar(sel);

        refrescarTabla();
    }

    @FXML
    public void editar(ActionEvent e) {
        Egreso sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) {
            alert("Seleccione un registro.");
            return;
        }

        // volver saldo previo
        double saldoCat = movimientoService.obtenerSaldoCategoria(sel.getCategoria(), usuarioActual);
        movimientoService.actualizarSaldoCategoria(sel.getCategoria(), saldoCat + sel.getMonto(), usuarioActual);

        // cargar campos
        cbxCategoria.setValue(sel.getCategoria());
        txtDescripcion.setText(sel.getDescripcion());
        txtMonto.setText(String.valueOf(sel.getMonto()));

        egresoService.eliminar(sel);
        refrescarTabla();
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
