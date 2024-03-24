package br.ufrn.caze.holterci.application.security

import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.ReentrantLock

/**
 * Controls the rate of requests at the application level.
 */
class TokenBucketRateLimiter(
    private val capacity: Long,
    private val refillTokens: Long,
    private val refillPeriod: TimeUnit
) {

    private val tokens = AtomicLong(capacity)
    private val lock = ReentrantLock()

    fun tryAcquire(): Boolean {
        lock.lock()
        try {
            refillTokens()
            return if (tokens.get() > 0) {
                tokens.decrementAndGet()
                true
            } else {
                false
            }
        } finally {
            lock.unlock()
        }
    }

    private fun refillTokens() {
        val currentTime = System.currentTimeMillis()
        val elapsedTime = currentTime - lastRefillTime
        val newTokens = elapsedTime * refillTokens / refillPeriod.toMillis(1)
        if (newTokens > 0) {
            tokens.set(Math.min(capacity, tokens.get() + newTokens))
            lastRefillTime = currentTime
        }
    }

    companion object {
        private var lastRefillTime = System.currentTimeMillis()
    }

}
