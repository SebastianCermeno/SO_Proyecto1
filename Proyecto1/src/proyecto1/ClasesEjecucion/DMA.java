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
    private ProcessList processList;
    
    // Constructor: Recibe cuantas operaciones de DMA puede manejar a la vez.
    public void DMA(int processesManaged) {
        IO_Semaphore = new Semaphore(processesManaged);
        messageQueue = new Cola();
        processesExitingBlocked = new Cola();
        processList = new ProcessList();
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
        }
        
        public void popLast() {
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
                int processListLength = processList.getLength();
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
                    }
                    else if (currentMessage.message == MessageFromProcess.USING_IO) {
                    
                    }
                }
            }
        // while true {
            // blockExitQueue.clearOut()
            // int permits_to_grant = 0
            // check how many permits are available
            // if (permits available > 0) {
                // permits_to_grant = permits available
            // }
            // for each process in ProcessList {
                // if (process.hasPermit == false AND permits_to_grant > 0) {
                    // process.message(CONNECT)
                // }
                // else if (process.hasPermit == true) {
                    // process.message(STAY)
                // }
                // else if (process.hasPermit == false AND permits_to_grant == 0) {
                    // process.message(AWAIT)
                // }
            // }
            // while true {
                // int responseNumber = messsageQueue.length
                // if responseNumber == ProcessList.length {
                    // break;
                // }
            // }
            // for message in messageQueue {
                // if message == DONE {
                    // Proceso exitingBlocked = ProcessList.findPopAndReturn(message.sender.ID)
                    // blockExitQueue.Queue(exitingBlocked)
                // }
                // if message == USING_IO {
                    // ProcessList.findAndUpdate(hasPermit, true, message.sender.ID)
                // }
            // }
            // Scheduler.deliverExitingFromBlocked()
            // Scheduler.message("Finished Cycle")
            // Interface.deliverCycleEndState()
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
