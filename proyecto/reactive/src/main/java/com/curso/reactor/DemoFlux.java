package com.curso.reactor;


import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;

public class DemoFlux {
    
    public static void main(String[] args) {
        Flux<Integer> numeros = dameMumeros(); 
        System.out.println("Tengo un Flux con futuros números... que aún no se han generado...");
        numeros.subscribe( DemoFlux::procesarSiguiente );

        List<Integer> numerosDeUna = dameMumerosDeUna(); 
        System.out.println("Tengo una lista con: " + numerosDeUna.size() + " números");
        numerosDeUna.forEach( DemoFlux::procesarSiguiente );

    }

    private static void procesarSiguiente(Integer numero) {
        tardoMuchoEnHacerAlgo(2000);
        System.out.println("Número recibido: " + numero);
    }


    private static List<Integer> dameMumerosDeUna() {
        return IntStream.range(1, 10)
                   .map( numero -> {
                                        System.out.println("Generando el doble de número: " + numero);
                                        // el ritmo de generación lo controla el productor... el productor va a generar números a su ritmo... y el consumidor va a ir procesando esos números a su ritmo... y mientras tanto, el hilo puede hacer otras cosas...
                                        return numero * 2;  
                                    } )
                   .boxed() // Esto convierte el IntStream en un Stream<Integer>
                   .toList();
    }

    private static Flux<Integer> dameMumeros() {
        return Flux.range(1, 10)
                   .map( numero -> {
                                        System.out.println("Generando el doble de número: " + numero);
                                        // el ritmo de generación lo controla el consumidor... el consumidor va a ir pidiendo números... y el productor va a ir generando números a ese ritmo... y mientras tanto, el hilo puede hacer otras cosas...
                                        return numero * 2;  
                                    } );
    }

    private static void tardoMuchoEnHacerAlgo(int milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
