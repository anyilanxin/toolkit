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
package io.zeebe.util.sched.channel;

import io.zeebe.util.sched.ActorCondition;
import org.junit.Test;

import static org.mockito.Mockito.*;

public class ActorConditionsTest {

  @Test
  public void shouldAddCondition() {
    // given
    final ActorConditions actorConditions = new ActorConditions();

    // when
    final ActorCondition condition = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition);

    // then
    actorConditions.signalConsumers();
    verify(condition).signal();
  }

  @Test
  public void shouldAddConditions() {
    // given
    final ActorConditions actorConditions = new ActorConditions();

    // when
    final ActorCondition condition1 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition1);

    final ActorCondition condition2 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition2);

    final ActorCondition condition3 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition3);

    // then
    actorConditions.signalConsumers();
    verify(condition1).signal();
    verify(condition2).signal();
    verify(condition3).signal();
  }

  @Test
  public void shouldRemoveCondition() {
    // given
    final ActorConditions actorConditions = new ActorConditions();
    final ActorCondition condition = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition);

    // when
    actorConditions.removeConsumer(condition);

    // then
    actorConditions.signalConsumers();
    verify(condition, never()).signal();
  }

  @Test
  public void shouldRemoveNotRegisteredCondition() {
    // given
    final ActorConditions actorConditions = new ActorConditions();
    final ActorCondition condition = mock(ActorCondition.class);
    final ActorCondition notRegistered = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition);

    // when
    actorConditions.removeConsumer(notRegistered);

    // then
    actorConditions.signalConsumers();
    verify(condition).signal();
  }

  @Test
  public void shouldRemoveConditionInMiddle() {
    // given
    final ActorConditions actorConditions = new ActorConditions();
    final ActorCondition condition1 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition1);

    final ActorCondition condition2 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition2);

    final ActorCondition condition3 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition3);

    // when
    actorConditions.removeConsumer(condition2);

    // then
    actorConditions.signalConsumers();
    verify(condition1).signal();
    verify(condition2, never()).signal();
    verify(condition3).signal();
  }

  @Test
  public void shouldRemoveFirstCondition() {
    // given
    final ActorConditions actorConditions = new ActorConditions();
    final ActorCondition condition1 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition1);

    final ActorCondition condition2 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition2);

    final ActorCondition condition3 = mock(ActorCondition.class);
    actorConditions.registerConsumer(condition3);

    // when
    actorConditions.removeConsumer(condition1);

    // then
    actorConditions.signalConsumers();
    verify(condition1, never()).signal();
    verify(condition2).signal();
    verify(condition3).signal();
  }
}
