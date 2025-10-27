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
package com.anyilanxin.toolkit.scheduler.lifecycle;

import com.anyilanxin.toolkit.scheduler.future.ActorFuture;
import com.anyilanxin.toolkit.scheduler.testing.ControlledActorSchedulerRule;
import com.anyilanxin.toolkit.util.TestUtil;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static com.anyilanxin.toolkit.scheduler.ActorTask.ActorLifecyclePhase.*;
import static com.anyilanxin.toolkit.scheduler.lifecycle.LifecycleRecordingActor.FULL_LIFECYCLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;

public class ActorLifecyclePhasesTest {
  @Rule
  public final ControlledActorSchedulerRule schedulerRule = new ControlledActorSchedulerRule();

  @Test
  public void shouldStartActor() {
    // given
    final LifecycleRecordingActor actor = new LifecycleRecordingActor();

    // when
    final ActorFuture<Void> startedFuture = schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // then
    assertThat(startedFuture).isDone();
    assertThat(actor.phases).isEqualTo(newArrayList(STARTING, STARTED));
  }

  @Test
  public void shouldCloseActor() {
    // given
    final LifecycleRecordingActor actor = new LifecycleRecordingActor();
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    final ActorFuture<Void> closeFuture = actor.close();
    schedulerRule.workUntilDone();

    // then
    assertThat(closeFuture).isDone();
    assertThat(actor.phases).isEqualTo(FULL_LIFECYCLE);
  }

  @Test
  public void shouldDoFullLifecycleIfClosedConcurrently() {
    // given
    final LifecycleRecordingActor actor = new LifecycleRecordingActor();
    schedulerRule.submitActor(actor);

    // when
    final ActorFuture<Void> closeFuture = actor.close(); // request close before doing work
    schedulerRule.workUntilDone();

    // then
    assertThat(closeFuture).isDone();
    assertThat(actor.phases).isEqualTo(FULL_LIFECYCLE);
  }

  @Test
  public void shouldCloseOnFailureWhileActorStarting() {
    // given
    final RuntimeException failure = new RuntimeException("foo");

    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            super.onActorStarting();

            throw failure;
          }
        };

    // when
    final ActorFuture<Void> startedFuture = schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // then
    assertThat(startedFuture.isCompletedExceptionally()).isTrue();
    assertThat(startedFuture.getException()).isEqualTo(failure);
    assertThat(actor.phases).isEqualTo(newArrayList(STARTING));
  }

  @Test
  public void shouldCloseOnFailureWhileActorClosing() {
    // given
    final RuntimeException failure = new RuntimeException("foo");

    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorClosing() {
            super.onActorClosing();

            throw failure;
          }
        };

    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    final ActorFuture<Void> closeFuture = actor.close();
    schedulerRule.workUntilDone();

    // then
    assertThat(closeFuture.isCompletedExceptionally()).isTrue();
    assertThat(closeFuture.getException()).isEqualTo(failure);
    assertThat(actor.phases).isEqualTo(newArrayList(STARTING, STARTED, CLOSE_REQUESTED, CLOSING));
  }

  @Test
  public void shouldPropagateFailureWhileActorStartingAndRun() {
    // given
    final RuntimeException failure = new RuntimeException("foo");

    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            super.onActorStarting();

              actor.run(
                () -> {
                  throw failure;
                });
          }
        };

    // when
    final ActorFuture<Void> startedFuture = schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // then
    assertThat(startedFuture.isCompletedExceptionally()).isTrue();
    assertThat(startedFuture.getException()).isEqualTo(failure);
    assertThat(actor.phases).isEqualTo(newArrayList(STARTING));
  }

  @Test
  public void shouldPropagateFailureWhileActorClosingAndRun() {
    // given
    final RuntimeException failure = new RuntimeException("foo");

    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorClosing() {
            super.onActorClosing();

              actor.run(
                () -> {
                  throw failure;
                });
          }
        };

    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    final ActorFuture<Void> closeFuture = actor.close();
    schedulerRule.workUntilDone();

    // then
    assertThat(closeFuture.isCompletedExceptionally()).isTrue();
    assertThat(closeFuture.getException()).isEqualTo(failure);
    assertThat(actor.phases).isEqualTo(newArrayList(STARTING, STARTED, CLOSE_REQUESTED, CLOSING));
  }

  @Test
  public void shouldDiscardJobsOnFailureWhileActorStarting() {
    // given
    final RuntimeException failure = new RuntimeException("foo");
    final AtomicBoolean isInvoked = new AtomicBoolean();

    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            super.onActorStarting();

              actor.run(() -> isInvoked.set(true));

            throw failure;
          }
        };

    // when
    final ActorFuture<Void> startedFuture = schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // then
    assertThat(startedFuture.isCompletedExceptionally()).isTrue();
    assertThat(isInvoked).isFalse();
  }

  @Test
  public void shouldNotCloseOnFailureWhileActorStarted() {
    // given
    final AtomicInteger invocations = new AtomicInteger();

    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarted() {
            super.onActorStarted();

              actor.runUntilDone(
                () -> {
                  final int inv = invocations.getAndIncrement();

                  if (inv == 0) {
                    throw new RuntimeException("foo");
                  } else if (inv == 10) {
                    actor.done();
                  }
                });
          }
        };

    // when
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    TestUtil.waitUntil(() -> invocations.get() >= 10);

    actor.close();
    schedulerRule.workUntilDone();

    // then
    assertThat(actor.phases).isEqualTo(FULL_LIFECYCLE);
  }
}
