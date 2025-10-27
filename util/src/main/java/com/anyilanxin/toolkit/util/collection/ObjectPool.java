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
package com.anyilanxin.toolkit.util.collection;

import java.util.function.Function;
import org.agrona.concurrent.ManyToManyConcurrentArrayQueue;

public class ObjectPool<T> {
  protected ManyToManyConcurrentArrayQueue<T> queue;

  public ObjectPool(final int capacity, final Function<ObjectPool<T>, T> objectFactory) {
    queue = new ManyToManyConcurrentArrayQueue<>(capacity);

    for (int i = 0; i < capacity; i++) {
      queue.add(objectFactory.apply(this));
    }
  }

  public void returnObject(final T pooledFuture) {
    queue.add(pooledFuture);
  }

  public T request() {
    return queue.poll();
  }
}
