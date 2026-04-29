
OJO!!!

Yo un servicio, lo puedo consumir de forma síncrona o asíncrona.
Y si es asíncrona, podrá ser bloqueante o de forma no bloqueante.

Con independencia de cómo esté montado el servicio.

PERO OJO!!!!!

Un servicio puede responder de una , o poco a poco.

Si el servicio está montado con SpringWeb... devolverá todo de golpe.

Si el servicio está montado con SpringWebFlux:
- Si el servicio devuelve Mono, devolverá todo de golpe.
- Si el servicio devuelve Flux, devolverá poco a poco.

    Y en este caso, el cliente podrá irlo consumiendo de poco a poco
    Desde el FLux

    Tendrá que usar un cliente que sepa consumir poco a poco, como el WebClient de SpringWebFlux.
    Para poder hacer eso, no das una respuesta http normal...
    Das una respuesta que tiene que tener un header especial, que es el header de tipo de contenido "text/event-stream"

    Puedo usar un Mono<List<String>> consumirlo cuando tenga toda la lista, 
    O puedo usar un Flux<String> consumirlo a medida que vayan llegando los elementos de la lista.

---

    Mono
        Y quiero transformar SINCRONAMENTE el resultado cuando llegue, lo que hago es un map
            A -B -> C
        Y quiero transformar ASINCRONAMENTE el resultado cuando llegue, lo que hago es un flatMap
            ENCADENAR OPERACIONES ASINCRONAS
            A -> B -> C
                (asinc)
        Quiero lanzar peticione asíncronas, pero en paralelo y juntar su resultado, lo que hago es un zip
            A, B -> C
        
        Quiero que me llegue un resultado, y luego en base al resultado hacer 2 peticiones asíncronas en paralelo, y luego juntar su resultado, lo que hago es un flatMap + zip
            A -> B, C -> D

---


# Java 21 y Virtual Threads.

Uno de los cambios grande de Java 21 fue el proyecto loom, que introdujo los virtual threads.

Qué son los virtual thread... 
A nivel de desarrollador.. ni me entero de que existen, ni me entero de que los estoy usando.
Es algo que gestiona la JVM por debajo, y que me permite tener un huevo de "hilos" para atender peticiones.

Pongo "hilos" entre comillas, porque no son hilos normales, son virtual threads.

Qué es eso de un hilo virtual...
Lo que hace la JVM es multiplexar los hilos del sistema operativo, que son pocos, con los virtual threads, que son muchos.

Ha virtualizado el concepto de Thread.
Lo que me entrega ya no es un hilo del sistema operativo, sino un virtual thread, que es un hilo gestionado por la JVM.
Ese hilo virtual opera sobre un hilo físico del sistema operativo.
Pero si el hilo queda en espera, la JVM lo detecta y usa el hilo real que hay por debajo para hacer el trabajo pendiente en otro virtual thread, y así puedo tener un huevo de hilos virtuales atendiendo peticiones concurrentes sin bloquear el servidor.

Me quita de toda la optimizacion que tenía que hacer para no bloquear el servidor.

Es igual que las VMs.
Tengo 4 máquinas virtuales con 2 cores cada una, corriendo sobre mi servidor con 4 cores.
Yo no tengo 8 cores reales. tengo 4.
El hipervisor (VirtualBox...) se encarga de multiplexar esos 4 cores reales con los 8 cores virtuales que me entrega a las máquinas virtuales.
Si los 8 cores se quieren poner al 100% no pueden... solo tengo 4 cores.
Pero si hacen pausas, el hipervisor lo detecta y asigna los cores reales a las máquinas virtuales que lo necesitan en ese momento.

Esto sustituye a project Reactor? A la programación reactiva? A webflux? 
Si... y principalmente NO!
La complejidad viene por algo que aún no os he contado! Otra magia de la programación reactiva, que es el backpressure.

Una cosa es que yo consiga poner al 100% mi servidor. Con eso me pueden ayudar los virtual threads.
También me puede ayudar la programación reactiva.
En ambos casos consigo NO BLOQUEAR HILOS REALES (atados a hardware) y atender muchas peticiones concurrentes.

Pero la programación reactiva introduce otro concepto: el backpressure.

De qué va esto?

Lo que buscamos es que el ritmo no lo marque el servidor, sino el cliente.... obvio con el límite físico del servidor.


HAGO UNA QUERY A LA BBDD para montar un ETL (script que saque los datos de la BBDD, los transforme y los meta en otro sitio).

Y la BBDD devuelve 1 millón de registros... Se los mando por JSON?
Yo no tengo problema... ahí van!
    Según voy mandando... los voy limpiando de ram.
Tu revientas... El cliente.. el que lo ha pedido.. cuando reciba ese JSON en MEMORIA... se va a morir... no va a poder procesar ese JSON... se va a quedar sin memoria.

Cuando trabajamos con programación reactivas, las tareas no se ejectan en modo eager (ansioso), sino en modo lazy (perezoso).
Es decir, no se ejecutan hasta que alguien las consuma.
La query realmente no se ejecuta hasta que alguien no consuma el resultado de esa query.
El microservicio no se llama hasta que alguien no consuma el resultado del microservicio.

Las operaciones se disparan con :
- block() 
- subscribe()

Hemos visto el subscribe de Mono.
No el de Flux.

En mono decimos, me subscribo a este Mono, y cuando me llegue el resultado, haz esto con el resultado... damos una función de callback, que se ejecutará cuando llegue el resultado.
En Flux, el subscribe es un poco más complejo, porque el resultado no es uno, sino que pueden ser muchos.
Y lo que doy es una función de callback que debe ejecutarse por cada elemento que llegue.
Pero el cliente es quien va pidiendo.
Dame uno, dame otro, dame otro, dame otro...dame 5 a la vez... dame 10 a la vez... ahora dame 1 ... que los otros 9 se me han atragantado un poco... 
Y para cada uno que se reciba, se ejecuta la función de callback que le he dado al subscribe... y se despacha.
Y a otra cosa.
Quien marca el ritmo es el cliente, no el servidor.
Puede ser que el cliente pida con mucha hambre... y el servidor no de a basto... bueno.. el servidor tendrá su límite físico... pero dentro de ese límite, el cliente es quien marca el ritmo.

ESTO NO LO RESUELVEN LOS VIRTUAL THREADS.

El modelo de backpressure es algo que introduce la programación reactiva, y que no lo resuelven los virtual threads.

Y LA DIFERENCIA ES GRANDE!

Porque ahogo al cliente... a base de mandarle datos.


PEAJE DE COCHES EN CARRETERA.

En el mercadona, el pago es SINCRONO. Se esperaa respuesta... Si no llega el OK, no sales por la puerta con tu compra.
Los pagos, son sincronos o asíncronos? ASINCRONO!
    Metes la tarjeta y registro la tarjeta!
    Vete.. y ya te cobraré!

Los pagos se van anotando... en un KAFKA.... pues anda que no puede haber pagos ahi... Tropecientos.

Habrá alguien que consuma esa cola luego... Dame pagos para mandarlos al banco
El banco tendrá su ritmo de procesamiento 5 segundos por pago.. lo que sea!

Y el programa de en medio...
Recibe TROPECIENTAS ANOTACIONES DE PAGOS... y las guarda el en RAM hasta que las pueda ior despachando?
NO..
El banco marca el ritmo. El programa acompaña ese ritmo. Y el kafka va mandando a medida que el programa pide... que será a media que el banco vaya procesando los pagos.

CONTROL DE BACKPRESSURE. Quien marca el ritmo es el cliente, no el servidor.

curl http://localhost:8080/pagosPendientes <- Quien marca el ritmo?
    YO QUE HAGO EL CURL?
    O EL SERVIDOR?
    El servidor.. hace su query.. y me devuelve 3.000.000 de datos.. ahí los llevas.
    Me he ahogado.

curl -N http://localhost:8080/pagosPendientesStream <- Quien marca el ritmo? (header de tipo de contenido "text/event-stream")
    YO QUE HAGO EL CURL?
    O EL SERVIDOR?
    El cliente es quien diciendo al servidor... dame más.. que esos que me has dado ya los he procesado
    Dame otra hornadita!


Si lo unico que me interesa es que el servidor mejore su uso de recursos, los virtual threads me pueden ayudar.
Pero si lo que necesito es control de backpressure, el cliente marcando el ritmo, entonces necesito programación reactiva.

Y esta es la gracia gorda de la programación reactiva, que no lo resuelven los virtual threads.

Tiene esto sentido para hacer un CRUD de Animalitos? No, no tiene sentido.
Voy a montar una función Mono<Animal> para que me la pida un App Android de un usuario? No, no tiene sentido.
    Usa Virtual Threads, y punto.

Esto tiene sentido para PROCESAMIENTO DE DATOS... en grandes cantidades.

Abro una conexión con un servidor y que me vaya mandando datos en streaming, pero no a medida que se generan, sino a medida que yo los voy procesando (con backpressure), eso tiene sentido.