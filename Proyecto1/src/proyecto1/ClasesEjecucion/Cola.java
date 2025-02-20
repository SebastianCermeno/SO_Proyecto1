/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyecto1.ClasesEjecucion;

/**
 *
 * @author Sebastian Cermeno
 */
public class Cola<T> {
    public Node first;
    public Node last;
    public int length;
    
    public void Queue(T element) {
        if(first == null) {
            Node constructedNode = new Node(element);
            first = constructedNode;
            last = constructedNode;
            length += 1;
        }
        else {
            Node constructedNode = new Node(element);
            constructedNode.previous = last;
            constructedNode.next = null;
            last.next = constructedNode;
            last = constructedNode;
            length += 1;
        }
    }
    
    public T getValue() {
        if (first != null) {
            return first.value;
        }
        else {
            return null;
        }
    }
    
    public T dequeue() {
        T value = getValue();
        first = first.next;
        length -= 1;
        if (length <= 0) {
            last = null;
        }
        return value;
    }
    
    public T popAt(T object) {
        if (first == null) { return null; }
        T value = first.getValue();
        Node currentPosition = first;
        while (value != object) {
            currentPosition = currentPosition.next;
            if (currentPosition == null) {
                break;
            }
            value = currentPosition.value;
        }
        if (currentPosition == null) { return null; }
        else {
            if (currentPosition.previous != null) {
                currentPosition.previous.next = currentPosition.next;
            }
            length--;
            return value;
        }
    }
    
    public void clearOut() {
        first = null;
        last = null;
        length = 0;
    }
    
    private class Node {
        private T value;
        public Node next;
        public Node previous;
        
        public T getValue() {
            return value;
        }
        
        public Node(T object) {
            value = object;
        }
    }
}
