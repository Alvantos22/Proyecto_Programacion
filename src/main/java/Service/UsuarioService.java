
package Service;

import Dao.UsuarioDao;
import Domain.Usuario;
import Domain.Rol;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class UsuarioService {
    private final UsuarioDao usuarioDao;
    Logger logger = Logger.getLogger(UsuarioService.class.getName());

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean registrarUsuario(String nombre, String email, String password) {

        Usuario usuarioExistente = usuarioDao.buscarUsuario(email);
        if (usuarioExistente != null) {
            logger.error("El email ya está registrado");
            return false;
        }


        Usuario nuevoUsuario = new Usuario(nombre, email, password, Rol.USER);
        usuarioDao.agregarUsuario(nuevoUsuario);
        logger.info("Usuario registrado exitosamente");
        return true;
    }

    public Usuario login(String email, String password) {
        Usuario usuario = usuarioDao.buscarUsuario(email);

        if (usuario != null && usuario.getPassword().equals(password)) {
            logger.info("✓ Autenticación exitosa");
            return usuario;
        }

        logger.warn("Intento de login fallido para email: " + email);
        System.out.println(" Email o contraseña incorrectos");
        return null;
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioDao.buscarUsuario(email);
    }

    public List<Usuario> listarTodos() {
        return usuarioDao.listarUsuarios();
    }

    public void eliminarUsuario(String email) {
        usuarioDao.eliminarUsuario(email);
    }


    public boolean actualizarNombre(String email, String nuevoNombre) {
        Usuario usuario = usuarioDao.buscarUsuario(email);
        if (usuario == null) {
            logger.error("Usuario no encontrado");
            return false;
        }
        usuario.setNombre(nuevoNombre);
        usuarioDao.agregarUsuario(usuario);
        logger.info("✓ Nombre actualizado");
        return true;
    }


    public boolean cambiarPassword(String email, String passwordActual, String passwordNueva) {

        Usuario usuario = usuarioDao.buscarUsuario(email);
        if (usuario == null) {
            logger.info("Usuario no encontrado");
            return false;
        }

        if (!usuario.getPassword().equals(passwordActual)) {
            logger.info("La contraseña es la misma");
            return false;
        }

        usuario.setPassword(passwordNueva);
        usuarioDao.agregarUsuario(usuario);
        logger.info("Contraseña cambiada");
        return true;
    }


    public boolean emailExiste(String email) {
        return usuarioDao.buscarUsuario(email) != null;
    }


    public Rol obtenerRol(String email) {
        Usuario usuario = usuarioDao.buscarUsuario(email);
        return usuario != null ? usuario.getRol() : null;
    }
}