package xyz.xenondevs.nova.util

import com.google.common.util.concurrent.ThreadFactoryBuilder
import io.papermc.paper.threadedregions.scheduler.ScheduledTask
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.scheduler.BukkitTask
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

/*fun runTaskLater(delay: Long, run: () -> Unit) =
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
    Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, run, delay, period)*/

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

fun runTaskLater(delay: Long, world: World, chunkX: Int, chunkZ: Int, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runDelayed(NOVA.loader, world, chunkX, chunkZ, { run }, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay))
    }
}

fun runTaskLater(delay: Long, location: Location, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runDelayed(NOVA.loader, location, { run }, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay))
    }
}

fun runTaskLater(delay: Long, entity: Entity, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(entity.scheduler.runDelayed(NOVA.loader, { run }, {}, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay))
    }
}

fun runTaskLater(delay: Long, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getGlobalRegionScheduler().runDelayed(NOVA.loader, { run }, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader, run, delay))
    }
}

fun runTask(location: Location, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().run(NOVA.loader, location) { run })
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader, run))
    }
}

fun runTask(world: World, chunkX: Int, chunkZ: Int, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().run(NOVA.loader, world, chunkX, chunkZ) { run })
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader, run))
    }
}

fun runTask(entity: Entity, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(entity.scheduler.run(NOVA.loader, { run }, {}))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader, run))
    }
}

fun runTask(run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getGlobalRegionScheduler().run(NOVA.loader) { run })
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader, run))
    }
}

fun runTaskTimer(delay: Long, period: Long, world: World, chunkX: Int, chunkZ: Int, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runAtFixedRate(NOVA.loader, world, chunkX, chunkZ, { run }, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period))
    }
}

fun runTaskTimer(delay: Long, period: Long, location: Location, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runAtFixedRate(NOVA.loader, location, { run }, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period))
    }
}

fun runTaskTimer(delay: Long, period: Long, entity: Entity, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(entity.scheduler.runAtFixedRate(NOVA.loader, { run }, {}, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period))
    }
}

fun runTaskTimer(delay: Long, period: Long, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getGlobalRegionScheduler().runAtFixedRate(NOVA.loader, { run }, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader, run, delay, period))
    }
}

fun runTaskSynchronized(lock: Any, world: World, chunkX: Int, chunkZ: Int, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().run(NOVA.loader, world, chunkX, chunkZ) { synchronized(lock) { run } })
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader,  Runnable { synchronized(lock) { run } }))
    }
}

fun runTaskSynchronized(lock: Any, location: Location, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().run(NOVA.loader, location) { synchronized(lock) { run } })
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader,  Runnable { synchronized(lock) { run } }))
    }
}

fun runTaskSynchronized(lock: Any, entity: Entity, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(entity.scheduler.run(NOVA.loader, { synchronized(lock) { run } }, {}))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader,  Runnable { synchronized(lock) { run } }))
    }
}

fun runTaskSynchronized(lock: Any, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getGlobalRegionScheduler().run(NOVA.loader) { synchronized(lock) { run } })
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTask(NOVA.loader,  Runnable { synchronized(lock) { run } }))
    }
}

fun runTaskLaterSynchronized(lock: Any, delay: Long, world: World, chunkX: Int, chunkZ: Int, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runDelayed(NOVA.loader, world, chunkX, chunkZ, { synchronized(lock) { run.run() } }, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay))
    }
}

fun runTaskLaterSynchronized(lock: Any, delay: Long, location: Location, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runDelayed(NOVA.loader, location, { synchronized(lock) { run.run() } }, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay))
    }
}

fun runTaskLaterSynchronized(lock: Any, delay: Long, entity: Entity, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(entity.scheduler.runDelayed(NOVA.loader, { synchronized(lock) { run } }, {}, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay))
    }
}

fun runTaskLaterSynchronized(lock: Any, delay: Long, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getGlobalRegionScheduler().runDelayed(NOVA.loader, { synchronized(lock) { run } }, delay))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskLater(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay))
    }
}

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, world: World, chunkX: Int, chunkZ: Int, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runAtFixedRate(NOVA.loader, world, chunkX, chunkZ, { synchronized(lock) { run } }, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay, period))
    }
}

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, location: Location, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getRegionScheduler().runAtFixedRate(NOVA.loader, location, { synchronized(lock) { run } }, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay, period))
    }
}

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, entity: Entity, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(entity.scheduler.runAtFixedRate(NOVA.loader, { synchronized(lock) { run } }, {}, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay, period))
    }
}

fun runTaskTimerSynchronized(lock: Any, delay: Long, period: Long, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getGlobalRegionScheduler().runAtFixedRate(NOVA.loader, { synchronized(lock) { run } }, delay, period))
    } else {
        Task.wrapBukkit(Bukkit.getScheduler().runTaskTimer(NOVA.loader,  Runnable { synchronized(lock) { run } }, delay, period))
    }
}

fun runSyncTaskWhenUnlocked(lock: ObservableLock,  world: World, chunkX: Int, chunkZ: Int, run: Runnable) {
    if (IS_FOLIA) {
        Bukkit.getRegionScheduler().runDelayed(NOVA.loader, world, chunkX, chunkZ, { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, world, chunkX, chunkZ, run) }, 1)
    } else {
        runTaskLater(1, world, chunkX, chunkZ) { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, world, chunkX, chunkZ, run) }
    }
}

fun runSyncTaskWhenUnlocked(lock: ObservableLock, location: Location, run: Runnable) {
    if (IS_FOLIA) {
        Bukkit.getRegionScheduler().runDelayed(NOVA.loader, location, { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, location, run) }, 1)
    } else {
        runTaskLater(1, location) { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, location, run) }
    }
}

fun runSyncTaskWhenUnlocked(lock: ObservableLock, entity: Entity, run: Runnable) {
    if (IS_FOLIA) {
        entity.scheduler.runDelayed(NOVA.loader, { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, entity, run) }, {},1)
    } else {
        runTaskLater(1, entity) { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, entity, run) }
    }
}

fun runSyncTaskWhenUnlocked(lock: ObservableLock, run: Runnable) {
    if (IS_FOLIA) {
        Bukkit.getGlobalRegionScheduler().runDelayed(NOVA.loader, { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, run) }, 1)
    } else {
        runTaskLater(1) { if (!lock.tryLockAndRun({ run })) runSyncTaskWhenUnlocked(lock, run) }
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

fun runAsyncTaskTimerSynchronized(lock: Any, delay: Long, period: Long, run: Runnable): Task {
    val task = Runnable { synchronized(lock, { run }) }
    return if (IS_FOLIA) {
        return Task.wrapFolia(Bukkit.getAsyncScheduler().runNow(NOVA.loader, { task }))
    } else Task.wrapBukkit(Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, task, delay, period))
}

fun runAsyncTaskTimer(delay: Long, period: Long, run: Runnable): Task {
    return if (IS_FOLIA) {
        Task.wrapFolia(Bukkit.getAsyncScheduler().runAtFixedRate(NOVA.loader, { run }, delay, period * 50, TimeUnit.MILLISECONDS))
    } else Task.wrapBukkit(Bukkit.getScheduler().runTaskTimerAsynchronously(NOVA.loader, run, delay, period))
}


class Task(private val wrapped: Any?, canceller: Consumer<Any>) {
    fun cancel() {
        if (wrapped != null) {
            canceller.accept(wrapped)
        }
    }

    private val canceller: Consumer<Any>

    init {
        this.canceller = canceller
    }

    companion object {
        fun wrapBukkit(runnable: BukkitTask?): Task {
            return Task(runnable) { task -> (task as BukkitTask).cancel() }
        }

        fun wrapFolia(scheduledTask: ScheduledTask?): Task {
            return Task(scheduledTask) { task -> (task as ScheduledTask).cancel() }
        }
    }
}
// Folia end
