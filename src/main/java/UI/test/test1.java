package UI.test;

import Dao.LibroDao;


public class test1 {
    static void main() {
        LibroDao ld= new LibroDao();

        ld.agregarLibro("Las rosas rojas","Alejandra","276326W","Tragicomedia",false,23);
        System.out.println("¿Libro creado? " + ld.listarLibros());
    }
}
