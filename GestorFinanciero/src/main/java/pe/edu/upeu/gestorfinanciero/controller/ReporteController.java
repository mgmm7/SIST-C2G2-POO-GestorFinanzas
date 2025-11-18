package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.dto.MovimientoDto;
import pe.edu.upeu.gestorfinanciero.model.Categoria;
import pe.edu.upeu.gestorfinanciero.model.Egreso;
import pe.edu.upeu.gestorfinanciero.model.Ingreso;
import pe.edu.upeu.gestorfinanciero.model.Usuario;
import pe.edu.upeu.gestorfinanciero.service.CategoriaService;
import pe.edu.upeu.gestorfinanciero.service.EgresoService;
import pe.edu.upeu.gestorfinanciero.service.IngresoService;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ReporteController {

    // === FXML ===
    @FXML private ComboBox<String> cmbCategorias;
    @FXML private TableView<MovimientoDto> tablaMovimientos;

    @FXML private TableColumn<MovimientoDto, String> colTipo;
    @FXML private TableColumn<MovimientoDto, String> colCategoria;
    @FXML private TableColumn<MovimientoDto, String> colDescripcion;
    @FXML private TableColumn<MovimientoDto, Double> colMonto;
    @FXML private TableColumn<MovimientoDto, String> colFecha;

    @FXML private ListView<String> lvCategorias;

    @FXML private Label lblTotalIngresos;
    @FXML private Label lblTotalEgresos;
    @FXML private Label lblSaldoActual;

    @FXML private Label lblDetCategoria;
    @FXML private Label lblDetPresupuesto;
    @FXML private Label lblDetLimite;
    @FXML private Label lblDetSaldo;

    @FXML private TableView<MovimientoDto> tablaDetalle;
    @FXML private TableColumn<MovimientoDto, String> detTipo;
    @FXML private TableColumn<MovimientoDto, String> detDesc;
    @FXML private TableColumn<MovimientoDto, Double> detMonto;
    @FXML private TableColumn<MovimientoDto, String> detFecha;

    // === SERVICES ===
    private final CategoriaService categoriaService;
    private final IngresoService ingresoService;
    private final EgresoService egresoService;
    private final UsuarioSesion usuarioSesion;

    @FXML
    public void initialize() {

        Usuario usuario = usuarioSesion.getUsuarioActual();

        // === Configurar tabla principal ===
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        // === Configurar tabla detalle ===
        detTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        detDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        detMonto.setCellValueFactory(new PropertyValueFactory<>("monto"));
        detFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        // --- Agregar categorías especiales ---
        cmbCategorias.getItems().addAll("General", "Ingresos", "Egresos");

        // --- Agregar categorías del usuario ---
        var cats = categoriaService.listarCategorias(usuario);
        for (Categoria c : cats) {
            cmbCategorias.getItems().add(c.getNombre());
        }

        cmbCategorias.setOnAction(e -> cargarCategoria(cmbCategorias.getValue()));

        // === Lista derecha ===
        lvCategorias.setItems(FXCollections.observableArrayList(
                cats.stream().map(Categoria::getNombre).toList()
        ));

        lvCategorias.setOnMouseClicked(e -> {
            String nombre = lvCategorias.getSelectionModel().getSelectedItem();
            if (nombre != null) cargarDetalleCategoria(nombre);
        });
        tablaMovimientos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaDetalle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // === Totales ===
        actualizarTotales();

    }



    // ==========================================================
    //                      TOTALES
    // ==========================================================
    private void actualizarTotales() {
        Usuario u = usuarioSesion.getUsuarioActual();

        double totalIng = ingresoService.total(u);
        double totalEg  = egresoService.total(u);

        lblTotalIngresos.setText("Total Ingresos: S/ " + totalIng);
        lblTotalEgresos.setText("Total Egresos: S/ " + totalEg);
        lblSaldoActual.setText("Saldo Actual: S/ " + (totalIng - totalEg));
    }



    // ==========================================================
    //              MOVIMIENTOS SEGÚN CATEGORÍA SELECCIONADA
    // ==========================================================
    private void cargarCategoria(String nombre) {

        Usuario u = usuarioSesion.getUsuarioActual();
        List<MovimientoDto> lista = new ArrayList<>();

        // === GENERAL ===
        if (nombre.equals("General")) {
            lista.addAll(obtenerTodos(u));
        }

        // === SOLO INGRESOS ===
        else if (nombre.equals("Ingresos")) {
            for (Ingreso i : ingresoService.listar(u)) {
                lista.add(new MovimientoDto(
                        "Ingreso",
                        "General",
                        i.getDescripcion(),
                        i.getMonto(),
                        i.getFecha()
                ));
            }
        }

        // === SOLO EGRESOS ===
        else if (nombre.equals("Egresos")) {
            for (Egreso eg : egresoService.listar(u)) {
                lista.add(new MovimientoDto(
                        "Egreso",
                        eg.getCategoria(),
                        eg.getDescripcion(),
                        eg.getMonto(),
                        eg.getFecha()
                ));
            }
        }

        // === CATEGORÍA DEL USUARIO ===
        else {
            Categoria c = categoriaService.listarCategorias(u)
                    .stream()
                    .filter(x -> x.getNombre().equals(nombre))
                    .findFirst().orElse(null);

            if (c != null) {
                lista.addAll(obtenerPorCategoria(u, c));
            }
        }

        // Orden descendente por fecha (string DD/MM/YYYY)
        lista.sort(Comparator.comparing(MovimientoDto::getFecha).reversed());

        tablaMovimientos.setItems(FXCollections.observableArrayList(lista));
    }


    // ==========================================================
    //                       DETALLE DERECHA
    // ==========================================================
    private void cargarDetalleCategoria(String nombre) {

        Usuario u = usuarioSesion.getUsuarioActual();

        Categoria c = categoriaService.listarCategorias(u)
                .stream()
                .filter(x -> x.getNombre().equals(nombre))
                .findFirst()
                .orElse(null);

        if (c == null) return;

        lblDetCategoria.setText(c.getNombre());
        lblDetPresupuesto.setText("S/ " + c.getPresupuesto());
        lblDetLimite.setText("S/ " + c.getLimite());
        lblDetSaldo.setText("S/ " + c.getSaldoDisponible());

        tablaDetalle.setItems(FXCollections.observableArrayList(
                obtenerPorCategoria(u, c)
        ));
    }



    // ==========================================================
    //                      MÉTODOS INTERNOS
    // ==========================================================
    private List<MovimientoDto> obtenerTodos(Usuario u) {

        List<MovimientoDto> lista = new ArrayList<>();

        // Ingresos
        for (Ingreso i : ingresoService.listar(u)) {
            lista.add(new MovimientoDto(
                    "Ingreso",
                    "General",
                    i.getDescripcion(),
                    i.getMonto(),
                    i.getFecha()
            ));
        }

        // Egresos
        for (Egreso eg : egresoService.listar(u)) {
            lista.add(new MovimientoDto(
                    "Egreso",
                    eg.getCategoria(),
                    eg.getDescripcion(),
                    eg.getMonto(),
                    eg.getFecha()
            ));
        }

        return lista;
    }


    private List<MovimientoDto> obtenerPorCategoria(Usuario u, Categoria c) {

        List<MovimientoDto> lista = new ArrayList<>();

        // Agregar solo egresos de esa categoría:
        for (Egreso e : egresoService.listar(u)) {
            if (e.getCategoria().equals(c.getNombre())) {
                lista.add(new MovimientoDto(
                        "Egreso",
                        c.getNombre(),
                        e.getDescripcion(),
                        e.getMonto(),
                        e.getFecha()
                ));
            }
        }

        return lista;
    }
}

