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
    
    // Atributos administrativos del Scheduler
    private int executionTime;
    
    // Atributos de control del Scheduler
        // newQueue -> Cola de Nuevos. No se muestra por pantalla y guarda todos los procesos en el ciclo de vida
            // de la simulación
        // readyQueue -> Cola de Listos
        // newToReadyQueue -> Cola de Transición de NEW -> READY
        // exitQueue -> Cola de Salientes
        // leavingBlocked -> Procesos que salen de Bloqueado
    public DMA blockedManager;
    
    private int cpuCount;
    private final Cola<Proceso> leavingBlocked = new Cola();
    private final Cola<Proceso> newToReadyQueue = new Cola();
    
    // private final CPU_List runningQueue = new CPU_List();
    private final SchedulingQueue readyQueue = new SchedulingQueue();
    private final SchedulingQueue newQueue = new SchedulingQueue();
    private final Cola<Proceso> exitQueue = new Cola();
    
    // Buzones de mensajes
    public final Cola<DMAToScheduler> messagesFromDMA = new Cola();
    public final Cola<MessageFromProcess> messagesFromRunning = new Cola(); 
    
    // Constructor para el Scheduler
    public Scheduler(int numberOfCPU) {
        executionTime = 0;
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
    
    // Método: 
    public void messageFromRunning(Proceso sender, ProcessToScheduler message) {
        MessageFromProcess newMessage = new MessageFromProcess(sender, message);
        messagesFromRunning.Queue(newMessage);
    }
    
    public void prepareForStart() {
    
    }
    
    private class MessageFromProcess {
        public Proceso sender;
        public ProcessToScheduler message;
        public MessageFromProcess(Proceso processSending, ProcessToScheduler messageSent) {
            sender = processSending;
            message = messageSent;
        }
    }
    
    // Extensión de la clase Cola, que contiene los métodos de Sorting necesarios para SJF, SRT y HRRN
    private class SchedulingQueue {
        public int upcomingExecution = -1;
        public Cola<Proceso> internalQueue = new Cola();
        
        // Método: Usa un sort básico para ordenar las colas por orden de llegada
        public void sortByArrivalTime() {
            Cola<Proceso> newQueue = new Cola();
            while (internalQueue.length > 0) {
                int startinglength = internalQueue.length;
                int earliestStart = -1;
                Proceso earliestProcess = null;
                
                for (int i = 0; i < startinglength; i++) {
                    Proceso currentProcess = internalQueue.dequeue();
                    if (earliestStart == -1) {
                        earliestProcess = currentProcess;
                        earliestStart = currentProcess.startCycle;
                    }
                    else {
                        if (earliestStart >= currentProcess.startCycle) {
                            earliestProcess = currentProcess;
                            earliestStart = currentProcess.startCycle;
                        }
                    }
                    internalQueue.Queue(currentProcess);
                }
                
                newQueue.Queue(earliestProcess);
                internalQueue.popAt(earliestProcess);
            }
            internalQueue = newQueue;
            upcomingExecution = newQueue.getValue().startCycle;
        }
        
        // Método: Usa un sort básico para ordenar la cola por tiempo total de ejecución.
        public void sortByExecutionLength() {
            Cola<Proceso> newQueue = new Cola();
            while (internalQueue.length > 0) {
                int startinglength = internalQueue.length;
                int smallestLength = -1;
                Proceso shortestProcess = null;
                
                for (int i = 0; i < startinglength; i++) {
                    Proceso currentProcess = internalQueue.dequeue();
                    if (smallestLength == -1) {
                        shortestProcess = currentProcess;
                        smallestLength = currentProcess.getCycleDuration();
                    }
                    else {
                        if (smallestLength >= currentProcess.getCycleDuration()) {
                            shortestProcess = currentProcess;
                            smallestLength = currentProcess.getCycleDuration();
                        }
                    }
                    internalQueue.Queue(currentProcess);
                }
                
                newQueue.Queue(shortestProcess);
                internalQueue.popAt(shortestProcess);
            }
            internalQueue = newQueue;
        }
        // Método: Usa un sort básico para ordenar la cola por tiempo total de ejecución.
        public void sortByExecutionRemaining() {
            Cola<Proceso> newQueue = new Cola();
            while (internalQueue.length > 0) {
                int startinglength = internalQueue.length;
                int minRemainingLength = -1;
                Proceso minRemainingProcess = null;
                
                for (int i = 0; i < startinglength; i++) {
                    Proceso currentProcess = internalQueue.dequeue();
                    if (minRemainingLength == -1) {
                        minRemainingProcess = currentProcess;
                        minRemainingLength = currentProcess.getCyclesDone();
                    }
                    else {
                        if (minRemainingLength >= currentProcess.getCyclesDone()) {
                            minRemainingProcess = currentProcess;
                            minRemainingLength = currentProcess.getCyclesDone();
                        }
                    }
                    internalQueue.Queue(currentProcess);
                }
                
                newQueue.Queue(minRemainingProcess);
                internalQueue.popAt(minRemainingProcess);
            }
            internalQueue = newQueue;
        }
        
        // Método: 
        public void sortHRRN() {
        }
        
        // Método: Comunica a cada uno de los Procesos en la Cola que inicien so ciclo de reloj
        public void activate() {
            int overallLength = internalQueue.length;
            for (int i = 0; i < overallLength; i++) {
                Proceso currentProcess = internalQueue.dequeue();
                currentProcess.orderProceed();
                internalQueue.Queue(currentProcess);
            }
        }
    }
    
    private class SchedulerExecution extends Thread {
        @Override
        public void run() {
            // Antes de comenzar:
            // Prev. #1: Todos los procesos de la simulación son cargados previamente a una SchedulingQueue
            // en estado NEW y ordenados por ciclo de entrada.
            // Prev. #2: Los procesos con ciclo de entrada = 0 son cargados a la cola de READY.
            // Prev. #3: Los procesos en la cola de READY son ordenados según el criterio de la política
            // inicial.
            // Prev. #4: Se hace extraen tantos procesos como hayan CPU y se colocan en la cola de READY,
            // saltando el estado de transición.
            // Prev. #5: Se ajusta el estado de todos los procesos cargados en READY o RUNNING.
            
            while (true) {
                // Pasos:
                // Paso #1: El Scheduler notifica al DMA que puede empezar su ciclo de reloj.
                blockedManager.isExecuting = true;
                // Paso #2: El Scheduler busca los procesos a entrar en ejecución el siguiente ciclo y los 
                // carga a la cola de transición NEW -> READY
                while (true) {
                    if (newQueue.internalQueue.length == 0) {
                        break;
                    }
                    if (newQueue.upcomingExecution == executionTime+1) {
                        newToReadyQueue.Queue(newQueue.internalQueue.dequeue());
                        if (newQueue.internalQueue.length == 0) {
                            newQueue.upcomingExecution = -1;
                            break;
                        }
                        newQueue.upcomingExecution = newQueue.internalQueue.getValue().startCycle;
                    }
                    else {
                        break;
                    }
                }
                // Paso #3: El Scheduler notifica a todos los procesos a su cargo que pueden empezar su ciclo de reloj.
                readyQueue.activate();
                // Paso #4: El Scheduler espera el resultado de ejecución de los procesos RUNNING. Si solicitaron IO
                // los mueve a la lista de transición RUNNING -> BLOCKED; si terminaron los mueve a la lista de
                // EXIT.
                
                // Paso #5: El Scheduler espera a que el DMA reporte con Finished
                // Paso #6: El Scheduler calcula las siguientes acciones de planificación, poblando la lista de
                // transición READY -> RUNNING y RUNNING -> READY como sea pertinente.
                // Paso #7: El Scheduler reporta al DataInterface todos los valores relevantes, incluyendo transiciones,
                // planificación, y ciclos restantes de ejecución.
                // Paso #8: El Scheduler ordena a los procesos en listas de transición a cambiar de estado.
                // Paso #9: El Scheduler colapsa las listas de transición en sus colas regulares.
                // Paso #10: El Scheduler recolecta las estadísticas de los procesos en la lista de EXIT y la
                // vacía.
                // Paso #11: El Scheduler espera hasta recibir la señal de continuar. Cuando la recibe, vuelve
                // al inicio del ciclo while().
            }
        }
    }
}
