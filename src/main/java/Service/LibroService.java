package Service;

import Dao.LibroDao;

import lombok.Data;


@Data
public class LibroService {
    private LibroDao ld = new LibroDao();

    public LibroService(LibroDao ld) {
        this.ld = ld;

    }

    public boolean agregarLibro(String titulo, String autor, String isbn, String genero, boolean disponible, int stock) {
        return ld.agregarLibro(titulo, autor, isbn, genero, disponible, stock);
    }
    public boolean agregarLibro(Domain.Libro libro) {
        return ld.agregarLibro(libro);
    }

}
