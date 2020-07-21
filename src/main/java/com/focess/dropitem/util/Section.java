package com.focess.dropitem.util;

import com.focess.dropitem.exception.TimeOutException;
import com.google.common.collect.Sets;

import java.util.Objects;
import java.util.Set;

public class Section {

    private static final Section section = new Section();
    private final Set<RunningSection> runningSections = Sets.newConcurrentHashSet();

    private Section(){}

    public static Section getInstance() {
        return section;
    }

    public static void checkSection() {
        final long now = System.currentTimeMillis();
        for (final RunningSection runningSection:section.runningSections)
            if (now - runningSection.getTime() > 60000) {
                section.runningSections.remove(runningSection);
                runningSection.getTask().stop();
                runningSection.getRunnable().run();
                throw new TimeOutException(runningSection.getName() + " have run more than 60s");
            }
    }

    public void startSection(final String sectionName, final Thread task, final Runnable runnable) {
        this.runningSections.add(new RunningSection(sectionName,System.currentTimeMillis(),task,runnable));
    }

    public long endSection(final String sectionName) {
        for (final RunningSection runningSection: this.runningSections)
            if (runningSection.getName().equals(sectionName)) {
                this.runningSections.remove(runningSection);
                return System.currentTimeMillis() - runningSection.getTime();
            }
        return 0L;
    }

    private static class RunningSection {
        private final String sectionName;
        private final long start;
        private final Thread task;
        private final Runnable runnable;

        private RunningSection(final String sectionName, final long start, final Thread task,final Runnable runnable) {
            this.task = task;
            this.sectionName = sectionName;
            this.start = start;
            this.runnable = runnable;
        }

        public Thread getTask() {
            return this.task;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            final RunningSection that = (RunningSection) o;
            return Objects.equals(this.sectionName, that.sectionName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.sectionName);
        }

        public String getName() {
            return this.sectionName;
        }

        public long getTime() {
            return this.start;
        }

        public Runnable getRunnable() {
            return this.runnable;
        }
    }


}
