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
    public enum ProcessToScheduler {
        EXECUTED,
        REQUESTED_IO,
        IS_READY,
        FINISHED_EXECUTION,
    }
    
    public enum DMAToScheduler {
        FINISHED,
    }
    
    // Atributos de control del Scheduler
    public DMA blockedManager;
    
    private int cpuCount;
    private final Cola<Proceso> leavingBlocked = new Cola();
    private final SchedulingQueue<Proceso> readyQueue = new SchedulingQueue();
    
    // Colas de mensajes
    public final Cola<DMAToScheduler> messagesFromDMA = new Cola();
    
    // Constructor para el Scheduler
    public Scheduler(int numberOfCPU) {
        cpuCount = numberOfCPU;
        
    }
    
    // Método: Conecta al DMA y al Scheduler, solo si no hay conexión previa.
    public void bindWithDMA(DMA newDMA) {
        if (blockedManager == null) {
            blockedManager = newDMA;
            blockedManager.shortTermDispatcher = this;
        }
    }
    
    // Método: Encola un proceso BLOCKED
    public void transitFromBlocked(Proceso exitingBlocked) {
        leavingBlocked.Queue(exitingBlocked);
    }
    
    private class SchedulingQueue<T> extends Cola<T> {
    
    }
}
