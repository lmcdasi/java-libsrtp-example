package org.lmcdasi.demo.srtp.util;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomThreadFactoryBuilder {
    private String namePrefix = null;
    private boolean daemon = false;
    private int priority = Thread.NORM_PRIORITY;

    public CustomThreadFactoryBuilder setNamePrefix(String namePrefix) {
        this.namePrefix = namePrefix;
        return this;
    }

    public CustomThreadFactoryBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public CustomThreadFactoryBuilder setPriority(int priority) {
        if (priority < Thread.MIN_PRIORITY || priority > Thread.MAX_PRIORITY) {
            throw new IllegalArgumentException(STR."Thread priority out of range: \{priority}");
        }
        this.priority = priority;
        return this;
    }

    public ThreadFactory build() {
        return new CustomThreadFactory(namePrefix, daemon, priority);
    }

    private static class CustomThreadFactory implements ThreadFactory {
        private final String namePrefix;
        private final boolean daemon;
        private final int priority;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private static final AtomicInteger poolNumber = new AtomicInteger(1);

        CustomThreadFactory(@Nullable String namePrefix, boolean daemon, int priority) {
            this.namePrefix = namePrefix != null ? namePrefix : STR."pool-\{poolNumber.getAndIncrement()}-thread";
            this.daemon = daemon;
            this.priority = priority;
        }

        @Override
        public Thread newThread(@Nonnull final Runnable r) {
            Thread thread = new Thread(r, STR."\{namePrefix}-\{threadNumber.getAndIncrement()}");
            thread.setDaemon(daemon);
            thread.setPriority(priority);
            return thread;
        }
    }
}

