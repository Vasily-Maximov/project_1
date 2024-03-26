package service;

import model.AbstractTask;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList<AbstractTask> tasksHistory = new CustomLinkedList<>();

    private final Map<Integer, Node<AbstractTask>> history = new HashMap<>();

    @Override
    public void add(AbstractTask task) {
        if (task != null) {
            int idTask = task.getId();
            remove(idTask);
            tasksHistory.linkLast(task);
            history.put(idTask, tasksHistory.tail);
        }
    }

    @Override
    public void remove(int id) {
        if (history.containsKey(id)) {
            Node<AbstractTask> curNode = history.get(id);
            tasksHistory.removeNode(curNode);
            history.remove(id);
        }
    }

    @Override
    public List<AbstractTask> getHistory() {
        return tasksHistory.getTasks();
    }

    private static class CustomLinkedList<T> {

        private Node<T> head;

        private Node<T> tail;

        public void linkLast(T element) {
            final Node<T> oldTail = tail;
            final Node<T> newNode = new Node<>(oldTail, element, null);
            tail = newNode;
            if (oldTail == null) {
                head = newNode;
            } else {
                oldTail.next = newNode;
            }
        }

        public ArrayList<T> getTasks() {
            ArrayList<T> tasks = new ArrayList<>();
            Node<T> node = head;
            while (node != null) {
                tasks.add(node.data);
                node = node.next;
            }
            return tasks;
        }

        public void removeNode(Node<T> node) {
            Node<T> prev = node.prev;
            Node<T> next = node.next;

            if (prev == null) {
                head = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                tail = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }
            node.data = null;
        }
    }

    private static class Node<T> {

        private Node<T> prev;

        private T data;

        private Node<T> next;

        public Node(Node<T> prev, T data, Node<T> next) {
            this.prev = prev;
            this.data = data;
            this.next = next;
        }
    }
}