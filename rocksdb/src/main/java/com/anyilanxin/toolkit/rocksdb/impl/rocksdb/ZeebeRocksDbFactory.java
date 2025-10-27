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
package com.anyilanxin.toolkit.rocksdb.impl.rocksdb;

import com.anyilanxin.toolkit.rocksdb.ZeebeDbFactory;
import com.anyilanxin.toolkit.rocksdb.impl.rocksdb.transaction.ZeebeTransactionDb;
import org.rocksdb.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ZeebeRocksDbFactory<ColumnFamilyType extends Enum<ColumnFamilyType>>
    implements ZeebeDbFactory<ColumnFamilyType> {

  static {
    RocksDB.loadLibrary();
  }

  private final Class<ColumnFamilyType> columnFamilyTypeClass;

  private ZeebeRocksDbFactory(final Class<ColumnFamilyType> columnFamilyTypeClass) {
    this.columnFamilyTypeClass = columnFamilyTypeClass;
  }

  public static <ColumnFamilyType extends Enum<ColumnFamilyType>>
      ZeebeDbFactory<ColumnFamilyType> newFactory(
          final Class<ColumnFamilyType> columnFamilyTypeClass) {
    return new ZeebeRocksDbFactory(columnFamilyTypeClass);
  }

  @Override
  public ZeebeTransactionDb<ColumnFamilyType> createDb(final File pathName) {
    return open(
        pathName,
        Arrays.stream(columnFamilyTypeClass.getEnumConstants())
            .map(c -> c.name().toLowerCase().getBytes())
            .collect(Collectors.toList()));
  }

  protected ZeebeTransactionDb<ColumnFamilyType> open(
      final File dbDirectory, final List<byte[]> columnFamilyNames) {

    final ZeebeTransactionDb<ColumnFamilyType> db;
    try {
      final List<AutoCloseable> closeables = new ArrayList<>();

      // column family options have to be closed as last
      final ColumnFamilyOptions columnFamilyOptions = createColumnFamilyOptions();
      closeables.add(columnFamilyOptions);

      final List<ColumnFamilyDescriptor> columnFamilyDescriptors =
          createFamilyDescriptors(columnFamilyNames, columnFamilyOptions);
      final DBOptions dbOptions =
          new DBOptions()
              .setCreateMissingColumnFamilies(true)
              .setErrorIfExists(false)
              .setCreateIfMissing(true)
              .setParanoidChecks(true);
      closeables.add(dbOptions);

      db =
          ZeebeTransactionDb.openTransactionalDb(
              dbOptions,
              dbDirectory.getAbsolutePath(),
              columnFamilyDescriptors,
              closeables,
              columnFamilyTypeClass);

    } catch (final RocksDBException e) {
      throw new RuntimeException("Unexpected error occurred trying to open the database", e);
    }
    return db;
  }

  private List<ColumnFamilyDescriptor> createFamilyDescriptors(
      final List<byte[]> columnFamilyNames, final ColumnFamilyOptions columnFamilyOptions) {
    final List<ColumnFamilyDescriptor> columnFamilyDescriptors = new ArrayList<>();

    if (columnFamilyNames != null && columnFamilyNames.size() > 0) {
      for (final byte[] name : columnFamilyNames) {
        final ColumnFamilyDescriptor columnFamilyDescriptor =
            new ColumnFamilyDescriptor(name, columnFamilyOptions);
        columnFamilyDescriptors.add(columnFamilyDescriptor);
      }
    }
    return columnFamilyDescriptors;
  }

  private static ColumnFamilyOptions createColumnFamilyOptions() {
    // Options which are used on all column families
    return new ColumnFamilyOptions()
        .setCompactionPriority(CompactionPriority.OldestSmallestSeqFirst);
  }
}
