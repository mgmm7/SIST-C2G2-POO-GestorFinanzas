package pe.edu.upeu.gestorfinanciero.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import pe.edu.upeu.gestorfinanciero.model.Participante;
import pe.edu.upeu.gestorfinanciero.service.IParticipanteService;

@Controller
 public class  ParticipanteController {
    @FXML
    private TextField txtUsuario, txtContraseña;
    @FXML
    private TableView<Participante> tableView;
    ObservableList<Participante> listaParticipantes;
    @FXML
    private TableColumn<Participante, String> usuarioColum, contraseñaColum;
    private TableColumn<Participante, Void> opcColum;
    @Autowired
    IParticipanteService ps;
    int indexE = -1;

    @FXML
    public void initialize() {
        definirColumnas();
        listarParticipantes();
    }


    public void limpiarFormulario() {
        txtUsuario.setText("");
        txtContraseña.setText("");
    }

    @FXML
    public void registrarParticipante() {
        Participante p = new Participante();
        p.setUsuario(txtUsuario.getText());
        p.setContrasenia(txtContraseña.getText());
        if(indexE==-1){
            ps.save(p);
        }else{
            ps.update(p);
            indexE=-1;
        }
        limpiarFormulario();
        listarParticipantes();

    }
    public void definirColumnas() {
        usuarioColum = new TableColumn("Usuario");
        contraseñaColum = new TableColumn<>("contraseña");
        opcColum = new TableColumn("Opciones");
        opcColum.setPrefWidth(200);
        tableView.getColumns().addAll(usuarioColum, contraseñaColum, opcColum);
    }
    public void agregarAccionBotones() {
        Callback<TableColumn<Participante, Void>, TableCell<Participante, Void>> cellFactory =
                param -> new TableCell<>() {
                    private final Button editarBtn = new Button("Editar");
                    private final Button eliminarBtn = new Button("Eliminar");

                    {
                        editarBtn.setOnAction(event -> {
                            Participante p = getTableView().getItems().get(getIndex());
                            editarDatos(p, getIndex());
                        });
                        eliminarBtn.setOnAction(event -> {
                            Participante p = getTableView().getItems().get(getIndex());
                            eliminarParticipante(p.getUsuario());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox hbox = new HBox(editarBtn, eliminarBtn);
                            hbox.setSpacing(10);
                            setGraphic(hbox);
                        }
                    }
                };
        opcColum.setCellFactory(cellFactory);
    }
    public void listarParticipantes(){

        usuarioColum.setCellValueFactory(cellData->
                new SimpleStringProperty(cellData.getValue().getUsuario()));
        contraseñaColum.setCellValueFactory(cellData->
                new SimpleStringProperty(cellData.getValue().getContrasenia()));

        agregarAccionBotones();
        listaParticipantes= FXCollections.observableArrayList(ps.findAll());
        tableView.setItems(listaParticipantes);
    }
    public void eliminarParticipante(String usuario){
        ps.delete(usuario);
        listarParticipantes();
    }
    public void editarDatos(Participante p, int index){
        txtUsuario.setText(p.getUsuario());
        txtContraseña.setText(p.getContrasenia());
        indexE=index;
    }


}