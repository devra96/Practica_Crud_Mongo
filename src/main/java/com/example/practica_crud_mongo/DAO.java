package com.example.practica_crud_mongo;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import javafx.collections.ObservableList;
import org.bson.Document;

public class DAO {
    public static MongoClient conectar(){
        try{
            final MongoClient conexion = new MongoClient(new MongoClientURI("mongodb://root:password@localhost:27017/?authSource=admin"));
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
    }

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
