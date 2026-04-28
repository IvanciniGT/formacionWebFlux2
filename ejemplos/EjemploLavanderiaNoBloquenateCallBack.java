import java.util.function.Consumer;

public class EjemploLavanderiaNoBloquenateCallBack {
    
    public static void dejarRopa( String ropa, Consumer<String> callback) {
        System.out.println("Entregando la ropa: " + ropa);

            new Thread(()-> {
                try {
                    System.out.println("Comienzo a lavar la ropa: " + ropa);
                    Thread.sleep(5000);
                    System.out.println("Ya he acabado de lavar la ropa: " + ropa);
                    callback.accept("Ropa lavada: "+ ropa);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        System.out.println("Ropa entregada la ropa: " + ropa);
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

        Consumer<String> callback =  EjemploLavanderiaNoBloquenateCallBack::mostrarAgradecimiento;
        EjemploLavanderiaNoBloquenateCallBack.dejarRopa("Camisa 1", callback);
        
        System.out.println("Hago otras cosas mientras espero a que acabe la tarea..."); // Yo aqui puedo hacer más cosas
        dormirUnRato(); // Incluso acostarme un rato
        
        EjemploLavanderiaNoBloquenateCallBack.dejarRopa("Camisa 2", callback);

        System.out.println("Me levanto y sigo haciendo cosas...");// Y hacer mas cosas cuando me levante
        dormirUnRato(); // Y Acostarme otra vez
        
        System.out.println("Y sigo haciendo cosas...");
        dormirUnRato(); // Y Acostarme otra vez
        
        System.out.println("Y sigo haciendo muchas mas cosas...");
        
    }
}
