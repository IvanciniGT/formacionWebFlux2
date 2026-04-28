package com.curso.helloworld;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import static java.net.URI.create;

@RestController
@RequestMapping("/api/v1")
public class MiControladorRest {
    
    @RequestMapping("/hola")
    public String decirHola() { // Esta es una función SINCRONA!
        return "Hola Mundo!"; // Aqui se da el código http por defecto: 200 OK
    }

    @RequestMapping("/hola2")
    public ResponseEntity<String> decirHola2() { // Esta es una función SINCRONA!
        //return new ResponseEntity<>("Hola Mundo!", HttpStatus.OK); // Aqui se da el código http por defecto: 200 OK
        return ResponseEntity.ok("Hola Mundo!"); // Aqui se da el código http por defecto: 200 OK
    }

    @RequestMapping("/dameBacon")
    public ResponseEntity<String> dameBaconEndPoint() { 
        long tin = System.nanoTime();
        String bacon =  dameBacon();  // Mi hilo se bloquea 1 segundo.. esperando bacon
        bacon = bacon.toUpperCase();  // síncrono
        long tout = System.nanoTime();
        System.out.println("Tiempo total de respuesta del endpoint (con el hilo parao): " + (tout - tin)/1_000_000 + " ms");
        return ResponseEntity.ok(bacon);
    }

    @RequestMapping("/dameBaconAsincrono")
    public Mono<String> dameBaconAsincronoEndPoint() { 
        long tin = System.nanoTime();
        Mono<String> bacon = dameBaconNoBloqueante(); // Mi hilo no espera.. le devuelve a Netty un Ticket...
                                                      // Cuando el bacon esté, el netty que use el ticket para canjearlo y devolverlo.
        bacon = bacon.map( bacon2 -> bacon2.toUpperCase() ); // Sincrono. Lo dejo anotado en el momento sobre el ticket.. De antemano!
                                     ////////////////////
                                     ///   Asíncrona.. cuando llegue el bacon
        long tout = System.nanoTime();
        System.out.println("Tiempo total de respuesta del endpoint asincrono (con el hilo parao): " + (tout - tin)/1_000_000 + " ms");
        return bacon;
    }
/*
    @RequestMapping("/dameBaconAsincronoMIERDA")
    public String dameBaconAsincronoMIERDAEndPoint() { 
        long tin = System.nanoTime();
        Mono<String> bacon = dameBaconNoBloqueante(); // Mi hilo no espera.. le devuelve a Netty un Ticket...
                                                      // Cuando el bacon esté, el netty que use el ticket para canjearlo y devolverlo.
        String baconEnMayusculas = bacon.block().toUpperCase(); // Sincrono. Lo dejo anotado en el momento sobre el ticket.. De antemano!
                                         //////////
                                         /// WAIT!
        long tout = System.nanoTime();
        System.out.println("Tiempo total de respuesta del endpoint asincrono (con el hilo parao): " + (tout - tin)/1_000_000 + " ms");
        return baconEnMayusculas;
    }
*/
    private String dameBacon(){
        // Me creo un cliente WEB con el API básico de JAVA...
        // Y pido bacon: https://baconipsum.com/api/?type=all-meat&paras=2&start-with-lorem=1

        HttpClient cliente = HttpClient.newBuilder().build();
        HttpRequest peticion = HttpRequest.newBuilder()
                                .uri(create("https://baconipsum.com/api/?type=all-meat&paras=2&start-with-lorem=1"))
                                .GET()
                                .build();
        try {
            // Petición síncrona, bloqueante!
            HttpResponse<String> respuesta = cliente.send(peticion, HttpResponse.BodyHandlers.ofString());
            return respuesta.body();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error al pedir bacon: " + e.getMessage();   
        }
    }
    private Mono<String> dameBaconNoBloqueante(){
        // Me creo un cliente WEB con el API de Reactive Streams...
        WebClient cliente = WebClient.create();
        long tin = System.nanoTime();
        Mono<String> peticion = cliente.get()
                                .uri("https://baconipsum.com/api/?type=all-meat&paras=2&start-with-lorem=1")
                                .retrieve()
                                .bodyToMono(String.class)
                                .doOnNext( 
                                    (bacon) -> {
                                        long tout = System.nanoTime();
                                        System.out.println("Bacon recibido. Tiempo total que ha tardado el bacon: " + (tout - tin)/1_000_000 + " ms");
                                    }
                                );
        return peticion;
    }

}
