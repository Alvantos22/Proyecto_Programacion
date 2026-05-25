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
        // Leer manualmente para evitar problemas de reflexión con LocalDate en algunas JVMs
        try (FileReader fr = new FileReader(archivoPrestamos)) {
            com.google.gson.JsonElement root = com.google.gson.JsonParser.parseReader(fr);
            if (root == null || !root.isJsonArray()) {
                logger.info(Constantes.INFO_ARCHIVO_VACIO);
                return;
            }

            com.google.gson.JsonArray arr = root.getAsJsonArray();
            List<Prestamo> cargados = new ArrayList<>();
            for (com.google.gson.JsonElement el : arr) {
                if (!el.isJsonObject()) continue;
                com.google.gson.JsonObject obj = el.getAsJsonObject();

                // usuario y libro se deserializan con gson
                com.google.gson.JsonElement usuarioEl = obj.get("usuario");
                com.google.gson.JsonElement libroEl = obj.get("libro");

                Domain.Usuario usuario = null;
                Domain.Libro libro = null;

                if (usuarioEl != null && !usuarioEl.isJsonNull()) {
                    usuario = gson.fromJson(usuarioEl, Domain.Usuario.class);
                }
                if (libroEl != null && !libroEl.isJsonNull()) {
                    libro = gson.fromJson(libroEl, Domain.Libro.class);
                }

                java.time.LocalDate fechaPrestamo = null;
                java.time.LocalDate fechaDevolucion = null;

                if (obj.has("fechaPrestamo") && !obj.get("fechaPrestamo").isJsonNull()) {
                    try {
                        fechaPrestamo = java.time.LocalDate.parse(obj.get("fechaPrestamo").getAsString());
                    } catch (Exception ex) {
                        logger.error(Constantes.ERROR_CARGA_ARCHIVO + ex.getMessage());
                    }
                }

                if (obj.has("fechaDevolucion") && !obj.get("fechaDevolucion").isJsonNull()) {
                    try {
                        fechaDevolucion = java.time.LocalDate.parse(obj.get("fechaDevolucion").getAsString());
                    } catch (Exception ex) {
                        logger.error(Constantes.ERROR_CARGA_ARCHIVO + ex.getMessage());
                    }
                }

                cargados.add(new Prestamo(usuario, libro, fechaPrestamo, fechaDevolucion));
            }

            this.prestamos = cargados;
            logger.info(Constantes.INFO_DATOS_CARGADOS + "prestamos");
        } catch (Exception e) {
            this.prestamos = new ArrayList<>();
            logger.error(Constantes.ERROR_CARGA_ARCHIVO + e.getMessage());
        }
    }

    private void guardarPrestamos() {
        // Guardado manual para controlar formato de LocalDate
        com.google.gson.JsonArray arr = new com.google.gson.JsonArray();
        for (Prestamo p : prestamos) {
            com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
            obj.add("usuario", p.getUsuario() == null ? com.google.gson.JsonNull.INSTANCE : gson.toJsonTree(p.getUsuario()));
            obj.add("libro", p.getLibro() == null ? com.google.gson.JsonNull.INSTANCE : gson.toJsonTree(p.getLibro()));
            obj.addProperty("fechaPrestamo", p.getFechaPrestamo() == null ? null : p.getFechaPrestamo().toString());
            obj.addProperty("fechaDevolucion", p.getFechaDevolucion() == null ? null : p.getFechaDevolucion().toString());
            arr.add(obj);
        }

        try (FileWriter fw = new FileWriter(archivoPrestamos)) {
            fw.write(arr.toString());
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