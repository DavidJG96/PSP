package org.example;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Hello world!
 *
 */

 interface ThreadPool {
     class createThreadPool {
        //tamaño del pool
        private final int poolSize;

        //array de threads
        private final threadArray[] thread;

        // cola
        private final LinkedBlockingQueue<Runnable> queue;

        public createThreadPool(int poolSize) {
            this.poolSize = poolSize;
            queue = new LinkedBlockingQueue<Runnable>();
            thread = new threadArray[poolSize];

            for (int i = 0; i < poolSize; i++) {

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
                boolean waitPendingTask=true;

                while (waitPendingTask==true) {
                    synchronized (queue) {
                        while (queue.isEmpty()) {
                            try {
                                //para dormir los threads
                                queue.wait();
                            } catch (InterruptedException e) {
                                System.out.println("SE ha parado por un error en la cola" + e.getMessage());
                            }
                        }
                        //avanzamos en la cola
                        task = queue.poll();

                    }

                    try {
                        //asignamos tarea
                        task.run();
                        if(queue.isEmpty()){
                            waitPendingTask=false;
                        }
                    } catch (RuntimeException e) {
                        System.out.println("El thread pool se ha parado por un error" + e.getMessage());
                    }
                }
                shutdownThreads();

            }
        }



        public void shutdownThreads() {


            for (int i = 0; i < poolSize; i++) {
                thread[i] = null;
            }
            System.out.println("Parando el threadpool");
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

}

 class ThreadPoolImpl implements ThreadPool {
    public static void main(String[] args) {
        createThreadPool threadPool1 = new createThreadPool(2);

        for (int i = 1; i <= 14; i++) {
            Task task = new Task("Tarea " + i);
            System.out.println("Tarea creada numero "+i);

            threadPool1.execute(task);
        }
    }
}
