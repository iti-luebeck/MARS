/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.concurrent;

import com.google.common.collect.Maps;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author damonkohler@google.com (Damon Kohler)
 */
public class RetryingExecutorService {

  private static final boolean DEBUG = true;
  private static final Log log = LogFactory.getLog(RetryingExecutorService.class);

  private static final long DEFAULT_RETRY_DELAY = 5;
  private static final TimeUnit DEFAULT_RETRY_TIME_UNIT = TimeUnit.SECONDS;

  private final Map<Callable<Boolean>, CountDownLatch> latches;
  private final Map<Future<Boolean>, Callable<Boolean>> callables;
  private final CompletionService<Boolean> completionService;
  private final RetryLoop retryLoop;
  private final ScheduledExecutorService retryExecutor;

  private long retryDelay;
  private TimeUnit retryTimeUnit;
  private boolean running;

  private class RetryLoop extends CancellableLoop {
    @Override
    public void loop() throws InterruptedException {
      Future<Boolean> future = completionService.take();
      final Callable<Boolean> callable = callables.remove(future);
      boolean retry;
      try {
        retry = future.get();
      } catch (ExecutionException e) {
        retry = true;
      }
      if (retry) {
        if (DEBUG) {
          log.info("Retry requested.");
        }
        retryExecutor.schedule(new Runnable() {
          @Override
          public void run() {
            submit(callable);
          }
        }, retryDelay, retryTimeUnit);
      } else {
        latches.get(callable).countDown();
      }
    }
  }

  public RetryingExecutorService(ExecutorService executorService) {
    retryLoop = new RetryLoop();
    retryExecutor = Executors.newSingleThreadScheduledExecutor();
    latches = Maps.newConcurrentMap();
    callables = Maps.newConcurrentMap();
    completionService = new ExecutorCompletionService<Boolean>(executorService);
    retryDelay = DEFAULT_RETRY_DELAY;
    retryTimeUnit = DEFAULT_RETRY_TIME_UNIT;
    running = true;
    // TODO(damonkohler): Unify this with the passed in ExecutorService.
    executorService.execute(retryLoop);
  }

  /**
   * Submit a new {@link Callable} to be executed.
   * 
   * @param callable
   *          the {@link Callable} to execute
   * @throws RejectedExecutionException
   *           if the {@link RetryingExecutorService} is shutting down
   */
  public synchronized void submit(Callable<Boolean> callable) {
    if (running) {
      Future<Boolean> future = completionService.submit(callable);
      latches.put(callable, new CountDownLatch(1));
      callables.put(future, callable);
    } else {
      throw new RejectedExecutionException();
    }
  }

  public void setRetryDelay(long delay, TimeUnit unit) {
    retryDelay = delay;
    retryTimeUnit = unit;
  }

  public void shutdown(long timeout, TimeUnit unit) throws InterruptedException {
    running = false;
    for (CountDownLatch latch : latches.values()) {
      latch.await(timeout, unit);
    }
    retryExecutor.shutdownNow();
    retryLoop.cancel();
  }
}