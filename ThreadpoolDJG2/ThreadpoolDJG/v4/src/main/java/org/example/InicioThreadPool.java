package org.example;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

 class ThreadPoolImpl extends ThreadCreate {
    public static void main(String[] args) {
        ThreadPool threadPool1 = new ThreadPool(4);
        Semaphore semaphore = new Semaphore(2);

        for (int i = 1; i <= 20; i++) {
            Task task = new Task(semaphore,"Tarea " + i);
            System.out.println("Tarea creada numero "+i);
            threadPool1.execute(task);
        }
    }
}
