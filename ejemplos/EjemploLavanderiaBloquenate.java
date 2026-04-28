import java.util.concurrent.CompletableFuture;

public class EjemploLavanderiaBloquenate {
    
    public static CompletableFuture<String> dejarRopa( String ropa) {
        System.out.println("Entregando la ropa: " + ropa);
            return CompletableFuture.supplyAsync(()-> {
                try {
                    System.out.println("Comienzo a lavar la ropa: " + ropa);
                    Thread.sleep(20000);
                    System.out.println("Ya he acabado de lavar la ropa: " + ropa);
                    return "Ropa lavada: "+ ropa;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return "Error lavando la ropa: " + ropa;
                }
            });

    }

    public static void mostrarAgradecimiento(String mensaje) {
        System.out.println("Gracias por avisarme: " + mensaje);
    }

    public static void dormirUnRato() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();    
        }
    }

    public static void main(String[] args) {
        // Abro un hilo para ejecutar la tarea que tarda 5 segundos en ejecutarse, y le paso un callback para que me avise cuando acabe.

        var ticketCamisa1 = EjemploLavanderiaBloquenate.dejarRopa("Camisa 1");

        System.out.println("Hago otras cosas mientras espero a que acabe la tarea...");// Yo aqui puedo hacer más cosas
        dormirUnRato();// Incluso acostarme un rato
        System.out.println("Me levanto y sigo haciendo cosas...");

        var ticketCamisa2 = EjemploLavanderiaBloquenate.dejarRopa("Camisa 2");
        
        dormirUnRato();     
        System.out.println("Y sigo haciendo cosas...");
        dormirUnRato();
        System.out.println("Y sigo haciendo muchas mas cosas...");
        

        System.out.println("Cuando quiera el resultado de la tarea, puedo bloquearme hasta que esté disponible:");
        try {
            String resultadoCamisa1 = ticketCamisa1.get(); // Este get hace un wait (BLOQUEO)
                                                           // Deja el hilo main pillao
            System.out.println("Resultado Camisa 1: " + resultadoCamisa1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            String resultadoCamisa2 = ticketCamisa2.get(); // Este get hace un wait (BLOQUEO)
                                                           // Deja el hilo main pillao
            System.out.println("Resultado Camisa 2: " + resultadoCamisa2);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Ahora ya puedo seguir con otras cosas...");
    }   
}
