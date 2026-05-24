package Dao;

import Domain.Libro;
import Common.Constantes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Data;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class LibroDao {
    private List<Libro> listaLibros;
    private final String libros = "libros.json";
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Logger logger = Logger.getLogger(LibroDao.class.getName());

    public LibroDao() {
        this.listaLibros = new ArrayList<>();
        cargarLibros();
    }


    public void cargarLibros() {
        File archivo = new File(libros);
        if (!archivo.exists()) {
            logger.info(Constantes.INFO_ARCHIVO_NO_EXISTE);
            cargarEjemplos();
            return;
        }

        try (FileReader fr = new FileReader(libros)) {
            Libro[] librosArray = gson.fromJson(fr, Libro[].class);
            if (librosArray != null) {
                listaLibros = new ArrayList<>(List.of(librosArray));
                logger.info(Constantes.INFO_DATOS_CARGADOS + "libros");
            } else {
                logger.info(Constantes.INFO_ARCHIVO_VACIO);
                cargarEjemplos();
            }

        } catch (IOException e) {
            logger.error(Constantes.ERROR_CARGA_ARCHIVO + e.getMessage());
        }

    }

    private void cargarEjemplos() {
        agregarLibro("1984", "George Orwell", "978-0451524935", "Distopía", true, 3);
        agregarLibro("El Quijote", "Miguel de Cervantes", "978-8491810087", "Novela", true, 2);
        agregarLibro("Clean Code", "Robert C. Martin", "978-0132350884", "Programación", true, 5);
        agregarLibro("El Principito", "Antoine de Saint-Exupéry", "978-8408089865", "Infantil", true, 4);
        logger.info(Constantes.INFO_EJEMPLOS_CARGADOS);
    }

    public void guardarLibros() {
        try (FileWriter fw = new FileWriter(libros)) {
            gson.toJson(listaLibros, fw);
            logger.info(Constantes.INFO_DATOS_GUARDADOS + "libros");
        } catch (IOException e) {
            logger.error(Constantes.ERROR_GUARDADO_ARCHIVO + e.getMessage());
        }
    }

    public boolean agregarLibro(Libro libro) {
        boolean chk = true;
        if (listaLibros.stream().noneMatch(l -> l.getIsbn().equals(libro.getIsbn()))) {
            listaLibros.add(libro);
            guardarLibros();
        } else {
            logger.error(Constantes.ERROR_DATO_DUPLICADO + "ISBN");
            chk = false;
        }
        return chk;
    }

    public boolean agregarLibro(String titulo, String autor, String isbn, String genero, boolean disponible, int stock) {
        boolean chk = true;
        if (listaLibros.stream().noneMatch(libro -> libro.getIsbn().equals(isbn))) {
            Libro nuevoLibro = new Libro(titulo, autor, isbn, genero, disponible, stock);
            listaLibros.add(nuevoLibro);
            guardarLibros();
        } else {
            logger.error(Constantes.ERROR_DATO_DUPLICADO + "ISBN");
            chk = false;
        }

        return chk;
    }

    public void eliminarLibro(String isbn) {
        listaLibros.removeIf(libro -> libro.getIsbn().equals(isbn));
        guardarLibros();
    }

    public void actualizarLibro(String isbn, Libro libroActualizado) {
        for (int i = 0; i < listaLibros.size(); ++i) {
            if (listaLibros.get(i).getIsbn().equals(isbn)) {
                listaLibros.set(i, libroActualizado);
                guardarLibros();
                return;
            }
        }
    }

    public Libro buscarLibro(String isbn) {
        return listaLibros.stream().filter(libro -> libro.getIsbn().equals(isbn)).findFirst().orElse(null);
    }

    public List<Libro> listarLibros() {
        return new ArrayList<>(listaLibros);
    }

    public void actualizarStock(String isbn, int nuevoStock) {
        listaLibros.stream().filter(libro -> libro.getIsbn().equals(isbn)).findFirst().ifPresent(libro -> {
            libro.setStock(nuevoStock);
            guardarLibros();
        });
    }

    public void actualizarLibro(String isbn, String nuevoTitulo, String nuevoAutor, String nuevoGenero, boolean nuevaDisponibilidad, int nuevoStock) {
        listaLibros.stream().filter(libro -> libro.getIsbn().equals(isbn)).findFirst().ifPresent(libro -> {
            libro.setTitulo(nuevoTitulo);
            libro.setAutor(nuevoAutor);
            libro.setGenero(nuevoGenero);
            libro.setDisponible(nuevaDisponibilidad);
            libro.setStock(nuevoStock);
            guardarLibros();
        });
    }

}
/* private void cargarLibros() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("Archivo JSON no existe. Iniciando vacío.");
            return;
        }

        try (FileReader reader = new FileReader(ARCHIVO)) {
            Type listType = new TypeToken<ArrayList<Libro>>(){}.getType();
            listaLibros = gson.fromJson(reader, listType);
            if (listaLibros == null) listaLibros = new ArrayList<>();
            System.out.println("✓ Libros cargados desde JSON");
        } catch (IOException e) {
            System.err.println("Error cargando JSON: " + e.getMessage());
            listaLibros = new ArrayList<>();
        }
    }
    */

