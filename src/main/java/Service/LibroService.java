package Service;

import Dao.LibroDao;

import Dao.PrestamoDao;
import Dao.UsuarioDao;
import Domain.Libro;
import lombok.Data;


@Data
public class LibroService {
    private LibroDao ld;
    private UsuarioDao ud;
    private PrestamoDao pd;

    public LibroService(LibroDao ld) {
        this.ld = ld;

    }

    public LibroService(LibroDao libroDao, UsuarioDao usuarioDao, PrestamoDao prestamoDao) {
        this.ld = libroDao;
        this.ud = usuarioDao;
        this.pd = prestamoDao;
    }

    public boolean agregarLibro(String titulo, String autor, String isbn, String genero, boolean disponible, int stock) {
        return ld.agregarLibro(titulo, autor, isbn, genero, disponible, stock);
    }
    public boolean agregarLibro(Libro libro) {
        return ld.agregarLibro(libro);
    }

        public boolean eliminarLibro(String isbn) {
            return ld.eliminarLibro(isbn);
        }

        public void actualizarLibro(String isbn, Libro libro) {
            ld.actualizarLibro(isbn, libro);
        }

    public boolean actualizarLibro(String isbn, String titulo, String autor, String genero, int stock) {
        Libro libroExistente = ld.buscarLibroIsbn(isbn);
        if (libroExistente != null) {
            libroExistente.setTitulo(titulo);
            libroExistente.setAutor(autor);
            libroExistente.setGenero(genero);
            libroExistente.setStock(stock);
            ld.actualizarLibro(isbn, libroExistente);
            return true;
        } else {
            return false;
        }
    }

    public Libro buscarLibro(String isbn) {
        return ld.buscarLibroIsbn(isbn);
    }

    public void actualizarStock(String isbn, int nuevoStock) {
        ld.actualizarStock(isbn, nuevoStock);
    }

        public void actualizarDisponibilidad(String isbn, boolean disponible) {
            Libro libro = ld.buscarLibroIsbn(isbn);
            if (libro != null) {
                libro.setDisponible(disponible);
                ld.actualizarLibro(isbn, libro);
            }
        }




}
