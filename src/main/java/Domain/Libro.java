package Domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class Libro {
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private boolean disponible;


    public Libro(String titulo, String autor, String isbn,String genero) {
        this.titulo = titulo;
        this.autor = autor;
        this.isbn = isbn;
        this.genero = genero;
        this.disponible = true; // Por defecto, el libro está disponible
    }



}