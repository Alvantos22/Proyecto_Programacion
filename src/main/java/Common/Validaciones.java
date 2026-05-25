package Common;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Validaciones{

    public static boolean validarEmail(String email) {
        String regex = "^[^@\\s]+@[^@\\s]+$";
        return !email.matches(regex);
    }



    public static boolean validarPassword(String password) {
        if (password == null) return true;
        String regex = "^[A-Za-z0-9]{4,}$";
        return !password.matches(regex);
    }

    public static LocalDate validarFechaDevolucion(String fechaTexto) {
        try {
            LocalDate fecha = LocalDate.parse(fechaTexto);
            if (fecha.isBefore(LocalDate.now())) {
                throw new EntradaInvalidaException("La fecha debe ser hoy o posterior.");
            }
            return fecha;
        } catch (DateTimeParseException e) {
            throw new EntradaInvalidaException("Fecha invalida. Usa yyyy-MM-dd y una fecha real (ej: 2026-06-30).");
        }
    }


}
