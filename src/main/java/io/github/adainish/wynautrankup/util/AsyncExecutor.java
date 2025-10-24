/*
 * Program: WynautRankup - Add a competitive ranked system to Cobblemon
 * Copyright (C) <2025> <Nicole "Adenydd" Catherine Stuut>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * See the `LICENSE` file in the project root or <https://www.gnu.org/licenses/>.
 */
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
