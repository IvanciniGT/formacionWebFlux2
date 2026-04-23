# Detalle de diagramas

## 1) Modelos separados (solo modelos)

```mermaid
classDiagram
    namespace com.curso.animales.controller.model {
        class AnimalRestDTO
        class CrearAnimalRequestDTO
        class ModificarAnimalRequestDTO
        class ErrorResponseDTO
    }

    namespace com.curso.animales.service.model {
        class AnimalDTO
        class CrearAnimalDTO
        class ModificarAnimalDTO
    }

    namespace com.curso.animales.persistence.model {
        class AnimalEntity
    }

    CrearAnimalRequestDTO --> CrearAnimalDTO : mapper
    ModificarAnimalRequestDTO --> ModificarAnimalDTO : mapper
    AnimalDTO --> AnimalRestDTO : mapper

    CrearAnimalDTO --> AnimalEntity : mapper
    ModificarAnimalDTO --> AnimalEntity : mapper
    AnimalEntity --> AnimalDTO : mapper
```

## 2) Logica + excepciones (un grafico por paquete)

### controller

```mermaid
classDiagram
    class AnimalControllerV1
    class AnimalControllerV1Impl
    class AnimalControllerExceptionHandler
    class ControllerValidationException

    AnimalControllerV1 <|.. AnimalControllerV1Impl
    AnimalControllerV1Impl --> AnimalControllerExceptionHandler
    AnimalControllerV1Impl ..> ControllerValidationException
```

### service

```mermaid
classDiagram
    class AnimalService
    class AnimalServiceImpl
    class AnimalServiceMapper
    class AnimalYaExisteException
    class AnimalNoEncontradoException
    class DatosInvalidosException

    AnimalService <|.. AnimalServiceImpl
    AnimalServiceImpl --> AnimalServiceMapper
    AnimalServiceImpl ..> AnimalYaExisteException
    AnimalServiceImpl ..> AnimalNoEncontradoException
    AnimalServiceImpl ..> DatosInvalidosException
```

### persistence

```mermaid
classDiagram
    class AnimalRepository
    class AnimalRepositoryJpaImpl
    class AnimalPersistenceMapper
    class RepositorioException

    AnimalRepository <|.. AnimalRepositoryJpaImpl
    AnimalRepositoryJpaImpl --> AnimalPersistenceMapper
    AnimalRepositoryJpaImpl ..> RepositorioException
```

## 3) Logica + modelos (un grafico por paquete)

### controller

```mermaid
classDiagram
    class AnimalControllerV1
    class AnimalControllerV1Impl
    class AnimalControllerMapper
    class AnimalRestDTO
    class CrearAnimalRequestDTO
    class ModificarAnimalRequestDTO

    AnimalControllerV1 <|.. AnimalControllerV1Impl
    AnimalControllerV1Impl --> AnimalControllerMapper
    AnimalControllerV1Impl --> CrearAnimalRequestDTO
    AnimalControllerV1Impl --> ModificarAnimalRequestDTO
    AnimalControllerV1Impl --> AnimalRestDTO
```

### service

```mermaid
classDiagram
    class AnimalService
    class AnimalServiceImpl
    class AnimalServiceMapper
    class AnimalDTO
    class CrearAnimalDTO
    class ModificarAnimalDTO

    AnimalService <|.. AnimalServiceImpl
    AnimalServiceImpl --> AnimalServiceMapper
    AnimalServiceImpl --> CrearAnimalDTO
    AnimalServiceImpl --> ModificarAnimalDTO
    AnimalServiceImpl --> AnimalDTO
```

### persistence

```mermaid
classDiagram
    class AnimalRepository
    class AnimalRepositoryJpaImpl
    class AnimalPersistenceMapper
    class AnimalEntity

    AnimalRepository <|.. AnimalRepositoryJpaImpl
    AnimalRepositoryJpaImpl --> AnimalPersistenceMapper
    AnimalRepositoryJpaImpl --> AnimalEntity
```
