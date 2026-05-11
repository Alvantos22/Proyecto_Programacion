package Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Libro {
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private boolean disponible;



}