package UI;

import Common.EntradaInvalidaException;
import Common.Validaciones;
import Dao.LibroDao;
import Dao.PrestamoDao;
import Dao.UsuarioDao;
import Domain.Libro;
import Domain.Prestamo;
import Domain.Rol;
import Domain.Usuario;
import Service.LibroService;
import Service.PrestamoService;
import Service.UsuarioService;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner = new Scanner(System.in);
    private final UsuarioService usuarioService;
    private final LibroService libroService;
    private final PrestamoService prestamoService;
    private Usuario usuarioLogueado = null;

    public Menu(UsuarioDao usuarioDao, LibroDao libroDao, PrestamoDao prestamoDao) {
        this.usuarioService = new UsuarioService(usuarioDao);
        this.libroService = new LibroService(libroDao, usuarioDao, prestamoDao);
        this.prestamoService = new PrestamoService(prestamoDao, libroDao, usuarioDao);
    }

    public void iniciar() {
        boolean salir = false;
        while (!salir) {
            if (usuarioLogueado == null) {
                salir = menuInicio();
            } else if (usuarioLogueado.getRol() == Rol.ADMIN) {
                menuAdmin();
            } else {
                menuUsuario();
            }
        }
        scanner.close();
    }

    private boolean menuInicio() {
        System.out.println("\n=== BIBLIOTECA ===");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");

        switch (scanner.nextLine().trim()) {
            case "1":
                login();
                return false;
            case "2":
                registrarse();
                return false;
            case "3":
                System.out.println("Hasta luego.");
                return true;
            default:
                System.out.println("Opción inválida.");
                return false;
        }
    }

    private void login() {
        try {
            String email = leerEmailValido();
            String password = leerPasswordValida();

            Usuario usuario = usuarioService.login(email, password);
            if (usuario != null) {
                usuarioLogueado = usuario;
                System.out.println("Bienvenido/a, " + usuario.getNombre() + ".");
            } else {
                System.out.println("Email o contraseña incorrectos.");
            }
        } catch (EntradaInvalidaException e) {
            System.out.println(e.getMessage());
        }
    }

    private void registrarse() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isBlank()) {
            System.out.println("El nombre no puede estar vacío.");
            return;
        }

        try {
            String email = leerEmailValido();
            String password = leerPasswordValida();

            if (usuarioService.registrarUsuario(nombre, email, password)) {
                System.out.println("Usuario registrado correctamente.");
            }
        } catch (EntradaInvalidaException e) {
            System.out.println(e.getMessage());
        }
    }

    private String leerEmailValido() {
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (!Validaciones.validarEmail(email)) {
            throw new EntradaInvalidaException("Email inválido. Usa el formato algo@algo.");
        }
        return email;
    }

    private String leerPasswordValida() {
        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();
        if (!Validaciones.validarPassword(password)) {
            throw new EntradaInvalidaException("Contraseña inválida. Mínimo 4 caracteres alfanuméricos.");
        }
        return password;
    }

    private void menuUsuario() {
        System.out.println("\n=== MENÚ USUARIO ===");
        System.out.println("1. Listar libros disponibles");
        System.out.println("2. Buscar libro por título");
        System.out.println("3. Reservar libro");
        System.out.println("4. Ver mis reservas");
        System.out.println("5. Devolver libro");
        System.out.println("6. Cerrar sesión");
        System.out.print("Seleccione una opción: ");

        switch (scanner.nextLine().trim()) {
            case "1":
                listarLibrosDisponibles();
                break;
            case "2":
                buscarLibroPorTitulo();
                break;
            case "3":
                reservarLibroNombre();
                break;
            case "4":
                verMisReservas();
                break;
            case "5":
                devolverLibro();
                break;
            case "6":
                usuarioLogueado = null;
                System.out.println("Sesión cerrada.");
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private void menuAdmin() {
        System.out.println("\n=== MENÚ ADMIN ===");
        System.out.println("1. Listar todos los libros");
        System.out.println("2. Crear libro");
        System.out.println("3. Editar libro");
        System.out.println("4. Borrar libro");
        System.out.println("5. Ver usuarios");
        System.out.println("6. Ver todas las reservas");
        System.out.println("7. Cerrar sesión");
        System.out.print("Seleccione una opción: ");

        switch (scanner.nextLine().trim()) {
            case "1":
                listarTodosLosLibros();
                break;
            case "2":
                crearLibro();
                break;
            case "3":
                editarLibro();
                break;
            case "4":
                borrarLibro();
                break;
            case "5":
                listarUsuarios();
                break;
            case "6":
                verTodasReservas();
                break;
            case "7":
                usuarioLogueado = null;
                System.out.println("Sesión cerrada.");
                break;
            default:
                System.out.println("Opción inválida.");
        }
    }

    private void listarTodosLosLibros() {
        mostrarLibros(libroService.getLd().listarLibros());
    }

    private void listarLibrosDisponibles() {
        List<Libro> disponibles = libroService.getLd().listarLibros().stream()
                .filter(libro -> libro.isDisponible() && libro.getStock() > 0)
                .toList();
        mostrarLibros(disponibles);
    }

    private void buscarLibroPorTitulo() {
        System.out.print("Título a buscar: ");
        String titulo = scanner.nextLine().trim().toLowerCase();

        List<Libro> resultados = libroService.getLd().listarLibros().stream()
                .filter(libro -> libro.getTitulo() != null && libro.getTitulo().toLowerCase().contains(titulo))
                .toList();

        mostrarLibros(resultados);
    }

    private void reservarLibroNombre() {
        System.out.println("Nombre del libro a reservar:");
        String nombre = scanner.nextLine().trim();

        Libro libro = libroService.getLd().listarLibros().stream()
                .filter(l -> l.getTitulo() != null && l.getTitulo().equalsIgnoreCase(nombre)).findFirst().orElse(null);
        if (libro == null) {
            System.out.println("Libro no encontrado.");
            return;
        }
        if (!libro.isDisponible() || libro.getStock() <= 0) {
            System.out.println("El libro no está disponible.");
            return;
        }

        try {
            LocalDate fechaDevolucion = leerFechaDevolucion();
            boolean ok = prestamoService.realizarPrestamoTitulo(usuarioLogueado.getEmail(), nombre, fechaDevolucion);
            System.out.println(ok ? "Reserva realizada correctamente." : "No se pudo realizar la reserva.");
        } catch (EntradaInvalidaException e) {
            System.out.println(e.getMessage());
        }
    }
    private void reservarLibroIsbn() {
        System.out.println("ISBN del libro a reservar:");
        String isbn = scanner.nextLine().trim();

        Libro libro = libroService.getLd().listarLibros().stream()
                .filter(l -> l.getIsbn() != null && l.getIsbn().equalsIgnoreCase(isbn)).findFirst().orElse(null);
        if (libro == null) {
            System.out.println("Libro no encontrado.");
            return;
        }
        if (!libro.isDisponible() || libro.getStock() <= 0) {
            System.out.println("El libro no está disponible.");
            return;
        }

        try {
            LocalDate fechaDevolucion = leerFechaDevolucion();
            boolean ok = prestamoService.realizarPrestamoIsbn(usuarioLogueado.getEmail(), isbn, fechaDevolucion);
            System.out.println(ok ? "Reserva realizada correctamente." : "No se pudo realizar la reserva.");
        } catch (EntradaInvalidaException e) {
            System.out.println(e.getMessage());
        }
    }

    private LocalDate leerFechaDevolucion() {
        System.out.print("Fecha de devolución (yyyy-MM-dd): ");
        String fecha = scanner.nextLine().trim();
        return Validaciones.validarFechaDevolucion(fecha);
    }

    private void verMisReservas() {
        mostrarPrestamos(prestamoService.listarMisPrestamos(usuarioLogueado.getEmail()));
    }

    private void devolverLibro() {
        System.out.print("ISBN del libro a devolver: ");
        String isbn = scanner.nextLine().trim();
        boolean ok = prestamoService.registrarDevolucion(usuarioLogueado.getEmail(), isbn);
        System.out.println(ok ? "Devolución realizada correctamente." : "No se pudo registrar la devolución.");
    }

    private void crearLibro() {
        System.out.print("Título: ");
        String titulo = scanner.nextLine().trim();
        System.out.print("Autor: ");
        String autor = scanner.nextLine().trim();
        System.out.print("ISBN: ");
        String isbn = scanner.nextLine().trim();
        System.out.print("Género: ");
        String genero = scanner.nextLine().trim();
        System.out.print("Stock: ");

        try {
            int stock = Integer.parseInt(scanner.nextLine().trim());
            boolean ok = libroService.agregarLibro(titulo, autor, isbn, genero, true, stock);
            System.out.println(ok ? "Libro creado correctamente." : "No se pudo crear el libro.");
        } catch (NumberFormatException e) {
            System.out.println("El stock debe ser un número.");
        }
    }

    private void editarLibro() {
        System.out.print("ISBN del libro a editar: ");
        String isbn = scanner.nextLine().trim();

        Libro libro = libroService.buscarLibro(isbn);
        if (libro == null) {
            System.out.println("Libro no encontrado.");
            return;
        }

        System.out.print("Nuevo título (Enter para mantener): ");
        String titulo = scanner.nextLine().trim();
        System.out.print("Nuevo autor (Enter para mantener): ");
        String autor = scanner.nextLine().trim();
        System.out.print("Nuevo género (Enter para mantener): ");
        String genero = scanner.nextLine().trim();
        System.out.print("Nuevo stock (Enter para mantener): ");
        String stockTxt = scanner.nextLine().trim();

        if (titulo.isBlank()) titulo = libro.getTitulo();
        if (autor.isBlank()) autor = libro.getAutor();
        if (genero.isBlank()) genero = libro.getGenero();

        int stock;
        try {
            stock = stockTxt.isBlank() ? libro.getStock() : Integer.parseInt(stockTxt);
        } catch (NumberFormatException e) {
            System.out.println("El stock debe ser un número.");
            return;
        }

        boolean ok = libroService.actualizarLibro(isbn, titulo, autor, genero, stock);
        System.out.println(ok ? "Libro actualizado correctamente." : "No se pudo actualizar el libro.");
    }

    private void borrarLibro() {
        System.out.print("ISBN del libro a borrar: ");
        String isbn = scanner.nextLine().trim();
        boolean ok = libroService.eliminarLibro(isbn);
        System.out.println(ok ? "Libro eliminado correctamente." : "No se pudo eliminar el libro.");
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = usuarioService.listarTodos();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }

        System.out.println("\n=== USUARIOS ===");
        usuarios.forEach(usuario -> System.out.println(
                "- " + usuario.getNombre() + " | " + usuario.getEmail() + " | " + usuario.getRol()
        ));
    }

    private void verTodasReservas() {
        mostrarPrestamos(prestamoService.listarTodosPrestamos());
    }

    private void mostrarLibros(List<Libro> libros) {
        if (libros == null || libros.isEmpty()) {
            System.out.println("No hay libros para mostrar.");
            return;
        }

        System.out.println("\n=== LIBROS ===");
        for (Libro libro : libros) {
            System.out.println(
                    "- " + libro.getTitulo() +
                            " | Autor: " + libro.getAutor() +
                            " | ISBN: " + libro.getIsbn() +
                            " | Género: " + libro.getGenero() +
                            " | Disponible: " + libro.isDisponible() +
                            " | Stock: " + libro.getStock()
            );
        }
    }

    private void mostrarPrestamos(List<Prestamo> prestamos) {
        if (prestamos == null || prestamos.isEmpty()) {
            System.out.println("No hay préstamos para mostrar.");
            return;
        }

        System.out.println("\n=== PRÉSTAMOS ===");
        for (Prestamo prestamo : prestamos) {
            System.out.println(
                    "- Usuario: " + prestamo.getUsuario().getNombre() +
                            " | Libro: " + prestamo.getLibro().getTitulo() +
                            " | Devuelve: " + prestamo.getFechaDevolucion()
            );
        }
    }
}
