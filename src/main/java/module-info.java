module com.example.practica_crud_mongo {
    requires javafx.controls;
    requires javafx.fxml;
            
        requires org.controlsfx.controls;
                            
    opens com.example.practica_crud_mongo to javafx.fxml;
    exports com.example.practica_crud_mongo;
}