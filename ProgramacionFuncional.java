import java.util.List;
import java.util.function.*;
// Ese paquete que aparece en Java 1.8 define Interfaced Funcionales.
// Es decir, interfaces (que me dan tipos de datos)
// para apuntar a funciones.
// Hay 4 principales:
// - Consumer<T>    Función void que recibe un parametro de tipo T 
    // setters
    // Define el método accept(T t)
// - Supplier<T>    Función que no acepta parámetros y devuelve un valor de tipo T
    // getters
    // Define el método get()
// - Function<T,R>  Función que recibe un parámetro de tipo T y devuelve un valor de tipo R
    // map
    // Define el método apply(T t)
// - Predicate<T>   Función que recibe un parámetro de tipo T y devuelve un valor booleano
    // isXXX?. hasXXX?
    // Define el método test(T t)
// en el paquete java.util.function tenemos variantes de estas interfaces funcionales:
// Para tipos primitivos:
// - IntConsumer, LongConsumer, DoubleConsumer
// - Para más de un parametro de entrada:
// - BiConsumer<T,U>    Función void que recibe dos parámetros de tipo T y
// - BiFunction<T,U,R>  Función que recibe dos parámetros de tipo T y U y devuelve un valor de tipo R
// - BiPredicate<T,U>   Función que recibe dos parámetros de tipo T y U y devuelve un valor booleano
public class ProgramacionFuncional {


    public String generarSaludoFormal(String nombre) {
        return "Buenos días " + nombre;
    }

    public String generarSaludoInformal(String nombre) {
        return "Hola " + nombre;
    }

    public static void saluda(String nombre) {
        System.out.println("Hola " + nombre);
    }

    public static void main(String[] args) {

        ProgramacionFuncional.saluda("Menchu");
        ProgramacionFuncional.saluda("Federico");

        String nombre = "Menchu";
        nombre = "Felipe";
        ProgramacionFuncional.saluda(nombre);
        Consumer<String> variable = ProgramacionFuncional::saluda;
        // para invocar una función mediante una interfaz funcional,
        // he de llamar al método definido en la interfaz funcional.
        variable.accept("Margarita");

        ProgramacionFuncional pf = new ProgramacionFuncional();
        pf.imprimirSaludo("Merlin", pf::generarSaludoFormal);
        pf.imprimirSaludo("Arturo", pf::generarSaludoInformal);

        GeneradorDeSaludos miInterfazFuncional = pf::generarSaludoFormal;
        String saludo = miInterfazFuncional.generarSaludo("Gandalf");   
        System.out.println(saludo); 

        // Quiero llamar nuevamente a imprimirSaludo, podría hacerlo como en las lineas de arriba... pero hay problemas:
        // 1. Necesito una función que pasar como argumento
        // 2. Si esa función la he definido de forma tradicional... en ocasiones eso ni me aporta reutilización ni claridad.

        // En este caso, tenemos las expresiones lambda.
        // Una expresión lambda es una NUEVA forma de definir funciones, sin necesidad de crear un método de forma tradicional.
        // Qué es una expresion lambda? Ante todo una expresión.
        // Una expresión es un trozo de código que devuelve un valor.

        int numero = 17; // Statement
        int numero2 = 17 + 18; // Esto también es un statement
                      ///////    Eso es una expresión, un trozo de código que devuelve un valor. En este caso, el resultado de sumar 17 + 18 = 35
        // Por tanto, las expresiones lambda son trozos de código que devuelven un valor... Qué devuelven exactamente?
        // Una función definida dentro de la misma expresión, función por cierto anónima, es decir: SIN NOMBRE.
                      

        Function<String,String> variable2 = pf::generarSaludoFormal;

        Function<String,String> variable3 = (String otroNombre) -> {
                                                                        return "Buenos días " + otroNombre;
                                                                    };
                                                                    // Cómo se sabe qué devuelve la función? 
                                                                    // Pues solo tengo que mirar el return "Buenos días " + otroNombre es un String, por tanto la función devuelve un String.
                                                                    // No hace fdalta explicitarlo... Lo puedo INFERIR del código.
        pf.imprimirSaludo("Morgana", variable3);
        // Java me permite empaquetar aún más esa sintaxis:
        // Empàquetado 1. Obviar el tipo de dato de entrada
        Function<String,String> variable4 = (otroNombre) -> {
                                                                return "Buenos días " + otroNombre;
                                                            };
                                                            // En este caso, de dónde infiere JAVA el tipo de dato de la variable otroNombre?
                                                            // De la variable a la que la asigno
        //.     ^^^^^^                                      // De ese String                                                            
        // Empaquetado 2. Si la función solo tiene un argumento, puedo obciar los paréntesis
        Function<String,String> variable5 = otroNombre -> {
                                                                return "Buenos días " + otroNombre;
                                                            };
        // Empaquetado 3. Si la función solo tiene una línea de código, puedo obvia las llaves y el return
        Function<String,String> variable6 = otroNombre -> "Buenos días " + otroNombre;

        pf.imprimirSaludo("Morgana", otroNombre -> "Buenos días " + otroNombre);


        // El API DE JAVA entero está migrando a programación funcional... cada versión de JAVA nueva incorpora más y más métodos que reciben funciones como parámetros... y eso es genial, porque me permite escribir código mucho más claro, reutilizable y fácil de testear.

        List<String> nombres = List.of("Merlin", "Arturo", "Morgana");
        // Pre java 1.5 iteraba la lista mediante sintaxis FOR
        for (int i = 0; i < nombres.size(); i++) {
            String nombreActual = nombres.get(i);
            System.out.println(nombreActual);
        }
        // Entre java 1,5 y java 1.8 iteraba la lista mediante un bucle FOR EACH (en JAVA 1.5 se añade el concepto de Iterator, que es un objeto que me permite iterar una colección de datos)
        for (String nombreActual : nombres) {
            System.out.println(nombreActual);
        }
        // Desde java 1.8 puedo usar bucles internos:
        nombres.forEach(nombreActual -> System.out.println(nombreActual));
        // El código de la función foreach por dentro será del tipo:
        /* 
            public void forEach(Consumer<String> action) {
                for (String nombreActual : this) {
                    action.accept(nombreActual);
                }
             }
        */


    }
/*
    public String generarSaludoInformal(String nombre) {
        return "Hola " + nombre;
    }
*/

    public void imprimirSaludo(String nombre, Function<String, String> funcionGeneradoraDeSaludos) {
        String saludo = funcionGeneradoraDeSaludos.apply(nombre);
        System.out.println(saludo);
    }

}