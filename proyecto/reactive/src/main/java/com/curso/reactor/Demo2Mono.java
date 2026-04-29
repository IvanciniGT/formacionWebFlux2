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
public class Demo2Mono {
    

    public static void main(String[] args) {
        unaFuncionExternaQueMeDaUnMono()                 // Una query a BBDD asíncrona que da un ID
                                        .map( valor -> ponerUnStringEnMayusculas(valor) ) // Pongo el ID en mayúculas (síncronamente)
                                        // Quiero llamar a un mricroservicio con el ID transformado // Esto de nuevo quiero que sejecute de forma ASINCRONA, no bloqueante... porque tardo mucho en obtener el resultado... y mientras tanto, el hilo puede hacer otras cosas...
                                        .flatMap( valor -> unaFuncionQueMeDaUnMono(valor) ) // Esto me devuelve un Mono<Mono<String>>... porque la función que me da el Mono, me devuelve un Mono...
                                                // Si lo hago con un map, me devolvería un Mono<Mono<String>>
                                                // Al hacerlo con flatMap me devuelve un Mono<String>... porque el flatMap se encarga de "aplanar" el resultado...
                                        .subscribe( valor -> System.out.println("Valor final: " + valor) ); // Cuando el Mono me de su valor, que se ejecute esta función... usando ese valor!
                                        // Dentro de webflux, el subscribe es Netty el que se encarga de suscribirse a los Monos que se devuelven desde los controladores... y de ejecutar la función que se le pasa al subscribe... cuando el Mono le da su valor...
    }
    //Yo voy a montar una función que devuelva ese Mono pero en Mayúsculas (transformado)
    // Peo quie no sea bloqueante, es decir, que siga devlviendo el Mono
    private static Mono<String> unaFuncionExternaQueMeDaUnMonoEnMayusculas(Mono<String> mono) {
        return mono.map(Demo2Mono::ponerUnStringEnMayusculas) ;    // Streams reaactivos: FLUJOS REACTIVOS DE TRANSFORMACIÓN DE DATOS... 
        // Cuando sea que el Mono me de su valor, que se ejecute esta función... usando ese valor!
        // Pero yo sigo devolviendo un Ticket... canjeable a futuro
    }


    private static Mono<String> unaFuncionQueMeDaUnMono(String parametro) { // Lib externa /BBDD, KAFKA, API REST, etc...
        // Luego, con el ID que he sacado de la BBDD
        // Quiero llamar a un microservicio externo que me devuelva un String... y quiero que esa llamada también sea asíncrona no bloqueante... porque tardo mucho en obtener el resultado... y mientras tanto, el hilo puede hacer otras cosas...
        tardoMuchoEnHacerAlgo(5000); // Este tiempo, el hilo lo podria invertir en hacer otras cosas...
        Mono<String> mono = Mono.just(parametro+parametro);
        return mono;
    }



    private static String ponerUnStringEnMayusculas(String str) {
        return str.toUpperCase();
    }

    private static Mono<String> unaFuncionExternaQueMeDaUnMono() { // Lib externa /BBDD, KAFKA, API REST, etc...
        // Esto puede ser una query a BBDD que nos devuelva un ID ( Y me interesa hacer esta peticion asíncrona no bloqueante... porque tardo mucho en obtener el resultado... y mientras tanto, el hilo puede hacer otras cosas...)
        Mono<String> mono = Mono.just("Hola mundo");
        mono = Mono.fromSupplier(Demo2Mono::generarUnStringAleatorioTardandoMucho);
        return mono;
    }

    private static String generarUnStringAleatorioTardandoMucho() {
        tardoMuchoEnHacerAlgo(5000);
        return "hola";
    }

    private static void tardoMuchoEnHacerAlgo(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
