# SpringWeb - Blueprint inicial

En proyecto/animalitos/mvn compile
   proyecto/animalitos/animalitos-app/mvn spring-boot:run


Este directorio contiene la primera propuesta de arquitectura para la app de animales.

## Documentos de arquitectura

- `01-paquetes.md`: diagrama de paquetes base.
- `02-clases-por-paquete.md`: inventario de clases por paquete sin detalle.
- `03-detalle-diagramas.md`: detalle por separado (modelos, logica+excepciones, logica+modelos).

## Estructura Maven multimodulo (para revision)

```text
proyecto/springweb/
├── pom.xml                                   (padre heredable)
|           BOM   Bill-of-Materials (versiones comunes)
|                 Spring Boot aporta un BOM con versiones compatibles
├── README.md
├── 01-paquetes.md
├── 02-clases-por-paquete.md
├── 03-detalle-diagramas.md
├── common/
│   ├── pom.xml
│   └── src/main/java/com/curso/animales/common/
│       ├── dto/
│       │   └── ValidationErrorDTO.java
│       └── validation/
│           ├── DniValido.java
│           └── FechaPasada.java
├── repository/
│   ├── pom.xml                               (padre de repository)
│   ├── api/
│   │   ├── pom.xml
│   │   └── src/main/java/com/curso/animales/repository/api/
│   │       ├── AnimalRepository.java         (interface de logica)
│   │       ├── models/
│   │       │   ├── AnimalDTO.java
│   │       │   ├── CrearAnimalDTO.java
│   │       │   └── ModificarAnimalDTO.java
│   │       └── exceptions/
│   │           └── RepositorioException.java
│   └── impl/
│       ├── pom.xml
│       └── src/main/java/com/curso/animales/repository/impl/
│           ├── AnimalRepositoryJpaImpl.java  (implementacion de logica)
│           ├── entities/
│           │   └── AnimalEntity.java
│           └── mappers/
│               └── AnimalRepositoryMapper.java
├── service/
│   ├── pom.xml                               (padre de service)
│   ├── api/
│   │   ├── pom.xml
│   │   └── src/main/java/com/curso/animales/service/api/
│   │       ├── AnimalService.java            (interface de logica)
│   │       ├── models/
│   │       │   ├── AnimalDTO.java
│   │       │   ├── CrearAnimalDTO.java
│   │       │   └── ModificarAnimalDTO.java
│   │       └── exceptions/
│   │           ├── AnimalYaExisteException.java
│   │           ├── AnimalNoEncontradoException.java
│   │           └── DatosInvalidosException.java
│   └── impl/
│       ├── pom.xml
│       └── src/main/java/com/curso/animales/service/impl/
│           ├── AnimalServiceImpl.java        (implementacion de logica)
│           └── mappers/
│               └── AnimalServiceMapper.java
├── controller/
│   ├── pom.xml                               (padre de controller)
│   ├── api/
│   │   ├── pom.xml
│   │   └── src/main/java/com/curso/animales/controller/api/
│   │       ├── AnimalControllerV1.java       (interface de logica)
│   │       └── models/
│   │           ├── AnimalResponseDTO.java
│   │           ├── CrearAnimalRequestDTO.java
│   │           └── ModificarAnimalRequestDTO.java
│   └── impl/
│       ├── pom.xml
│       └── src/main/java/com/curso/animales/controller/impl/
│           ├── AnimalControllerRestV1Impl.java
│           ├── mappers/
│           │   └── AnimalControllerMapper.java
│           └── advice/
│               └── AnimalControllerExceptionHandler.java
└── app/
    ├── pom.xml
    └── src/main/java/com/curso/animales/app/
        └── AnimalesApplication.java
```

## Modulos del padre

- common
- repository
- service
- controller
- app

### Submodulos por carpeta padre

- repository: api, impl
- service: api, impl
- controller: api, impl

## Relacion entre modulos (alto nivel)

- common no depende de modulos de negocio.
- repository-impl depende de repository-api y common.
- service-api depende de repository-api y common.
- service-impl depende de service-api, repository-api y common.
- controller-api depende de service-api y common.
- controller-impl depende de controller-api, service-api y common.
- app depende de los modulos impl para ensamblar la aplicacion.

## Convenciones iniciales

- Paquete base: `com.curso.animales`.
- En `api`: interfaces de logica en raiz y carpeta `models` (+ `exceptions` donde aplique).
- En `impl`: implementaciones de logica en raiz y carpeta `mappers`.
- Paquetes adicionales: `entities` (repository/impl), `advice` (controller/impl).
- En `common`: `validation` para anotaciones/validadores y `dto` compartidos.
