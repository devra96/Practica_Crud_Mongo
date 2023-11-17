package com.example.practica_crud_mongo;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.collections.ObservableList;
import org.bson.Document;

import java.util.Properties;

import static utils.R.getProperties;

public class DAO {
    public static MongoClient conectar(){
        try{
            // =========== CONEXION POR ARCHIVO "database.properties" ==========
            Properties configuration = new Properties();
            configuration.load(getProperties("database.properties"));
            String host = configuration.getProperty("host");
            String port = configuration.getProperty("port");

            // =========== SI NO SE USA USUARIO Y CONTRASEÑA ==========
            final MongoClient conexion = new MongoClient(new MongoClientURI("mongodb://" + host + ":" + port + "/?authSource=admin"));

            // =========== SI SE USA USUARIO Y CONTRASEÑA ==========
//            String username = configuration.getProperty("username");
//            String password = configuration.getProperty("password");
//            final MongoClient conexion = new MongoClient(new MongoClientURI("mongodb://" + username + ":" + password + "@" + host + ":" + port + "/?authSource=admin"));

            // EJEMPLO
//            final MongoClient conexion = new MongoClient(new MongoClientURI("mongodb://root:password@localhost:27017/?authSource=admin"));

            System.out.println("Conexion establecida correctamente.");
            return conexion;
        }
        catch(Exception e){
            System.out.println("Conexion fallida.");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void desconectar(MongoClient c){
        c.close();
        System.out.println("Desconectado.");
    }

    /**
     * Metodo que carga los registros de la collection en una ObservableList introducida por parametro.
     *
     * @param listaDocumentos La lista donde cargaremos los registros de la collection de la BBDD.
     * @param collection La collection de la BBDD de donde sacaremos los registros.
     * @return La lista con los registros cargados en ella.
     */
    public static ObservableList<Dispositivo> cargarDocumentos(ObservableList<Dispositivo> listaDocumentos, MongoCollection<Document> collection){
        MongoCursor<Document> cursor = collection.find().iterator();
        Gson gson = new Gson();

        try{
            while(cursor.hasNext()){
                Dispositivo d = gson.fromJson(cursor.next().toJson(), Dispositivo.class);
                listaDocumentos.add(d);
            }
        }
        catch(Exception e){
            System.out.println("EXCEPCION EN LA FUNCION cargarDocumentos()");
            System.out.println(e.getMessage());
        }
        finally{
            cursor.close();
        }

        return listaDocumentos;
    }
}
