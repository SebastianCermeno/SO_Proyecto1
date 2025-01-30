package proyecto1.ClasesEjecucion;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

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
        return first.getValue();
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
