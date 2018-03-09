package runner.parallelrunner;

import org.junit.runners.Parameterized;
import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Parallelized extends Parameterized {

    private static class ThreadPoolScheduler implements RunnerScheduler {

        private ExecutorService executor;

        public ThreadPoolScheduler() {
            String threads = System.getProperty("junit.parallelrunner.threads", "16");
            int numAvailableThreads = Integer.parseInt(threads);
            executor = Executors.newFixedThreadPool(4);
        }

        @Override
        public void schedule(Runnable childFunction) {
            executor.submit(childFunction);
        }

        @Override
        public void finished() {
            executor.shutdown();
            try {
                executor.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    }

    public Parallelized(Class<?> klass) throws Throwable {
        super(klass);
        setScheduler(new ThreadPoolScheduler());
    }



}
