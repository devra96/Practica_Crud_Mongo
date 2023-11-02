module com.example.practica_crud_mongo {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
    requires mongodb.driver;
    requires org.mongodb.bson;
    requires com.google.gson;

    opens com.example.practica_crud_mongo to javafx.fxml;
    exports com.example.practica_crud_mongo;
}