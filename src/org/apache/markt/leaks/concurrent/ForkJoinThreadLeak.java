package org.apache.markt.leaks.concurrent;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

import org.apache.markt.leaks.LeakBase;

/**
 * Java 5 - ForkJoin not supported
 * Java 6 - ForkJoin not supported
 * Java 7 - no leak
 * Java 8 - no leak
 */
public class ForkJoinThreadLeak extends LeakBase {

    private static ForkJoinPool commonPool;

    public static void main(String[] args) {
        // This doesn't help. While InnocuousForkJoinWorkerThread instances do
        // return the system class loader for calls to getContextClassLoader()
        // they don't ever set the field in the super class so
        // Thread.contextClassLoader retains the reference to the original class
        // loader thereby creating the leak.
        //
        // System.setProperty("java.util.concurrent.ForkJoinPool.common.threadFactory",
        //        "java.util.concurrent.ForkJoinPool$InnocuousForkJoinWorkerThreadFactory");

        commonPool = new ForkJoinPool();

        ForkJoinThreadLeak forkJoinThreadLeak = new ForkJoinThreadLeak();
        forkJoinThreadLeak.doLeakTest();
    }


    @Override
    protected void createLeakingObjects() {
        commonPool.invoke(new NoopAction(5));
    }


    @Override
    protected void cleanUpLeakingObjects() {
        // None
    }

    private class NoopAction extends RecursiveAction {

        private static final long serialVersionUID = 1L;

        final int count;

        public NoopAction(int count) {
            this.count = count;
        }

        @Override
        protected void compute() {
            if (count > 1) {
                int mid = count / 2;
                NoopAction left = new NoopAction(mid);
                NoopAction right = new NoopAction(count - mid);
                left.fork();
                right.compute();
                left.join();
            }
        }
    }
}