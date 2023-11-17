package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class AlertUtils {
    public static void mostrarError(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setContentText(mensaje);
        alerta.show();
    }

    public static boolean confirmacion(){
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("BORRAR DISPOSITIVO");
//        a.setHeaderText("");
        a.setContentText("¿Estas seguro?");
        Optional<ButtonType> r = a.showAndWait();
        if(r.get() == ButtonType.OK){
            return true;
        }
        return false;
    }
}
