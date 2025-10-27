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
package com.anyilanxin.toolkit.util.buffer;

import org.agrona.DirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;

public class DirectBufferReader implements BufferReader {
  protected final UnsafeBuffer readBuffer = new UnsafeBuffer(0, 0);

  @Override
  public void wrap(final DirectBuffer buffer, final int offset, final int length) {
    readBuffer.wrap(buffer, offset, length);
  }

  public DirectBuffer getBuffer() {
    return readBuffer;
  }

  public byte[] byteArray() {
    final byte[] array = new byte[readBuffer.capacity()];

    readBuffer.getBytes(0, array);

    return array;
  }
}
