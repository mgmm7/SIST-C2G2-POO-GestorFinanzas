package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.CategoriaService;
import pe.edu.upeu.gestorfinanciero.service.MovimientoService;
import pe.edu.upeu.gestorfinanciero.service.IngresoService;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MovimientoController {

    private final UsuarioSesion usuarioSesion;
    private final MovimientoService movimientoService;
    private final CategoriaService categoriaService;
    private final IngresoService ingresoService; // <-- inyectamos IngresoService

    private Usuario usuarioActual;

    // === CAMBIO DE MONEDA ===
    private String currentCurrency = "S/";
    @FXML private ComboBox<String> cbMoneda;

    @FXML private Label lblSaldoActual; // etiqueta arriba a la derecha

    @FXML private TextField txtNuevaCategoria;
    @FXML private TextField txtPresupuesto;
    @FXML private TextField txtLimite;

    @FXML private ComboBox<String> cbxPresupuesto;
    @FXML private ComboBox<String> cbxLimite;
    @FXML private ComboBox<String> cbxCategoriaEditar;

    @FXML private TableView<Categoria> tablaCategorias;

    @FXML private TableColumn<Categoria, String> colNombre;
    @FXML private TableColumn<Categoria, Double> colPresupuesto;
    @FXML private TableColumn<Categoria, Double> colLimite;
    @FXML private TableColumn<Categoria, Double> colSaldo;

    @FXML
    public void initialize() {
        usuarioActual = usuarioSesion.getUsuarioActual();

        // cargar opciones de moneda
        cbMoneda.setItems(FXCollections.observableArrayList("S/", "$", "€"));
        cbMoneda.setValue("S/");

        cbMoneda.valueProperty().addListener((obs, oldV, newV) -> {
            currentCurrency = newV;
            refrescarTodo();
        });

        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));
        colPresupuesto.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(convert(c.getValue().getPresupuesto())).asObject());
        colLimite.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(convert(c.getValue().getLimite())).asObject());
        colSaldo.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(convert(c.getValue().getSaldoDisponible())).asObject());

        refrescarTodo();
    }

    private double convert(double monto) {
        return switch (currentCurrency) {
            case "$" -> monto * 0.27;   // soles → dólares
            case "€" -> monto * 0.25;   // soles → euros
            default -> monto;           // S/ sin cambio
        };
    }

    private void refrescarSaldoGeneral() {
        // obtenemos el saldo general usando IngresoService (igual que en IngresoController)
        double totalPen = ingresoService.total(usuarioActual);
        double totalConv;

        switch (currentCurrency) {
            case "$" -> totalConv = totalPen * 0.27;
            case "€" -> totalConv = totalPen * 0.25;
            default -> totalConv = totalPen;
        }

        lblSaldoActual.setText("Saldo total: " + currentCurrency + String.format("%.2f", totalConv));
    }

    private void refrescarTodo() {
        List<Categoria> cats = movimientoService.listarCategoriasUsuario(usuarioActual);
        tablaCategorias.getItems().setAll(cats);

        var nombres = FXCollections.observableArrayList(cats.stream().map(Categoria::getNombre).toList());
        cbxPresupuesto.setItems(nombres);
        cbxLimite.setItems(nombres);
        cbxCategoriaEditar.setItems(nombres);

        refrescarSaldoGeneral(); // <--- actualizamos el saldo general
    }

    @FXML
    public void crearCategoria() {
        String nombre = txtNuevaCategoria.getText().trim();
        if (nombre.isEmpty()) { alerta("Ingrese un nombre."); return; }

        Categoria c = movimientoService.crearCategoria(nombre, usuarioActual);
        if (c == null) { alerta("Ya existe una categoría con ese nombre."); return; }

        txtNuevaCategoria.clear();
        refrescarTodo();
    }

    @FXML
    public void asignarPresupuesto() {
        String cat = cbxPresupuesto.getValue();
        if (cat == null) { alerta("Seleccione una categoría."); return; }
        double monto;
        try { monto = Double.parseDouble(txtPresupuesto.getText().trim()); }
        catch (Exception ex) { alerta("Monto inválido."); return; }

        movimientoService.asignarPresupuesto(cat, monto, usuarioActual);
        txtPresupuesto.clear();
        refrescarTodo();
    }

    @FXML
    public void asignarLimite() {
        String cat = cbxLimite.getValue();
        if (cat == null) { alerta("Seleccione una categoría."); return; }
        double limite;
        try { limite = Double.parseDouble(txtLimite.getText().trim()); }
        catch (Exception ex) { alerta("Límite inválido."); return; }

        movimientoService.asignarLimite(cat, limite, usuarioActual);
        txtLimite.clear();
        refrescarTodo();
    }

    @FXML
    public void editarCategoria() {
        String cat = cbxCategoriaEditar.getValue();
        if (cat == null) { alerta("Seleccione una categoría."); return; }

        TextInputDialog dlg = new TextInputDialog(cat);
        dlg.setTitle("Editar Categoría");
        dlg.setHeaderText("Ingrese nuevo nombre");
        dlg.setContentText("Nuevo nombre:");

        Optional<String> res = dlg.showAndWait();
        if (res.isPresent()) {
            String nuevo = res.get().trim();
            if (nuevo.isEmpty()) { alerta("Nombre vacío."); return; }

            movimientoService.editarCategoria(cat, nuevo, usuarioActual);
            refrescarTodo();
        }
    }

    @FXML
    public void eliminarCategoria() {
        String cat = cbxCategoriaEditar.getValue();
        if (cat == null) { alerta("Seleccione una categoría."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Eliminar \"" + cat + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.setHeaderText(null);
        Optional<ButtonType> r = confirm.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) {
            movimientoService.eliminarCategoria(cat, usuarioActual);
            refrescarTodo();
        }
    }

    private void alerta(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
