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
package com.anyilanxin.toolkit.rocksdb.impl;

import com.anyilanxin.toolkit.rocksdb.DbKey;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

public class DbCompositeKey<FirstKeyType extends DbKey, SecondKeyType extends DbKey>
    implements DbKey {

  private final FirstKeyType firstKeyTypePart;
  private final SecondKeyType secondKeyTypePart;

  public DbCompositeKey(
      final FirstKeyType firstKeyTypePart, final SecondKeyType secondKeyTypePart) {
    this.firstKeyTypePart = firstKeyTypePart;
    this.secondKeyTypePart = secondKeyTypePart;
  }

  public FirstKeyType getFirst() {
    return firstKeyTypePart;
  }

  public SecondKeyType getSecond() {
    return secondKeyTypePart;
  }

  @Override
  public void wrap(final DirectBuffer directBuffer, final int offset, final int length) {
    firstKeyTypePart.wrap(directBuffer, offset, length);
    final int firstKeyLength = firstKeyTypePart.getLength();
    secondKeyTypePart.wrap(directBuffer, offset + firstKeyLength, length - firstKeyLength);
  }

  @Override
  public int getLength() {
    return firstKeyTypePart.getLength() + secondKeyTypePart.getLength();
  }

  @Override
  public void write(final MutableDirectBuffer mutableDirectBuffer, final int offset) {
    firstKeyTypePart.write(mutableDirectBuffer, offset);
    final int firstKeyPartLength = firstKeyTypePart.getLength();
    secondKeyTypePart.write(mutableDirectBuffer, offset + firstKeyPartLength);
  }
}
