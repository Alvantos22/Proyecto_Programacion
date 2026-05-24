package UI;

import Dao.LibroDao;
import Dao.UsuarioDao;
import Dao.PrestamoDao;
import Domain.Usuario;
import Domain.Libro;
import Domain.Rol;
import Domain.Prestamo;
import Service.UsuarioService;
import Service.LibroService;
import Service.PrestamoService;

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
                salir = menuLogin();
            } else if (usuarioLogueado.getRol() == Rol.ADMIN) {
                menuAdmin();
            } else {
                menuUsuario();
            }
        }
        scanner.close();
    }

    // ============ MENÚ LOGIN ============
    private boolean menuLogin() {
        System.out.println("\n╔═══════════════════════════════════╗");
        System.out.println("║       BIBLIOTECA - BIBLIOTECA      ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("1. Iniciar sesión");
        System.out.println("2. Registrarse");
        System.out.println("3. Salir");
        System.out.print("Selecciona opción: ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                login();
                break;
            case "2":
                registrarse();
                break;
            case "3":
                System.out.println("¡Hasta luego!");
                return true;
            default:
                System.out.println("❌ Opción inválida");
        }
        return false;
    }

    private void login() {
        System.out.print("\nEmail: ");
        String email = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        Usuario usuario = usuarioService.login(email, password);
        if (usuario != null) {
            usuarioLogueado = usuario;
            System.out.println("\n✓ ¡Bienvenido, " + usuario.getNombre() + "!");
        } else {
            System.out.println("\n❌ Email o contraseña incorrectos");
        }
    }

    private void registrarse() {
        System.out.print("\nNombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Contraseña: ");
        String password = scanner.nextLine().trim();

        boolean registrado = usuarioService.registrarUsuario(nombre, email, password);
        if (registrado) {
            System.out.println("\n✓ Usuario registrado exitosamente");
        } else {
            System.out.println("\n❌ El email ya está registrado o hay campos vacíos");
        }
    }

    // ============ MENÚ USUARIO (NORMAL) ============
    private void menuUsuario() {
        System.out.println("\n╔═══════════════════════════════════╗");
        System.out.println("║    MENÚ USUARIO - " + usuarioLogueado.getNombre() + "           ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("1. Listar libros disponibles");
        System.out.println("2. Buscar libro");
        System.out.println("3. Reservar libro");
        System.out.println("4. Ver mis reservas");
        System.out.println("5. Cerrar sesión");
        System.out.print("Selecciona opción: ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                listarLibrosDisponibles();
                break;
            case "2":
                buscarLibro();
                break;
            case "3":
                reservarLibro();
                break;
            case "4":
                verMisReservas();
                break;
            case "5":
                usuarioLogueado = null;
                System.out.println("\n✓ Sesión cerrada");
                break;
            default:
                System.out.println("\n❌ Opción inválida");
        }
    }

    private void listarLibrosDisponibles() {
        System.out.println("\n📚 LIBROS DISPONIBLES:");
        List<Libro> libros = libroService.listarDisponibles();
        if (libros.isEmpty()) {
            System.out.println("No hay libros disponibles");
        } else {
            libros.forEach(libro -> System.out.println(
                "  • " + libro.getTitulo() + " - " + libro.getAutor() +
                " (ISBN: " + libro.getIsbn() + ") [Stock: " + libro.getStock() + "]"
            ));
        }
    }

    private void buscarLibro() {
        System.out.print("\nBuscar por título: ");
        String titulo = scanner.nextLine().trim();
        List<Libro> resultados = libroService.buscarPorTitulo(titulo);
        
        if (resultados.isEmpty()) {
            System.out.println("\n❌ No se encontraron libros");
        } else {
            System.out.println("\n📚 RESULTADOS:");
            resultados.forEach(libro -> System.out.println(
                "  • " + libro.getTitulo() + " - " + libro.getAutor() +
                " (ISBN: " + libro.getIsbn() + ") [" + 
                (libro.isDisponible() ? "Disponible" : "No disponible") + "]"
            ));
        }
    }

    private void reservarLibro() {
        System.out.print("\nISBN del libro a reservar: ");
        String isbn = scanner.nextLine().trim();
        
        Libro libro = libroService.obtenerPorIsbn(isbn);
        if (libro == null) {
            System.out.println("❌ Libro no encontrado");
            return;
        }
        
        if (!libro.isDisponible()) {
            System.out.println("❌ El libro no está disponible");
            return;
        }

        System.out.print("Fecha de devolución (yyyy-MM-dd): ");
        String fechaStr = scanner.nextLine().trim();
        
        try {
            LocalDate fechaDevolucion = LocalDate.parse(fechaStr);
            if (fechaDevolucion.isBefore(LocalDate.now())) {
                System.out.println("❌ La fecha debe ser posterior a hoy");
                return;
            }
            
            boolean prestado = prestamoService.realizarPrestamo(usuarioLogueado.getEmail(), isbn, fechaDevolucion);
            
            if (prestado) {
                System.out.println("\n✓ Libro reservado exitosamente");
            } else {
                System.out.println("\n❌ No se pudo realizar la reserva");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Fecha inválida (formato: yyyy-MM-dd)");
        }
    }

    private void verMisReservas() {
        System.out.println("\n📋 MIS RESERVAS:");
        List<Prestamo> prestamos = prestamoService.listarMisPrestamos(usuarioLogueado.getEmail());
        
        if (prestamos.isEmpty()) {
            System.out.println("No tienes reservas");
        } else {
            prestamos.forEach(p -> System.out.println(
                "  • " + p.getLibro().getTitulo() + " - " + p.getLibro().getAutor() +
                " (Devolución: " + p.getFechaDevolucion() + ")"
            ));
        }
    }

    // ============ MENÚ ADMIN ============
    private void menuAdmin() {
        System.out.println("\n╔═══════════════════════════════════╗");
        System.out.println("║   MENÚ ADMIN - " + usuarioLogueado.getNombre() + "             ║");
        System.out.println("╚═══════════════════════════════════╝");
        System.out.println("1. Listar todos los libros");
        System.out.println("2. Crear libro");
        System.out.println("3. Editar libro");
        System.out.println("4. Borrar libro");
        System.out.println("5. Ver todas las reservas");
        System.out.println("6. Cerrar sesión");
        System.out.print("Selecciona opción: ");

        String opcion = scanner.nextLine().trim();

        switch (opcion) {
            case "1":
                listarTodosLibros();
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
                verTodasReservas();
                break;
            case "6":
                usuarioLogueado = null;
                System.out.println("\n✓ Sesión cerrada");
                break;
            default:
                System.out.println("\n❌ Opción inválida");
        }
    }

    private void listarTodosLibros() {
        System.out.println("\n📚 TODOS LOS LIBROS:");
        List<Libro> libros = libroService.listarTodos();
        if (libros.isEmpty()) {
            System.out.println("No hay libros");
        } else {
            libros.forEach(libro -> System.out.println(
                "  • " + libro.getTitulo() + " - " + libro.getAutor() +
                " (ISBN: " + libro.getIsbn() + ") [Stock: " + libro.getStock() + "] " +
                (libro.isDisponible() ? "✓" : "✗")
            ));
        }
    }

    private void crearLibro() {
        System.out.println("\n--- CREAR NUEVO LIBRO ---");
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
            boolean creado = libroService.crearLibro(titulo, autor, isbn, genero, stock);
            if (creado) {
                System.out.println("\n✓ Libro creado");
            } else {
                System.out.println("\n❌ El ISBN ya existe");
            }
        } catch (NumberFormatException e) {
            System.out.println("\n❌ Stock debe ser un número");
        }
    }

    private void editarLibro() {
        System.out.println("\n--- EDITAR LIBRO ---");
        System.out.print("ISBN del libro a editar: ");
        String isbn = scanner.nextLine().trim();
        
        Libro libro = libroService.obtenerPorIsbn(isbn);
        if (libro == null) {
            System.out.println("❌ Libro no encontrado");
            return;
        }

        System.out.println("\nDatos actuales:");
        System.out.println("  Título: " + libro.getTitulo());
        System.out.println("  Autor: " + libro.getAutor());
        System.out.println("  Género: " + libro.getGenero());
        System.out.println("  Stock: " + libro.getStock());

        System.out.print("\nNuevo título (Enter para no cambiar): ");
        String titulo = scanner.nextLine().trim();
        System.out.print("Nuevo autor (Enter para no cambiar): ");
        String autor = scanner.nextLine().trim();
        System.out.print("Nuevo género (Enter para no cambiar): ");
        String genero = scanner.nextLine().trim();
        System.out.print("Nuevo stock (Enter para no cambiar): ");
        String stockStr = scanner.nextLine().trim();

        // Usar valores actuales si no se ingresa nada
        if (titulo.isEmpty()) titulo = libro.getTitulo();
        if (autor.isEmpty()) autor = libro.getAutor();
        if (genero.isEmpty()) genero = libro.getGenero();
        int stock = stockStr.isEmpty() ? libro.getStock() : Integer.parseInt(stockStr);

        boolean actualizado = libroService.actualizarLibro(isbn, titulo, autor, genero, stock);
        if (actualizado) {
            System.out.println("\n✓ Libro actualizado");
        } else {
            System.out.println("\n❌ No se pudo actualizar");
        }
    }

    private void borrarLibro() {
        System.out.println("\n--- BORRAR LIBRO ---");
        System.out.print("ISBN del libro a borrar: ");
        String isbn = scanner.nextLine().trim();
        
        Libro libro = libroService.obtenerPorIsbn(isbn);
        if (libro == null) {
            System.out.println("❌ Libro no encontrado");
            return;
        }

        System.out.println("\n⚠️  Vas a borrar: " + libro.getTitulo() + " - " + libro.getAutor());
        System.out.print("¿Estás seguro? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        
        if (confirmacion.equals("s")) {
            boolean eliminado = libroService.eliminarLibro(isbn);
            if (eliminado) {
                System.out.println("\n✓ Libro eliminado");
            } else {
                System.out.println("\n❌ No se pudo eliminar");
            }
        } else {
            System.out.println("Cancelado");
        }
    }

    private void verTodasReservas() {
        System.out.println("\n📋 TODAS LAS RESERVAS:");
        List<Prestamo> prestamos = prestamoService.listarTodosPrestamos();
        
        if (prestamos.isEmpty()) {
            System.out.println("No hay reservas");
        } else {
            prestamos.forEach(p -> System.out.println(
                "  • " + p.getUsuario().getNombre() + " - " + p.getLibro().getTitulo() +
                " (Devolución: " + p.getFechaDevolucion() + ")"
            ));
        }
    }
}

