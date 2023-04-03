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
            TaskUtils.GLOBAL -> Bukkit.getGlobalRegionScheduler().runDelayed(NOVA.loader, { run }, delay)
            TaskUtils.REGIONAL -> Bukkit.getRegionScheduler().runDelayed(NOVA.loader, location, { run }, delay)
            TaskUtils.ENTITY -> entity.scheduler.runDelayed(NOVA.loader, { run }, {}, delay)
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay)
    }
}

fun runTask(location: Location, entity: Entity, type: TaskUtils, run: Runnable) {
    if (IS_FOLIA) {
        when (type) {
            TaskUtils.GLOBAL -> Bukkit.getGlobalRegionScheduler().run(NOVA.loader) { run }
            TaskUtils.REGIONAL -> Bukkit.getRegionScheduler().run(NOVA.loader, location) { run }
            TaskUtils.ENTITY -> entity.scheduler.run(NOVA.loader, { run }, {})
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTask(NOVA.loader, run)
    }
}

fun runTaskTimer(delay: Long, period: Long, location: Location, entity: Entity, type: TaskUtils, run: Runnable) {
    if (IS_FOLIA) {
        when (type) {
            TaskUtils.GLOBAL -> Bukkit.getGlobalRegionScheduler().runAtFixedRate(NOVA.loader, { run }, delay, period)
            TaskUtils.REGIONAL -> Bukkit.getRegionScheduler().runAtFixedRate(NOVA.loader, location, { run }, delay, period)
            TaskUtils.ENTITY -> entity.scheduler.runAtFixedRate(NOVA.loader, { run }, {}, delay, period)
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period)
    }
}

fun runTaskSynchronized(lock: Any, location: Location, entity: Entity, type: TaskUtils, run: Runnable) {
    if (IS_FOLIA) {
        when (type) {
            TaskUtils.GLOBAL -> Bukkit.getGlobalRegionScheduler().run(NOVA.loader) { synchronized(lock) { run } }
            TaskUtils.REGIONAL -> Bukkit.getRegionScheduler().run(NOVA.loader, location) { synchronized(lock) { run } }
            TaskUtils.ENTITY -> entity.scheduler.run(NOVA.loader, { synchronized(lock) { run } }, {})
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTask(NOVA.loader,  Runnable { synchronized(lock) { run } })
    }
}

fun runTaskLaterSynchronized(lock: Any, delay: Long, location: Location, entity: Entity, type: TaskUtils, run: Runnable) {
    if (IS_FOLIA) {
        when (type) {
            TaskUtils.GLOBAL -> Bukkit.getGlobalRegionScheduler().runDelayed(NOVA.loader, { synchronized(lock) { run } }, delay)
            TaskUtils.REGIONAL -> Bukkit.getRegionScheduler().runDelayed(NOVA.loader, location, { synchronized(lock) { run.run() } }, delay)
            TaskUtils.ENTITY -> entity.scheduler.runDelayed(NOVA.loader, { synchronized(lock) { run } }, {}, delay)
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTaskLater(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay)
    }
}

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, location: Location, entity: Entity, type: TaskUtils, run: Runnable) {
    if (IS_FOLIA) {
        when (type) {
            TaskUtils.GLOBAL -> Bukkit.getGlobalRegionScheduler().runAtFixedRate(NOVA.loader, { synchronized(lock) { run } }, delay, period)
            TaskUtils.REGIONAL -> Bukkit.getRegionScheduler().runAtFixedRate(NOVA.loader, location, { synchronized(lock) { run } }, delay, period)
            TaskUtils.ENTITY -> entity.scheduler.runAtFixedRate(NOVA.loader, { synchronized(lock) { run } }, {}, delay, period)
            else -> {}
        }
    } else {
        Bukkit.getScheduler().runTaskTimer(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay, period)
    }
}

fun runAsyncTask(run: Runnable) {
    if (USE_NOVA_SCHEDULER) {
        AsyncExecutor.run { run }
    } else if (IS_FOLIA) {
        Bukkit.getAsyncScheduler().runNow(NOVA.loader, { run })
    } else Bukkit.getScheduler().runTaskAsynchronously(NOVA.loader, run)
}

fun runAsyncTaskLater(delay: Long, run: Runnable) {
    if (USE_NOVA_SCHEDULER) {
        AsyncExecutor.runLater(delay * 50, { run })
    } else if (IS_FOLIA) {
        Bukkit.getAsyncScheduler().runDelayed(NOVA.loader, { run }, delay * 50, TimeUnit.MILLISECONDS)
    } else Bukkit.getScheduler().runTaskLaterAsynchronously(NOVA.loader, run, delay)
}


fun runAsyncTaskSynchronized(lock: Any, run: Runnable) {
    val task = Runnable{ synchronized(lock, { run }) }
    if (USE_NOVA_SCHEDULER) {
        AsyncExecutor.run({ task })
    } else if (IS_FOLIA) {
        Bukkit.getAsyncScheduler().runNow(NOVA.loader, { task })
    } else Bukkit.getScheduler().runTaskAsynchronously(NOVA.loader, task)
}

fun runAsyncTaskWithLock(lock: ObservableLock, run: Runnable) {
    val task = { lock.lockAndRun({ run }) }
    if (USE_NOVA_SCHEDULER) {
        AsyncExecutor.run({ task })
    } else if (IS_FOLIA) {
        Bukkit.getAsyncScheduler().runNow(NOVA.loader, { task })
    } else Bukkit.getScheduler().runTaskAsynchronously(NOVA.loader, task)
}

fun runAsyncTaskTimerSynchronized(lock: Any, delay: Long, period: Long, run: Runnable) {
    val task = Runnable { synchronized(lock, { run }) }
    if (IS_FOLIA) {
        Bukkit.getAsyncScheduler().runNow(NOVA.loader, { task })
    } else Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, task, delay, period)
}

fun runAsyncTaskTimer(delay: Long, period: Long, run: Runnable) {
    if (IS_FOLIA) {
        Bukkit.getAsyncScheduler().runAtFixedRate(NOVA.loader, { run }, delay, period * 50, TimeUnit.MILLISECONDS)
    } else Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, run, delay, period)
}
// Folia end
