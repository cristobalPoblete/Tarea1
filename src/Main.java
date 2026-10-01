import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// Representación del Nodo según las especificaciones de la tarea
class NodoTrie {
    NodoTrie[] P;
    int B; // Mapa de bits

    public NodoTrie() {
        P = new NodoTrie[26];
        B = 0;
    }
}

class Trie {
    private NodoTrie raiz;

    public Trie() {
        raiz = new NodoTrie();
    }

    public void insertar(String w) {
        if (w == null || w.isEmpty()) return;
        NodoTrie actual = raiz;

        for (int k = 0; k < w.length(); k++) {
            int i = w.charAt(k) - 'A';

            if (k == w.length() - 1) {
                // Si es la última letra, encendemos el bit i en el nodo actual
                actual.B |= (1 << i);
            } else {
                // Si hay más letras, creamos la continuación si no existe
                if (actual.P[i] == null) {
                    actual.P[i] = new NodoTrie();
                }
                actual = actual.P[i];
            }
        }
    }

    public boolean buscar(String w) {
        if (w == null || w.isEmpty()) return false;
        NodoTrie actual = raiz;

        for (int k = 0; k < w.length(); k++) {
            int i = w.charAt(k) - 'A';

            if (k == w.length() - 1) {
                // Verificamos si el bit i está encendido (es fin de palabra)
                return (actual.B & (1 << i)) != 0;
            }
            if (actual.P[i] == null) return false;
            actual = actual.P[i];
        }
        return false;
    }

    public boolean eliminar(String w) {
        if (w == null || w.isEmpty()) return false;
        NodoTrie actual = raiz;

        for (int k = 0; k < w.length(); k++) {
            int i = w.charAt(k) - 'A';

            if (k == w.length() - 1) {
                if ((actual.B & (1 << i)) != 0) {
                    // Apagamos el bit i usando el operador NOT (~) y AND (&)
                    actual.B &= ~(1 << i);
                    return true;
                }
                return false;
            }
            if (actual.P[i] == null) return false;
            actual = actual.P[i];
        }
        return false;
    }

    public List<String> autocompletar(String s) {
        List<String> resultados = new ArrayList<>();
        if (s == null || s.isEmpty()) return resultados;

        NodoTrie actual = raiz;
        for (int k = 0; k < s.length(); k++) {
            int i = s.charAt(k) - 'A';

            if (k == s.length() - 1) {
                // Si el prefijo exacto es una palabra, lo agregamos
                if ((actual.B & (1 << i)) != 0) {
                    resultados.add(s);
                }
                // Si hay continuaciones, buscamos recursivamente
                if (actual.P[i] != null) {
                    dfs(actual.P[i], s, resultados);
                }
                return resultados;
            } else {
                if (actual.P[i] == null) return resultados;
                actual = actual.P[i];
            }


        }
        return resultados;
    }

    private void dfs(NodoTrie nodo, String prefijo, List<String> resultados) {
        for (int i = 0; i < 26; i++) {
            // Si el bit i es 1, se forma una palabra completa
            if ((nodo.B & (1 << i)) != 0) {
                resultados.add(prefijo + (char)('A' + i));
            }
            // Si hay un nodo hijo, seguimos bajando
            if (nodo.P[i] != null) {
                dfs(nodo.P[i], prefijo + (char)('A' + i), resultados);
            }
        }
    }
}

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static List<String> ultimasSugerencias = new ArrayList<>();

    public static void main(String[] args) {
        Trie trie = new Trie();
        cargarDiccionario(trie);

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENÚ DEL TRIE ---");
            System.out.println("1. Buscar una palabra.");
            System.out.println("2. Insertar una nueva palabra.");
            System.out.println("3. Eliminar una palabra existente.");
            System.out.println("4. Ingresar un prefijo y obtener sugerencias de autocompletado.");
            System.out.println("5. Seleccionar una de las sugerencias para completar la palabra ingresada.");
            System.out.println("6. Salir de la aplicación.");
            System.out.print("Seleccione una opción: ");

            String opcion = sc.nextLine().trim();

            switch (opcion) {
                case "1":
                    System.out.print("Ingrese la palabra a buscar: ");
                    String palabraBuscar = leerPalabraValida();
                    if (trie.buscar(palabraBuscar)) {
                        System.out.println("La palabra '" + palabraBuscar + "' SÍ se encuentra en el Trie.");
                    } else {
                        System.out.println("La palabra '" + palabraBuscar + "' NO se encuentra almacenada.");
                    }
                    break;
                case "2":
                    System.out.print("Ingrese la palabra a insertar: ");
                    String palabraInsertar = leerPalabraValida();
                    if (trie.buscar(palabraInsertar)) {
                        System.out.println("La palabra ya existe en el Trie.");
                    } else {
                        trie.insertar(palabraInsertar);
                        System.out.println("Palabra insertada exitosamente.");
                    }
                    break;
                case "3":
                    System.out.print("Ingrese la palabra a eliminar: ");
                    String palabraEliminar = leerPalabraValida();
                    if (trie.eliminar(palabraEliminar)) {
                        System.out.println("Palabra eliminada exitosamente.");
                    } else {
                        System.out.println("La palabra no se encontraba en el Trie.");
                    }
                    break;
                case "4":
                    System.out.print("Ingrese un prefijo: ");
                    String prefijo = leerPalabraValida();
                    ultimasSugerencias = trie.autocompletar(prefijo);
                    if (ultimasSugerencias.isEmpty()) {
                        System.out.println("No existen palabras con el prefijo ingresado.");
                    } else {
                        System.out.println("Sugerencias encontradas:");
                        for (int i = 0; i < ultimasSugerencias.size(); i++) {
                            System.out.println((i + 1) + ". " + ultimasSugerencias.get(i));
                        }
                    }
                    break;
                case "5":
                    if (ultimasSugerencias.isEmpty()) {
                        System.out.println("No hay sugerencias disponibles. Utilice la opción 4 primero.");
                    } else {
                        System.out.print("Seleccione el número de la sugerencia deseada: ");
                        try {
                            int indice = Integer.parseInt(sc.nextLine().trim()) - 1;
                            if (indice >= 0 && indice < ultimasSugerencias.size()) {
                                System.out.println("Palabra seleccionada: " + ultimasSugerencias.get(indice));
                            } else {
                                System.out.println("Número fuera de rango.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Entrada inválida. Debe ingresar un número.");
                        }
                    }
                    break;
                case "6":
                    salir = true;
                    System.out.println("Saliendo de la aplicación...");
                    break;
                default:
                    System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    private static String leerPalabraValida() {
        while (true) {
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.print("Entrada vacía. Intente de nuevo: ");
                continue;
            }
            boolean valida = true;
            for (char c : input.toCharArray()) {
                if (c < 'A' || c > 'Z') {
                    valida = false;
                    break;
                }
            }
            if (valida) return input;
            System.out.print("Símbolos no válidos. Solo se permiten letras mayúsculas (A-Z). Ingrese nuevamente: ");
        }
    }

    private static void cargarDiccionario(Trie trie) {
        try (BufferedReader br = new BufferedReader(new FileReader("diccionario.txt"))) {
            String linea;
            int contador = 0;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (!linea.isEmpty()) {
                    trie.insertar(linea);
                    contador++;
                }
            }
            System.out.println("Diccionario cargado correctamente con " + contador + " palabras.");
        } catch (IOException e) {
            System.out.println("No se pudo cargar 'diccionario.txt'. Se iniciará con un Trie vacío.");
        }
    }
}
