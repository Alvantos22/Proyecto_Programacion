package Dao;

import Common.Constantes;
import Common.ConfigManager;
import Common.LocalDateAdapter;
import Domain.Prestamo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDao {
    private List<Prestamo> prestamos;
    private final String archivoPrestamos = ConfigManager.get("archivo.prestamos", "prestamos.json");
    private final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new LocalDateAdapter()).setPrettyPrinting().create();
    private Logger logger = Logger.getLogger(PrestamoDao.class.getName());

    public PrestamoDao() {
        this.prestamos = new ArrayList<>();
        cargarPrestamos();
    }

    private void cargarPrestamos() {
        File archivo = new File(archivoPrestamos);
        if (!archivo.exists()) {
            logger.info(Constantes.INFO_ARCHIVO_NO_EXISTE);
            return;
        }
        try (FileReader fr = new FileReader(archivoPrestamos)) {
            Prestamo[] cargados = gson.fromJson(fr, Prestamo[].class);
            if (cargados == null) {
                logger.info(Constantes.INFO_ARCHIVO_VACIO);
                return;
            }

            this.prestamos = new ArrayList<>(Arrays.asList(cargados));
            logger.info(Constantes.INFO_DATOS_CARGADOS + "prestamos");
        } catch (Exception e) {
            this.prestamos = new ArrayList<>();
            logger.error(Constantes.ERROR_CARGA_ARCHIVO + e.getMessage());
        }
    }

    private void guardarPrestamos() {
        try (FileWriter fw = new FileWriter(archivoPrestamos)) {
            gson.toJson(prestamos, fw);
            logger.info(Constantes.INFO_DATOS_GUARDADOS + "prestamos");
        } catch (IOException e) {
            logger.error(Constantes.ERROR_GUARDADO_ARCHIVO + e.getMessage());
        }
    }

    public void agregarPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
        guardarPrestamos();
    }

    public List<Prestamo> listarPrestamos() {
        return new ArrayList<>(prestamos);
    }

    public List<Prestamo> buscarPorEmailUsuario(String email) {
        return prestamos.stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getEmail().equalsIgnoreCase(email)).toList();
    }

    public List<Prestamo> buscarPorIsbnLibro(String isbn) {
        return prestamos.stream()
                .filter(p -> p.getLibro() != null && p.getLibro().getIsbn().equalsIgnoreCase(isbn)).toList();
    }

    public boolean eliminarPrestamo(String emailUsuario, String isbnLibro) {
        boolean eliminado = prestamos.removeIf(p -> p.getUsuario() != null && p.getLibro() != null && p.getUsuario().getEmail().equalsIgnoreCase(emailUsuario) && p.getLibro().getIsbn().equalsIgnoreCase(isbnLibro));
        if (eliminado) {
            guardarPrestamos();
        }
        return eliminado;
    }
}