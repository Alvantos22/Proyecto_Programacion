package Domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Libro {
    private String titulo;
    private String autor;
    private String isbn;
    private String genero;
    private boolean disponible;
    private int stock;
}