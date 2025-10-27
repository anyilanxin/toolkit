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
import org.agrona.DeadlineTimerWheel;
import org.agrona.collections.Long2ObjectHashMap;

import java.util.concurrent.TimeUnit;

public class ActorTimerQueue extends DeadlineTimerWheel {
  private static final int DEFAULT_TICKS_PER_WHEEL = 32;
  private final Long2ObjectHashMap<TimerSubscription> timerJobMap = new Long2ObjectHashMap<>();

  private final TimerHandler timerHandler =
      new TimerHandler() {
        @Override
        public boolean onTimerExpiry(final TimeUnit timeUnit, final long now, final long timerId) {
          final TimerSubscription timer = timerJobMap.remove(timerId);

          if (timer != null) {
            timer.onTimerExpired(timeUnit, now);
          }

          return true;
        }
      };

    public ActorTimerQueue(final ActorClock clock) {
    this(clock, DEFAULT_TICKS_PER_WHEEL);
  }

    public ActorTimerQueue(final ActorClock clock, final int ticksPerWheel) {
    super(TimeUnit.MILLISECONDS, clock.getTimeMillis(), 1, ticksPerWheel);
  }

    public void processExpiredTimers(final ActorClock clock) {
    int timersProcessed = 0;

    do {
      timersProcessed = poll(clock.getTimeMillis(), timerHandler, Integer.MAX_VALUE);
    } while (timersProcessed > 0);
  }

    public void schedule(final TimerSubscription timer, final ActorClock now) {
    final long deadline =
        now.getTimeMillis() + timeUnit().convert(timer.getDeadline(), timer.getTimeUnit());

    final long timerId = scheduleTimer(deadline);
    timer.setTimerId(timerId);

    timerJobMap.put(timerId, timer);
  }

    public void remove(final TimerSubscription timer) {
    final long timerId = timer.getTimerId();

    timerJobMap.remove(timerId);
    cancelTimer(timerId);
  }
}
