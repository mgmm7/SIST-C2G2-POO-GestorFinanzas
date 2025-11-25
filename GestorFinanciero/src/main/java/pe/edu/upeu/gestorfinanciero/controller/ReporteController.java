package pe.edu.upeu.gestorfinanciero.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.config.UsuarioSesion;
import pe.edu.upeu.gestorfinanciero.dto.MovimientoDto;
import pe.edu.upeu.gestorfinanciero.model.*;
import pe.edu.upeu.gestorfinanciero.service.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class ReporteController {

    // ======================== FXML PRINCIPALES ==========================
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
    @FXML private ComboBox<String> cbReporteMoneda; // NUEVO: cambio de moneda

    // ====================================================================

    private final CategoriaService categoriaService;
    private final IngresoService ingresoService;
    private final EgresoService egresoService;
    private final MovimientoService movimientoService;
    private final MetaService metaService;
    private final AporteService aporteService;
    private final UsuarioSesion usuarioSesion;

    private final DateTimeFormatter F = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private List<Categoria> cats = new ArrayList<>();
    private List<Meta> metas = new ArrayList<>();

    private String currentCurrency = "S/"; // moneda actual

    private double convert(double m) {
        return switch (currentCurrency) {
            case "$" -> m * 0.27;
            case "€" -> m * 0.25;
            default -> m;
        };
    }

    @FXML
    public void initialize() {
        Usuario usuario = usuarioSesion.getUsuarioActual();

        // ====================== Configuración de columnas ===================
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colMonto.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(convert(c.getValue().getMonto())).asObject()
        );
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        detTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        detDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        detMonto.setCellValueFactory(c ->
                new javafx.beans.property.SimpleDoubleProperty(convert(c.getValue().getMonto())).asObject()
        );
        detFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tablaMovimientos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaDetalle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // ======================= Inicializar monedas ======================
        cbReporteMoneda.setItems(FXCollections.observableArrayList("S/", "$", "€"));
        cbReporteMoneda.setValue("S/");
        cbReporteMoneda.valueProperty().addListener((o, v, n) -> {
            currentCurrency = n;
            actualizarTotales();
            cargarCategoria(cmbCategorias.getValue());
        });

        // ======================= Cargar datos iniciales ==================
        refreshCategoriasYMetas();

        cmbCategorias.getItems().clear();
        cmbCategorias.getItems().addAll("General", "Ingresos", "Egresos", "Metas");
        cats.forEach(c -> cmbCategorias.getItems().add(c.getNombre()));
        metas.forEach(m -> cmbCategorias.getItems().add("Meta: " + m.getNombre()));

        cmbCategorias.setOnAction(e -> cargarCategoria(cmbCategorias.getValue()));

        List<String> lateral = new ArrayList<>();
        lateral.addAll(cats.stream().map(Categoria::getNombre).toList());
        lateral.addAll(metas.stream().map(m -> "[Meta] " + m.getNombre()).toList());
        lvCategorias.setItems(FXCollections.observableArrayList(lateral));

        lvCategorias.setOnMouseClicked(e -> {
            String sel = lvCategorias.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            if (sel.startsWith("[Meta] ")) {
                String nombreMeta = sel.substring(7);
                Meta m = metas.stream().filter(x -> x.getNombre().equals(nombreMeta)).findFirst().orElse(null);
                if (m != null) cargarDetalleMeta(m);
            } else {
                cargarDetalleCategoria(sel);
            }
        });

        actualizarTotales();
    }

    private void refreshCategoriasYMetas() {
        Usuario u = usuarioSesion.getUsuarioActual();
        this.cats = movimientoService.listarCategoriasUsuario(u);
        this.metas = metaService.listarMetasUsuario(u);
    }

    private void actualizarTotales() {
        Usuario u = usuarioSesion.getUsuarioActual();
        double totIng = ingresoService.total(u);
        double totEg  = egresoService.total(u);

        lblTotalIngresos.setText("Total Ingresos: " + currentCurrency + " " + String.format("%.2f", convert(totIng)));
        lblTotalEgresos.setText("Total Egresos: " + currentCurrency + " " + String.format("%.2f", convert(totEg)));

    }

    private void cargarCategoria(String nombre) {
        if (nombre == null) return;
        Usuario u = usuarioSesion.getUsuarioActual();
        List<MovimientoDto> lista = new ArrayList<>();

        switch (nombre) {
            case "General" -> lista.addAll(obtenerTodos(u));
            case "Ingresos" -> ingresoService.listar(u).forEach(i ->
                    lista.add(new MovimientoDto(
                            i.getId(),
                            i.getTipo(),
                            "General",
                            i.getDescripcion(),
                            convert(i.getMonto()),
                            safe(i.getFecha()),
                            null))
            );
            case "Egresos" -> egresoService.listar(u).forEach(e ->
                    lista.add(new MovimientoDto(
                            e.getId(),
                            "Egreso",
                            e.getCategoria(),
                            e.getDescripcion(),
                            convert(e.getMonto()),
                            safe(e.getFecha()),
                            null))
            );
            case "Metas" -> metas.forEach(m ->
                    lista.add(new MovimientoDto(
                            m.getId(),
                            "Meta",
                            m.getNombre(),
                            "Creación de meta",
                            convert(m.getAportado()),
                            safeDate(m.getFechaRegistro()),
                            null))
            );
            default -> {
                if (nombre.startsWith("Meta: ")) {
                    String metaNom = nombre.substring(6);
                    Meta m = metas.stream().filter(x -> x.getNombre().equals(metaNom)).findFirst().orElse(null);
                    if (m != null) {
                        aporteService.listarPorMeta(m).forEach(a ->
                                lista.add(new MovimientoDto(
                                        a.getId(),
                                        "Aporte",
                                        m.getNombre(),
                                        a.getDescripcion(),
                                        convert(a.getMonto()),
                                        safeDate(a.getFecha()),
                                        a.getEvidencia()
                                ))
                        );
                    }
                } else {
                    Categoria c = cats.stream().filter(x -> x.getNombre().equals(nombre)).findFirst().orElse(null);
                    if (c != null) lista.addAll(obtenerPorCategoria(u, c));
                }
            }
        }

        lista.sort(Comparator.comparing(this::toDate, Comparator.nullsLast(Comparator.reverseOrder())));
        tablaMovimientos.setItems(FXCollections.observableArrayList(lista));
    }

    private void cargarDetalleCategoria(String nombre) {
        Usuario u = usuarioSesion.getUsuarioActual();
        Categoria c = cats.stream().filter(x -> x.getNombre().equals(nombre)).findFirst().orElse(null);
        if (c == null) return;

        lblDetCategoria.setText(c.getNombre());
        lblDetPresupuesto.setText(currentCurrency + " " + String.format("%.2f", convert(c.getPresupuesto())));
        lblDetLimite.setText(currentCurrency + " " + String.format("%.2f", convert(c.getLimite())));
        lblDetSaldo.setText(currentCurrency + " " + String.format("%.2f", convert(c.getSaldoDisponible())));

        tablaDetalle.setItems(FXCollections.observableArrayList(
                obtenerPorCategoria(u, c)
        ));
    }

    private void cargarDetalleMeta(Meta m) {
        lblDetCategoria.setText("[Meta] " + m.getNombre());
        lblDetPresupuesto.setText("Objetivo: " + currentCurrency + " " + String.format("%.2f", convert(m.getObjetivo())));
        lblDetLimite.setText("Aportado: " + currentCurrency + " " + String.format("%.2f", convert(m.getAportado())));
        lblDetSaldo.setText("Fecha límite: " + safeDate(m.getFechaLimite()));

        List<MovimientoDto> lista = aporteService.listarPorMeta(m).stream()
                .map(a -> new MovimientoDto(
                        a.getId(),
                        "Aporte",
                        m.getNombre(),
                        a.getDescripcion(),
                        convert(a.getMonto()),
                        safeDate(a.getFecha()),
                        a.getEvidencia()
                ))
                .collect(Collectors.toList());

        tablaDetalle.setItems(FXCollections.observableArrayList(lista));
    }

    private List<MovimientoDto> obtenerTodos(Usuario u) {
        List<MovimientoDto> lista = new ArrayList<>();
        ingresoService.listar(u).forEach(i ->
                lista.add(new MovimientoDto(
                        i.getId(),
                        i.getTipo(),
                        "General",
                        i.getDescripcion(),
                        convert(i.getMonto()),
                        safe(i.getFecha()),
                        null))
        );

        egresoService.listar(u).forEach(e ->
                lista.add(new MovimientoDto(
                        e.getId(),
                        "Egreso",
                        e.getCategoria(),
                        e.getDescripcion(),
                        convert(e.getMonto()),
                        safe(e.getFecha()),
                        null))
        );

        metas.forEach(m ->
                aporteService.listarPorMeta(m).forEach(a ->
                        lista.add(new MovimientoDto(
                                a.getId(),
                                "Aporte",
                                "Meta: " + m.getNombre(),
                                a.getDescripcion(),
                                convert(a.getMonto()),
                                safeDate(a.getFecha()),
                                a.getEvidencia()
                        ))
                )
        );

        return lista;
    }

    private List<MovimientoDto> obtenerPorCategoria(Usuario u, Categoria c) {
        return egresoService.listar(u).stream()
                .filter(e -> c.getNombre().equals(e.getCategoria()))
                .map(e -> new MovimientoDto(
                        e.getId(),
                        "Egreso",
                        c.getNombre(),
                        e.getDescripcion(),
                        convert(e.getMonto()),
                        safe(e.getFecha()),
                        null))
                .collect(Collectors.toList());
    }

    private LocalDate toDate(MovimientoDto m) {
        if (m == null || m.getFecha() == null || m.getFecha().isBlank()) return null;
        try { return LocalDate.parse(m.getFecha(), F); }
        catch (Exception e) { return null; }
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private String safeDate(LocalDate d) {
        return d == null ? "" : d.format(F);
    }
}
