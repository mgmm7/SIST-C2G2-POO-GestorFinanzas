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
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.MovimientoService;

@Controller
@RequiredArgsConstructor
public class MovimientoController {

    private final UsuarioSesion usuarioSesion;
    private final MovimientoService movimientoService;

    private Usuario usuarioActual;

    @FXML private TextField txtNuevaCategoria;
    @FXML private TextField txtPresupuesto;
    @FXML private TextField txtLimite;

    @FXML private ComboBox<String> cbxPresupuesto;
    @FXML private ComboBox<String> cbxLimite;
    @FXML private ComboBox<String> cbxEditar;

    @FXML private TableView<Categoria> tablaCategorias;

    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Double> colPresupuesto;
    @FXML private TableColumn<Categoria, Double> colLimite;
    @FXML private TableColumn<Categoria, Double> colSaldo;

    private final ObservableList<Categoria> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        usuarioActual = usuarioSesion.getUsuarioActual();

        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colPresupuesto.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getPresupuesto()).asObject());
        colLimite.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getLimite()).asObject());
        colSaldo.setCellValueFactory(c -> new javafx.beans.property.SimpleDoubleProperty(c.getValue().getSaldoDisponible()).asObject());

        refrescarTodo();
    }

    private void refrescarTodo() {
        lista.setAll(movimientoService.listarCategoriasUsuario(usuarioActual));
        tablaCategorias.setItems(lista);

        var nombres = FXCollections.observableArrayList(
                lista.stream().map(Categoria::getNombre).toList()
        );

        cbxPresupuesto.setItems(nombres);
        cbxLimite.setItems(nombres);
        cbxEditar.setItems(nombres);
    }

    @FXML
    public void crearCategoria(ActionEvent e) {
        String nombre = txtNuevaCategoria.getText().trim();
        if (nombre.isEmpty()) {
            alert("Ingrese un nombre.");
            return;
        }

        Categoria c = movimientoService.crearCategoria(nombre, usuarioActual);
        if (c == null) {
            alert("Ya existe una categoría con ese nombre.");
            return;
        }

        txtNuevaCategoria.clear();
        refrescarTodo();
    }

    @FXML
    public void asignarPresupuesto(ActionEvent e) {
        String cat = cbxPresupuesto.getValue();
        if (cat == null) {
            alert("Seleccione una categoría.");
            return;
        }

        double monto;
        try { monto = Double.parseDouble(txtPresupuesto.getText()); }
        catch (Exception ex) {
            alert("Monto inválido.");
            return;
        }

        movimientoService.asignarPresupuesto(cat, monto, usuarioActual);
        txtPresupuesto.clear();
        refrescarTodo();
    }

    @FXML
    public void asignarLimite(ActionEvent e) {
        String cat = cbxLimite.getValue();
        if (cat == null) {
            alert("Seleccione una categoría.");
            return;
        }

        double limite;
        try { limite = Double.parseDouble(txtLimite.getText()); }
        catch (Exception ex) {
            alert("Límite inválido.");
            return;
        }

        movimientoService.asignarLimite(cat, limite, usuarioActual);
        txtLimite.clear();
        refrescarTodo();
    }

    @FXML
    public void editarOCategorias(ActionEvent e) {
        String cat = cbxEditar.getValue();
        if (cat == null) {
            alert("Seleccione una categoría.");
            return;
        }

        TextInputDialog dlg = new TextInputDialog(cat);
        dlg.setTitle("Editar Categoría");
        dlg.setHeaderText("Ingrese nuevo nombre (o vacío para eliminar)");
        dlg.setContentText("Nuevo nombre:");

        var res = dlg.showAndWait();
        if (res.isPresent()) {
            String nuevo = res.get().trim();
            if (nuevo.isEmpty()) {
                movimientoService.eliminarCategoria(cat, usuarioActual);
            } else {
                movimientoService.editarCategoria(cat, nuevo, usuarioActual);
            }
            refrescarTodo();
        }
    }

    private void alert(String m) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(m);
        a.show();
    }
}
