
# Paradigmas de programacion:

Es un nombre hortera que los desarrolladores damos a las formas en las que podemos usar un lenguaje de programación para expresar una idea.
La realidad es que no solamente en los lenguajes de programación hay "paradigmas", en los lenguajes naturales (como el español) también hay paradigmas. 

> Felipe, debajo de la ventana pon una silla.  > IMPERATIVO


- Imperativo            Cuando damos instrucciones al computador que debe procesar literal y secuanlmente.
                        En ocasiones queremos romper la secuencialidad y para ello tiramos de las típicas expresiones de control de flujo, típicas del paradigma imperativo: if, for, while, switch, etc.
                        JAVA soporta paradigma imperativo? SI... casi todo el código es imperativo
- Procedural            Cuando el lenguaje me permite agrupar una secuencia de instrucciones en un bloque con 
                        un nombre. Y posteriormente solicitar la ejecución de ese conjunto de instrucciones a través de su nombre. A esos bloques les llamamos: procedimientos, funciones, métodos, subrutinas, etc.
                        JAVA soporta paradigma prodecural? SI... creamos funciones de continuo.
                        Qué ventajas me dan las funciones?
                        - Reutilización de código
                        - Facilitar la mantenibilidad del código
- Orientado a objetos   Cualquier lenguaje permite trabajar con datos. 
                        Y esos datos tienen un determinado TIPO de datos.
                        Cualquier lenguaje viene con una serie de tipos de datos predefinidos: enteros, decimales, booleanos, caracteres, etc. Cada uno tiene datos que los caracterizan y operaciones que se pueden realizar con ellos.

                                    Caracteriza                     Operaciones
                        String      secuencia de caracteres         ponteEnMayusculas, longitud, etc.
                        Fecha       dia , mes , año                 caesEnBisiesto, caes en Jueves, sumaDias
                        Lista       secuencia de objetos            longitud, añadir, eliminar, etc.

                        Cuando el lenguaje me permite definir mis propios tipos de datos (clase), con sus propias características y operaciones, entonces ese lenguaje soporta el paradigma orientado a objetos.

                        Luego habrá funcionalidades o capacidades concretas que aporte cada lenguaje:
                        - Polimorfismo, herencia (simple, multiple), encapsulación, etc.
                        JAVA soporta paradigma orientado a objetos? SI.

- Funcional             Cuando el lenguaje me permite que una variable apunte a una función y posteriormente
                        ejecutar esa función a través de la variable, entonces ese lenguaje soporta el paradigma funcional.
                        Lo curioso no es lo que es la programación funcional.. que es una chorrada.
                        Lo curioso es lo que puedo llegar a hacer una vez que el lenguaje soporta esto:
                        - Crear funciones que reciban funciones como parámetros.   
                          Hay veces que cuando creo el cpntenido de una función, parte de la lógica que debe ejecutar me es desconocido... o puede ser variable. 
                        - Crear funciones que devuelvan funciones. CLOSURE
                        
                        Para quñe creamos funciones?
                        - Reutilización de código
                        - Facilitar la mantenibilidad del código
                        Pero.. al usar programación funcional aprarece un ter uso o motivo para crear funciones:
                        - Por cojones! Necesito llamar a una función.. y esa función me pide una función como argumento.. no me quedan má cojones que crear una función.


                        JAVA soporta programación funcional? Desde JAVA 1.8, con la aparición de:
                        - Paquete java.util.function
                        - 2 operadores:
                          - ::
                          - -> Definir expresiones lambda
- Declarativo           <<< Todo Springboot lo que único que hace es ofrecerme un paradigma de programación declarativo. ... eso de lass anotaciones!

Cada día odiamos más la programación imperativa. ES UNA MIERDA!
Estamos muy acostumbrados a ella... pero es una mierda!

> Felipe, IF (Condicional) hay algo que no sea una silla debajo de la ventana:
>    Felipe: QUITALO                                                  IMPERATIVO
> Felipe IF no hay silla debajo de la ventana, then:
>   FELIPE, IF not silla (silla == false) then GOTO IKEA !
>   Felipe, debajo de la ventana pon una silla                          IMPERATIVO

Esto es el lenguaje imperativo. Me hace olvidar mi objetivo, para centrarme en las tareas que debere hacer felipe apra conseguir mi objetivo.
Y ponerme en todos lo sposibles casos que puedan presentarse a felipe.

> Felipe, debajo de la ventana tiene que haber una silla. Es tu responsabilidad.      DECLARATIVO

Solo enuncio lo que es.
A partir de ahí, ya no es mi problema. Lo he delegado a felipe. Es su responsabilidad.
Él tiene la responsabilidad de conseguir lo que quiero.

Adoramos el lenguaje DECLARATIVO. En JAVA, el lenguaje declarativo se consigue mediante el uso de anotaciones.
Conocéis lombok?

```java

@Data // Esta anotación es de lombok.
// Quiero que esta clase tenga:
// - Un constructor con todos los argumentos
// - Un constructor sin argumentos
// - Getters y setters para todos los atributos
// - Un método toString personalizado
// - Un método equals personalizado
// - Un método hashCode personalizado
public class Persona {
    private String nombre;
    private String apellido;
    private int edad;
}

@Service // Esta anotación es de SpringBoot
// SpringBoot, quiero que me generes en el arranque una instancia de esta clase.
// Al hacerlo, quiero que mires su constructor. Si requiere parámetros, búscate la vida para rellenarlos.
// Si alguien en algún momento te pide que necesita una implementación de IServicioDePersonas, entonces le das esta instancia que has creado de ServicioDePersonas.
// Mantienes esa instancia viva mientras la aplciación esté en funcionamiento.. es decir, aunque en mi código no lo haya explicitado, quiero que esa clase tenga el mismo comportamiento que tendría un Singleton.
// En paralelo... A cualquier desarrollador que vea este código.. Dentro de esta clase defino LOGICA DE NEGOCIO (eso es el concepto SEMANTICO de servicio) relacionada con personas.
public class ServicioDePersonas implements IServicioDePersonas {
    // Aquí va la lógica de mi servicio de personas
}
```

Dicho de otra forma.. quién va ha hacer el `new ServicioDePersonas()`? 
YO? NOOOO!
SpringBoot? SI! SpringBoot es el encargado de crear la instancia de ServicioDePersonas

Imaginad que quiero la siguiente aplicación:
Quiero una aplicación que:
- Sea una ETL (un programa que Extrae datos de un origen, los Transforma y los Load en un destino). El típico batch que ejecutamos a las 3am todas las noches para importar datos a un sistema.
- Cuando acabe quiero que me mande un email con un resumen de lo que ha hecho.
- Cuando empiece tambien quiero un email de aviso.
- Validará el dni de las personas y si no es correcto (no tiene u buen formato o la letra no encaja con el numero), lo mete en un fichero de datos_incorrectos.txt
- Por cierto, quiero que cargue datos de un excel
- Los datos son de personas: id, nombre, apellido, dni, fecha de nacimiento.
- Ah.. y quiero que si la fecha de nacimiento es menor de edad, lo meta en el fichero de personas_no_admitidas.txt
- Si el dato es bueno, quiero que lo guarde en mi bbdd.
- Ah.. y si hay datos de personas no válidos o no admitidos, quiero que me mande un email a parte, con esos ficheros adjuntos cuando acabe.

^^^ AQUI TENIA YA EL CODIGO... pero escrito en lenguaje declarativo.
    ESTO ES LO QUE HACEMOS CON SPRING

    En Spring normal, esto lo declaro en ficheros de configuración XML, con un lenguaje específico para configurar el comportamiento de mi aplicación.
    En SpringBoot, esto lo declaro mediante anotaciones en el código de mi aplicación.

    Y pasa algo mágico. Spring es quién pone el flujo de mi app.

    Spring es un CONTENEDOR DE INVERSIÓN DE CONTROL.
    
    La inversión de control es otro patrón de desarrollo de software por el cual, el flujo de mi aplicación no lo escribo yo en lenguaje imperativo, sino que lo delego a un framework, el framework que me ofrece el CONTENEDOR DE INVERSIÓN DE CONTROL, que se encarga de ejecutar mi código según un flujo que él impone. 

    Nunca veré el flújo (la función main) de mi aplicación en Spring... y eso me descoloca... no es lo que espero... 
    Estoy cómodo viendo una función main con un montón de instrucciones... para entender qué cojones hace mi programa.
    Pero en nuestro caso, eso lo delegamos a Spring. Spring pone flujo. Conocemos ese flujo?
    Spring al rrarncar una aplicación (SI SPRING ES QUIEN LA ARRANCA!!!!!) hace un huecvo de cosas:
    - Busca un fichero de configuración (application.properties o application.yml)
    - Inicializa el contexto de la aplicación
    - Crea los beans definidos en el contexto (Es decir, crea instancias de las clases que yo le diga)
    - Al crearlas, si necesitan instancias de otras clases las suministra... y esto implica que las instancias hay que crearlas en un orden muy concreto.
    - Y antes de todo esto, hace un grafo de dependencias entre mis clases... para determinar qué instancias hay que crear primero
    - Dependiendo del tipo de aplicación...
      - Si es una app web: Levanta un tomcat /jetty, le configura los endpoints de mis servicios
      - Si es una ETL: Arranca un flujo con la extraccion de datos que yo haya definido... y mirando si he definido listeners que debaj ejecutarse antes o después de cada paso del proceso.
      - Si tengo una aplciación de terminal, carga y valida los argumentos que le paso a la aplicación, en base a la declaración que yo haya hecho de esos argumentos... y posteriormente ejecuta el código que yo le haya dicho que ejecute para cada argumento.

    Una cosa es la funcionalidad de CONTENEDOR DE INVERSIÓN DE CONTROL, que es lo que se incluye en Spring Core.
    Otra cosa que quiera montar una web: necesito Spring Web.
    Otra cosa que quiera montar una ETL: necesito Spring Batch.
    Otra cosa que quiera montar una aplicación de terminal: necesito Spring Shell.
    O que quiera montar una aplciación web reactiva: necesito Spring WebFlux.

    Lo que haremos es DECLARAR en spring los componentes (@Component) que tiene mi aplicación. Y ya Spring la ejecuta, poniendo el flujo que considere oportuno, y ejecutando el código de mis componentes en el momento que considere oportuno.

    @Service, @Repository, @Controller, @RestController, @Advice, etc.
    Todo eso son extensiones de la anotación @Component, que es la anotación más genérica para declarar un componente en Spring.
    Muchas de ellas solo aportan valor SEMANTICO para desarrolladores humanos, peromás allá de lo que heredan de la anotación @Component, no aportan ninguna funcionalidad adicional a nivel de Spring.
    Le digo, quiero tener este componente. Y ya el verá!

    Como Spring es quien aporta el flujo de mi aplicación, Spring es quien se encarga de crear las instancias de mis clases... de hacer los new!
    Y al hacerlo le inyecta! los datos que necesite en automático.. yo me olvido.
    Gracias a ello, Spring faciliota la adopción/uso del patrón de inyección de dependencias, que a su vez me garantiza el cumplimiento del principio de inversión de dependencias. Lo que redunda en que mi código sea más mantenible y fácil de evolucionar.
    OJO! Lo facilita! Pero si yo no   quiero y quiero seguir montando mi puñetero monolito, lo  puedo hacer... Pero en ese caso, para que ostias quiero Spring? 
    


En pseudo-lenguaje imperativo:

1     Mandar email de aviso de que el proceso ha empezado
2     Cargar el excel con los datos de las personas
3     Por cada persona del excel hacer: (BUCLE-FOREACH)
4         Validar el dni de la persona
5         Si el dni no es correcto, entonces escribir los datos de esa persona en el fichero de datos_incorrectos.txt   IF
6         En caso contrario   ELSE
7             Validar la fecha de nacimiento de la persona
8             Si la persona es menor de edad, entonces escribir los datos de esa persona en el fichero de personas_no_admitidas.txt   IF
9             En caso contrario   ELSE
10                Guardar los datos de esa persona en la bbdd
11    Si el fichero de datos_incorrectos.txt no está vacío, entonces mandar un email con ese fichero adjunto.   IF
12    Si el fichero de personas_no_admitidas.txt no está vacío, entonces mandar un email con ese fichero adjunto.   IF
13    Mandar email de aviso de que el proceso ha terminado



---



# Modelos de programación

MapReduce es un modelo de programación... basado en programación funcional... donde usamos funciones de tipo map y reduce para procesar grandes cantidades de datos de forma distribuida.
La programación Reactiva, que está definida en el libro "Reactive Systems", es un modelo de programación basado en programación funcional.


```java

package com.diccionario.api;

public interface Diccionario {

    String getIdioma();

    Optional<List<String>> getSignificados(String palabra);

    boolean existe(String palabra);

}

public interface SuministradorDeDiccionarios {

    boolean tienesDiccionarioDe(String idioma);

    Optional<Diccionario> getDiccionario(String idioma);

}

package com.diccionario.impl.ficheros;

public class DiccionarioDesdeFichero implements Diccionario {

    public DiccionarioDesdeFichero() { // Con los args que queramos
        // TODO Lo que sea guay!
    }

    public String getIdioma() {
        // TODO
    }

    public Optional<List<String>> getSignificados(String palabra) {
        // TODO
    }

    public boolean existe(String palabra) {
        // TODO
    }

}

public class SuministradorDeDiccionariosDesdeFichero implements SuministradorDeDiccionarios {

    public SuministradorDeDiccionariosDesdeFichero() { // Con los args que queramos
        // TODO Lo que sea guay!
    }

    public boolean tienesDiccionarioDe(String idioma) {
        // TODO
    }

    public Optional<Diccionario> getDiccionario(String idioma) {
        // TODO
    }

}


package com.diccionario.app.consola;

import com.diccionario.api.Diccionario;
import com.diccionario.api.SuministradorDeDiccionarios;
import com.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFichero; 
    // Y AQUI LA ACABAMOS DE JODER ENTERA
    // EL PROYECTO ACABA DE MORIR!
    // Este import apunta a una implementación
    // Me acabo de cagar en uno de los principios SOLID.
    // Al meter este import hemos metido una dependencia a una implementación...
    // Nos hemos cagado en uno de nuestros conceptos de partida!
    // Estoy ACOPLANDO mi aplicación de consola a una implementación concreta de un suministrador de diccionarios...

public class AplicacionDeConsola {

    public static void main(String[] args) {
        String idioma = args[1];
        String palabra = args[2];
        
        SuministradorDeDiccionarios suministrador = new SuministradorDeDiccionariosDesdeFichero();
        // Aqui si dependo de una implementación concreta... PERO AQUI ESTA BIEN! En un rato vemos claro el por quñé está bien
        // De entrada lo que si tengo es un único sitio donde cambiar la implementación del suministrador de diccionarios. Si alguna vez he de hacerlo,... vengo aquí... 
        // Y éste se la pasa a todas las clases que lo necesiten...
        // Aunque sigue siendo un fastidio! Pero ya es una mejora.. lo dejamos aquí por ahora.
        ProcesadorDePeticiones procesador = new ProcesadorDePeticiones();
        procesador.procesarPeticion(idioma, palabra, suministrador);
    }
}

import com.diccionario.api.Diccionario;
import com.diccionario.api.SuministradorDeDiccionarios;
//import com.diccionario.impl.ficheros.SuministradorDeDiccionariosDesdeFichero; 
// Ahora cumplo con el principio de inversión de dependencias, gracias a usar un patrón de inyección de dependencias.

public class ProcesadorDePeticiones {

    public void procesarPeticion(String idioma, String palabra, SuministradorDeDiccionarios suministrador) {
        //SuministradorDeDiccionarios suministrador = new SuministradorDeDiccionariosDesdeFichero();
        if(suministrador.tienesDiccionarioDe(idioma)) {
            Diccionario diccionario = suministrador.getDiccionario(idioma).orElseThrow(
                () -> new RuntimeException("No se ha podido cargar el diccionario de " + idioma)
            );
            if(diccionario.existe(palabra)) {
                List<String> significados = diccionario.getSignificados(palabra).orElseThrow(
                    () -> new RuntimeException("No se han podido cargar los significados de la palabra " + palabra)
                );
                System.out.println("La palabra " + palabra + " existe en el diccionario de " + idioma);
                System.out.println("Significa:");
                significados.forEach(significado -> System.out.println("- " + significado));
            } else {
                System.out.println("La palabra " + palabra + " no existe en el diccionario de " + idioma);
            }
        } else {
            System.out.println("No tengo diccionario para el idioma " + idioma);
        }
    }

}
```

                  SuministradorDeDiccionarios
                     ^                  ^
        AplicacionDeConsola    SuministradorDeDiccionariosDesdeFichero
                            -> Diccionario

La gran crisis del software. Se produjo a finales de los 60.
El software consttruido durante las 2 décadas anteriores se estaba volviendo inmantenible, difícil de evolucionar y con muchos bugs. Se empezó a hablar de la crisis del software, y se empezó a buscar soluciones para mejorar la calidad del software. -> Aquí es donde surge el concepto de ingeniería del software.

El problema es que fuimos muy rígidos en la forma de tratar de organizar ese caos:
- Metodologías de desarrollo: Waterfall, V, RUP, etc.
- Monolitos.
- Programación estructurada: Cohesión y acoplamiento.

---


"Un producto de software, por definición, es un producto sujeto a cambios y mantenimientos"

El que un programa funcione se da por descontado.

---

Aplicación java que vamos a montar:

$ buscarPalabra ES manzana

La palabra manzana existe en el diccionario de español.
Significa:
1. Fruto del manzano.

$ buscarPalabra ES archilococo

La palabra archilococo no existe en el diccionario de español.

$ buscarPalabra ELF ingaltur

No tengo diccionario para el idioma ELF.

> Pregunta 1.  Cuantos proyectos java monto para esto?     
             - Cuantos repos de git quiero?
             - Cuantos archivos pom.xml quiero?

Puedo montar 1... es lo más simple -> Monolito!
- Necesito gestionar palabras, diccionarios, significados... 
  - Cuántos significados puede tener una palabra?                                   Quién produce los diccionarios?
  - Admitimos sinónimos?                                                               Bajo qué criterios? ACTOR X
  - Ponemos ejemplos en nuestro sistema?
  - Las guardo en un fichero o en una BBDD? En qué tipo de BBDD? Relacional, mongo?   ACTOR Y
- Distintas formas de comunicarme: consola, web, móvil, desktop, servicio...
  - consola <- Qué necesito que me saque por pantalla el programa?     USUARIO


# Principios SOLID

Son 5 principios que enunció El tio Bob (Robert C. Martin) para escribir código limpio, mantenible y fácil de evolucionar. Tl tio Bob es uno de los firmantes del manifiesto ágil.

S - SRP: Single Responsibility Principle. Principio de responsabilidad única.
O - OCP: Open/Closed Principle. Principio de abierto/cerrado.
L - LSP: Barbara Liskov Substitution Principle. Principio de sustitución de Liskov.
I - ISP: Interface Segregation Principle. Principio de segregación de interfaces.
D - DIP: Dependency Inversion Principle. Principio de inversión de dependencias.

## Single Responsibility Principle

Una clase/método debe tener una única responsabilidad.      Fue la primera definición.. y de ahí el nombre... 
Además de que el concepto responsabilidad es ambigüo, hay otro problema.. Solapa con otro concepto que tenemos en la industría desde hace más de 60 años... la cohesión / acoplamiento.

Dío una segunda definición: "Una clase/método debe tener una única razón para cambiar".
Igual de ruina que la primera... Qué es cambio?

A la tercera fue la vencida (la dío en el libro Clean Architecture): 
"Una clase debe atender a un único actor"

Actor? Cualquier perona o entidad organizacional (departamento) que interactúa con el sistema o tiene algñun tipo de responsabilidad sobre el sistema.

El SRP es una evolución del concepto de cohesión. 

    La cohesión es una forma de medir/cuantificar cómo de relacionados están los métodos de una clase entre sí. O a nivel de proyecto, cómo de relacionados están las clases que hay en el proyecto entre sí.

    El SRP nos garantiza que el acoplamiento sea el mínimo posible. De forma que un cambio en un componente del sistema no afecte a otros componentes del sistema.

    El SRP no es un principio tecnológico, es un principio que trata sobre las personas.


## D - DIP: Dependency Inversion Principle. Principio de inversión de dependencias.

Un módulo de alto nivel no debe depender de un módulo de bajo nivel. En su lugar, ambos deben depender de abstracciones (interfaces). Las abstracciones no deben depender de los detalles. Los detalles deben depender de las abstracciones.

    En cristiano...
    Si pinto un diagrama de clases de mi sistema, de las clases solo pueden salir flechas.
    Solo pueden llegar flechas a las interfaces, no a las clases concretas.

## Inyección de dependencias.

Patrón de desarrollo de software por el cual, una clase NUNCA crea instancias de los objetos que necesita.
En su lugar le son suministradas desde el exterior.
La gracia de este patrón es que si lo aplico, en automático estoy cumpliendo el principio de inversión de dependencias.

---










---

# Que es Spring?

Framework de desarrollo en JAVA que nos facilita la vida... al entregarnos un CONTENEDOR DE INVERSIÓN DE CONTROL
Eso nos permite olvidarnos del flujo de nuestra aplicación, y centrarnos en escribir el código de los componentes que forman nuestra aplicación, y delegar a Spring la responsabilidad de ejecutar ese código en el momento adecuado.
Además, por facilitar la adopción del patrón de inyección de dependencias, nos facilita la adopción de una arquitectura de COMPONENTES @Component desacoplados entre sí, lo que redunda en que nuestro código sea más mantenible y fácil de evolucionar.

La configuración de los componentes de mi aplciación tradicionalmente se hacía en ficheros XML, con un lenguaje específico para configurar el comportamiento de mi aplicación.
Como eso era inhumano, aparece Spring boot, una LIBRERIA que me permite configurar mi aplicación mediante anotaciones en el código de mi aplicación, más fácil. Además Springboot me da otras cosillas.. que ya hablaremos.

Spring framework vs Spring Boot.

