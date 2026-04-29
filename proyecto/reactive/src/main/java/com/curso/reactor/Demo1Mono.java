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
public class Demo1Mono {
    

    public static void main(String[] args) {
        System.out.println("Inicio del programa. Soy el hilo: " + Thread.currentThread().getName() );
        
        System.out.println("Llamo a la función que me devuelve un Mono...");
        
        long tin = System.nanoTime();

        Mono<String> mono = unaFuncionExternaQueMeDaUnMono(); // Lo normal que esto sea librería externa
        
        long tout = System.nanoTime();
        long tiempo = (tout - tin)/1000000; // Convertimos a milisegundos
        System.out.println("Tiempo que tardo en obtener el Mono: " + tiempo + " milisegundos");

        // Aquñi la gracia es que puedo hacer cosas mientras.
        System.out.println("Hago otras cosas mientras tanto que tardan un rato...");
        tardoMuchoEnHacerAlgo(2000);

        System.out.println("Hago otras cosas más...");

        // En un momento dado, necesito el valor del mono... Para hacer una última cosa.
        // Opciones: 1) Bloqueo el hilo... a la espera de que el mono me de el valor.
        tout = System.nanoTime();
        tiempo = (tout - tin)/1000000; // Convertimos a milisegundos
        System.out.println("Tiempo que tardo en llegar a la parte de obtener el valor del Mono: " + tiempo + " milisegundos");

        tout = System.nanoTime();
        String valor = mono.block(); // Esto es lo que no queremos hacer... bloquear el hilo.
        tout = System.nanoTime();

        tiempo = (tout - tin)/1000000; // Convertimos a milisegundos
        System.out.println("Tiempo total que tardo en obtener el valor del Mono: " + tiempo + " milisegundos");

        System.out.println("Valor del Mono: " + valor + " y saco el valor desde el hilo: " + Thread.currentThread().getName() );
        // Lo que hemos hecho es un desastre!
        // Nos hemos cargado toda la gracia de la programación reactiva... que es no bloquear el hilo...
        // RUINA.
        // Oye.. a veces hacemos cosas así: Por ejemplo, en TEST... que espero al resultado... para hacer la comprobación.

        System.out.println("Inicio segunda parte del programa");
        
        System.out.println("Llamo a la función que me devuelve un Mono...");
        
        tin = System.nanoTime();

        mono = unaFuncionExternaQueMeDaUnMono(); // Lo normal que esto sea librería externa
        
        tout = System.nanoTime();
        tiempo = (tout - tin)/1000000; // Convertimos a milisegundos
        System.out.println("Tiempo que tardo en obtener el Mono: " + tiempo + " milisegundos");

        // AQUI VIENE EL CAMBIO. En lugar de hacer mis cosas... y cuando acabe con ellas bloquear el hilo... 
        // para esperar al resultado del Mono... 
        // LO QUE HACEMOS ES DEJAR PRE-PROGRAMADO lo que quiero que se haga cuando el Mono me de su valor... 
        // y después, hago ya mis cosas... sin bloquear el hilo en ningún momento.

        Consumer<String> funcionQueDebeEjecutarseCuandoElMonoMeDeSuValor = (String datoGenerado) -> {
            System.out.println("Valor del Mono: " + datoGenerado + " y saco el valor desde el hilo: " + Thread.currentThread().getName() );
        };
        mono.subscribe( funcionQueDebeEjecutarseCuandoElMonoMeDeSuValor );
        // Cuando el mono devuelva valor, que se ejecute esta función.. usando ese valor!
        // Eso ya habrá un hilo que se encargue de ejecutar esa función... 
        // La gestión de esos hilos ejecutores la hace el projecto Reactor... nosotros no tenemos que preocuparnos de eso...


        // Aquñi la gracia es que puedo hacer cosas mientras.
        System.out.println("Hago otras cosas mientras tanto que tardan un rato...");
        tardoMuchoEnHacerAlgo(2000);

        // Aquñi la gracia es que puedo hacer cosas mientras.
        System.out.println("Hago otras cosas mientras tanto que tardan un rato...");
        tardoMuchoEnHacerAlgo(2000);

        // Aquñi la gracia es que puedo hacer cosas mientras.
        System.out.println("Hago otras cosas mientras tanto que tardan un rato...");
        tardoMuchoEnHacerAlgo(2000);

        // Aquñi la gracia es que puedo hacer cosas mientras.
        System.out.println("Hago otras cosas mientras tanto que tardan un rato...");
        tardoMuchoEnHacerAlgo(2000);

        // Aquñi la gracia es que puedo hacer cosas mientras.
        System.out.println("Hago otras cosas mientras tanto que tardan un rato...");
        tardoMuchoEnHacerAlgo(2000);

        System.out.println("Hago otras cosas más...");

    }



    // Lo normal es que yo opere contra un Driver de BBDD
    // Un cliente web 
    // Un cliente de un sistema de mensajería
    // Lo que sea que me devuelva Monos (tickets de trabajo)
    // No es habitual en los proyectos que nosotros nos creemos un Mono, sino que lo recibamos de una librería externa.
    private static Mono<String> unaFuncionExternaQueMeDaUnMono() {
        Mono<String> mono = Mono.just("Hola mundo"); // Sería un mono, que resuelve su valor al momento
        // Esto es trampa! No es síncrono.. pero como si lo fuera.
        // En cuanto pida el valor, lo tengo. No hay nada que esperar.
        mono = Mono.fromSupplier(Demo1Mono::generarUnStringAleatorioTardandoMucho);
        // Esto si tiene sentido... ascabamos me meter Asíncronía
        return mono;
    }

    // La función es síncrona, pero tarda mucho en resolver su valor.
    // Esta función no devuelve un Mono.
    private static String generarUnStringAleatorioTardandoMucho() {
        tardoMuchoEnHacerAlgo(5000);
        return "" + System.nanoTime();
    }

    private static void tardoMuchoEnHacerAlgo(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
