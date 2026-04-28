package com.curso.animalitos.app;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Punto de entrada de la aplicacion. Aqui aplicamos la inversion de control
 * delegando el flujo a Spring (dia2).
 *
 * La definicion global de OpenAPI vive aqui (titulo, version, contacto,
 * servidores...). Los detalles por endpoint estan anotados en la interfaz
 * {@code AnimalitosControllerV1} del modulo controller-api.
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.curso.animalitos")
@EntityScan(basePackages = "com.curso.animalitos.repository.impl.entities")
@EnableJpaRepositories(basePackages = "com.curso.animalitos.repository.impl")
@OpenAPIDefinition(
        info = @Info(
                title = "Animalitos API",
                version = "1.0.0",
                description = "API CRUD para la gestion de animalitos. Contrato publico documentado con OpenAPI 3.",
                contact = @Contact(name = "Equipo Animalitos", email = "animalitos@curso.com"),
                license = @License(name = "Apache 2.0", url = "https://www.apache.org/licenses/LICENSE-2.0")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "Local")
        }
)
public class AnimalitosApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnimalitosApplication.class, args); // Invcersión de control
    }
    
}
