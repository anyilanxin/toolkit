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

import com.anyilanxin.toolkit.scheduler.future.CompletableActorFuture;
import com.anyilanxin.toolkit.scheduler.testing.ControlledActorSchedulerRule;
import org.junit.Rule;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ActorLifecyclePhasesAndRunTest {
  @Rule
  public final ControlledActorSchedulerRule schedulerRule = new ControlledActorSchedulerRule();

  @Test
  public void shouldExecuteInternallySubmittedActionsInStartingPhase() throws Exception {
    // given
    final Runnable runnable = mock(Runnable.class);
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            actor.run(runnable);
            blockPhase();
          }
        };

    // when
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldExecuteInternallySubmittedActionsInStartingPhaseWhenInStartedPhase()
      throws Exception {
    // given
    final Runnable runnable = mock(Runnable.class);
    final CompletableActorFuture<Void> future = new CompletableActorFuture<>();
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            actor.run(runnable);
            blockPhase(future);
          }
        };
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    schedulerRule.workUntilDone();
    verify(runnable, times(1)).run();

    // when then
    future.complete(null);
    schedulerRule.workUntilDone();
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldExecuteInternallySubmittedActionsInStartedPhase() throws Exception {
    // given
    final Runnable runnable = mock(Runnable.class);
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarted() {
            actor.run(runnable);
          }
        };

    // when
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldExecuteInternallySubmittedActionsInCloseRequestedPhase() throws Exception {
    // given
    final Runnable runnable = mock(Runnable.class);
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorCloseRequested() {
            actor.run(runnable);
            blockPhase();
          }
        };

    // when
    schedulerRule.submitActor(actor);
    actor.close();
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldExecuteInternallySubmittedActionsInClosingPhase() throws Exception {
    // given
    final Runnable runnable = mock(Runnable.class);
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorClosing() {
            actor.run(runnable);
            blockPhase();
          }
        };

    // when
    schedulerRule.submitActor(actor);
    actor.close();
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldExecuteInternallySubmittedActionsInClosedPhase() throws Exception {
    // given
    final Runnable runnable = mock(Runnable.class);
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorClosed() {
            actor.run(runnable);
            blockPhase();
          }
        };

    // when
    schedulerRule.submitActor(actor);
    actor.close();
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldNotExecuteExternallySubmittedActionsInStartingPhase() throws Exception {
    // given
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            blockPhase();
          }
        };
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    final Runnable runnable = mock(Runnable.class);
    actor.control().run(runnable);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(0)).run();
  }

  @Test
  public void shouldExecuteExternallySubmittedActionsSubmittedInStartingPhaseWhenInStartedPhase()
      throws Exception {
    // given
    final CompletableActorFuture<Void> future = new CompletableActorFuture<>();
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorStarting() {
            blockPhase(future);
          }
        };
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    final Runnable runnable = mock(Runnable.class);
    actor.control().run(runnable);
    schedulerRule.workUntilDone();
    verify(runnable, times(0)).run();

    // when then
    future.complete(null);
    schedulerRule.workUntilDone();
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldExecuteExternallySubmittedActionsInStartedPhase() throws Exception {
    // given
    final LifecycleRecordingActor actor = new LifecycleRecordingActor();
    schedulerRule.submitActor(actor);
    schedulerRule.workUntilDone();

    // when
    final Runnable runnable = mock(Runnable.class);
    actor.control().run(runnable);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(1)).run();
  }

  @Test
  public void shouldNotExecuteExternallySubmittedActionsInCloseRequestedPhase() throws Exception {
    // given
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorCloseRequested() {
            blockPhase();
          }
        };
    schedulerRule.submitActor(actor);
    actor.close();
    schedulerRule.workUntilDone();

    // when
    final Runnable runnable = mock(Runnable.class);
    actor.control().run(runnable);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(0)).run();
  }

  @Test
  public void shouldNotExecuteExternallySubmittedActionsInClosingPhase() throws Exception {
    // given
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorClosing() {
            blockPhase();
          }
        };

    schedulerRule.submitActor(actor);
    actor.close();
    schedulerRule.workUntilDone();

    // when
    final Runnable runnable = mock(Runnable.class);
    actor.control().run(runnable);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(0)).run();
  }

  @Test
  public void shouldNotExecuteExternallySubmittedActionsInClosedPhase() throws Exception {
    // given
    final LifecycleRecordingActor actor =
        new LifecycleRecordingActor() {
          @Override
          public void onActorClosed() {
            blockPhase();
          }
        };

    schedulerRule.submitActor(actor);
    actor.close();
    schedulerRule.workUntilDone();

    // when
    final Runnable runnable = mock(Runnable.class);
    actor.control().run(runnable);
    schedulerRule.workUntilDone();

    // then
    verify(runnable, times(0)).run();
  }
}
