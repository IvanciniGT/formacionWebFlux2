package curso.animales.service.api;

import java.util.List;
import java.util.Optional;

import main.java.com.curso.animales.service.api.models.AnimalDTO;
import main.java.com.curso.animales.service.api.models.DatosCreacionDeAnimalDTO;
import main.java.com.curso.animales.service.api.models.DatosModificablesDeAnimalDTO;

public interface AnimalitosService {
    
    Optional<AnimalDTO> getAnimal(String id);
    List<AnimalDTO> getAllAnimales();
    AnimalDTO createAnimal(DatosCreacionDeAnimalDTO animal);
    AnimalDTO updateAnimal(String id, DatosModificablesDeAnimalDTO animal);
    Optional<AnimalDTO> deleteAnimal(String id);

}
