package org.example;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 *
 */
public class InicioThreadPool {
    //tamaño del pool
    private final int poolSize;

    //array de threads
    private final threadIniciado[] thread;

    // cola
    private final LinkedBlockingQueue<Runnable> queue;

    public InicioThreadPool(int poolSize) {
        this.poolSize = poolSize;
        queue = new LinkedBlockingQueue<Runnable>();
        thread = new threadIniciado[poolSize];

        for (int i = 0; i < poolSize; i++) {

            thread[i] = new threadIniciado();
            thread[i].start();

        }
    }

    public void execute(Runnable task) {
        synchronized (queue) {
            queue.add(task);
            queue.notify();
        }
    }

    private class threadIniciado extends Thread {
        public void run() {
            Runnable task;

            while (true) {
                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException e) {
                            System.out.println("SE ha parado por un error en la cola" + e.getMessage());
                        }
                    }
                    task = queue.poll();
                }

                try {
                    task.run();
                } catch (RuntimeException e) {
                    System.out.println("El thread pool se ha parado por un error" + e.getMessage());
                }
            }
        }
    }

    public void apagarThreads() {
        System.out.println("Parando el threadpool");
        for (int i = 0; i < poolSize; i++) {
            thread[i] = null;
        }
    }

}
class Task implements Runnable {
    private final String name;

    public Task(String name) {
        this.name = name;
    }


    public void run() {
        String nameT = Thread.currentThread().getName();
        try {
            Thread.sleep(2000l);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Task [" + name + "] finalizada "+ nameT);
    }
}

class InicioTareasyThreadPool {
    public static void main(String[] args) {
        InicioThreadPool customThreadPool = new InicioThreadPool(2);

        for (int i = 1; i <= 13; i++) {
            Task task = new Task("Tarea " + i);
            System.out.println("Tarea creada numero "+i);

            customThreadPool.execute(task);
        }
    }
}
