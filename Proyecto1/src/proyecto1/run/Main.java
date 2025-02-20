/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1.run;
import proyecto1.ClasesEjecucion.Cola;
/**
 *
 * @author Sebastian Cermeno
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Cola<String> miCola = new Cola();
        miCola.Queue("Hola ");
        miCola.Queue("Mundo!");
        miCola.Queue(">:[");
        System.out.println(miCola.length);
        miCola.popAt(">:[");
        System.out.println(miCola.length);
        while (miCola.length > 0) {
            System.out.print(miCola.dequeue());
        }
    }
    
}
