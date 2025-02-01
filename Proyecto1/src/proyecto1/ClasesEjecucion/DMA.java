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
        AWAITING,
    }
    
    // Atributos que permiten al DMA administrar E/S
    public Semaphore IO_Semaphore;
    public Cola<ProcessMessageContainer> messageQueue;
    
    // Constructor: Recibe cuantas operaciones de DMA puede manejar a la vez.
    public void DMA(int processesManaged) {
        IO_Semaphore = new Semaphore(processesManaged);
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
    private class ProcessLinkedList {
        private int length;
        private ProcessNode root;
        private ProcessNode last;
        
        public int getLength() {
            return length;
        }
        private class ProcessNode {
            public ProcessNode previous;
            public ProcessNode next;
            public Proceso storedProcess;
        }
    }
    
    // Método: Recibe y guarda un mensaje del Proceso
    public void deliverMessage(MessageFromProcess message, Proceso sender) {
        messageQueue.Queue(new ProcessMessageContainer(message, sender));
    }
}
