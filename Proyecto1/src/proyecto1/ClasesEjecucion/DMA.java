/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ClasesEjecucion;

import java.util.concurrent.Semaphore;

/**
 *
 * @author Sebastian Cermeno
 */
public class DMA {
    // Mensajes posibles que puede recibir el DMA de un proceso
    public enum MessageFromProcess {
        DONE,
        USING_IO,
        COULD_NOT_ACQUIRE,
        AWAITING,
    }
    
    // Atributos que permiten al DMA administrar E/S
    public Semaphore IO_Semaphore;
    public Cola<ProcessMessageContainer> messageQueue;
    public Cola<Proceso> processesExitingBlocked;
    public Scheduler shortTermDispatcher;
    private ProcessList processList;
    
    // Constructor: Recibe cuantas operaciones de DMA puede manejar a la vez.
    public void DMA(int processesManaged) {
        IO_Semaphore = new Semaphore(processesManaged);
        messageQueue = new Cola();
        processesExitingBlocked = new Cola();
        processList = new ProcessList();
        shortTermDispatcher = null;
    }
    
    // Método: Entrega al Scheduler los procesos que salen de BLOCKED
    public void deliverExitingBlocked() {
        int numberOfExits = processesExitingBlocked.length;
        for (int i = 0; i < numberOfExits; i++) {
            shortTermDispatcher.transitFromBlocked(processesExitingBlocked.dequeue());
        }
    }
    
    // Contenedor de mensajes, con referencia al proceso que los envía
    private class ProcessMessageContainer{
        public MessageFromProcess message;
        public Proceso process;
        
        public ProcessMessageContainer(MessageFromProcess newMessage, Proceso sender){
            message = newMessage;
            process = sender;
        }
    }
    
    // Contenedor de Procesos, estructura dinámica
    private class ProcessList {
        private int length;
        public ProcessNode root;
        public ProcessNode last;
        
        public int getLength() {
            return length;
        }
        
        public void append(Proceso processToAppend){
        }
        
        public void popFirst() {
            if (root != null) {
                root = root.next;
                length--;
            }
        }
        
        public void popLast() {
            if (last != null) {
                ProcessNode newLast = last.previous;
                newLast.next = null;
                last = newLast;
                length--;
            }
        }
        
        public ProcessNode popAndReturn(int processID, ProcessNode access) {
            if (access == null) {
                return null;
            }
            else {
                if (access.storedProcess.getProcessID() == processID) {
                    return access;
                }
                else {
                    return popAndReturn(processID, access.next);
                }
            }
        }
        
        public void findAndUpdatePermit(boolean permitStatus, int processID, ProcessNode access) {
            if (access.storedProcess.getProcessID() == processID) {
                access.hasPermit = permitStatus;
            }
            else if (access.next != null) {
                findAndUpdatePermit(permitStatus, processID, access.next);
            }
        }
    }
    
    private class ProcessNode {
        public ProcessNode previous;
        public ProcessNode next;
        public Proceso storedProcess;
        public boolean hasPermit;
    }
    
    private class DMAExecution extends Thread {
        int availablePermits = 0;
        @Override
        public void run(){
            while (true) {
                processesExitingBlocked.clearOut();
                availablePermits += IO_Semaphore.availablePermits();
                orderDMA(processList.root);
                while (true) {
                    int responseNumber = messageQueue.length;
                    if (responseNumber == processList.getLength()) {
                        break;
                    }
                }
                int responseNumber = messageQueue.length;
                for (int i = 0; i < responseNumber; i++) {
                    ProcessMessageContainer currentMessage = messageQueue.dequeue();
                    if (currentMessage.message == MessageFromProcess.DONE) {
                        ProcessNode toLeaveBlocked = processList.popAndReturn(currentMessage.process.getProcessID(), processList.root);
                        if (toLeaveBlocked != null) {
                            processesExitingBlocked.Queue(toLeaveBlocked.storedProcess);
                        }
                        else {
                            System.out.println("Process not found");
                        }
                    }
                    else if (currentMessage.message == MessageFromProcess.USING_IO) {
                        processList.findAndUpdatePermit(true, currentMessage.process.getProcessID(), processList.root);
                    }
                }
                deliverExitingBlocked();
                shortTermDispatcher.messagesFromDMA.Queue(Scheduler.DMAToScheduler.FINISHED);
                // Rutina de manejo de interfaz
            }
        }
        private void orderDMA(ProcessNode startingPoint) {
            if ((startingPoint.hasPermit == false) && (availablePermits > 0)) {
                startingPoint.storedProcess.getMessageFromDMA(Proceso.DMA_Messages.CONNECT);
            }
            else if (startingPoint.hasPermit == true) {
                startingPoint.storedProcess.getMessageFromDMA(Proceso.DMA_Messages.STAY);
            }
            else if ((startingPoint.hasPermit == false) && (availablePermits <= 0)) {
                startingPoint.storedProcess.getMessageFromDMA(Proceso.DMA_Messages.AWAIT);
            }
            if (startingPoint.next != null) {
                orderDMA(startingPoint.next);
            }
        }
    }
    
    // Método: Recibe y guarda un mensaje del Proceso
    public void deliverMessage(MessageFromProcess message, Proceso sender) {
        messageQueue.Queue(new ProcessMessageContainer(message, sender));
    }
}
