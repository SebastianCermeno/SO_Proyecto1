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
    
    // Método: Retorna el ID del Proceso
    public int getProcessID() {
        return processID;
    }
    
    // Clase Interna, contiene la ejecución de los procesos
    public class ProcessExecution extends Thread {
        // Excepciones para manejar la ejecución del thread.
        public class ClockCycleException extends RuntimeException {
        }
        public class ExecutionEndException extends RuntimeException {
        }
        
        // public Cola messageQueue;
        
        @Override
        public void run(){
            try {
                while (true) {
                // Código de ejecución del Thread
                    try {
                        // Ciclo de checkeo de la cola de mensajes
                        // Throws una interrupción dependiendo del mensaje
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
