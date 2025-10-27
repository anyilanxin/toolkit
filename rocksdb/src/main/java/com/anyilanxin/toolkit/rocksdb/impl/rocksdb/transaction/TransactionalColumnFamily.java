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
package com.anyilanxin.toolkit.rocksdb.impl.rocksdb.transaction;

import com.anyilanxin.toolkit.rocksdb.*;
import org.agrona.DirectBuffer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

class TransactionalColumnFamily<
        ColumnFamilyNames extends Enum<ColumnFamilyNames>,
        KeyType extends DbKey,
        ValueType extends DbValue>
    implements ColumnFamily<KeyType, ValueType> {

  private final ZeebeTransactionDb<ColumnFamilyNames> transactionDb;
  private final long handle;

  private final DbContext context;

  private final ValueType valueInstance;
  private final KeyType keyInstance;

  TransactionalColumnFamily(
          final ZeebeTransactionDb<ColumnFamilyNames> transactionDb,
          final ColumnFamilyNames columnFamily,
          final DbContext context,
          final KeyType keyInstance,
          final ValueType valueInstance) {
    this.transactionDb = transactionDb;
    handle = this.transactionDb.getColumnFamilyHandle(columnFamily);
    this.context = context;
    this.keyInstance = keyInstance;
    this.valueInstance = valueInstance;
  }

  @Override
  public void put(final KeyType key, final ValueType value) {
    put(context, key, value);
  }

  @Override
  public void put(final DbContext context, final KeyType key, final ValueType value) {
    transactionDb.put(handle, context, key, value);
  }

  @Override
  public ValueType get(final KeyType key) {
    return get(context, key);
  }

    public ValueType get(final DbContext context, final KeyType key) {
    return get(context, key, valueInstance);
  }

  @Override
  public ValueType get(final DbContext context, final KeyType key, final ValueType value) {
    final DirectBuffer valueBuffer = transactionDb.get(handle, context, key);
    if (valueBuffer != null) {

      value.wrap(valueBuffer, 0, valueBuffer.capacity());
      return value;
    }
    return null;
  }

  @Override
  public void forEach(final Consumer<ValueType> consumer) {
    forEach(context, consumer);
  }

    public void forEach(final DbContext context, final Consumer<ValueType> consumer) {
    transactionDb.foreach(handle, context, valueInstance, consumer);
  }

  @Override
  public void forEach(final BiConsumer<KeyType, ValueType> consumer) {
    forEach(context, consumer);
  }

    public void forEach(final DbContext context, final BiConsumer<KeyType, ValueType> consumer) {
    transactionDb.foreach(handle, context, keyInstance, valueInstance, consumer);
  }

  @Override
  public void whileTrue(final KeyValuePairVisitor<KeyType, ValueType> visitor) {
    whileTrue(context, visitor);
  }

    public void whileTrue(final DbContext context, final KeyValuePairVisitor<KeyType, ValueType> visitor) {
    whileTrue(context, visitor, keyInstance, valueInstance);
  }

  @Override
  public void whileTrue(
          final DbContext context,
          final KeyValuePairVisitor<KeyType, ValueType> visitor,
          final KeyType key,
          final ValueType value) {
    transactionDb.whileTrue(handle, context, key, value, visitor);
  }

  @Override
  public void whileEqualPrefix(final DbKey keyPrefix, final BiConsumer<KeyType, ValueType> visitor) {
    whileEqualPrefix(context, keyPrefix, visitor);
  }

  public void whileEqualPrefix(
          final DbContext context, final DbKey keyPrefix, final BiConsumer<KeyType, ValueType> visitor) {
    transactionDb.whileEqualPrefix(handle, context, keyPrefix, keyInstance, valueInstance, visitor);
  }

  @Override
  public void whileEqualPrefix(final DbKey keyPrefix, final KeyValuePairVisitor<KeyType, ValueType> visitor) {
    whileEqualPrefix(context, keyPrefix, visitor);
  }

  public void whileEqualPrefix(
          final DbContext context, final DbKey keyPrefix, final KeyValuePairVisitor<KeyType, ValueType> visitor) {
    transactionDb.whileEqualPrefix(handle, context, keyPrefix, keyInstance, valueInstance, visitor);
  }

  @Override
  public void delete(final KeyType key) {
    delete(context, key);
  }

  @Override
  public void delete(final DbContext context, final KeyType key) {
    transactionDb.delete(handle, context, key);
  }

  @Override
  public boolean exists(final KeyType key) {
    return exists(context, key);
  }

    public boolean exists(final DbContext context, final KeyType key) {
    return transactionDb.exists(handle, context, key);
  }

  @Override
  public boolean isEmpty() {
    return isEmpty(context);
  }

  @Override
  public boolean isEmpty(final DbContext context) {
    return transactionDb.isEmpty(handle, context);
  }
}
