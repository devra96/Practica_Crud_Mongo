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

    private String databaseName = "misDispositivos"; // NOMBRE DE LA BBDD A LA QUE NOS VAMOS A CONECTAR
    private ObservableList<Dispositivo> listaDocumentos; // LISTA DONDE INTRODUCIREMOS LOS REGISTROS DE LA BBDD.

    MongoClient conexion;
    MongoCollection<Document> collection = null;
    Gson gson = new Gson();
    Document doc, doc_old;

    private String json; // GUARDAREMOS UN OBJETO Dispositivo CONVERTIDO EN JSON
    private String json_old; // GUARDAREMOS EL OBJETO Dispositivo "dispSeleccionado" CONVERTIDO EN JSON

    private Dispositivo dispSeleccionado; // GUARDAREMOS EL REGISTRO SELECCIONADO EN LA TABLA
    private boolean modoModificacion; // FLAG QUE CONTROLARA SI VAMOS A MODIFICAR O INSERTAR

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaDocumentos = FXCollections.observableArrayList();
        this.colMac.setCellValueFactory(new PropertyValueFactory("mac"));
        this.colMarca.setCellValueFactory(new PropertyValueFactory("marca"));
        this.colModelo.setCellValueFactory(new PropertyValueFactory("modelo"));
        this.colTipo.setCellValueFactory(new PropertyValueFactory("tipo"));

        String[] tipos = new String[]{"PC Portatil","PC Sobremesa","Smartphone","Tablet"};
        cbTipo.setItems(FXCollections.observableArrayList(tipos));

        // CONECTAMOS A LA BBDD Y CARGAMOS LOS REGISTROS EN LA TABLA
        ConexionYCollection();
        ModoInicio();
    }

    /**
     * Funcion que se llama al pulsar el boton "Nuevo".
     * Vacia los campos editables, ubica el focus en el EditText "txtMac" y activa
     * el "Modo edicion".
     */
    @FXML
    void NuevoDisp(ActionEvent event) {
        vaciarCajas();
        txtMac.requestFocus();
        modoEdicion(true);
    }

    /**
     * Funcion que se llama al pulsar el boton "Modificar".
     * Comprueba si esta pulsado algun campo de la tabla y, si esta pulsado, activa
     * el "Modo edicion" y el "Modo modificacion".
     * En caso contrario, nos saldra un aviso.
     */
    @FXML
    void ModificarDisp(ActionEvent event) {
        if(tabla.getSelectionModel().getSelectedItem() == null){
            AlertUtils.mostrarError("NO HAS SELECCIONADO NINGUN DISPOSITIVO.");
        }
        else{
            modoEdicion(true);
            modoModificacion = true;
        }
    }

    /**
     * Funcion que se llama al pulsar el boton "Eliminar".
     * Comprueba si esta pulsado algun campo de la tabla y, si esta pulsado, nos
     * pedira confirmacion de si queremos borrar o no dicho campo de la BBDD.
     * Si confirmamos, nos borra el campo de la BBDD. En caso contrario, vuelve al "Modo inicio".
     * Si no hemos seleccionado ningun campo de la tabla, nos saldra un aviso.
     */
    @FXML
    void EliminarDisp(ActionEvent event) {
        if(tabla.getSelectionModel().getSelectedItem() == null){
            AlertUtils.mostrarError("NO HAS SELECCIONADO NINGUN DISPOSITIVO.");
        }
        else{
            if(AlertUtils.confirmacion()){
                String mac = txtMac.getText();
                String marca = txtMarca.getText();
                String modelo = txtModelo.getText();
                String tipo = cbTipo.getValue();
                Dispositivo d = new Dispositivo(mac,marca,modelo,tipo);

                // VACIAMOS CAMPOS Y CONECTAMOS A LA BBDD
                ConexionYCollection();

                // DELETE
                json = gson.toJson(d);
                doc = Document.parse(json);
                collection.deleteOne(doc);
                System.out.println("Dispositivo borrado correctamente.");

                // ACTUALIZAMOS LA TABLA Y DESCONECTAMOS DE LA BBDD.
                ModoInicio();
            }
            else{
                // LO MISMO QUE EL BOTON CANCELAR
                ConexionYCollection();
                ModoInicio();
            }
        }
    }

    /**
     * Funcion que se llama al pulsar el boton "Guardar".
     * Primero comprueba si hay algun campo sin rellenar (si lo hay, mostrara un aviso).
     * Despues comprobara si estamos en el "Modo modificacion" o no:
     *
     * - SI ESTAMOS EN MODO MODIFICACION, primero comprueba si hemos modificado algun
     *   campo (si no es asi, mostrara un aviso).
     *   Cuando hayamos modificado algun campo, modificara el registro seleccionado en
     *   la BBDD.
     *
     * - SI NO ESTAMOS EN MODO MODIFICACION, se insertara un registro en la BBDD con los
     *   datos indicados.
     *
     */
    @FXML
    void GuardarDisp(ActionEvent event) {
        // SI ALGUNO DE LOS CAMPOS ESTA VACIO
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
        // SI SE HAN RELLENADO TODOS LOS CAMPOS
        else{
            // CREAMOS UN OBJETO Dispositivo CON LOS DATOS INTRODUCIDOS
            String mac = txtMac.getText();
            String marca = txtMarca.getText();
            String modelo = txtModelo.getText();
            String tipo = cbTipo.getValue();
            Dispositivo d = new Dispositivo(mac,marca,modelo,tipo);

            // SI QUEREMOS AÑADIR UN DISPOSITIVO
            if(!modoModificacion){
                // VACIAMOS CAMPOS Y CONECTAMOS A LA BBDD
                ConexionYCollection();

                // INSERT
                json = gson.toJson(d);
                doc = Document.parse(json);
                collection.insertOne(doc);
                System.out.println("Dispositivo insertado correctamente.");

                // ACTUALIZAMOS LA TABLA Y DESCONECTAMOS DE LA BBDD.
                ModoInicio();
            }
            // SI QUEREMOS MODIFICAR UN DISPOSITIVO
            else{
                // SI NO SE HA MODIFICADO NINGUN CAMPO
                if(d.getMac().equals(dispSeleccionado.getMac()) &&
                        d.getMarca().equals(dispSeleccionado.getMarca()) &&
                        d.getModelo().equals(dispSeleccionado.getModelo()) &&
                        d.getTipo().equals(dispSeleccionado.getTipo())){
                    AlertUtils.mostrarError("¡NO HAS MODIFICADO NADA!");
                }
                else{
                    // VACIAMOS CAMPOS Y CONECTAMOS A LA BBDD
                    ConexionYCollection();

                    // UPDATE
                    json = gson.toJson(d);
                    json_old = gson.toJson(dispSeleccionado);
                    doc = Document.parse(json);
                    doc_old = Document.parse(json_old);
                    collection.updateOne(doc_old,new Document("$set",doc));
                    System.out.println("Dispositivo modificado correctamente.");

                    // ACTUALIZAMOS LA TABLA Y DESCONECTAMOS DE LA BBDD.
                    ModoInicio();
                }
            }
        }
    }

    /**
     * Funcion que se llama al pulsar el boton "Cancelar".
     * Nos conectamos a la BBDD y descargamos los registros de la collection
     * para volver a introducirlos en la tabla. Esto se hace para eliminar el
     * focus en la tabla si lo hubiera. Tambien activamos el "Modo inicio".
     */
    @FXML
    void Cancelar(ActionEvent event) {
        ConexionYCollection();
        ModoInicio();
    }

    /**
     * Funcion que se llama al pulsar un campo de la tabla.
     * Rellena los EditText del programa con los valores del registro pulsado
     * y se guarda en la variable Dispositivo "dispSeleccionado" el objeto de
     * dicho registro
     */
    @FXML
    void seleccionarDispositivo(MouseEvent event) {
        dispSeleccionado = tabla.getSelectionModel().getSelectedItem();
        cargarDispositivo(dispSeleccionado);
    }

    /**
     * Metodo que simplifica:
     *
     * - Vaciar los EditText del programa (para cuando se van a insertar
     *   un registro o se da al boton cancelar para ir al "Modo inicio")
     * - Conectar a la BBDD
     * - Seleccionar una coleccion.
     */
    private void ConexionYCollection(){
        vaciarCajas();

        conexion = DAO.conectar();

        MongoDatabase database = conexion.getDatabase(databaseName);
        collection = database.getCollection("dispositivos");
    }

    /**
     * Metodo que activa el "Modo inicio", es decir, que pone al programa
     * en el estado inicial que veeriamos al arrancar el programa.
     *
     * - Vacia los registros de la tabla
     * - Carga los registros de la BBDD en la tabla
     * - Se desconecta de la BBDD
     * - Desactiva el "Modo edicion" y el "Modo modificacion"
     */
    private void ModoInicio(){
        tabla.getItems().clear();

        DAO.cargarDocumentos(listaDocumentos,collection);
        tabla.setItems(listaDocumentos);

        DAO.desconectar(conexion);

        modoEdicion(false);
        modoModificacion = false;
    }

    /**
     * Metodo que carga los atributos de un dispositivo en los EditText
     * del programa.
     * @param d El dispositivo del cual queremos cargar los atributos.
     */
    private void cargarDispositivo(Dispositivo d){
        txtMac.setText(d.getMac());
        txtMarca.setText(d.getMarca());
        txtModelo.setText(d.getModelo());
        cbTipo.setValue(d.getTipo());
    }

    /**
     * Metodo que vacia los EditText
     */
    private void vaciarCajas(){
        txtMac.setText("");
        txtMarca.setText("");
        txtModelo.setText("");
        cbTipo.setValue("");
    }

    /**
     * Metodo que activa el "Modo edicion" o no, segun el valor del parametro "activo".
     * Activa o desactiva algunos elementos del programa en base a la accion que queramos
     * realizar.
     * @param activo Valor booleano para activar o no el "Modo edicion"
     */
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
        tabla.setDisable(activo);
    }
}
