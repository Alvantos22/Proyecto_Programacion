package Common;

public class EntradaInvalidaException extends RuntimeException {
    public EntradaInvalidaException(String mensaje) {
        System.out.println("Formato inválido: " + mensaje);

    }
}

