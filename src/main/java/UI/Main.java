package UI;

import Dao.LibroDao;
import Dao.UsuarioDao;
import Dao.PrestamoDao;

public class Main {
    public static void main(String[] args) {
        LibroDao libroDao = new LibroDao();
        UsuarioDao usuarioDao = new UsuarioDao();
        PrestamoDao prestamoDao = new PrestamoDao();

        Menu menu = new Menu(usuarioDao, libroDao, prestamoDao);
        menu.iniciar();
    }
}
