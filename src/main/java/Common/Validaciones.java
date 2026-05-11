package Common;

public class Validaciones{

    public static boolean validarEmail(String email) {
        // Expresión regular para validar el formato del correo electrónico
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return email.matches(regex);
    }

    public static boolean validarTelefono(String telefono) {
        // Expresión regular para validar el formato del número de teléfono
        String regex = "^\\d{10}$";
        return telefono.matches(regex);
    }

}
