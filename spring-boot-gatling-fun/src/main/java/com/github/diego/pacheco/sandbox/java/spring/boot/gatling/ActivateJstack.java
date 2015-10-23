package com.github.diego.pacheco.sandbox.java.spring.boot.gatling;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

public class ActivateJstack {

	private static final int APP_WARMUP = 10;
	private static final int POLLING_CYCLE = 10;

	public static class ExecuteJStackTask {

		private static final char PID_SEPERATOR = '@';

		private final String pathToJStack;
		private final String pid;
		private final ScheduledExecutorService scheduler;
		private final LongAdder adder;

		private long threshold;

		private static String acquirePid() {
			String mxName = ManagementFactory.getRuntimeMXBean().getName();

			int index = mxName.indexOf(PID_SEPERATOR);

			String result;

			if (index != -1) {
				result = mxName.substring(0, index);
			} else {
				throw new IllegalStateException("Could not acquire pid using " + mxName);
			}

			return result;
		}

		private void executeJstack() {
			ProcessInterface pi = new ProcessInterface();

			int exitCode;

			try {
				exitCode = pi.run(new String[] { pathToJStack, "-l", pid, }, System.err);
			} catch (Exception e) {
				throw new IllegalStateException("Error invoking jstack", e);
			}

			if (exitCode != 0) {
				throw new IllegalStateException("Bad jstack exit code " + exitCode);
			}
		}

		public ExecuteJStackTask(String pathToJStack, long maxResponseTime) {
			this.pathToJStack = pathToJStack;
			this.pid = acquirePid();
			this.adder = new LongAdder();
			this.scheduler = Executors.newScheduledThreadPool(1);
			this.adder.add(0);
			this.threshold = maxResponseTime;
		}

		public void startScheduleTask() {

			scheduler.scheduleAtFixedRate(new Runnable() {
				public void run() {

					checkResponseTime();

				}
			}, APP_WARMUP, POLLING_CYCLE, TimeUnit.SECONDS);
		}

		private void checkResponseTime() {
			long responseTime = adder.longValue();

			if (responseTime > threshold) {
				Thread.currentThread().setName("Thread validating that  value is below " + threshold + " find out that the current value is " + responseTime);
				System.err.println("Minimal throughput failed: executing jstack");
				executeJstack();
			}

			adder.reset();
		}

		public void incThrughput(long val) {
			adder.add(val);
		}
	}
}
