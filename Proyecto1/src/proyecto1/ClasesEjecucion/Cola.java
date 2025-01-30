package proyecto1.ClasesEjecucion;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Sebastian Cermeno
 */
public class Cola {
    public Node first;
    public Node last;
    public int length;
    
    public <T> void Queue(T element) {
        if(first == null) {
            Node<T> constructedNode = new Node<T>(element);
            first = constructedNode;
            last = constructedNode;
            length += 1;
        }
        else {
            Node<T> constructedNode = new Node<T>(element);
            constructedNode.previous = last;
            constructedNode.next = null;
            last.next = constructedNode;
            last = constructedNode;
            length += 1;
        }
    }
    
    // public T getValue() {
        // return first.getValue();
    // }
    
    private class Node<T> {
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
