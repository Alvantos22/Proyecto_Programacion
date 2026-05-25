
package Service;

import Common.EntradaInvalidaException;
import Common.Validaciones;
import Dao.UsuarioDao;
import Domain.Usuario;
import Domain.Rol;
import org.apache.log4j.Logger;

import java.util.List;

public class UsuarioService {
    private final UsuarioDao usuarioDao;
    private final Logger logger = Logger.getLogger(UsuarioService.class.getName());

    public UsuarioService(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    public boolean registrarUsuario(String nombre, String email, String password) {
        validarNombre(nombre);
        validarEmail(email);
        validarPassword(password);

        Usuario usuarioExistente = usuarioDao.buscarUsuario(email);
        if (usuarioExistente != null) {
            logger.warn("Intento de registro con email ya existente: " + email);
            throw new EntradaInvalidaException("El email ya está registrado.");
        }


        Usuario nuevoUsuario = new Usuario(nombre, email, password, Rol.USER);
        usuarioDao.agregarUsuario(nuevoUsuario);
        logger.info("Usuario registrado exitosamente");
        return true;
    }

    public Usuario login(String email, String password) {
        validarEmail(email);
        validarPassword(password);

        Usuario usuario = usuarioDao.buscarUsuario(email);

        if (usuario != null && usuario.getPassword().equals(password)) {
            logger.info("Autenticación exitosa");
            return usuario;
        }

        logger.warn("Intento de login fallido para email: " + email);
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
        logger.info(" Nombre actualizado");
        return true;
    }


    public boolean cambiarPassword(String email, String passwordActual, String passwordNueva) {
        validarEmail(email);
        validarPassword(passwordActual);
        validarPassword(passwordNueva);

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

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new EntradaInvalidaException("El nombre no puede estar vacío.");
        }
    }

    private void validarEmail(String email) {
        if (!Validaciones.validarEmail(email)) {
            throw new EntradaInvalidaException("Email inválido. Usa el formato algo@algo.");
        }
    }

    private void validarPassword(String password) {
        if (!Validaciones.validarPassword(password)) {
            throw new EntradaInvalidaException("Contraseña inválida. Debe tener al menos 4 caracteres alfanuméricos.");
        }
    }


    public boolean emailExiste(String email) {
        return usuarioDao.buscarUsuario(email) != null;
    }


    public Rol obtenerRol(String email) {
        Usuario usuario = usuarioDao.buscarUsuario(email);
        return usuario != null ? usuario.getRol() : null;
    }
}