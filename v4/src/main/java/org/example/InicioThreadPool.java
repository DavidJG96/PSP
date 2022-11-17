package org.example;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

// JAVI: Esto no sé qué pinta aquí ...
/**
 * Hello world!
 *
 */

     // JAVI: Nombre my malo para una clase, y por favor, puedes hacer esta clase en otro archivo, aquí es un poco chapuza
     class createThreadPool {

        //array de threads
        // JAVI: Por qué usas un array en vez de una lista????
        private final ThreadOne[] thread;

        // cola
        private final Queue<Runnable> queue;

          // JAVI: El semaforo no va aquí
            Semaphore semaphore;
          // JAVI: No tengo ni idea de para qué sirve esta variable
            int threadName;

          // JAVI: Mal indentado
    public createThreadPool(int poolSize) {

            queue = new LinkedBlockingQueue<Runnable>();
            thread = new ThreadOne[poolSize];

               // No sé qué pinta este dos aquí
            for (int i = 0; i < 2; i++) {
                // No entoendo que hacen estas dos variables, ..., y el semáforo lo inicializas a 1????
                this.semaphore = new Semaphore(1);
                this.threadName = i;
                 
                thread[i] = new ThreadOne();
                thread[i].start();

            }
        }

        public void execute(Runnable task) {
             // JAVI: No quedamos en que esto no era así? sino que se tenía que usar el semáforo para despertar a los hilos??
            synchronized (queue) {
                queue.add(task);
                queue.notify();
            }
        }

          // JAVI: Mal nombre para la clase
        private class ThreadOne extends Thread {
            public void run() {
                Runnable task;
                //empezamos con los threads dormidos com el semaforo a 0

                 // Esto que hace aquí?!?!?!?!
                try {
                    semaphore.acquire();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                   while (true) {
                    //sincronizamos la cola
                    synchronized (queue) {
                        if (queue.isEmpty()) {
                            try {
                                //esperamos a la cola
                                 // Ya quedamos en que esto no iba aquí
                                queue.wait();
                            } catch (InterruptedException e) {
                                System.out.println("SE ha parado por un error en la cola" + e.getMessage());
                            }
                        }
                        //avanzamos en la cola

                        task = queue.poll();

                    }
                    try {

                        //ejecutamos la primera tarea y despues comprobamos para despertar a los demas threads si hubiese tareas
                        task.run();
                        //comprobamos la cola y vemos si hay mas tareas depues de ejecutar la primera
                        if(!queue.isEmpty()){
                            //liberamos 1 thread mas, hasta ahora solo se habia despertado el thread 0, en la siguiente iteracion de bucle se habra despertado en thread 1
                            //en la primera iteracio el thread 0 completa la tarea 0 y en la siguiente iteracion el thread 0 completa la tarea 1 y el thread 1 completaria la tarea 2
                             // No entiendo que hace esto aquí?
                            semaphore.release(1);
                        }else{
                            //comprobamos si quedan tareas pendientes y si no quedan salimos del bucle volviendo a dormir los threads con semaphore acquire
                             // JAVI: Esto no hace lo que dices que hace
                            break;
                        }
                        } catch (RuntimeException e) {
                            System.out.println("El thread pool se ha parado por un error" + e.getMessage());
                        }
                   }


            }
        }


//parada de threads si fuese necesario
        public void shutdownThreads() {


             // JAVI: Ya comentamos en su momento que esto no era así
            for (int i = 0; i < 2; i++) {
                thread[i] = null;
            }
            System.out.println("Parando el threadpool");
        }



    }

// JAVI: Por favor, ponme la clase del thread pool en otro fichero
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
