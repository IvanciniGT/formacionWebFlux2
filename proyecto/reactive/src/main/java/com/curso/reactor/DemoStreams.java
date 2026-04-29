package com.curso.reactor;

import java.util.stream.*;

public class DemoStreams {
    public static void main(String[] args) {
        
        LongStream stream = LongStream.range(0, 1000*1000*1000*1000*1000*1000*1000*1000*1000*1000*1000*1000);

        long tin = System.nanoTime();
        // Cuanto tiempo va a tardar este trozo de código?
        LongStream result = stream.map(   numero -> numero * 2        )  // Esto se ejecuta lazy
                                 .filter( numero -> numero % 300 == 0 ); // Esto se ejecuta lazy
                                 // Esto no ha hecho sino apuntar que esas operaciones se tienen que hacer...
        /////
        long tout = System.nanoTime();
        long tiempo = (tout - tin)/1_000_000; // Convertimos a milisegundos
        System.out.println("Tiempo que tardo en obtener el Stream: " + tiempo + " milisegundos");


        long tin2 = System.nanoTime();
        System.out.println ("Tenemos un total de " + result.count() + " números que cumplen la condición");
            // Count es quien dispara la ejecución de todo el Stream... cuando necesito los datos.
        long tout2 = System.nanoTime();
        long tiempo2 = (tout2 - tin2)/1_000_000; //
        System.out.println("Tiempo que tardo en obtener el resultado del Stream: " + tiempo2 + " milisegundos");
    }
}
