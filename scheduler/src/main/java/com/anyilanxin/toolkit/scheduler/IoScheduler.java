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
package com.anyilanxin.toolkit.scheduler;

import com.anyilanxin.toolkit.scheduler.clock.ActorClock;

/**
 * TaskScheduler implementation of IoActors. Currently, this implementation does nothing special. In
 * the future, more sophisticated logic can be implemented such as limiting concurrency by Io Device
 * or similar schemes.
 */
public class IoScheduler implements TaskScheduler {

  private final MultiLevelWorkstealingGroup tasks;

  public IoScheduler(final MultiLevelWorkstealingGroup tasks) {
    this.tasks = tasks;
  }

  @Override
  public ActorTask getNextTask(final ActorClock now) {
    return tasks.getNextTask(0);
  }
}
