/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ClasesEjecucion;

/**
 *
 * @author Sebastian Cermeno
 */
public class Scheduler {
    // Necesita: Cola de Planificación
        // La cola debe poder hacerse Sorting a sí misma, según planificación
    // Necesita: Lista de Ejecución
    // Necesita: Método para comunicarse con los procesos Ready y Running
    
    // Enumerador contiene los posibles mensajes que el Scheduler puede recibir de un Proceso
    public enum MessagesToScheduler {
        EXECUTED,
        REQUESTED_IO,
        IS_READY,
        FINISHED_EXECUTION,
    }
    
    private int cpuCount;
    private SchedulingQueue<Proceso> readyQueue = new SchedulingQueue();
    
    private class SchedulingQueue<T> extends Cola<T> {
    
    }
}
