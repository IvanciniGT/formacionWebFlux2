package com.curso.reactor;

import java.util.function.Consumer;

import reactor.core.publisher.Mono;

/*
Inicio del programa
Llamo a la función que me devuelve un Mono...
Tiempo que tardo en obtener el Mono: 31 milisegundos   // El Mono no tarda nada en ejecutarse!
Hago otras cosas mientras tanto que tardan un rato...
Hago otras cosas más...
Tiempo que tardo en llegar a la parte de obtener el valor del Mono: 2036 milisegundos
Tiempo total que tardo en obtener el valor del Mono: 7040 milisegundos
Valor del Mono: 6225153523681
*/

import org.springframework.web.reactive.function.client.WebClient;

public class Demo4DameMuchoBacon {
    

    public static void main(String[] args) {
        Mono<String> resultadoDeLaBusqueda = resultadoDeLaBusqueda(); // Esto es una llamada a un servicio que me devuelve un Mono... y ese Mono se va a completar cuando tenga el resultado de la búsqueda... que puede ser dentro de 1 segundo, 5 segundos, etc... y mientras tanto, el hilo puede hacer otras cosas...
        resultadoDeLaBusqueda.subscribe( resultado -> System.out.println("Resultado de la búsqueda: " + resultado) ); // Cuando el Mono se complete, que se ejecute esta función... usando el
    }

    private static Mono<String> resultadoDeLaBusqueda() {
        Mono<String> resultadoMicroServicio1 = dameBaconNoBloqueante(); // Una llamada a un microservicio
        Mono<String> resultadoMicroServicio2 = dameBaconNoBloqueante(); // Otra llamada a un microservicio

        // Combinamos los resultados de ambos microservicios
        Mono<String> resultadoCombinado = resultadoMicroServicio1.zipWith(resultadoMicroServicio2, (res1, res2) -> res1 + " " + res2);
        return resultadoCombinado;
    }

    private static Mono<String> dameBaconNoBloqueante(){
        // Me creo un cliente WEB con el API de Reactive Streams...
        WebClient cliente = WebClient.create();
        long tin = System.nanoTime();
        Mono<String> peticion = cliente.get()
                                .uri("https://baconipsum.com/api/?type=all-meat&paras=2&start-with-lorem=1")
                                .retrieve()
                                .bodyToMono(String.class);
        return peticion;
    }
}
