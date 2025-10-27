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
package io.zeebe.db.impl;

import io.zeebe.db.DbValue;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public final class DbNil implements DbValue {

  public static final DbNil INSTANCE = new DbNil();

  private static final byte EXISTENCE_BYTE = (byte) -1;

  private DbNil() {}

  @Override
  public void wrap(final DirectBuffer directBuffer, final int offset, final int length) {
    // nothing to do
  }

  @Override
  public int getLength() {
    return Byte.BYTES;
  }

  @Override
  public void write(final MutableDirectBuffer mutableDirectBuffer, final int offset) {
    mutableDirectBuffer.putByte(offset, EXISTENCE_BYTE);
  }
}
