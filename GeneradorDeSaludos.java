// En java, usamos interfaces para definir tipos de datos genéricos.
// Las interfaces simples, las que llevamos usando años, 
// me permiten apuntar a Objetos.
// Pero desde hjava 1.8 hay un nuevo tipo de interfaz: las funcionales.
// Puedo crear mis propias interfaces funcionales 
// Que apunten a funciones.
// Para ello, defino una :

/*public interface MiInterfazFuncional {
    void metodo();
}*/

// Eso por si no sería una interfaz funcional, para convertirlo en una 
// interfaz funcional, tengo que añadirle la anotación  
@FunctionalInterface
public interface GeneradorDeSaludos {
    String generarSaludo(String nombre);
}