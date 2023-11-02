package com.example.practica_crud_mongo;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import org.bson.Document;
import utils.AlertUtils;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {

    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnModificar;
    @FXML
    private Button btnNuevo;
    @FXML
    private ComboBox<String> cbTipo;
    @FXML
    private TableColumn<?, ?> colMac;
    @FXML
    private TableColumn<?, ?> colMarca;
    @FXML
    private TableColumn<?, ?> colModelo;
    @FXML
    private TableColumn<?, ?> colTipo;
    @FXML
    private TableView<Dispositivo> tabla;
    @FXML
    private TextField txtMac;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtModelo;

    private ObservableList<Dispositivo> listaDocumentos;

    MongoClient conexion;
    MongoCollection<Document> collection = null;
    Gson gson = new Gson();
    Document doc;

    private String databaseName = "misDispositivos";
    private String json;

    private Dispositivo dispSeleccionado;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaDocumentos = FXCollections.observableArrayList();
        this.colMac.setCellValueFactory(new PropertyValueFactory("mac"));
        this.colMarca.setCellValueFactory(new PropertyValueFactory("marca"));
        this.colModelo.setCellValueFactory(new PropertyValueFactory("modelo"));
        this.colTipo.setCellValueFactory(new PropertyValueFactory("tipo"));

        String[] tipos = new String[]{"PC Portatil","PC Sobremesa","Smartphone","Tablet"};
        cbTipo.setItems(FXCollections.observableArrayList(tipos));
        modoEdicion(false);

        conexion = DAO.conectar();
        MongoDatabase database = conexion.getDatabase(databaseName);
        collection = database.getCollection("dispositivos");
        DAO.cargarDocumentos(listaDocumentos,collection);
        tabla.setItems(listaDocumentos);
    }

    @FXML
    void Cancelar(ActionEvent event) {
        vaciarCajas();
        modoEdicion(false);
    }

    @FXML
    void EliminarDisp(ActionEvent event) {

    }

    @FXML
    void GuardarDisp(ActionEvent event) {
        if(txtMac.getText().equals("")){
            AlertUtils.mostrarError("Debes rellenar el campo MAC");
        }
        else if(txtMarca.getText().equals("")){
            AlertUtils.mostrarError("Debes rellenar el campo Marca");
        }
        else if(txtModelo.getText().equals("")){
            AlertUtils.mostrarError("Debes rellenar el campo Modelo");
        }
        else if(cbTipo.getValue().equals("")){
            AlertUtils.mostrarError("Debes seleccionar el tipo de dispositivo");
        }
        else{
            String mac = txtMac.getText();
            String marca = txtMarca.getText();
            String modelo = txtModelo.getText();
            String tipo = cbTipo.getValue();
            Dispositivo d = new Dispositivo(mac,marca,modelo,tipo);

            json = gson.toJson(d);
            doc = Document.parse(json);
            collection.insertOne(doc);
        }
    }

    @FXML
    void ModificarDisp(ActionEvent event) {
        modoEdicion(true);
    }

    @FXML
    void NuevoDisp(ActionEvent event) {
        vaciarCajas();
        txtMac.requestFocus();
        modoEdicion(true);
    }

    @FXML
    void seleccionarDispositivo(MouseEvent event) {
        dispSeleccionado = tabla.getSelectionModel().getSelectedItem();
        cargarDispositivo(dispSeleccionado);
    }

    private void cargarDispositivo(Dispositivo d){
        txtMac.setText(d.getMac());
        txtMarca.setText(d.getMarca());
        txtModelo.setText(d.getModelo());
        cbTipo.setValue(d.getTipo());
    }

    private void vaciarCajas(){
        txtMac.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        cbTipo.setValue("");
    }

    private void modoEdicion(boolean activo){
        btnNuevo.setDisable(activo);
        btnGuardar.setDisable(!activo);
        btnModificar.setDisable(activo);
        btnEliminar.setDisable(activo);
        btnCancelar.setDisable(!activo);

        txtMac.setEditable(activo);
        txtMarca.setEditable(activo);
        txtModelo.setEditable(activo);
        cbTipo.setDisable(!activo);
    }
}
