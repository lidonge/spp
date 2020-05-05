package spp.java.core.util;

import java.util.concurrent.atomic.AtomicReference;

public class UnsignedIntStack {
    AtomicReference<Node> top = new AtomicReference<Node>();

    public void push(int item) {
        Node newHead = new Node(item);
        Node oldHead;
        do {
            oldHead = top.get();
            newHead.next = oldHead;
        } while (!top.compareAndSet(oldHead, newHead));
    }

    public int pop() {
        Node oldHead;
        Node newHead;
        do {
            oldHead = top.get();
            if(oldHead == null) {
                return -1;
            }
            newHead = oldHead.next;

        } while (!top.compareAndSet(oldHead, newHead));
        return oldHead.item;
    }

    private static class Node {
        public final int item;
        public Node next;

        private Node(int item) {
            this.item = item;
        }
    }
}
