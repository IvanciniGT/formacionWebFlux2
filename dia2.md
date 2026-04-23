
Paradigmas de programación:

- Imperativo
- Procedural
- Orientado a objetos

- Funcional             Cuando el lenguaje me permite asignar una variable a una función y posteriormente ejecutar esa función a través de la variable, entonces ese lenguaje es un lenguaje de programación funcional. 
                        En Java, a partir de la versión 1.8, tenemos funciones de primera clase, lo que nos permite escribir código funcional en Java.
                        Nos abre la puerta a:
                        - Crear funciones que acepten funciones como argumentos.
                        - Crear funciones que devuelvan funciones. CLOSURES
                          Modelos de programación que hacen uso intensivo de programación funcional: REACTIVE PROGRAMMING, PROGRAMACION MAP/REDUCE... 
- Declarativo           Decimos que quiero.. pero no cómo conseguirlo (eso es programación imperativa).
                        En JAVA , las anotaciones son un ejemplo de programación declarativa. 

---

Un producto de software por definición es un producto sujeto a cambios y mantenimiento.
Nos da igual tardar más tiempo hoy en crearlo!
Hay un concepto llamado LCC - Coste del ciclo de vida (Lifecycle cost) que se refiere al coste total de un producto a lo largo de su ciclo de vida, incluyendo el desarrollo, mantenimiento, evolución, etc...
Es mucho más dinero y esfuerzo el operar y mentener un sistema 24x7 durante años que el coste de desarrollo inicial.
Y el desarrollo de los evolutivos será también mayor que el coste de desarrollo inicial.
Y aquñi hay un problema grande: DEUDA TÉCNICA

Y eso lo sabemos no porque seamos muy listos... Ya lo vivimos muchas veces.. La primera en la gran crisis del software (finales de los 60).

Hay principios que con el tiempo se han ido desarrollando para ayudarnos a montar sistemas que sean más fáciles de mantener y evolucionar, y por tanto, a reducir la deuda técnica:
 YAGNI, DRY, KISS, SOLID, GRASP, etc...

# SOLID

S - Single responsibility principle        Tiene que ver con personas!
                                           Que la definición simple que nos han enseñado no es correcta. Eso es COHEXION
                                           Un módulo o compoente solo debe responder ante un único actor.
O - Open/closed principle
L - Liskov substitution principle
I - Interface segregation principle
D - Dependency inversion principle

# Dependency inversion principle

Un componente de alto nivel no debe depender de implementaciones de componentes de bajo nivel, sino de abstracciones.
No quiero ni un triste import en una clase que apunte a otra.
No quiero que un jar que contenga implementaciones apunte a otro jar que tenga implementaciones (dependencias del pom)

# Inyección de dependencias

Es un patrón de desarrollo, una forma de escribir código. Esta forma nos ayuda a respetar el principio de inversión de dependencias, y por tanto, a escribir código más mantenible y fácil de evolucionar.

    Una clase no debe crear instancias de objetos que necesita. En su lugar, le serán suministrados desde el exterior.

# Inversión de control

Yo no aporto el flujo a mi app.
Lo delego en un framework. El framework, en nuestro caso Spring, es quien aporta el flujo... quien ejecuta mi programa y mis componentes.

```java
    public class MiApp {

        public static void main (String[] args) {
                SpringApplication.run(MiApp.class, args); // Aqui es donde aplicamos la inversión de control. 
        }

    }
```
 
Esto tiene 2 ventajas:
 1. Me olvido del flujo de mi aplicación, y me centro en escribir el código de los componentes que forman mi aplicación.
 2. Gracias a que Spring es quien ejecuta mis componentes y sus funciones, y también quien crea instancias de mis componentes, me facilita la adopción del patrón de inyección de dependencias, nos hace más fácil respetar el principio de inversión de dependencias, y por tanto, escribir código más mantenible y fácil de evolucionar, al adoptar una arquitectura de componentes desacoplados entre sí.


Vamos a hacer mucho MUCHO uso de otro de los grande principios del desarrollo de software: SOC.

SOC - Separation of concerns. Separación de preocupaciones. Lo enunció en un papel llamado THE HUMBLE PROGRAMMER el desarrollador: Edsger W. Dijkstra

---

# Spring / SpringBoot

## Cómo puedo pedirle cosas (DEPENDENCIAS, OBJETOS QUE NECESITO) a Spring?

Yo voy a montar clases... y a veces, mis clases necesitan instancias de otros objetos. 

Cómo se lo pido a Spring. Hay 3 estrategias:

### Anotación @Autowired

```java
import MiDependencia;

public class MiClase {

    @Autowired
    private MiDependencia miDependencia;

    public MiClase() {
            // Podría hacer uso de la variable miDependencia aquí?
            // NO, porque Spring aún no la ha inyectado. No la hace hasta que crea la instancia.. es decir, hasta que termina de ejecutar el constructor. Por tanto, no puedo usar esa variable en el constructor, ni en ningún método que se ejecute desde el constructor.
    }

    public void miFuncion() {
        miDependencia.hacerAlgo();
    }

}
```
NOTA:
- MiDependencia será una clase o una interfaz? UNA INTERFAZ, ya que de lo contrario me cagaría en el principio de inversión de dependencias.

Esto funciona si y solo si:
- Hay 1 única implementación de MiDependencia que Spring conozca.
- Es Spring el que genera la intancia de MiClase, es decir, si yo genero la instancia de MiClase, entonces Spring no tiene control sobre esa instancia, y por tanto, no puede inyectar la dependencia.

    Es decir, el new MiClass() lo debe hacer Spring, no yo.
    Cómo le pido a Spring que haga él un new MiClass? YA LO VEREMOS! SOC!

NOTAS IMPORTANTES:
- ESTO ESTA TOTALMENTE DESACONSEJADO, NO SE USA DESDE HACE AÑOS! Por qué?

Vamos a imaginar cómo es el código de Spring:

```java
MiDependencia miDependencia = // lo que sea;
MiClase miClase = new MiClase();
miClase.miDependencia = miDependencia; // Esto compila en JAVA? La variable es privada... por ende se puede rellenar desde fuera? NO
```

Entonces cómo ostias lo hace Spring?
Spring usa una chapuza gigante que existe en JAVA, que es la reflexión (paquete java.lang.reflect). Este paquete permite atacar directamente a la memoria RAM saltándose cualquier restriccion de acceso que hayamos usado en el código.
Lo primero, eso es una vulnerabilidad de seguridad (de hecho en muchos entornos ese paquete está desactivado)
Lo segundo, eso tarda un huevo... hay un montón de operaciones por debajo para conseguir esa funcionaldiad.
Por eso NO QUEREMOS BAJO NINGÜN CONCEPTO USAR ESTA FORMA DE INYECTAR DEPENDENCIAS, que funciona! PERO NO APORTA NADA BUENO.. sobre todo porque hay alternativas más decentes.



### Ponerlo en cualquier función de mi clase como argumento

```java
public class MiClase {

    public MiClase() {
    }

    public void miFuncion(MiDependencia miDependencia) {
        miDependencia.hacerAlgo();
    }

}
```
Esto funciona si y solo si:
- Hay 1 única implementación de MiDependencia que Spring conozca.
- Es Spring quien llama a la función, si soy yo quien la llama Spring no va a pasar el argumento por mi.

Spring cuando va a invocar una función, mira los argumentos que esa función necesita, y si los puede rellenar, los rellena. Si no puede, ERROR.

Y esto da lugar a una cosita:


```java
import MiDependencia;

public class MiClase {

    private MiDependencia miDependencia;

    public MiClase(MiDependencia miDependencia) {
        this.miDependencia = miDependencia;
    }

    public void miFuncion() {
        miDependencia.hacerAlgo();
    }
    public void miFuncion2() {
        miDependencia.hacerAlgo();
    }

}
```
Esto funciona si y solo si:
- Hay 1 única implementación de MiDependencia que Spring conozca.
- Es Spring quien llama a la función, si soy yo quien la llama Spring no va a pasar el argumento por mi.
  Es decir, si es Spring el que genera la intancia de MiClase, es decir, si yo genero la instancia de MiClase, entonces Spring no tiene control sobre esa instancia, y por tanto, no puede inyectar la dependencia.



## Cómo le digo a Spring qué debe entregar cuando alguien le pide una dependencia?

Hay 2 formas de hacerlo:

### Forma 1, la GUAY! MEDIANTE LA ANOTACION COMPONENT o una derivada

```java

public interface MiDependencia {
    void hacerAlgo();
}

@Component 
// Spring, si alguien te pide una instancia de MiDependencia, entrega una instancia de esta clase.
// Spring en el arranque de la aplciación, escanea el código buscando clases anotadas con @Component (o una derivada), y cuando encuentra una, genera una instancia de ella, y la guarda en un MAP interno (contexto de spring), con la clave siendo el tipo de dato que esa clase implementa (en este caso MiDependencia) y el valor siendo la instancia de esa clase.
// Desde este momento si alguien le pide a Spring una instancia de MiDependencia, Spring le entrega la instancia de MiDependenciaImpl que ha creado.
public class MiDependenciaImpl implements MiDependencia {
    public void hacerAlgo() {
        // implementación de la función
    }
}

public class MiClaseQueNecesitaUnaDependencia {

    private MiDependencia miDependencia;

    public MiClaseQueNecesitaUnaDependencia(MiDependencia miDependencia) {  // Si Spring crea la instancia de esta clase
                                                                            // Spring suministrará la dependencia
        this.miDependencia = miDependencia;
    }

    public void miFuncion() {
        miDependencia.hacerAlgo();
    }

}
```

> Pregunta: Puede haber más de una clase que implemente la misma interfaz con @Component? 

Sin problema. El problema está en otro sitio.

Si tengo 2 clases que implementan la misma interfaz, y ambas anotadas con @Component...

- Si alguien pide una Lista de esa interfaz, Spring le entrega una lista con las 2 implementaciones.

```java
public interface MiDependencia {
    void hacerAlgo();
}
@Component
public class MiDependenciaImpl1 implements MiDependencia {
    public void hacerAlgo() {
        // implementación de la función
    }
}
@Component
public class MiDependenciaImpl2 implements MiDependencia {
    public void hacerAlgo() {
        // implementación de la función
    }
}

public class MiClaseQueNecesitaUnaDependencia {

    private List<MiDependencia> misDependencias;

    public MiClaseQueNecesitaUnaDependencia(List<MiDependencia> misDependencias) {  // Si Spring crea la instancia de esta clase
                                                                            // Spring suministrará la lista con las 2 implementaciones
        this.misDependencias = misDependencias;
    }

    public void miFuncion() {
        for (MiDependencia miDependencia : misDependencias) {
            miDependencia.hacerAlgo();
        }
    }

}
```

Ese código funciona perfecto.

El problema es situviera una clase que pidiera solo una instancia.

```java
public class MiClaseQueNecesitaUnaDependencia {

    private MiDependencia miDependencia;

    public MiClaseQueNecesitaUnaDependencia(MiDependencia miDependencia) {  
        this.miDependencia = miDependencia;
    }

    public void miFuncion() {
        miDependencia.hacerAlgo();
    }

}
```

Spring arranca.. y crea las instancias de las 2 clases anteriores (implementaciones).. pero cuando llega a este punto dice... NO SE CUAL ENCHUFAR. ERROR ABORTO EJECUCION DEL PROGRAMA.

En este caso, tenemos 2 estrategias:
- Usar la anotación @Primary en una de las implementaciones, para indicarle a Spring que esa es la implementación por defecto.
  Esto es especialmente util esn TESTS.
- Usar la anotación @Qualifier para indicarle a Spring qué implementación queremos en cada caso.
  Nos permite asignar "tipos" de implementación.


```java
public interface MiDependencia {
    void hacerAlgo();
}
@Component
@Qualifier("miDependenciaImpl1")
public class MiDependenciaImpl1 implements MiDependencia {
    public void hacerAlgo() {
        // implementación de la función
    }
}
@Component
@Qualifier("miDependenciaImpl2")
public class MiDependenciaImpl2 implements MiDependencia {
    public void hacerAlgo() {
        // implementación de la función
    }
}
public class MiClaseQueNecesitaUnaDependencia {

    private MiDependencia miDependencia;

    public MiClaseQueNecesitaUnaDependencia(@Qualifier("miDependenciaImpl1") MiDependencia miDependencia) {  
        this.miDependencia = miDependencia;
    }

    public void miFuncion() {
        miDependencia.hacerAlgo();
    }

}
```

Todo esto que os he contado es el funcionamiento por defecto.
Spring por defecto, cuando encuentra CUALQUIER COMPONENTE @Component, le aplica un patrón sinleton, es decir, crea una única instancia de esa clase, y se la entrega a todo el mundo que se la pida.

Eso se puede cambiar... mediante otra anotación que pongo pegadita a la anotación @Component, que es @Scope, y le digo el scope que quiero para esa clase.
Hay varios scopes:
- @Component @Scope("singleton") -> El comportamiento por defecto, una única instancia para toda la aplicación.
- @Component @Scope("prototype") -> Cada vez que alguien pida una instancia de esa clase, Spring le entrega una nueva instancia.
- @Component @Scope("request") -> Cada vez que alguien pida una instancia de esa clase, Spring le entrega una nueva instancia, pero además, si esa clase es usada en el contexto de una petición HTTP, entonces esa instancia se comparte entre todos los componentes que la necesiten durante el procesamiento de esa petición HTTP. Es decir, cada petición HTTP tiene su propia instancia de esa clase, y esa instancia se comparte entre todos los componentes que la neces

```java

public class MiSingleton {
    private static final volatile MiSingleton instance = new MiSingleton();
    // Volatile.
    // El código se ejecuta en una CPU.. y las CPUS tiene cache!
    // Con es palabra le digo a JAVA que no haga uso de la cache de CPU para esa variable.. ya que puede haber otro hilo en paralelo estableciendo la variable y si ese hilo se está ejecutando en otro coore/cpu entonces ese hilo no verá el cambio que se ha hecho en la variable, y eso puede llevar a que se creen varias instancias de la clase, lo que rompe el patrón singleton.
    private MiSingleton() {
        // constructor privado para evitar instanciación externa
    }

    public static MiSingleton getInstance() {
        if (instance == null) {                   // Evita el sincronized una vez que la variable instance ya ha sido inicializada.
                                                  // el synchronized es una operación muy costosa
            synchronized (MiSingleton.class) {    // Esto implica COLAS.
                                                  // Evita lo que llamamos un RACE CONDITION, que es cuando 2 hilos intentan crear la instancia al mismo tiempo, y ambos ven que instance es null, y ambos crean una instancia, lo que rompe el patrón singleton.   
                if (instance == null) {           // Este asegura que solo se crea una instancia de la clase
                    instance = new MiSingleton();
                }
            }
        }
        return instance;
    }
}
```
NOTA. SI PUEDO HACER ESTO (el poner @Component en la clase) NO SIGO LEYENDO ESTE DOCUMENTO.

### Forma 2

Lo anterior es la opción que hay que elegir siempre que sea posible... pero a veces no es posible.

```java
public interface MiDependencia {
    void hacerAlgo();
}
public class MiDependenciaImpl implements MiDependencia {
    public void hacerAlgo() {
        // implementación de la función
    }
}
// Y resulta que esa clase y esa interfaz de arriba no son mias... son de una librería de terceros.

public class MiClaseQueNecesitaUnaDependencia {

    private MiDependencia miDependencia;

    public MiClaseQueNecesitaUnaDependencia(MiDependencia miDependencia) {  
        this.miDependencia = miDependencia;
    }

    public void miFuncion() {
        miDependencia.hacerAlgo();
    }

}
```

Puedo ir a la clase MiDependenciaImpl y ponerle la anotación @Component? NO.. no es mi clase.
SOLO EN ESTE ESCENARIO usamos esta segunda opción:

```java
@Configuration
// Spring busca en esta clase, que por ahi seguro encuentras algo con anotación @Bean que te interese.
public class MiConfiguracion {
    @Bean
    // Ejecuta esta función. GUARDAS EL RESULTADO. Si alguien te pide una instancia de lo que devuelve esta función según su firma (MiDependencia) le entregas el resultado que has guardado.
    public MiDependencia federico( ) {
        return new MiDependenciaImpl(); // Ahora soy yo quien crea la instancia... Pero Spring es quien llama a la función federico.
    }   

}
```

La segunda opción es crear una clase con anotación @Configuration, y dentro de esa clase, crear un método que devuelva una instancia de la clase que quiero que Spring entregue cuando alguien le pida una instancia la interfaz que me interese, y ese método lo anoto con @Bean.

Spring cuando arranque, además de buscar clases anotadas con @Component, también buscará clases anotadas con @Configuration.

Cuando encuentra una, mira todos los métodos de esa clase, y si encuentra alguno (o varios) anotado con @Bean, los ejecuta, y el valor que devuelva ese método lo guarda en su contexto, con la clave siendo el tipo de dato que devuelve el método (en este caso MiDependencia) y el valor siendo el valor que devuelve ese método (en este caso una instancia de MiDependenciaImpl).

Sobre estas funciones, es decir, apralelo a  @Bean se aplican los mismos conceptos que a los componentes @Component, es decir, por defecto se aplicará el patrón singleton, pero se puede cambiar mediante la anotación @Scope.
Además si tenemos más de un Bean que devuelva el mismo tipo de dato, entonces se aplican las mismas estrategias que con los componentes @Component, es decir, @Primary y @Qualifier.

---

## Pruebas de software

### Vocabulario:

- Error     Los humanos cometemos errores. 
- Defecto   Al cometer un error, un humano puede introducir un defecto en un producto. 
            Un defecto es la huella, la cicatriz que un error humano ha dejado en el producto.
- Fallo     La manifestación de un defecto... que puede producirse o no!
            Desviación con respecto a lo esperado.

                ERROR -> DEFECTO -> FALLO

### Para que sirven las pruebas?

- Asegurar el cumplimiento de unos requisitos funcionales y no funcionales.
- Mi objetivo es identificar la mayor cantidad posible de defectos en mi producto antes del paso a producción, o de la entrega al cliente.
    Hay 2 estrategias:
    - Usar el producto y tratar de provocar fallos. ESTO ES LO HABITUAL.
      Una vez identificado un fallo, hacemos DEPURACION O DEBUGGING, que es el proceso de identificar el defecto (BUG) que ha provocado ese fallo, y corregirlo. 
    - Revisar el producto sin usarlo. Busco DEFECTOS DIRECTAMENTE.
- Recopilar la mayor cantidad posible de información para facilitar el debugging. Cuanta más información tenga sobre el fallo, más fácil será identificar el defecto que lo ha provocado.
- Para saber qué tal va el proyecto
- ...

### Tipos de pruebas

Hay muchas taxonomías para clasificar los tipos de pruebas.
Toda pruena se centra en UNA UNICA COSA... por qué?
Para facilitar el debugging. Si la prueba falla, sé que ha fallado.
Si un a prueba prueba 4 cosas... si falla... no se que ha fallado... mira ver cuál de las 4 ha fallado.

#### En base al procedimiento de ejecución:
- Pruebas dinámicas: Consisten en ejecutar el producto, y trato de provocar fallos.
- Pruebas estáticas: Consisten en revisar el producto sin ejecutarlo, tratando de identificar defectos directamente.
                     SonarQube, Revisión de código, etc... 

#### En base a la naturaleza del objeto de prueba:
- Pruebas funcionales           Se centran en verificar que el producto cumple con los requisitos funcionales.
- Pruebas no funcionales        Se centran en verificar que el producto cumple con los requisitos no funcionales:
  - Pruebas de seguridad
  - Pruebas de carga
  - Pruebas de estres
  - Pruebas de rendimiento
  - Pruebas de usabilidad
  - Pruebas de HA   
  - Prueba de experiencia de usuario (UX)
  - ...

#### En base al contexto de ejecución: (SCOPE)

- Test unitario                 Se centra en una característica de un componente AISLADO del sistema.
- Test de integración           Se centra en la comuniacción de 2 COMPONENTES del sistema.
- Test de sistema (end2end)     Se centra en el COMPORTAMIENTO del sistema en su conjunto
- Test de aceptación            Se centran en si mi producto es el adecuado para mi cliente

> Soy un fabricante de bicicletas: BTWIN

- Fabrico yo el sillín?         Lo habitual es que no. Lo subcontrato.. lo compro
- Y el sistema de frenos?       Tampoco
- Ruedas?                       No
- Pedales?                      No

Cuál es mi trabajo? Diseño la bicicleta, especifico los componentes y los integro.

Me llega el sillín, qué hago? Posiblemente debo probarlos...
Los monto en una bici para hacer pruebas? Qué bici? Solo tengo sillín!

Cojo el sillín y lo monto en un bastidor (4 hierros mal soldaos.. pero firmes.. en los que confío). Y entonces:
    - UNITARIA DE CARGA:        Siento a una persona de 150 Kgs a ver si aguanta.
    - UNITARIA DE SEGURIDAD:    Lo inclino 45 grados a ver si no me resbalo
    - UNITARIA DE UX:           Me siento 4 horas a ver si no me echa chispas el culo!
    - UNITARIA DE ESTRES:       Me subo y bajo 5k veces.. a ver si el cuero aguanta.. o lo simulo.. frotando una lija un buen rato.

    Pregunta... esto garantiza que la bici está lista? Que bici? Solo tengo el sillín! o ruedas.. 20 componentes separados.
    Para qué hago estas pruebas? CONFIANZA +1  
                                 Vamos bien!

Cojo el sistema de frenos y lo monto en un bastidor... 
    Y hago una prueba: Aprieto la palanca de frenos ( sistemaDeFrenos.apretarPalanca(); ) y miro a ver si:
        - Si la pinza de freno cierra.
        - Si lo hace con fuerza suficiente (instalando un sensor de presión entre las pinzas de freno)

El bastidor o el sensor en el mundo del software los llamamos TEST DOUBLES (dobles de prueba). Hay muchos tipos:
- Mocks
- Dummies
- Spies
- Stubs
- Fakes


Me llega rueda y sistema de frenos. A cada uno les he hecho sus pruebas unitarias... voy a hacer las de integración, ya que hay una dependencia entre estos componentes.

Cojo el sistema de frenos y lo monto en un bastidor... y en este caso, entre las zapatas (pinza de freno) coloco la rueda.
    Le pego un viaje a la rueda... que gire... y entonces:  
    Y hago una prueba: Aprieto la palanca de frenos ( sistemaDeFrenos.apretarPalanca(); ) y miro a ver si:
        - Si rueda se para.
  
   Mira por donde... la rueda no se para!
   Las pinzas cierran, pero no lo suficiente como para apretar la rueda... 
   No COMUNICAN fuerza de rozamiento suficiente para parar la rueda.
        La rueda es mala/defectuosa? NO
        El sistema de frenos es defectuoso? NO
        Lo que tengo es un problema de integración entre ambos componentes.
        O la rueda es demadiado estrecha para el sistema de frenos, o el sistema de frenos no cierra lo suficiente para esa rueda.


    Pregunta... esto garantiza que la bici está lista? Que bici? Solo tengolas ruedas y el sistema de frenos! o el sillín.. 20 componentes separados.. que empiezo a integrar.
    Para qué hago estas pruebas? CONFIANZA +1  
                                 Vamos bien!

Entonces, cuando hago todas estas pruebas, monto la bici! Y hago pruebas de sistema

Por ejemplo... subo a un tio, lanzo la bici a 50kms hora... y que frene en menos de 10 seguendos

    Tengo una bicicleta, tengo en ella el sistema de frenos, las rueda, un tio... la bici andando  y ejecuto la función:
        sistemaDeFrenos.apretarPalanca();

Pregunta. Tengo estas pruebas hechas de sistema... Ya tengo bici lista para entregar? SI
Ya está!

Ahora... para qué entonces sirven las pruebas de aceptación?
A mi cliente le gustará? 

    Y mira que no... porque mi cliente lo que quiere es una bicicleta de montaña... y yo he hecho una de carreras!
    Está mal mi bici? Es defectuosa? NO... es cojonuda
    Simplemente no es la bicicleta que mi cliente quería... no es el producto adecuado para mi cliente.


## Microservicio con Springboot tradicional (sin programación reactiva)

Microservicio CRUD animalitos:

Modelo: Animal
Entidad: Animal

AnimalitosRepository                Lógica de persistencia
AnimalitosService                   Lógica de negocio
AnimalitosHttpRestControllerV1      Lógica de exposición del servicio



                AnimalitosHttpRestControllerV1 -> AnimalitosService -> AnimalitosRepository -> Base de datos

Capa de persistencia            -> animalitos-persistence-api.jar
                                        AnimalitosRepository
                                        Animal
Capa de persistencia            -> animalitos-persistence-impl.jar
                                        AnimalitosRepositoryJPAImpl
                                        AnimalEntity
Capa de negocio                 -> animalitos-service-api.jar
                                        AnimalitosService
Capa de negocio                 -> animalitos-service-impl.jar
                                        AnimalitosServiceImpl
Capa de exposición http/rest    -> animalitos-http-rest-controller-api.jar
                                        AnimalitosHttpRestControllerV1
Capa de exposición http/rest    -> animalitos-http-rest-controller-impl.jar
                                        AnimalitosHttpRestControllerV1Impl
Aplicación                      -> aplicacion-fermin
                                        AplicacionDeFermin

                AnimalitosHttpRestControllerV1Impl
                            ^
                AnimalitosHttpRestControllerV1Impl -> AnimalitosService
                            ^                               ^
                            ^                         AnimalitosServiceImpl  -> AnimalitosRepository
                            ^                                 ^                            ^
                            ^                                 |                  AnimalitosRepositoryJPAImpl  -> Base de datos
                            ^                                 |                            ^               JPA   BBDD Relacional
                Aplicación de Fermín--------------------------+----------------------------+------------------>     Oracle





interface DatosModificablesDeAnimal {
    String getNombre();
    int getEdad();
}

interface DatosDeCreacionDeAnimal extends DatosModificablesDeAnimal {
    String getEspecie();
}

interface Animal extends DatosDeCreacionDeAnimal {
    String getId();
    ZonedDateTime getFechaDeAlta();
}

interface AnimalitosRepository {
    Optional<Animal>        eliminar(String id);
    Animal                  crear(DatosDeCreacionDeAnimal animal)                   throws AnimalYaExisteException;
                                                                                    throws DatosInvalidosException;
                                 -> A la BBDD
    Animal                  guardar(String id, DatosModificablesDeAnimal animal)    throws AnimalNoExisteException;
                                                                                    throws DatosInvalidosException;
    Optional<Animal>        recuperarAnimal(String id);
    List<Animal>            recuperarTodosLosAnimales();
}


class DatosModificablesDeAnimalDTO {
    String getNombre();
    int getEdad();
}

class DatosDeCreacionDeAnimalDTO extends DatosModificablesDeAnimalDTO {
    String getEspecie();
}

class AnimalDTO extends DatosDeCreacionDeAnimalDTO {
    String getId();
    ZonedDateTime getFechaDeAlta();
}

interface AnimalitosService {
    Optional<AnimalDTO>     eliminar(String id);
    Animal                  crear(DatosDeCreacionDeAnimalDTO animal)                throws AnimalYaExisteException;
                                                                                    throws DatosInvalidosException;
                                 -> Al repositorio
                                 -> Mando email a mis clientes
                                 -> Notifico a un kafka que el nuevo animalito esta disponible, para que en las apps desktop de mis empleados aparezca un mensaje...
    Animal                  guardar(String id, DatosModificablesDeAnimalDTO animal) throws AnimalNoExisteException;
                                                                                    throws DatosInvalidosException;
    Optional<AnimalDTO>     recuperarAnimal(String id);
    List<AnimalDTO>         recuperarTodosLosAnimales();
}


Esto ya NO se caga en el principio SOLID de la Segregación de Interfaces.

I = Interface segregation principle
Mejor muchas interfaces específicas, que una única interfaz genérica.
No uses interfaces con métodos que no vas a usar, porque eso despiesta a quien lo usa.

JPA es una especificación parte del estandar J2EE (JakartaEE)

JEE es una colección de especificaciones para el desarrollo de aplicaciones empresariales en JAVA. 
- JPA es una de esas especificaciones, que define un conjunto de reglas y anotaciones para mapear objetos JAVA a tablas en una base de datos relacional, y viceversa.
- JMS
- EJB
- Servlets






















---

## Intro a la programación reactiva




---

# Metodologías ágiles.

Extraído del manifiesto ágil:

> El software funcionando es la medida principal de progreso. > ESTO DEFINE UN INDICADOR PARA UN CUADRO DE MANDO

la medida principal de progreso es El software funcionando

La forma en la que vamos a medir qué tal va el proyecto es mediante el concepto "software funcionando"

"Software funcionando": Que hace lo que tiene que hacer, que funciona adecuadamente.

Quién dice eso? 
- ~~El cliente~~ Es ridículo. Al cliente le tiene que llegar un producto que funcione.
- Las pruebas!
  Las pruebas garantizan que el coche es GUAY! que funciona! que no tiene defecto!
  Otra cosa es si mi coche es adecuado para mi cliente... 
  Mi cliente ayuda en la definición de los requisitos, y luego verá si el producto encaja con lo que esperaba.








---

## Setters y Getters en JAVA

Una de la muchas grandes CAGADAS que tiene el lenguaje JAVA en su SINTAXIS.
Los getters y los setters son una mierda de cojones! que solo existen en JAVA. No porque sea JAVA GUAY! 
Sino porque JAVA es una mierda como lenguaje!

Para que sirven?

```java
// DÍA 1
public class Persona{

    public String nombre;
    public int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public Persona(){
        this("Anónimo", 0);
    }

}

// DEL DIA 2-100, En algn sitio de mi código:

Persona p1 = new Persona();
System.out.println(p1.nombre);
p1.edad = 33;
System.out.println(p1.edad);

// DIA 101: Anda.. voy a hacer que la edad no pueda ser negativa

public class Persona{

    public String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.nombre = nombre;
        this.setEdad(edad);
    }

    public Persona(){
        this("Anónimo", 0);
    }

    public void setEdad(int edad) {
        if (edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.edad = edad;
    }

    public int getEdad() {
        return edad;
    }

}
// DIA 102: 1k tios kalasnikov en mano búscandome porque su código no compila!

```
Si hubiera escrito este código en un proyecto, estoy fuera! Ese código sigue una muy mala práctica. La buena práctica en JAVA es que todas mis variables sean privadas y en las que me interese meter getters y setters.

```java

public class Persona{

    private String nombre;
    private int edad;

    public Persona(String nombre, int edad) {
        this.setNombre(nombre);
        this.setEdad(edad);
    }

    public Persona(){
        this("Anónimo", 0);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

}

// En algn sitio de mi código:

Persona p1 = new Persona();
System.out.println(p1.getNombre());
p1.setEdad(33);
System.out.println(p1.getEdad());

```

Siempre hay que encapsular las variables... y necesito setters y getters para ello.
Esto es verdad. La mierda es que en JAVA solo hay esta forma de conseguir la encapsulación.

Por si acaso algún día necesitas control sobre tus variables, crea el día 1 getters y setters para todo, y así si los necesiita en el futuro tienes donde meter código de control para tus variables... y no jodes el código de quien esté usando tu clase.

La política de usar setters y getters desde el día 0 es solo para FACILITAR LA EVOLUCIÓN DE MI CÓDIGO, no porque sea una buena práctica en sí misma.

En cualquier otro lenguaje existe el concepto de properties: 
Existe en .net C#
Existe en python
Existe en JS, TS

Que me permite definir funciones en el futuro que sean invocadas mediante la sintaxis de asignación tradicional.

```python
class Persona:

    def __init__(self, nombre="Anónimo", edad=0):
        self.nombre = nombre
        self.edad = edad

# Día 1:

p1 = Persona()
print(p1.nombre)
p1.edad = 33
print(p1.edad)

# Día 101: Quiero controlar que la edad no sea negativa, pero sin cambiar la sintaxis de uso de mi clase.

class Persona:

    def __init__(self, nombre="Anónimo", edad=0):
        self.nombre = nombre
        self._edad = edad

    @property
    def edad(self):
        return self._edad

    @edad.setter
    def edad(self, value):
        if value < 0:
            raise ValueError("La edad no puede ser negativa")
        self._edad = value
# Día 102:

p1 = Persona()
print(p1.nombre)
p1.edad = 33 # Y en realidad, por detras de ese = lo que se está invocando es a la función set_edad(33)
print(p1.edad) # Y en realidad por detras de ese print se está invocando a la función get_edad()
```








Estos son lenguajes que ofrecen una sintaxis alternativa a la de JAVA para producior BYTE CODE
Kotlin
Scala


    .java -> javac -> .class <- JVM (interpreta en tiempo de ejecución el byte-code)
                        ^^^^
                        byte-code
    .kt -> kotlinc -> .class
    .scala -> scalac -> .class


Kotlin (creado por JetBrains a petición de Google). Todo SPRING Se ha migrado a kotlin.