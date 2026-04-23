# Sobre Spring

## Cómo pedir objetos a Sprint? Solicitar una inyección de dependencias?

- @Autowired en prop privada (opera con reflection, no es recomendable)
- Pedir un argumento en cualquier función que sea invocada por Spring.
- Si me interea un dato a nivel de clase, lo pido en el constructor (una función más).

NOTAS:
- Si hay varias implementaciones de una interfaz y se pide solo una, es necesrio decir a Spring cual entregar:
  - @Qualifier("nombreDeLaImplementacion") en el argumento del constructor.
  - @Primary en la clase de la implementación que quiero que se entregue por defecto.

## Cómo configurar lo que Spring debe entregar cuando alguien solicita una inyección de dependencias?

- @Component (o derivado: @Service, @Repository, @Controller, @RestController)
  Por defecto (@Scope Singleton): Se genera una instancia de la clase anotada y se guarda en el contenedor de Spring, para que la inyecte posteriormente si alguien se lo solicita.
  Se puede cambiar el scope a @Scope("prototype") para que se genere una nueva instancia cada vez que alguien lo solicite.

- @Configuration (Para librerias de terceros)
    Se anota una clase con esta anotación, y dentro de ella se definen funciones anotadas con @Bean, que devuelven el objeto que quiero que Spring entregue cuando alguien lo solicite.
    Esas funciones por defecto tienen scope singleton, pero se puede cambiar a @Scope("prototype") para que se genere una nueva instancia cada vez que alguien lo solicite. 
    También se pueden Qualifier para diferenciar entre varias implementaciones de una misma interfaz (igual que los componentes)



---
    FRONTAL BOT
    FRONTAL IVR
    FRONTAL iOS
    FRONTAL ANDROID
    FRONTAL (TS Angular)                              BACKEND (Java Spring)
--------------------------------------------    ---------------------------------------------------------------------------------------------------------------------------

                                                AnimalitosHttpRestControllerV1Impl
                                                            ^
                                                AnimalitosHttpRestControllerV1Impl -> AnimalitosService
                                                            ^                               ^
                                                            ^                         AnimalitosServiceImpl  -> AnimalitosRepository
                                                            ^                                 ^                            ^
                                                            ^                                 |                  AnimalitosRepositoryJPAImpl  -> Base de datos
                                                            ^                                 |                            ^               JPA   BBDD Relacional
   FormularioCaptura  >>>  ServicioAnimales  >>  Aplicación de Fermín-------------------------+----------------------------+------------------>     Oracle
   DatosAnimal
                                AnimalBACK       AnimalRestV1DTO crearAnimal(...)      AnimalDTO crearAnimal(...)    Animal crearAnimal(...)
                                 fn                         edad              ---maper-->    fn              ---maper-->  fn                     TABLA ANIMALITOS
                                  V                                                                                                                 - id            INT
                                  V                                                                                                                 - publicId      VARCHAR(20)
                                AnimalFront                                                                                                         - nombre        VARCHAR2(255)
                                 edad                                                                                                               ~~- edad          INT~~
                                                                                                                                                    - fechaNacimiento DATE
                                                                                                                                                    - especie       INT
                                                                                                                                                    - fechaDeAlta   DATE
                                                                                                                                                    - DNI           VARCHAR2(9)
                                            <-JSON- AnimalitosHttpRestControllerV2Impl
                                                            fn

   ^^^^^^^^^^^           ^^^^^^^^^^^                   ^^^^^^^^^^^                      ^^^^^^^^^^^                      ^^^^^^^^^^^                  ^^^^^^^^^
   Capturar datos        Comunicación              Lógica de exposición                 Lógica de negocio                Lógica de persistencia       Lógica de datos
                         con backend                  del servicio
                                                  POST /api/v1/animalitos
                                                    -> crearAnimal(...)
                                                  BODY (JSON) -> DatosDeCreacionDeAnimalDTO
                                                                    <--- Exception
                                                        201 Created
                                                        400 Bad Request
                                                        500 Internal Server Error




                AnimalitosCapaPersistencia-JPA-IMPL
                - AnimalitosRepositoryJPAImpl
                - class AnimalJPAEntity {
                    @Id
                    @GeneratedValue(strategy = GenerationType.IDENTITY)
                    private Long id;
                    private String publicId;
                    private String nombre;
                    ~~private Integer edad;~~
                    private LocalDate fechaNacimiento;
                    private Integer especie;
                    private LocalDate fechaDeAlta;
                  }
                            |
                         mapper (publicId -> id)
                            |
                            v
                  
                AnimalitosCapaPersistencia-API
                class Animal {
                    String getId();
                    String getNombre();
                    ~~Integer getEdad();~~
                    LocalDate getFechaNacimiento();
                    Especie getEspecie();
                    LocalDate getFechaDeAlta();
                }

                            |
                         mapper (fechaNacimiento -> edad)
                            |
                            v
                AnimalitosCapaServicio-API
                class Animal {
                    String getId();
                    String getNombre();
                    Integer getEdad();
                    Especie getEspecie();
                    LocalDate getFechaDeAlta();
                }


Los DTOs junto con los mapeadores son BARRERAS DE CONTENTION DE CAMBIOS. Me sirven para frenar la propagación de los cambios que se produzcan en una capa, antes de que lleguen a la siguiente.
---

## Programación Orientada a Aspectos.

Esta es otra de las gracias de Spring, que nos permite aplicar el paradigma de programación orientada a aspectos de forma muy sencilla.

En nuestro caso, la vamos a aplciar en capa de exposición de nuestro servicio, para la gestión de las excepciones que se puedan producir en la capa de negocio.
Vamos a usar un @ControllerAdvice para capturar las excepciones que se produzcan en la capa de negocio, y devolver una respuesta adecuada al cliente.

La programación orientada a aspectos es otro patrón de diseño basado en el concepto de Proxy.

Lo que hacemos es crear una clase por encima de la clase que queremos "aspectizar", y metemos en esa clase los mismos métodos que la clase que queremos "aspectizar", pero con la lógica que queremos aplicar antes o después de la ejecución del método original. Tiene 2 gracias:
1. Cohesión: Todo lo que tiene que ver con la gestión de excepciones, por ejemplo, lo tenemos encapsulado en una clase.
2. Reutilización: Podemos aplicar la misma lógica de gestión de excepciones a varias clases o varios métodos sin tener que repetir el código en cada una de ellas.


                                                            AnimalitosRestControllerV1Impl     AnimalitosServiceImpl      Exceptiones
                                                                crearAnimal(...)            -->  crearAnimal(...)         AnimalYaExisteException, DatosInvalidosException,    
                                                                                                                          RepositorioException, etc
                                                                borrarAnimal(...)           -->  eliminar(...)            RepositorioException
                                                                recuperarAnimal(...)        -->  recuperarAnimal(...)     RepositorioException
                                                                modificarAnimal(...)        -->  guardar(...)             RepositorioException, DatosInvalidosException

                AnimalitosRestControllerV1ImplProxy   ---> AnimalitosRestControllerV1Impl

```java
public class AnimalitosRestControllerV1ImplProxy implements AnimalitosRestControllerV1 {

    private final AnimalitosRestControllerV1Impl target;

    public AnimalitosRestControllerV1ImplProxy(AnimalitosRestControllerV1Impl target) {
        this.target = target;
    }

    @Override
    public ResponseEntity<AnimalDTO> crearAnimal(DatosDeCreacionDeAnimalDTO datos) {
        try {
            return target.crearAnimal(datos);
        } catch (Exception e) {
            // Manejo de excepciones
            return processException(e);
        }
    }

    @Override
    public ResponseEntity<Void> borrarAnimal(String id) {
        try {
            return target.borrarAnimal(id);
        } catch (Exception e) {
            // Manejo de excepciones
            return processException(e);
        }
    }

    @Override
    public ResponseEntity<AnimalDTO> recuperarAnimal(String id) {
        try {
            return target.recuperarAnimal(id);
        } catch (Exception e) {
            // Manejo de excepciones
            return processException(e);
        }
    }

    @Override
    public ResponseEntity<AnimalDTO> modificarAnimal(String id, DatosModificablesDeAnimalDTO datos) {
        try {
            return target.modificarAnimal(id, datos);
        } catch (Exception e) {
            // Manejo de excepciones
            return processException(e);
        }
    }

    private ResponseEntity processException(Exception e) {
        // Aquí iría la lógica para mapear las excepciones a respuestas HTTP adecuadas
        if (e instanceof AnimalYaExisteException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else if (e instanceof DatosInvalidosException) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } else if (e instanceof RepositorioException) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
```

Esto nos permite:
1. A nivel de la implementacion real, centrarnos en el HAPPY PATH, sin preocuparnos por la gestión de excepciones. Salen funciones muy limpias, fáciles de leer y de mantener.
2. Tener una unica clase y dentro de ella casi una única función que se encarga de mapear las excepciones a respuestas HTTP, lo que nos da una gran cohesión y facilita la reutilización de esa lógica de gestión de excepciones en otras clases o métodos si lo necesitamos.

La gracia, que Spring monta por mi en automático y en tiempo de ejecución esa clase. Lo único qye tengo que decirle es cómo quiero gestionar cada una de las excepciones que se puedan producir en la capa de negocio, y Spring se encarga de generar el proxy que va a aplicar esa lógica de gestión de excepciones a cada una de las funciones de mi controlador.

En Spring esto se llama un Advisor.
En el caso de que estemos montando una aplicación con servicios REST, lo que hacemos es crear un @ControllerAdvice, que es un Advisor específico para controladores REST, y dentro de él definimos métodos anotados con @ExceptionHandler, que se encargan de gestionar cada una de las excepciones que se puedan producir en la capa de negocio.

Por cierto @ControllerAdvice es un @Component.

El patrón PROXY a su vez es uina evolución o un uso concreto del patrón DECORATOR, que es un patrón de diseño estructural que nos permite añadir funcionalidad a un objeto de forma dinámica, sin tener que modificar su código.

Por ejemplo, esto lo podemos usar para logs.

---
# Versionado de software- Esquema smver = Esquema de versionado semántico

vA.B.C
                    ¿Cuándo suben?
    A    MAJOR      Breaking changes: Cambio que rompe compatibilidad
    B    MINOR      Nuevas funcionalidades
                    Funcionalidades marcadas como "deprecated"
                        + opcionalmente pueden venir bug fixes 
    C    PATCH      Bug fixes


---

# Programación Reactiva

Es un modelo de programación, para programación asíncrona, basado en el concepto de flujos de datos, y la propagación de cambios.
Su gracia adicional es que NO ES BLOQUEANTE, lo que significa que no bloquea el hilo de ejecución mientras espera a que se produzca un evento o a que se complete una operación asíncrona.

Una comunicación la puedo hacer Síncrona o Asíncrona.
A veces dedido yo... otras, el método me impone algo... pero en general siempre decido yo.

- Síncrono          Espero a que me contesten cuando pido algo
- Asíncrono         No espero a que me contesten cuando pido algo, sigo con mi vida.

A veces, incluso procedimientos Asíncronos, los puedo usar de forma síncrona.

    Mensaje por whatsapp...LA COMUNICACION ES ASINCRONA
    Pero... me puedo quedar mirando la pantalla hasta que me respondan... y decidir no hacer nada más hasta que me respondan... LA USO DE FORMA SINCRONA

Qué estrategias tenemos en programación para gestionar la asincronía?

- Quién ejecuta código en una CPU? Un HILO de ejecución THREAD (que son aportados por el SO)
  Hay CPUs físicas que admiten MULTITHREADING, lo que significa que pueden ejecutar varios hilos de ejecución a la vez.

  Yo puedo estar haciendo una cosa y abrir un thread para que haga otra cosa.
  Trabajar con hilos es duro... sobre todo la sincronización y la comunicación.

  Muchas veces nos aislamos de los hilos, y usamos otras abstracciones que nos permiten trabajar con asincronía sin tener que preocuparnos por los hilos.

  Hay un patrón muy típico que usamos en programación: 
  - En el mundo JS es mediane las PROMESAS
  - El mismo patrón lo tenemos en JAVA: FUTURES

Que es eso? Es lo mismo que si voy a la lavandería y me entregan TICKET/VALE.
Ese Ticket es lo que en programación llamamos una Promesa o un Future. 
Es un objeto que me representa el resultado de una operación asíncrona que todavía no ha terminado, pero que va a terminar (CONFIEMOS) en algún momento.

Dios mediante, con el ticker en el futuro, me entregarán la camisa. 
Lo tengo garantizado? NO. Si se quema la camisa o la lavandería.. pues no me la darán.. No me JURAN que me la van a dar, pero me la prometen.
VOY A HACER MI MEJOR ESFUERZO PARA ENTREGARTE LA CAMISA, PERO NO TE LO PUEDO GARANTIZAR / JURAR.

```java
                                Ropa unaRopa = new Camisa();
                                Ticket<Camisa> miTicket = lavandería.<Camisa>dejar(unaRopa); // Internamente la función dejar() abrirá un hilo de ejecución o ... ella sabrá... 
                                                                                             // para que yo me quede libre. En el caso de la lavandería, hay un hilo (un empleado) que se encargará de lavar la ropa, y cuando termine, me entregará el ticket con la camisa limpia.
                                //Aquí puedo ir haciendo más cosas.
                                //- Ir al mercadona
                                //- Ir a hacer deporte
                                //- Ir a tomar algo con mis amigos
                                Y si me interesa en algún momento puedo preguntar:
                                if (miTicket.estaListo()) {
                                    Camisa miCamisaLimpia = miTicket.get();
                                } else{
                                    - Voy a cenar y me acuesto
                                }
                                if (miTicket.estaListo()) {
                                    Camisa miCamisaLimpia = miTicket.get();
                                } else{
                                    - Voy a cenar y me acuesto
                                }
                                //También puedo plantarme.
                                //Voy a la lavandería y me quedo en la puerta... esperando, hasta que me entreguen la camisa.
                                ticket.await();                                                 // Esto es lo mismo que quedarme esperando en la puerta de la lavandería, 
                                                                                                //hasta que me entreguen la camisa. No hago nada más, no voy a hacer nada más, no voy a ir a ningún sitio, no voy a hacer nada... solo esperar a que me entreguen la camisa.
```

Esto es una forma de trabajar.
Pero... hay otras formas. Una de ellas, que nos gusta mucho es mediante programación funcional.

Lo que hago es a nivel del petición, configurarle de antemano lo que quiero que ocurra cuando aquello esté listo... o lo que quiero que ocurra si se produce un error... o lo que quiero que ocurra si se cancela la operación... etc. Para eso dejo programadas funciones de callback, que se ejecutarán cuando el ticket esté listo, o cuando se produzca un error, o cuando se cancele la operación... etc.

lavandería.<Camisa>dejar(unaRopa, funcionQueQuieroQueHagasCuandoEsteLista, funcionQueQuieroQueHagasSiSeProduceUnError);
                                  llamaAMiMadreYSeLaEntregasAElla        , llamaAMiAbogadoYLeDicesQueTePaseFactura

Por ejemplo... ESTO SE USA UN HUEVO EN PROGRAMACION DE FRONTALES.

Muestro un <div id="panel"> Cargando...</div> 
Hago una petición a un backend... y dejo configurado que cuando esa petición esté lista, quiero que se ejecute una función que me actualice el contenido del <div id="panel"> con la información que me ha llegado del backend... y también dejo configurado que si se produce un error, quiero que se ejecute otra función que me actualice el contenido del <div id="panel"> con un mensaje de error.


---

# Cual es el prpoblema de fondo.

Dependiendo del patrón que use, para gestionar la asincronía, bloqueo o no el hilo de ejecución.

Cuántos hilos puedo abrir en un entorno? Infinitos?
Cada hilo requiere:
- CPU para ejecutarse (esto no es problema... si no hay CPU... espera a que la haya. El SO encola las tareas y las va ejecutando cuando hay CPU disponible)
- Memoria para su pila de ejecución y sus variables locales (esto sí es un problema... no puedo abrir infinitos hilos... si abro muchos hilos, me quedo sin memoria y el sistema se cae)

En un servidor... para una app java... el número de hilo  depende de la capacidad de la máquina donde esté trabajando...
Pero 20, 100 hilos... 200... 1000 (ya es una burrada) son las cifras normales... que un servidor aguanta.

Y si quiero dar un salto de magnitud? y quiero atender 1 millon de peticiones concurrentes? Aquí qué?

En especial en entornos donde esas peticiones ESPERAN MUCHO a terceros! 


Servidor de aplicaciones tradicional: TOMCAT o derivados o similares:

    Estos servidores de aplicaciones, lo primero que les configuro es un Pool de ejecutores, que es un conjunto de hilos de ejecución, que se van a encargar de atender las peticiones que lleguen al servidor.

                                                Pool de ejecutores
         Cola de peticiones : Queue FIFO                                  BBDD          LOGS     KAFKA
    --------------------------------------      Hilo 1 - P1 XXXXXXXXXXXX........XXXXXXX......XXXX......XXXXXXXX
        PN+5, PN+4, PN+3, PN+2, PN+1        <   Hilo 2 - P2 XXXXXXXXXXXX........XXXXXXX......XXXX......XXXXXXXX
                                                Hilo 3 - P3 XXXXXXXXXXXX........XXXXXXX......XXXX......XXXXXXXX
                                                ...
                                                Hilo n - Pn XXXXXXXXXXXX........XXXXXXX......XXXX......XXXXXXXX
    --------------------------------------

        X = Está usando CPU
        . = Está esperando a que se produzca un evento o a que se complete una operación síncrona

    El problema es que quizás la CPU no está a tope... la tengo bajita.


Lo que queremos es parsar a este modelo:
                                                Pool de ejecutores
         Cola de peticiones : Queue FIFO                                  BBDD          LOGS     KAFKA
    --------------------------------------      Hilo 1 - P1(Tarea1)XXXXXXXXXXXXPN+1(Tarea1)XXXXXXXP2(Tarea2)XXXXXXXXXXXX
        PN+5, PN+4, PN+3, PN+2, PN+1        <   Hilo 2 - P2(Tarea1)XXXXXXXXXXXXPN+2(Tarea1)XXXXXXXXP3(Tarea2)XXXXXXXXXXXX
                                                Hilo 3 - P3(Tarea1)XXXXXXXXXXXXPN+3(Tarea1)XXXXXXP1(Tarea2)XXXXXXXXXXXX
                                                ...
                                                Hilo n - Pn(Tarea1)XXXXXXXXXXXXPN+n(Tarea1)XXXXXXX
    --------------------------------------


    En lugar de asociar cada hilo a una petición, lo que hacemos es ir pidiendo a los hilos que vayan atendiendo tareas consecuencia de las peticiones que van llegando, y cuando un hilo termina de atender una tarea, le pido que atienda otra tarea, y así sucesivamente. De esta forma, puedo atender muchas más peticiones concurrentes, sin tener que abrir muchos hilos de ejecución, y sin quedarme sin memoria.

A esto solo le saco partido si:
- Tengo la máquina con CPU de sobra, para que los hilos puedan ejecutar las tareas sin problemas.
- Tengo mucho tiempo de espera en las tareas, para que los hilos puedan atender muchas tareas mientras esperan a que se produzca un evento o a que se complete una operación síncrona.
  

Ir aprogramación reactiva no es que cambie un paquete de JAVA por otro.
Todo el stack completo debe soportar la programación reactiva, desde el controlador REST, pasando por la capa de servicio, la capa de persistencia, hasta:
- Servidor de aplicaciones que soporte programación reactiva Que ya no vale tomcat, ni el el weblogic, ni el jboss... etc. Necesito un servidor de aplicaciones que soporte programación reactiva, como por ejemplo NETTY.
- Necesito drivers de conexión a la base de datos que soporten programación reactiva, como por ejemplo R2DBC.
- Necesito librerías de conexión a otros sistemas que soporten programación reactiva, como por ejemplo, para conectar con Kafka, necesito una librería de conexión a Kafka que soporte programación reactiva, como por ejemplo Reactor Kafka.

Todo el stack tecnológico cambia.
No es una función la hago reactiva y otra no.
Es más... en cuanto hay una tarea bloqueante en el stack, ya no estoy sacando partido a la programación reactiva, porque el hilo de ejecución se va a quedar bloqueado esperando a que se complete esa tarea bloqueante, y no va a poder atender otras tareas mientras tanto.

La gracia.. o el mecanismo es que preconfiguro lo que voy a hacer cuando se procesa una petición:

    - En el controlador:
      SINCRONO:        Al recibir una petición, lo que hago es Mapear el RESTDTO a DTO de servicio
                       Luego llamo a la capa de servicio.
                       Espero respuesta de la capa de servicio:  Capa de servicio me devuelve una "Future" ("Mono"|"Flux").
                       Y configuro que cuando esa "Future" esté listo, quiero que:
                            - se ejecute una función que me mapee el DTO de servicio a un RESTDTO   // ASINCRONO
                            - y me devuelva la respuesta al cliente.                                // ASINCRONO
    - En la capa de servicio:
      SINCRONO:        Al recibir la llamada del controlador, lo que hago es Mapear el DTO de servicio a un DTO de persistencia
                       Luego llamo a la capa de persistencia.
                       Espero respuesta de la capa de persistencia:  Capa de persistencia me devuelve una "Future" ("Mono"|"Flux").
                       Y configuro que cuando esa "Future" esté listo, quiero que:
                            - se ejecute una función que me mapee el DTO de persistencia a un DTO de servicio   // ASINCRONO
                            - y me devuelva la respuesta al controlador.                                        // ASINCRONO
    - En la capa de persistencia:
      SINCRONO:        Al recibir la llamada de la capa de servicio, lo que hago es Mapear el DTO de persistencia a un DTO de base de datos
                       Luego llamo a la base de datos.
                       Espero respuesta de la base de datos:  La base de datos me devuelve una "Future" ("Mono"|"Flux").
                       Y configuro que cuando esa "Future" esté listo, quiero que:
                            - se ejecute una función que me mapee el DTO de base de datos a un DTO de persistencia   // ASINCRONO
                            - y me devuelva la respuesta a la capa de servicio.                                      // ASINCRONO 
      
      Y en este momento el hilo queda libre



      Controlador:                      SERVICIO                    PERSISTENCIA
      Al recibir una petición, lo que hago es Mapear el RESTDTO a DTO de servicio
      Luego llamo a la capa de servicio.
      Espero respuesta de la capa de servicio:  Capa de servicio me devuelve una "Future" ("Mono"|"Flux").
                                      Al recibir la llamada del controlador, lo que hago es Mapear el DTO de servicio a un DTO de persistencia
                                      Luego llamo a la capa de persistencia.
                                      Espero respuesta de la capa de persistencia:  Capa de persistencia me devuelve una "Future" ("Mono"|"Flux").

                                                                    Al recibir la llamada de la capa de servicio, lo que hago es Mapear el DTO de persistencia a un DTO de base de datos
                                                                    Luego llamo a la base de datos.
                                                                    Espero respuesta de la base de datos:  La base de datos me devuelve una "Future" ("Mono"|"Flux").
                                                                    Y configuro que cuando esa "Future" esté listo, quiero que:
                                                                          - se ejecute una función que me mapee el DTO de base de datos a un DTO de persistencia  (1)                          // ASINCRONO
                                                                          - y me devuelva la respuesta a la capa de servicio.  (2)             // ASINCRONO

                                       Y configuro que cuando esa "Future" esté listo, quiero que:
                                         - se ejecute una función que me mapee el DTO de persistencia a un DTO de servicio   (3)                          // ASINCRONO
                                         - y me devuelva la respuesta al controlador.  (4)                                        // ASINCRONO


      Y configuro que cuando esa "Future" esté listo, quiero que:
        - se ejecute una función que me mapee el DTO de servicio a un RESTDTO   (5)                          // ASINCRONO
        - y me devuelva la respuesta al cliente.  (6)                                   // ASINCRONO

La BBDD eventualmente contestará... y entonces:
      - (1) se ejecutará una función que me mapee el DTO de base de datos a un DTO de persistencia                            // ASINCRONO
      - (2) me devolverá la respuesta a la capa de servicio.                                                                      // ASINCRONO
      - (3) se ejecutará una función que me mapee el DTO de persistencia a un DTO de servicio                                           // ASINCRONO
      - (4) me devolverá la respuesta al controlador.                                                                                 // ASINCRONO
      - (5) se ejecutará una función que me mapee el DTO de servicio a un RESTDTO                                                                 // ASINCRONO
      - (6) me devolverá la respuesta al cliente.                                                                                                   // ASINCRONO


Esto está guay... para cierto tipo de sistemas... y aún hoy en día se sigue usando. Tiene más años que Matusalén.
Es más.. su uso hoy en día ha caído mucho.
Por qué? Por la apareción en la versión de JAVA 21 de los hilos virtuales, que nos permiten tener muchos más hilos de ejecución sin quedarnos sin memoria, lo que hace que el modelo tradicional de programación síncrona con hilos sea mucho más escalable y eficiente, y por lo tanto, mucho más atractivo para los desarrolladores.

Desde hace unas cuantas versiones, en JAVA tenemos los virtual threads... Es un cambio que se hizo en la JVM y que nos permite tener muchos más hilos de ejecución sin quedarnos sin memoria, lo que hace que el modelo tradicional de programación síncrona con hilos sea mucho más escalable y eficiente, y por lo tanto, mucho más atractivo para los desarrolladores.

Me permite simplemente cambiar un parametro de configuración en el servidor de aplicaciones (SPRING), para que en lugar de usar hilos tradicionales, use hilos virtuales, Y ESO ME PERMITE YA ABRIR 100k hilos din problema... y sin necesidad de complicarme la vida con la programación reactiva, que es un modelo de programación mucho más complejo y difícil de entender y de mantener.

Si quiero ir a tope: PROGRAMACION REACTIVA 
Pero si lo quiero es darle solo un BUEN BOOST, sin complicarme la vida, sin tener que cambiar todo el stack tecnológico, sin tener que cambiar toda la forma de programar... lo que hago es pasar a usar hilos virtuales, y me olvido de la programación reactiva, y me quedo con el modelo tradicional de programación síncrona con hilos, que es mucho más sencillo de entender y de mantener, y que con los hilos virtuales, es mucho más escalable y eficiente.


Como patrón de programación se usa un huevo.
Especialmente en programación de frontales, Angular, React... trabajan mucho con una libreria de JS... que es la implementación de REACTIVE STREAMS para JS... que se llama RXJS, y que nos permite trabajar con programación reactiva de una forma muy sencilla y muy potente.