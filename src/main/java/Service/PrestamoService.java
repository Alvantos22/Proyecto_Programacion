package Service;

import Dao.LibroDao;
import Dao.PrestamoDao;
import Dao.UsuarioDao;
import Common.Constantes;
import Domain.Prestamo;
import Domain.Usuario;
import Domain.Libro;
import org.apache.log4j.Logger;

import java.time.LocalDate;
import java.util.List;

public class PrestamoService {
    private final PrestamoDao prestamoDao;
    private final LibroDao libroDao;
    private final UsuarioDao usuarioDao;
    private final Logger logger = Logger.getLogger(PrestamoService.class.getName());

    public PrestamoService(PrestamoDao prestamoDao, LibroDao libroDao, UsuarioDao usuarioDao) {
        this.prestamoDao = prestamoDao;
        this.libroDao = libroDao;
        this.usuarioDao = usuarioDao;
    }


    public boolean realizarPrestamoIsbn(String emailUsuario, String isbn, LocalDate fechaDevolucion) {
        Usuario usuario = usuarioDao.buscarUsuario(emailUsuario);
        if (usuario == null) {
            logger.error(Constantes.WARN_REGISTRO_NO_ENCONTRADO  + emailUsuario);
            return false;
        }

        Libro libro = libroDao.buscarLibroIsbn(isbn);
        if (libro == null) {
            logger.error(Constantes.WARN_REGISTRO_NO_ENCONTRADO + "libro: " + isbn);
            return false;
        }

        if (!libro.isDisponible() || libro.getStock() <= 0) {
            logger.warn("Libro no disponible: " + isbn);
            return false;
        }


        libro.setStock(libro.getStock() - 1);
        if (libro.getStock() <= 0) {
            libro.setDisponible(false);
        }
        libroDao.actualizarLibro(isbn, libro);
        LocalDate fechaPrestamo = LocalDate.now();
        Prestamo prestamo = new Prestamo(usuario, libro, fechaPrestamo, fechaDevolucion);
        prestamoDao.agregarPrestamo(prestamo);

        logger.info("Préstamo realizado: " + emailUsuario + " - " + isbn);
        return true;
    }
    public boolean realizarPrestamoTitulo(String emailUsuario, String titulo, LocalDate fechaDevolucion) {
        Usuario usuario = usuarioDao.buscarUsuario(emailUsuario);
        if (usuario == null) {
            logger.error(Constantes.WARN_REGISTRO_NO_ENCONTRADO  + emailUsuario);
            return false;
        }

        Libro libro = libroDao.buscarLibroTitulo(titulo);
        if (libro == null) {
            logger.error(Constantes.WARN_REGISTRO_NO_ENCONTRADO + "libro: " + titulo);
            return false;
        }

        if (!libro.isDisponible() || libro.getStock() <= 0) {
            logger.warn("Libro no disponible: " + titulo);
            return false;
        }

        libro.setStock(libro.getStock() - 1);
        if (libro.getStock() <= 0) {
            libro.setDisponible(false);
        }
        libroDao.actualizarLibro(libro.getIsbn(), libro);
        LocalDate fechaPrestamo = LocalDate.now();
        Prestamo prestamo = new Prestamo(usuario, libro, fechaPrestamo, fechaDevolucion);
        prestamoDao.agregarPrestamo(prestamo);

        logger.info("Préstamo realizado: " + emailUsuario + " - " + titulo);
        return true;
    }


    public List<Prestamo> listarMisPrestamos(String emailUsuario) {

        return prestamoDao.buscarPorEmailUsuario(emailUsuario);
    }


    public List<Prestamo> listarTodosPrestamos() {
        return prestamoDao.listarPrestamos();
    }


    public boolean registrarDevolucion(String emailUsuario, String isbn) {
        // Eliminar el préstamo
        boolean eliminado = prestamoDao.eliminarPrestamo(emailUsuario, isbn);
        if (!eliminado) {
            logger.warn("No se encontró préstamo para: " + emailUsuario + " - " + isbn);
            return false;
        }

        Libro libro = libroDao.buscarLibroIsbn(isbn);
        if (libro != null) {
            libro.setStock(libro.getStock() + 1);
            libro.setDisponible(true);
            libroDao.actualizarLibro(isbn, libro);
            logger.info("Devolución registrada: " + emailUsuario + " - " + isbn);
        }

        return true;
    }
}
