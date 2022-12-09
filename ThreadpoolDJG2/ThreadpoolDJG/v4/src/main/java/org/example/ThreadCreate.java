package org.example;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

public class ThreadCreate {
    static class ThreadPool {

        //array de threads
        private final OneThread[] thread;

        // cola
        private final Queue<Runnable> queue;


        public ThreadPool(int poolSize) {

            queue = new LinkedBlockingQueue<Runnable>();
            thread = new OneThread[poolSize];

            for (int i = 0; i < poolSize; i++) {
                thread[i] = new OneThread();
                thread[i].start();
            }
        }


        public void execute(Runnable task) {

            synchronized (queue) {
                queue.add(task);
                queue.notify();
            }
        }

        private class OneThread extends Thread {

            public void run() {
                Runnable task;

                while (true) {
                    //sincronizamos la cola para protegerla
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            try {
                                //esperamos a la cola
                                queue.wait();
                            } catch (InterruptedException e) {
                                System.out.println("Se ha parado por un error en la cola" + e.getMessage());
                            }
                        }
                        //avanzamos en la cola
                        //asignamos una tarea de la cola
                        task = queue.poll();
                    }
                    try {
                        //ejecutamos la primera tarea
                        task.run();

                        //comprobamos la cola y vemos si hay mas tareas despues de ejecutar la primera
                        if (!queue.isEmpty()) {

                        } else {
                            // si no quedan tareas pendientes salimos del bucle
                            break;
                        }
                    } catch (RuntimeException e) {
                        System.out.println("El thread pool se ha parado por un error" + e.getMessage());
                    }
                }
            }
        }

        //parada de threads si fuese necesario
        //sin el executor service no puedo puedo usar el .shutdown(), el .stop esta deprecated y no encuentro otra forma de pararlo.
        public void terminate() {
            for (int i = 0; i < 2; i++) {
                thread[i]=null;
            }
            System.out.println("Parando el threadpool");
        }
    }

    static class Task implements Runnable {
        private final String name;
        private Semaphore semaphore;


        public Task(Semaphore semaphore,  String name) {
            this.semaphore = semaphore;
            this.name = name;
        }


        public void run() {
            String nameT = Thread.currentThread().getName();

            try {
                semaphore.acquire();
                Thread.sleep(3000l);
                semaphore.release();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("Task [" + name + "] finalizada " + nameT);

        }
    }

}
