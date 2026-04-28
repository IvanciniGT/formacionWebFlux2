
Cuando creamos pruebas unitarias o de sistema o integración con JUnit 5 (Jupiter), creamos clases con métodos anotados con `@Test`.

Eso luego hay que ejecutarlo...
Hace falta un método main.
Ese método main lo aporta JUnit, no hace falta que lo escribamos nosotros.

Si llamara al programa de pruebas desde linea de comandos:

$ java -jar junit-platform-console-standalone-1.9.3.jar -cp . --scan-classpath

Este --scan-classpath le dice a JUnit que busque en el classpath las clases con pruebas.
En el jar ese, viene un archivo preconfigurado: META-INF en un XML.
La clase que se ejecuta que tiene el main, es la clase org.junit.platform.console.ConsoleLauncher.
Esas clase crea la instancia de mi clase de Pruebas.
Y esa clase ejecuta los métodos anotados con @Test de la instancia que ella ha creado de nuestra clase.



No es NETTTY DICE pido comandas...
    Y voy haciendo más trabajo...
    Y luego cuando están todas las comandas, las sirvo al cliente.



Es NETTY DICE pido comandas...
    Y Dejo dicho que cuando estén listas se sirvan a los clientes.
    Y voy haciendo más trabajo...
