package xyz.xenondevs.nova.util

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitRunnable
import xyz.xenondevs.nova.IS_FOLIA
import xyz.xenondevs.nova.LOGGER
import xyz.xenondevs.nova.NOVA
import xyz.xenondevs.nova.data.config.DEFAULT_CONFIG
import xyz.xenondevs.nova.data.config.configReloadable
import xyz.xenondevs.nova.util.concurrent.ObservableLock
import xyz.xenondevs.nova.util.concurrent.lockAndRun
import xyz.xenondevs.nova.util.concurrent.tryLockAndRun
import java.util.concurrent.*
import java.util.function.Consumer
import java.util.logging.Level


val USE_NOVA_SCHEDULER by configReloadable { DEFAULT_CONFIG.getBoolean("performance.nova_executor.enabled") }

fun runTaskLater(delay: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay)

fun runTask(run: () -> Unit) =
    Bukkit.getScheduler().runTask(NOVA.loader, run)

fun runTaskTimer(delay: Long, period: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period)

fun runTaskSynchronized(lock: Any, run: () -> Unit) =
    Bukkit.getScheduler().runTask(NOVA.loader, Runnable { synchronized(lock, run) })

fun runTaskLaterSynchronized(lock: Any, delay: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskLater(NOVA.loader, Runnable { synchronized(lock, run) }, delay)

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskTimer(NOVA.loader, Runnable { synchronized(lock, run) }, delay, period)

fun runSyncTaskWhenUnlocked(lock: ObservableLock, run: () -> Unit) {
    runTaskLater(1) { if (!lock.tryLockAndRun(run)) runSyncTaskWhenUnlocked(lock, run) }
}

fun runAsyncTask(run: () -> Unit) {
    if (USE_NOVA_SCHEDULER) AsyncExecutor.run(run)
    else Bukkit.getScheduler().runTaskAsynchronously(NOVA.loader, run)
}

fun runAsyncTaskLater(delay: Long, run: () -> Unit) {
    if (USE_NOVA_SCHEDULER) AsyncExecutor.runLater(delay * 50, run)
    else Bukkit.getScheduler().runTaskLaterAsynchronously(NOVA.loader, run, delay)
}

fun runAsyncTaskSynchronized(lock: Any, run: () -> Unit) {
    val task = { synchronized(lock, run) }
    if (USE_NOVA_SCHEDULER) AsyncExecutor.run(task)
    else Bukkit.getScheduler().runTaskAsynchronously(NOVA.loader, task)
}

fun runAsyncTaskWithLock(lock: ObservableLock, run: () -> Unit) {
    val task = { lock.lockAndRun(run) }
    if (USE_NOVA_SCHEDULER) AsyncExecutor.run(task)
    else Bukkit.getScheduler().runTaskAsynchronously(NOVA.loader, task)
}

fun runAsyncTaskTimerSynchronized(lock: Any, delay: Long, period: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, Runnable { synchronized(lock, run) }, delay, period)

fun runAsyncTaskTimer(delay: Long, period: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, run, delay, period)

internal object AsyncExecutor {
    
    private val THREADS = DEFAULT_CONFIG.getInt("performance.nova_executor.threads")
    
    private lateinit var threadFactory: ThreadFactory
    private lateinit var executorService: ScheduledExecutorService
    
    init {
        if (USE_NOVA_SCHEDULER) {
            threadFactory = ThreadFactoryBuilder().setNameFormat("Async Nova Worker - %d").build()
            executorService = ScheduledThreadPoolExecutor(THREADS, threadFactory)
            
            NOVA.disableHandlers += executorService::shutdown
        }
    }
    
    fun run(task: () -> Unit): Future<*> =
        executorService.submit {
            try {
                task()
            } catch (t: Throwable) {
                LOGGER.log(Level.SEVERE, "An exception occurred running a task", t)
            }
        }
    
    fun runLater(delay: Long, task: () -> Unit): Future<*> =
        executorService.schedule({
            try {
                task()
            } catch (t: Throwable) {
                LOGGER.log(Level.SEVERE, "An exception occurred running a task", t)
            }
        }, delay, TimeUnit.MILLISECONDS)
    
}

// Folia start

fun runTaskLater(delay: Long, location: Location, entity: Entity, type: TaskUtils, run: Runnable) {
    if (IS_FOLIA) {
        when (type) {
            TaskUtils.GLOBAL -> runGlobalTaskLater(delay, run)
            TaskUtils.REGIONAL -> runRegionalTaskLater(delay, location, run)
            TaskUtils.ENTITY -> runEntityTaskLater(delay, entity, run)
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay)
    }
}

fun runRegionalTaskLater(delay: Long, location: Location, run: Runnable) =
    Bukkit.getRegionScheduler().runDelayed(NOVA.loader, location, { run.run() }, delay)

fun runGlobalTaskLater(delay: Long, run: Runnable) =
    Bukkit.getGlobalRegionScheduler().runDelayed(NOVA.loader, { run.run() }, delay)

fun runEntityTaskLater(delay: Long, entity: Entity, run: Runnable) =
    entity.scheduler.runDelayed(NOVA.loader, { run.run() }, {}, delay)

/*fun runTask(run: () -> Unit) =
    Bukkit.getScheduler().runTask(NOVA.loader, run)

fun runTaskTimer(delay: Long, period: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period)

fun runTaskSynchronized(lock: Any, run: () -> Unit) =
    Bukkit.getScheduler().runTask(NOVA.loader, Runnable { synchronized(lock, run) })

fun runTaskLaterSynchronized(lock: Any, delay: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskLater(NOVA.loader, Runnable { synchronized(lock, run) }, delay)

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, run: () -> Unit) =
    Bukkit.getScheduler().runTaskTimer(NOVA.loader, Runnable { synchronized(lock, run) }, delay, period)

fun runSyncTaskWhenUnlocked(lock: ObservableLock, run: () -> Unit) {
    runTaskLater(1) { if (!lock.tryLockAndRun(run)) runSyncTaskWhenUnlocked(lock, run) }
}*/

internal class Task(private val wrapped: Any, canceller: Consumer<Any>) {
    fun cancel() {
        canceller.accept(wrapped)
    }

    private val canceller: Consumer<Any>

    init {
        this.canceller = canceller
    }

    companion object {
        fun wrapBukkit(runnable: BukkitRunnable): Task {
            return Task(runnable) { task -> (task as BukkitRunnable).cancel() }
        }

        fun wrapFolia(scheduledTask: ScheduledTask): Task {
            return Task(scheduledTask) { task -> (task as ScheduledTask).cancel() }
        }
    }
}

// Folia end
