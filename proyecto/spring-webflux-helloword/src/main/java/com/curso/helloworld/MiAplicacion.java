package com.curso.helloworld;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Qué dice esa Anotación?
// Lo que dices es:
// Busca todos los componentes (@Component) en este paquete (com.curso.helloworld) y en los subpaquetes
// y regístralos como beans en el contexto de Spring.
public class MiAplicacion {

    public static void main(String[] args) {
        SpringApplication.run(MiAplicacion.class, args);
    }
    
}
