//Código Actividad n10
//Validación de contraseña con Lambda
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class Main {

    private static final String NOMBRE_ARCHIVO = "registro_contrasenas.txt";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ExecutorService executorService = Executors.newFixedThreadPool(5); // Pool de hilos

        // Limpia el archivo al inicio del programa
        limpiarArchivo();

        System.out.println("=== VALIDACIÓN DE CONTRASEÑAS CONCURRENTE ===");
        System.out.print("¿Cuántas contraseñas desea validar?: ");
        int cantidadContrasenas = scanner.nextInt();
        scanner.nextLine(); // Consumir el salto de línea

        // Lista para almacenar contraseñas
        List<String> contrasenas = new ArrayList<>();
        for (int i = 0; i < cantidadContrasenas; i++) {
            System.out.printf("Ingrese la contraseña #%d: ", i + 1);
            contrasenas.add(scanner.nextLine());
        }

        System.out.println("\nValidando contraseñas...\n");

        // Procesar cada contraseña en un hilo separado
        contrasenas.forEach(contrasena -> executorService.execute(() -> {
            String resultado = validarContrasena(contrasena);
            registrarResultado(contrasena, resultado);
        }));

        executorService.shutdown(); // Cerrar el pool de hilos
    }

    // Método para validar una contraseña y retornar el resultado
    private static String validarContrasena(String contrasena) {
        System.out.printf("Validando contraseña: \"%s\"\n", contrasena);

        // Expresiones regulares
        Pattern LONGITUD_MINIMA = Pattern.compile(".{8,}");
        Pattern CARACTER_ESPECIAL = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
        Pattern DOS_MAYUSCULAS = Pattern.compile(".*[A-Z].*[A-Z].*");
        Pattern TRES_MINUSCULAS = Pattern.compile(".*[a-z].*[a-z].*[a-z].*");
        Pattern NUMERO = Pattern.compile(".*\\d.*");

        boolean esValida = true;
        StringBuilder errores = new StringBuilder();

        if (!LONGITUD_MINIMA.matcher(contrasena).matches()) {
            errores.append(" - No tiene al menos 8 caracteres.\n");
            esValida = false;
        }
        if (!CARACTER_ESPECIAL.matcher(contrasena).matches()) {
            errores.append(" - No contiene un carácter especial.\n");
            esValida = false;
        }
        if (!DOS_MAYUSCULAS.matcher(contrasena).matches()) {
            errores.append(" - No contiene al menos 2 letras mayúsculas.\n");
            esValida = false;
        }
        if (!TRES_MINUSCULAS.matcher(contrasena).matches()) {
            errores.append(" - No contiene al menos 3 letras minúsculas.\n");
            esValida = false;
        }
        if (!NUMERO.matcher(contrasena).matches()) {
            errores.append(" - No contiene un número.\n");
            esValida = false;
        }

        if (esValida) {
            System.out.printf("La contraseña \"%s\" es válida.\n\n", contrasena);
            return "Válida";
        } else {
            System.out.printf("La contraseña \"%s\" es inválida:\n%s\n", contrasena, errores);
            return "Inválida - " + errores.toString().replace("\n", " ");
        }
    }

    // Método sincronizado para registrar resultados en un archivo
    private static synchronized void registrarResultado(String contrasena, String resultado) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO, true))) {
            writer.write(String.format("Contraseña: %s | Resultado: %s\n", contrasena, resultado));
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    // Método para limpiar el archivo de registros
    private static void limpiarArchivo() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(NOMBRE_ARCHIVO))) {
            writer.write(""); // Sobrescribe el archivo y lo deja vacío
        } catch (IOException e) {
            System.out.println("Error al limpiar el archivo: " + e.getMessage());
        }
    }
}
