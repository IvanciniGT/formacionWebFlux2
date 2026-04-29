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
public class Demo3Mono {
    

    public static void main(String[] args) {
        Mono<String> resultadoDeLaBusqueda = resultadoDeLaBusqueda(); // Esto es una llamada a un servicio que me devuelve un Mono... y ese Mono se va a completar cuando tenga el resultado de la búsqueda... que puede ser dentro de 1 segundo, 5 segundos, etc... y mientras tanto, el hilo puede hacer otras cosas...
        resultadoDeLaBusqueda.subscribe( resultado -> System.out.println("Resultado de la búsqueda: " + resultado) ); // Cuando el Mono se complete, que se ejecute esta función... usando el
    }

    private static Mono<String> resultadoDeLaBusqueda() {
        Mono<String> resultadoMicroServicio1 = unaFuncionExternaQueMeDaUnMono(); // Una llamada a un microservicio 
        // En paralelo, lanzo otra llamada a otro microservicio... que también me devuelve un Mono...
        Mono<String> resultadoMicroServicio2 = unaFuncionExternaQueMeDaUnMono(); // 

        // Y quiero es componer el resultado de ambos microservicios... juntándolos en un único String
        // Pero no quiero bloquear... quiero seguir devolviendo un Mono... que se complete cuando tenga el resultado de ambos microservicios... y mientras tanto, el hilo puede hacer otras cosas...
        Mono<String> resultadoCombinado = resultadoMicroServicio1.zipWith(resultadoMicroServicio2, (res1, res2) -> res1 + " " + res2);
        return resultadoCombinado;

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
        mono = Mono.fromSupplier(Demo3Mono::generarUnStringAleatorioTardandoMucho);
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
