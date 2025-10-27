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

import java.util.concurrent.Callable;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class RunnableAdapter<T> implements Runnable {
  private final Callable<T> callable;

  private T value;
  private Throwable exception;

  public RunnableAdapter(final Callable<T> callable) {
    this.callable = callable;
  }

  @Override
  public void run() {
    try {
      value = callable.call();
    } catch (final Exception e) {
      exception = e;
    }
  }

  public static <T> RunnableAdapter<T> wrapCallable(final Callable<T> callable) {
    return new RunnableAdapter<>(callable);
  }

  public static RunnableAdapter<Void> wrapRunnable(final Runnable callable) {
    return new RunnableAdapter<Void>(
        () -> {
          callable.run();
          return null;
        });
  }

  public Runnable wrapBiConsumer(final BiConsumer<T, Throwable> consumer) {
    return () -> {
      consumer.accept(value, exception);
    };
  }

  public Runnable wrapConsumer(final Consumer<Throwable> consumer) {
    return () -> {
      consumer.accept(exception);
    };
  }
}
