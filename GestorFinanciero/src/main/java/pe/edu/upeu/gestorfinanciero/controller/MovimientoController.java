package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.service.MovimientoService;

import java.util.Optional;

@Controller
public class MovimientoController {

    @FXML private TextField txtNuevaCategoria;
    @FXML private TextField txtPresupuesto;
    @FXML private TextField txtLimite;
    @FXML private ComboBox<String> cbxCategoriaPresupuesto;
    @FXML private ComboBox<String> cbxCategoriaLimite;
    @FXML private ComboBox<String> cbxCategoriaEditar;
    @FXML private TableView<Categoria> tablaCategorias;
    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Number> colPresupuesto;
    @FXML private TableColumn<Categoria, Number> colLimite;
    @FXML private TableColumn<Categoria, Number> colSaldoDisp;
    @FXML private Label lblSaldoActual;

    private final MovimientoService movimientoService;
    private final ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @FXML
    public void initialize() {
        colNombre.setCellValueFactory(data -> data.getValue().nombreProperty());
        colPresupuesto.setCellValueFactory(data -> data.getValue().presupuestoProperty());
        colLimite.setCellValueFactory(data -> data.getValue().limiteProperty());
        colSaldoDisp.setCellValueFactory(data -> data.getValue().saldoDisponibleProperty());

        refrescarCategorias();
        actualizarSaldoActual();
    }

    @FXML
    public void crearCategoria(ActionEvent e) {
        String nombre = txtNuevaCategoria.getText().trim();
        if (!nombre.isEmpty()) {
            movimientoService.crearCategoria(nombre);
            txtNuevaCategoria.clear();
            refrescarCategorias();
            actualizarSaldoActual();
        } else {
            mostrarAlerta("Error", "Debe ingresar un nombre de categoría.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void asignarPresupuesto(ActionEvent e) {
        String categoria = cbxCategoriaPresupuesto.getValue();
        if (categoria != null && !txtPresupuesto.getText().isEmpty()) {
            try {
                double monto = Double.parseDouble(txtPresupuesto.getText());
                movimientoService.asignarPresupuesto(categoria, monto);
                txtPresupuesto.clear();
                refrescarCategorias();
                actualizarSaldoActual();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Monto inválido.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Error", "Seleccione una categoría y un monto.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void asignarLimite(ActionEvent e) {
        String categoria = cbxCategoriaLimite.getValue();
        if (categoria != null && !txtLimite.getText().isEmpty()) {
            try {
                double limite = Double.parseDouble(txtLimite.getText());
                movimientoService.asignarLimite(categoria, limite);
                txtLimite.clear();
                refrescarCategorias();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Error", "Límite inválido.", Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Error", "Seleccione una categoría y un límite.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    public void editarOCategorias(ActionEvent e) {
        String categoria = cbxCategoriaEditar.getValue();
        if (categoria == null) {
            mostrarAlerta("Error", "Seleccione una categoría para editar o eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Categoria cat = movimientoService.listarCategorias().stream()
                .filter(c -> c.getNombre().equals(categoria))
                .findFirst().orElse(null);

        if (cat == null) {
            mostrarAlerta("Error", "Categoría no encontrada.", Alert.AlertType.ERROR);
            return;
        }

        TextInputDialog dialog = new TextInputDialog(cat.getNombre());
        dialog.setTitle("Editar o Eliminar Categoría");
        dialog.setHeaderText("Modificar nombre o eliminar categoría");
        dialog.setContentText("Nuevo nombre (déjelo vacío para eliminar):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nuevo = result.get().trim();
            if (nuevo.isEmpty()) {
                movimientoService.eliminarCategoria(categoria);
                mostrarAlerta("Eliminada", "Categoría eliminada correctamente.", Alert.AlertType.INFORMATION);
            } else {
                movimientoService.editarCategoria(categoria, nuevo, cat.getPresupuesto());
                mostrarAlerta("Actualizada", "Categoría actualizada correctamente.", Alert.AlertType.INFORMATION);
            }
            refrescarCategorias();
            actualizarSaldoActual();
        }
    }

    private void refrescarCategorias() {
        listaCategorias.setAll(movimientoService.listarCategorias());
        tablaCategorias.setItems(listaCategorias);

        var categorias = FXCollections.observableArrayList(movimientoService.obtenerCategorias());
        cbxCategoriaPresupuesto.setItems(categorias);
        cbxCategoriaLimite.setItems(categorias);
        cbxCategoriaEditar.setItems(categorias);
    }

    private void actualizarSaldoActual() {
        double saldo = movimientoService.listarCategorias()
                .stream()
                .mapToDouble(Categoria::getSaldoDisponible)
                .sum();
        lblSaldoActual.setText("Saldo total: S/ " + String.format("%.2f", saldo));
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}