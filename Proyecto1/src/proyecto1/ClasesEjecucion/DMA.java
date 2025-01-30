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
    public Semaphore IO_Semaphore;
    public void DMA(int processesManaged) {
        IO_Semaphore = new Semaphore(processesManaged);
    }
    
    private class ProcessLinkedList {
        private int length;
        private ProcessNode root;
        private ProcessNode last;
        
        public int getLength() {
            return length;
        }
        private class ProcessNode {
            ProcessNode previous;
            ProcessNode next;
            Proceso storedProcess;
            
        }
    }
}
