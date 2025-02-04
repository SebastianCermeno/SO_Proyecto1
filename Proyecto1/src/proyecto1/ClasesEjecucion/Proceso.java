/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ClasesEjecucion;

/**
 *
 * @author Sebastian Cermeno
 */
public class Proceso {
    // Enumerador contiene los estados posibles del proceso
    enum ProcessState {
        RUNNING,
        BLOCKED,
        READY,
        NEW,
    }
    // Enumerador contiene los estados posibles del MAR
    enum MemoryAddressRegister {
        PROGRAM_DATA,
        IO_DATA,
    }
    
    // Enumerador contiene los posibles mensajes del DMA cuando el proceso
    // está BLOCKED.
    public enum DMA_Messages {
        AWAIT,
        CONNECT,
        STAY,
    }
    
    // Enumerador contiene los posibles mensajes del Scheduler cuando el proceso
    // está en RUNNING o BLOCKED
    public enum Scheduling_Messages {
        MAINTAIN_STATE,
        BECOME_RUNNING,
        BECOME_READY,
        BECOME_BLOCKED
    }
    
    // Contenedor de información de los comportamientos IO Bound
    private class IO_Bound_Behavior {
        public int callFrequency;
        public boolean hasMadeCall;
        public int cyclesWaitingIO;
        public DMA IO_Provider;
        
        public IO_Bound_Behavior(int frequencyOfCall, DMA IO_Grant){
            callFrequency = frequencyOfCall;
            hasMadeCall = false;
            cyclesWaitingIO = 0;
            IO_Provider = IO_Grant;
        }
    }
    
    // Variables del Process Control Block
    private int processID;
    ProcessState currentState;
    private String processName;
    private int cyclesToFinish;
    MemoryAddressRegister stateMAR;
    
    // Variables de ejecución
    private int cyclesPerformed;
    private IO_Bound_Behavior ioBound;
    
    // Variables de IPC (Interprocess Communication)
    Cola<DMA_Messages> dmaQueue = new Cola();
    Cola<Scheduling_Messages> schedulingQueue = new Cola();
    
    // Constructor 1: Proceso no usa E/S
    public void Proceso(int newProcessID, String newProcessName, int duration){
        processID = newProcessID;
        processName = newProcessName;
        cyclesToFinish = duration;
        currentState = ProcessState.NEW;
        
        cyclesPerformed = 0;
        ioBound = null;
    }
    
    // Constructor 2: Proceso usa E/S
    public void Proceso(int newProcessID, String newProcessName, int duration, int ioFrequency, DMA dmaReference) {
        processID = newProcessID;
        processName = newProcessName;
        cyclesToFinish = duration;
        currentState = ProcessState.NEW;
        
        cyclesPerformed = 0;
        ioBound = new IO_Bound_Behavior(ioFrequency, dmaReference);
    }
    
    // Método: Guarda un mensaje del DMA
    public void getMessageFromDMA(DMA_Messages newMessage) {
        dmaQueue.Queue(newMessage);
    }
    // Método: Retorna el ID del Proceso
    public int getProcessID() {
        return processID;
    }
    
    // Método: Retorna el número de ciclos total que debe ejecutarse el programa
    public int getCyclesToFinish() {
        return cyclesToFinish;
    }
    
    // Método: Retorna el número de ciclos de ejecución realizados hasta ahora
    public int getCyclesDone() {
        return cyclesPerformed;
    }
    
    // Método: Retorna un booleano true si el Proceso es IO Bound; false de lo contrario
    public boolean isIOBound() {
        if (ioBound != null) {
            return true;
        }
        else {
            return false;
        }
    }
    
    // Método: Retorna el componente IOBound del Proceso
    public IO_Bound_Behavior getIOComponent() {
        return ioBound;
    }
    // Clase Interna, contiene la ejecución de los procesos
    public class ProcessExecution extends Thread {
        // Excepciones para manejar la ejecución del thread.
        public class ClockCycleException extends RuntimeException {
        }
        public class ExecutionEndException extends RuntimeException {
        }
        
        public Cola schedulingMessageQueue;
        public Proceso parentProcess;
        public int cyclesAlive = 0;
        
        public int cyclesToFinish;
        public int realCyclesPerformed;
        public boolean isIOBound;
        public IO_Bound_Behavior parentIO;
        public int ioActivationRate;
        private int ioCyclesToWait;
        private int ioCyclesWaited;
        
        public void ProcessExecution(Cola messageInbox, Proceso parent) {
            schedulingMessageQueue = messageInbox;
            parentProcess = parent;
            
            cyclesToFinish = parentProcess.getCyclesToFinish();
            realCyclesPerformed = parentProcess.getCyclesDone();
            isIOBound = parentProcess.isIOBound();
            parentIO = parentProcess.getIOComponent();
        }
        @Override
        public void run(){
            int cyclesUntilNextIO = 0;
            if (parentIO != null) {
                cyclesUntilNextIO = parentIO.callFrequency;
            }
            try {
                while (true) {
                // Código de ejecución del Thread
                    try {
                        switch(parentProcess.currentState){
                            case ProcessState.NEW:
                                // Code
                                break;
                            case ProcessState.RUNNING:
                                // Code
                                // realCyclesDone += 1
                                // if parent.isIOBound {
                                    // cyclesUntilNextIO -= 1
                                    // if cyclesUntilNextIO == 0 {
                                        // scheduler.message("I made an IO Call")
                                        // cyclesUntilNextIO = parent.iofrequency
                                    // }
                                    // else {
                                        // scheduler.message("Executed normally")
                                    // }
                                // }
                                // else {
                                    // scheduler.message("Executed normally")
                                // }
                                break;
                            case ProcessState.BLOCKED:
                                DMA_Messages currentOperation;
                                while (true) {
                                    if (dmaQueue.length == 0) {
                                        continue;
                                    }
                                    else {
                                        currentOperation = (DMA_Messages)dmaQueue.dequeue();
                                        break;
                                    }
                                }
                                switch (currentOperation) {
                                    // El DMA no provee E/S este ciclo
                                    case AWAIT:
                                        break;
                                    // El DMA comienza a realizar E/S para el proceso este ciclo
                                    case CONNECT:
                                        boolean gotPermit = parentIO.IO_Provider.IO_Semaphore.tryAcquire();
                                        switch (gotPermit){
                                            case (null):
                                                break;
                                            case (true):
                                                ioCyclesWaited += 1;
                                                if (ioCyclesWaited == ioCyclesToWait) {
                                                    parentIO.IO_Provider.deliverMessage(DMA.MessageFromProcess.DONE, parentProcess);
                                                    parentIO.IO_Provider.IO_Semaphore.release();
                                                }
                                                else {
                                                    parentIO.IO_Provider.deliverMessage(DMA.MessageFromProcess.USING_IO, parentProcess);
                                                }
                                                break;
                                            case (false):
                                                System.out.println("Failed to Acquire on Connect Message");
                                                break;
                                        }
                                        break;
                                    // El DMA continua otorgando E/S este ciclo
                                    case STAY:
                                        ioCyclesWaited += 1;
                                        if (ioCyclesWaited == ioCyclesToWait) {
                                            parentIO.IO_Provider.deliverMessage(DMA.MessageFromProcess.DONE, parentProcess);
                                            parentIO.IO_Provider.IO_Semaphore.release();
                                        }
                                        else {
                                            parentIO.IO_Provider.deliverMessage(DMA.MessageFromProcess.USING_IO, parentProcess);
                                        }
                                        break;
                                }
                                break;
                            case ProcessState.READY:
                                // Code
                                break;
                        }
                        cyclesAlive += 1;
                        while (true) {
                            int hasMessage = schedulingQueue.length;
                            if (hasMessage == 1) {
                                break;
                            }
                        }
                        Scheduling_Messages currentMessage = schedulingQueue.dequeue();
                        switch (currentMessage) {
                            // pendiente código de cambio de estado
                            case Scheduling_Messages.MAINTAIN_STATE:
                                break;
                            case Scheduling_Messages.BECOME_READY:
                                break;
                            case Scheduling_Messages.BECOME_RUNNING:
                                break;
                            case Scheduling_Messages.BECOME_BLOCKED:
                                break;
                        }
                        
                        // Ciclo de checkeo de la cola de mensajes
                        // Throws una interrupción dependiendo del mensaje
                        // while (true) {
                            // check schedulingMessage Queue
                            // if null sleep X seconds
                            // if message RUNNING throw clock exception(running)
                            // if message READY throw clock exception(ready)
                            // if message BLOCKED throw clock exception(block)
                            // }
                    }
                    catch (ClockCycleException e) {
                        // Guardado del estado del proceso en thread
                        continue;
                    }
                }
            }
            catch (ExecutionEndException e) {
                // Guardado del estado del proceso en Proceso
            }
        }
    }
}
