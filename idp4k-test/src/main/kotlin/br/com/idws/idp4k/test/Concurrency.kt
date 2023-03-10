package br.com.idws.idp4k.test

import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.function.Supplier

fun <T : Any> doInMultipleThreads(threadCount: Int, f: () -> T): List<T?> {
    val pool = Executors.newFixedThreadPool(threadCount)

    val semaphore = Semaphore(0)
    val supplier = Supplier {
        semaphore.acquire()
        runCatching(f).onFailure { it.printStackTrace() }.getOrNull()
    }

    val tasks = (1..threadCount).map {
        CompletableFuture.supplyAsync(supplier, pool)
    }
    semaphore.release(threadCount)

    return tasks.map { it.join() }
}