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
}
