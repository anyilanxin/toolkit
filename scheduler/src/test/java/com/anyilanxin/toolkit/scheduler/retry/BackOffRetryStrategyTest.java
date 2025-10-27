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
package com.anyilanxin.toolkit.scheduler.retry;

import com.anyilanxin.toolkit.scheduler.Actor;
import com.anyilanxin.toolkit.scheduler.ActorControl;
import com.anyilanxin.toolkit.scheduler.clock.ActorClock;
import com.anyilanxin.toolkit.scheduler.future.ActorFuture;
import com.anyilanxin.toolkit.scheduler.testing.ControlledActorSchedulerRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class BackOffRetryStrategyTest {

  @Rule public ControlledActorSchedulerRule schedulerRule = new ControlledActorSchedulerRule();

  private BackOffRetryStrategy retryStrategy;
  private ActorControl actorControl;
  private ActorFuture<Boolean> resultFuture;

  @Before
  public void setUp() {
    final ControllableActor actor = new ControllableActor();
      actorControl = actor.getActor();
    retryStrategy = new BackOffRetryStrategy(actorControl, Duration.ofSeconds(10));

    schedulerRule.submitActor(actor);
  }

  @Test
  public void shouldRunWithoutDelay() throws Exception {
    // given
    final List<Long> callRecorder = new ArrayList<>();

    // when
    final long startTime = schedulerRule.getClock().getCurrentTimeInMillis();
    actorControl.run(
        () -> {
          resultFuture =
              retryStrategy.runWithRetry(
                  () -> {
                    callRecorder.add(ActorClock.current().getTimeMillis());
                    return true;
                  });
        });

    schedulerRule.workUntilDone();

    // then
    assertThat(callRecorder.size()).isEqualTo(1);
    assertThat(resultFuture.isDone()).isTrue();
    assertThat(resultFuture.get()).isTrue();

    assertThat(callRecorder.get(0) - startTime).isLessThan(500);
  }

  @Test
  public void shouldRunWithBackOff() throws Exception {
    // given
    final AtomicInteger count = new AtomicInteger(0);
    final List<Long> callRecorder = new ArrayList<>();

    // when
    actorControl.run(
        () -> {
          resultFuture =
              retryStrategy.runWithRetry(
                  () -> {
                    callRecorder.add(ActorClock.current().getTimeMillis());
                    return count.incrementAndGet() == 10;
                  });
        });

    while (count.get() != 10) {
      schedulerRule.workUntilDone();
      schedulerRule.getClock().addTime(Duration.ofSeconds(1));
    }

    // then
    assertThat(count.get()).isEqualTo(10);
    assertThat(resultFuture.isDone()).isTrue();
    assertThat(resultFuture.get()).isTrue();

    final long beforeLastCall = callRecorder.get(callRecorder.size() - 2);
    final long lastCall = callRecorder.get(callRecorder.size() - 1);
    assertThat(lastCall - beforeLastCall).isBetween(10_000L, 10_500L);
  }

  @Test
  public void shouldStopWhenAbortConditionReturnsTrue() throws Exception {
    // given

    // when
    actorControl.run(
        () -> {
          resultFuture = retryStrategy.runWithRetry(() -> false, () -> true);
        });

    schedulerRule.workUntilDone();

    // then
    assertThat(resultFuture.isDone()).isTrue();
    assertThat(resultFuture.get()).isFalse();
  }

  @Test
  public void shouldRetryOnExceptionAndAbortWhenConditionReturnsTrue() throws Exception {
    // given
    final AtomicInteger count = new AtomicInteger(0);

    // when
    actorControl.run(
        () -> {
          resultFuture =
              retryStrategy.runWithRetry(
                  () -> {
                    throw new RuntimeException();
                  },
                  () -> count.incrementAndGet() == 2);
        });

    schedulerRule.workUntilDone();

    // then
    assertThat(count.get()).isEqualTo(1);
    assertThat(resultFuture.isDone()).isFalse();

    schedulerRule.getClock().addTime(Duration.ofSeconds(2));
    schedulerRule.workUntilDone();

    // then
    assertThat(count.get()).isEqualTo(2);
    assertThat(resultFuture.isDone()).isTrue();
    assertThat(resultFuture.get()).isFalse();
  }

  @Test
  public void shouldRetryOnExceptionWithMaxBackOff() throws Exception {
    // given
    final AtomicInteger count = new AtomicInteger(0);
    final AtomicBoolean toggle = new AtomicBoolean(false);
    final List<Long> callRecorder = new ArrayList<>();

    // when
    actorControl.run(
        () -> {
          resultFuture =
              retryStrategy.runWithRetry(
                  () -> {
                    callRecorder.add(ActorClock.current().getTimeMillis());
                    toggle.set(!toggle.get());
                    if (toggle.get()) {
                      throw new RuntimeException("expected");
                    }
                    return count.incrementAndGet() == 10;
                  });
        });

    while (count.get() != 10) {
      schedulerRule.workUntilDone();
      schedulerRule.getClock().addTime(Duration.ofSeconds(1));
    }

    // then
    assertThat(count.get()).isEqualTo(10);
    assertThat(resultFuture.isDone()).isTrue();
    assertThat(resultFuture.get()).isTrue();

    final long beforeLastCall = callRecorder.get(callRecorder.size() - 2);
    final long lastCall = callRecorder.get(callRecorder.size() - 1);
    assertThat(lastCall - beforeLastCall).isBetween(10_000L, 10_500L);
  }

  private final class ControllableActor extends Actor {
    public ActorControl getActor() {
      return actor;
    }
  }
}
