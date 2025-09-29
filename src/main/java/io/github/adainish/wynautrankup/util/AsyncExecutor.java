package io.github.adainish.wynautrankup.util;

import java.util.concurrent.*;

/**
 * Utility class for managing asynchronous task execution.
 */
public class AsyncExecutor
{
    private final ExecutorService executorService;

    public AsyncExecutor(int threadCount) {
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    public static CompletionStage<Object> runAsync(Object o) {
        return CompletableFuture.supplyAsync(() -> o);
    }

    public void submitTask(Runnable task) {
        executorService.submit(task);
    }

    public void shutdownExecutor(long timeout, TimeUnit timeUnit) {
        executorService.shutdown(); // Initiates an orderly shutdown
        try {
            if (!executorService.awaitTermination(timeout, timeUnit)) {
                executorService.shutdownNow(); // Force shutdown if tasks don't finish in time
                if (!executorService.awaitTermination(timeout, timeUnit)) {
                    System.err.println("Executor did not terminate cleanly.");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow(); // Force shutdown on interruption
            Thread.currentThread().interrupt(); // Restore interrupt status
        }
    }
    public void shutdownExecutor() {
        shutdownExecutor(60, TimeUnit.SECONDS); // Default timeout of 60 seconds
    }

    public Executor getExecutorService() {
        return executorService;
    }
}
