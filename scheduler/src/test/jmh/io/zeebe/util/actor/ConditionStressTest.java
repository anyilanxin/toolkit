/*
 * Copyright © 2017 camunda services GmbH (info@camunda.com)
 * Copyright © 2025 anyilanxin zxh(anyilanxin@aliyun.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.zeebe.util.actor;

import io.zeebe.util.sched.Actor;
import io.zeebe.util.sched.ActorCondition;
import io.zeebe.util.sched.ActorScheduler;
import io.zeebe.util.sched.future.ActorFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@BenchmarkMode(Mode.Throughput)
@Fork(1)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
public class ConditionStressTest {
  static final AtomicInteger THREAD_ID = new AtomicInteger(0);
  private static final int BURST_SIZE = 1_000_000;

  @Benchmark
  @Threads(1)
  public void shouldConsumeBurst1Thread(ThreadContext ctx) {
    sendBurst(ctx);
  }

  @Benchmark
  @Threads(2)
  public void shouldConsumeBurst2Threads(ThreadContext ctx) {
    sendBurst(ctx);
  }

  private void sendBurst(ThreadContext ctx) {
    final AtomicBoolean burstCompleteField = ctx.burstCompleteField;
    final ActorCondition condition = ctx.condition;

    burstCompleteField.set(false);

    for (int i = 0; i < BURST_SIZE; i++) {
      condition.signal();
    }

    while (!burstCompleteField.get()) {
      // spin
    }
  }

  @State(Scope.Thread)
  public static class ThreadContext {
    Integer threadId;
    AtomicBoolean burstCompleteField;
    ActorCondition condition;

    @Setup
    public void setup(final BenchmarkContext benchmarkContext) {
      threadId = THREAD_ID.getAndIncrement();
      burstCompleteField = benchmarkContext.burstCompleteFields[threadId];
      condition = benchmarkContext.conditions[threadId];
    }
  }

  @State(Scope.Benchmark)
  public static class BenchmarkContext {
    ActorScheduler scheduler =
        ActorScheduler.newActorScheduler()
            .setIoBoundActorThreadCount(0)
            .setCpuBoundActorThreadCount(2)
            .build();
    ConditionalActor actor1 = new ConditionalActor(0);
    ConditionalActor actor2 = new ConditionalActor(1);
    AtomicBoolean[] burstCompleteFields = new AtomicBoolean[2];
    ActorCondition[] conditions = new ActorCondition[2];

    @Setup
    public void setUp() {
      for (int i = 0; i < burstCompleteFields.length; i++) {
        burstCompleteFields[i] = new AtomicBoolean(false);
      }

      scheduler.start();
      scheduler.submitActor(actor1).join();
      scheduler.submitActor(actor2).join();
    }

    @TearDown
    public void tearDown() throws InterruptedException, ExecutionException, TimeoutException {
      actor1.close().join();
      actor2.close().join();
      scheduler.stop().get(2, TimeUnit.SECONDS);
    }

    class ConditionalActor extends Actor {
      private int threadId;
      private int counter;

      ConditionalActor(int condutionId) {
        this.threadId = condutionId;
      }

      @Override
      protected void onActorStarting() {
        conditions[threadId] =
            actor.onCondition(
                "test",
                () -> {
                  if (++counter >= BURST_SIZE) {
                    burstCompleteFields[threadId].set(true);
                  }
                });
      }

      public ActorFuture<Void> close() {
        return actor.close();
      }
    }
  }
}
