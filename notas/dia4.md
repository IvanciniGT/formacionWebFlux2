
# Módulos

      Capa de dominio - animales-api.jar                                     Conceptos generales, que existen más allá del proyecto.
                                                                             Más allá de mi lógica de negocio
        interface Animal
      Capa Persistencia - animales-persistence-api.jar
        interface AnimalesRepository (CRUD)
      Capa de infraestructura - animales-persistence-jpa-impl.jar
        class AnimalesJPARepository implements AnimalesRepository
      Capa de servicio - animales-service-api.jar
        interface AnimalesService
        interface / class AnimalDTO
        class DatosModificablesAnimalDTO
        class DatosCreacionAnimalDTO
      Implementación de servicio - animales-service-impl.jar
        class AnimalesServiceImpl implements AnimalesService



maven sonar:sonar
maven jacoco:report
maven spring-boot:run
maven spring-boot:image


Una cosa son los plugins... que aportan tareas
Otra cosa es el ciclo de vida de una app maven, que tiene goals.

  GOALS           -> Plugins con tareas
  clean
  compile            generate-sources, compile
  test-compile       generate-sources, compile, ...
  test               surefire
  package            jar, ear, war
  install



```java
  public interface AnimalitosService {
      
      Optional<AnimalDTO> getAnimal(String id);
      List<AnimalDTO> getAllAnimales();
      AnimalDTO createAnimal(DatosCreacionDeAnimalDTO animal);
      AnimalDTO updateAnimal(String id, DatosModificablesDeAnimalDTO animal);
      Optional<AnimalDTO> deleteAnimal(String id);

  }
```


# Pruebas de caja blanca vs negra

Pruebas de caja negra: Cuando no conozco o intencionalmente paso de tener en cuenta la implementación de la clase que estoy probando. Solo me importa su interfaz, Comportamiento externo esperado

      Optional<AnimalDTO> getAnimal(String id);

```java
    // EJECUTO LA MISMA PRUEBAS con 2 datos:
        // LUCAS, nombre: "Lucas", especie: "Gato", edad: 3 años
        // FIRULAIS, nombre: "Firulais", especie: "Perro", edad: 5 años

     // Dado:       Que tengo un ID = "LUCAS"
                    String id = "LUCAS";
     //             Y que tengo un repo de mentirijilla (CARTON PIEDRA)
                    AnimalitosRepositoryMentirijilla repo = new AnimalitosRepositoryMentirijilla();
     //             Y que en el repositorio hay un animal con ese ID
     //             cuyos datos son "Lucas", "Gato", 3 años
                    Animal animal = new Animal(id, ALEATORIO, ALEATORIO2, ALEATORIO3);
                    repo.setAnimalADevolver(animal);
                  

      //Cuando:     Llamamos al método getAnimal con ese ID
                    AnimalitosServiceImpl service = new AnimalitosServiceImpl(repo);
                    Optional<AnimalDTO> resultado = service.getAnimal(id);
      //Entonces:   Esperamos un AnimalDTO no nulo, con nombre "Lucas", especie "Gato", edad 3 años e ID "LUCAS"
                    Assertions.notNull(resultado);
                    Assertions.isTrue(resultado.isPresent());
                    AnimalDTO animalDTO = resultado.get();
                    Assertions.equals(animal.getId(), animalDTO.getId());
                    Assertions.equals(animal.getNombre(), animalDTO.getNombre());
                    Assertions.equals(animal.getEspecie(), animalDTO.getEspecie());
                    Assertions.equals(animal.getEdad(), animalDTO.getEdad());
                    Assertions.assertTrue(repo.teHanLlamado());
                    Assertions.equals(id, repo.getIdConElQueTeHanLlamado());

  public class AnimalitosRepositoryMentirijilla implements AnimalesRepository {   // MOCK de un repositorio
    private Animal animal= null;
    private String idConElQueTeHanLlamado = null;
    public AnimalitosRepositoryMentirijilla() {}
    public void setAnimalADevolver(Animal animal) {
        this.animal = animal;
    }
    boolean teHanLlamado() {
        return this.idConElQueTeHanLlamado != null;
    }
    String getIdConElQueTeHanLlamado() {
        return this.idConElQueTeHanLlamado;
    }
    @Override
    public Optional<Animal> findById(String id) {
        this.idConElQueTeHanLlamado = id;
        if(id==null || !id.equals(animal.getId())) {
            Assertions.fail("El ID no coincide con el animal que tengo configurado para devolver");
        }
         return Optional.of(animal);
    }
    
  }
```
STUB, una clase que devuelve un valor prefijado
En muchas ocasiones, eso por si solo no me vale.
Me gustaría saber que una función, realmente está llamado a la función del repositorio, y no se ha saltado esa parte de la lógica. Monto un MOCK, que es un STUB pero que además me permite hacer de SPY,verificar que se han llamado a ciertos métodos, con ciertos parámetros.

```java
public class AnimalitosServiceImpl implements AnimalitosService {
      
    private final AnimalesRepository repo;
    public AnimalitosServiceImpl(AnimalesRepository repo) {
        this.repo = repo;
    }

    public Optional<AnimalDTO> getAnimal(String id) {
      if(id.equalsIgnoreCase("LUCAS")) {
        return Optional.of(new AnimalDTO(id, "Lucas", "Gato", 3));
      }else if(id.equalsIgnoreCase("FIRULAIS")) {
        return Optional.of(new AnimalDTO(id, "Firulais", "Perro", 5));
      }
      return Optional.empty();
    }
    
  }
```
Si tengo 50 lineas de código... FACIL, pero FACIL, necesitará una 500 lineas de pruebas.

Pruebas de caja blanca: Cuando conozco la implementación de la clase que estoy probando, y quiero probar su comportamiento interno.


```java

public class AnimalitosServiceImpl implements AnimalitosService {
      
    private final AnimalesRepository repo;
    private static final ConcurrentHashMap<String, WeakReference<AnimalDTO>> cache = new ConcurrentHashMap<>();
    private AnimalitosServiceMapper mapper = new AnimalitosServiceMapper();
    public AnimalitosServiceImpl(AnimalesRepository repo) {
        this.repo = repo;
    }

    public Optional<AnimalDTO> getAnimal(String id) {
      // IMPLEMENTACION 1 repo.findById(id).map(animal -> mapper.toAnimalDTO(animal));
      if(!this.cache.containsKey(id) || this.cache.get(id).get() == null ) {
        Optional<Animal> animalOpt = repo.findById(id);
        if(animalOpt.isEmpty()) {
          this.cache.put(id, new WeakReference<>(null));
        }else{
          Animal animal = animalOpt.get();
          AnimalDTO animalDTO = mapper.toAnimalDTO(animal);
          this.cache.put(id, new WeakReference<>(animalDTO));
        }
      }
      WeakReference<AnimalDTO> animalDTORef = this.cache.get(id);
      if(animalDTORef.get() == null) {
        return Optional.empty();
      }
      return Optional.of(animalDTORef.get());
    }
}

```
En este caso, mi implementación debe:
1. Asegurar las pruebas a nivel de caja negra, es decir, que el resultado externo esperado se cumple.
2. Asegurar las de caja blanca.. y en mi caso ahora hay 2 caminos posibles, el caso en el que el animal no está en cache, y el caso en el que sí está en cache. 
   Con la prueba de caja blanca, pruebo el camino de que no está en cache.
   Pero esa prueba no está probando mi mecanismo de cache... 
   Y nadie que no conozca que mi implementación tiene cache, se va a preocupar por probar el camino de que el animal ya está en cache... y sin embargo, es un camino que existe, y que quiero asegurar que funciona correctamente.

Esta prueba iría a nivel de IMPL. Es de caja blanca.

```java
     // Dado:       Que tengo un ID = "LUCAS"
                    String id = "LUCAS";
     //             Y que tengo un repo de mentirijilla (CARTON PIEDRA)
                    AnimalitosRepositoryMentirijilla repo = new AnimalitosRepositoryMentirijilla();
     //             Y que en el repositorio hay un animal con ese ID
     //             cuyos datos son "Lucas", "Gato", 3 años
                    Animal animal = new Animal(id, ALEATORIO, ALEATORIO2, ALEATORIO3);
                    repo.setAnimalADevolver(animal);
                  

      //Cuando:     Llamamos al método getAnimal con ese ID
                    AnimalitosServiceImpl service = new AnimalitosServiceImpl(repo);
                    Optional<AnimalDTO> resultado = service.getAnimal(id);
                    Optional<AnimalDTO> resultado2 = service.getAnimal(id);
      //Entonces:   Esperamos un AnimalDTO no nulo, con nombre "Lucas", especie "Gato", edad 3 años e ID "LUCAS"
                    Assertions.equals(resultado, resultado2); // Si el resultado no es el mismo, es que no se ha cacheado
                    Assertions.notNull(resultado);
                    Assertions.isTrue(resultado.isPresent());
                    AnimalDTO animalDTO = resultado.get();
                    Assertions.equals(animal.getId(), animalDTO.getId());
                    Assertions.equals(animal.getNombre(), animalDTO.getNombre());
                    Assertions.equals(animal.getEspecie(), animalDTO.getEspecie());
                    Assertions.equals(animal.getEdad(), animalDTO.getEdad());
```
# Definición de pruebas:

Una prueba tiene 3 partes:
Contexto : GIVEN, DADO
Acción : WHEN, CUANDO
Resultado esperado : THEN, ENTONCES


---

Cuando hacemos pruebas, siempre definimos:
- Contexto: el estado del mundo antes de ejecutar la acción que queremos probar. Es el GIVEN o DADO.
- Acción: la acción que queremos probar, el WHEN o CUANDO.
- Resultado esperado: el resultado que esperamos obtener después de ejecutar la acción, el THEN o ENTONCES.

Elresultado tenemos varias formas de expresarlo:
- Aserciones: Assertions.equals(resultado, resultado2);        Lenguaje muy imperativo y de bajo nivel
                                                               Pruebas unitarias creadas por desarrolladores para desarrolladores. 
El lenguaje de assertions es duro. A rabiar.
Hay otras sintaxis que podemos usar:
Aserciones fluidas: 

  assertThat(resultado).isEqualTo(resultado2);   Lenguaje más fluido, más cercano al lenguaje natural, más fácil de leer y escribir.  Esto es más para pruebas de testers y QA.

  En java, tenemos varias librerías que nos permiten escribir aserciones fluidas, como AssertJ

  Hamcrest nos ofrece otra sintaxis de aserciones fluidas, pero es más verbosa que AssertJ... y más expresiva que las aserciones tradicionales.

  resultado.shouldBeEqualTo(resultado2);   Lenguaje aún más fluido, más cercano al lenguaje natural, más fácil de leer y escribir. Esto es más para pruebas de testers y QA.  Esto es lo que nos ofrece la librería Kluent en Kotlin.


En Desarrollo tenemos los principios SOLID
En testing tenemos los principios FIRST

F - Fast: Las pruebas deben ser rápidas de ejecutar, para que se ejecuten con frecuencia.
I - Independent: Las pruebas deben ser independientes entre sí, para que puedan ejecutarse en cualquier orden.
R - Repeatable: Las pruebas deben ser repetibles, para que puedan ejecutarse múltiples veces con los mismos resultados.
S - Self-Validating: Las pruebas deben ser auto-validantes, para que puedan determinar automáticamente si han pasado o fallado.
T - Timely: Las pruebas deben ser escritas a tiempo, para que puedan detectar errores lo antes posible.


-
--

JAVA 17 records
public record Persona(String nombre, int edad) {
    public Persona {
        if(edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
    }
}

Eso es lo mismo que si escribiera:

public class Persona {
    private final String nombre;
    private final int edad;

    public Persona(String nombre, int edad) {
        if(edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Persona persona = (Persona) o;
        return edad == persona.edad && Objects.equals(nombre, persona.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre, edad);
    }

    @Override
    public String toString() {
        return "Persona{" +
                "nombre='" + nombre + '\'' +
                ", edad=" + edad +
                '}';
    }
}

El problema es que llegaron tarde... ya llevabamos usando @ Value de lombok años:

@Value
public class Persona {
  
    String nombre;
    int edad;

    public Persona(String nombre, int edad) {
        if(edad < 0) {
            throw new IllegalArgumentException("La edad no puede ser negativa");
        }
        this.nombre = nombre;
        this.edad = edad;
    }
}