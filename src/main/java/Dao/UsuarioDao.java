package Dao;

import Common.Constantes;
import Common.ConfigManager;
import Domain.Usuario;
import Domain.Rol;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class UsuarioDao {
    private List<Usuario> usuarios;
    private final String users = ConfigManager.get("archivo.usuarios", "usuarios.json");
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Logger logger = Logger.getLogger(UsuarioDao.class.getName());

    public UsuarioDao() {
        this.usuarios = new ArrayList<>();
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        File usuariosFile = new File(users);
        if (!usuariosFile.exists()) {
            logger.info(Constantes.INFO_ARCHIVO_NO_EXISTE);
            cargarEjemplos();
            return;
        }
        try (FileReader fr = new FileReader(users)) {
            Usuario[] usuariosArray = gson.fromJson(fr, Usuario[].class);
            if (usuariosArray != null) {
                this.usuarios = new ArrayList<>(List.of(usuariosArray));
                logger.info(Constantes.INFO_DATOS_CARGADOS + "usuarios");
            } else {
                logger.info(Constantes.INFO_ARCHIVO_VACIO);
                cargarEjemplos();
            }
        } catch (IOException e) {
            logger.error(Constantes.ERROR_CARGA_ARCHIVO + e.getMessage());
        }
    }

    private void cargarEjemplos() {
        agregarUsuario(new Usuario("Admin", "admin@mail.com", "a123", Rol.ADMIN));
        agregarUsuario(new Usuario("Juan", "juan@mail.com", "usr123", Rol.USER));
        agregarUsuario(new Usuario("Ana", "ana@mail.com", "usr123", Rol.USER));
        logger.info(Constantes.INFO_EJEMPLOS_CARGADOS);
    }

    private void guardarUsuarios() {
        try (FileWriter fw = new FileWriter(users)) {
            gson.toJson(usuarios, fw);
            logger.info(Constantes.INFO_DATOS_GUARDADOS + "usuarios");
        } catch (IOException e) {
            logger.error(Constantes.ERROR_GUARDADO_ARCHIVO + e.getMessage());
        }
    }

    public void agregarUsuario(Usuario usuario) {
        usuarios.add(usuario);
        guardarUsuarios();
    }

    public Usuario buscarUsuario(String email) {
        return usuarios.stream().filter(usuario -> usuario.getEmail().equals(email)).findFirst().orElse(null);
    }

    public List<Usuario> listarUsuarios() {
        return new ArrayList<>(usuarios);
    }

    public void eliminarUsuario(String email) {
        usuarios.stream().filter(usuario -> usuario.getEmail().equals(email)).findFirst().ifPresentOrElse(usuario -> {
            usuarios.remove(usuario);
            guardarUsuarios();
            logger.info(Constantes.INFO_REGISTRO_ELIMINADO);
        }, () -> logger.warn(Constantes.WARN_REGISTRO_NO_ENCONTRADO + email));
    }
}
