package org.example;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * Hello world!
 *
 */


     class createThreadPool {
        //tamaño del pool


        //array de threads
        private final threadArray[] thread;

        // cola
        private final Queue<Runnable> queue;
        Semaphore sem = new Semaphore(1);

        public createThreadPool(int poolSize) {

            queue = new LinkedBlockingQueue<Runnable>();
            thread = new threadArray[poolSize];

            for (int i = 0; i < 2; i++) {

                thread[i] = new threadArray();
                thread[i].start();

            }
        }

        public void execute(Runnable task) {
            synchronized (queue) {
                queue.add(task);
                queue.notify();
            }
        }

        private class threadArray extends Thread {
            public void run() {
                Runnable task;

               //empezamos con los threads dormidos com el semaforo a 0
                try {
                    sem.acquire(0);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                while (true) {
                    //sincronizamos la cola
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            try {
                                //esperamos a la cola
                                queue.wait();
                            } catch (InterruptedException e) {
                                System.out.println("SE ha parado por un error en la cola" + e.getMessage());
                            }
                        }
                        try {
                            //avanzamos en la cola
                            task = queue.poll();
                            //liberamos un thread cada vez
                            sem.acquire(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }



                    }

                    try {

                        //ejecutamos la primera tarea y despues comprobamos para despertar a los demas threads si hubiese tareas
                        task.run();
                        //comprobamos la cola y vemos si hay mas tareas depues de ejecutar la primera
                        if(!queue.isEmpty()){
                            sem.release();
                        }else{
                            //comprobamos si quedan tareas pendientes y si no quedan salimos del bucle volvienso a dormir los threads
                            break;
                        }
                    } catch (RuntimeException e) {
                        System.out.println("El thread pool se ha parado por un error" + e.getMessage());
                    }

                }


            }
        }


/*
        public void shutdownThreads() {


            for (int i = 0; i < poolSize; i++) {
                thread[i] = null;
            }
            System.out.println("Parando el threadpool");
        }

 */

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



 class ThreadPoolImpl  {
    public static void main(String[] args) {
        createThreadPool threadPool1 = new createThreadPool(2);

        for (int i = 1; i <= 14; i++) {
            Task task = new Task("Tarea " + i);
            System.out.println("Tarea creada numero "+i);

            threadPool1.execute(task);
        }
    }
}
