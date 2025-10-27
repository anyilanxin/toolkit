/*
 * Copyright © 2025 anyilanxin zxh (anyilanxin@aliyun.com)
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anyilanxin.toolkit.rocksdb.impl;

import com.anyilanxin.toolkit.rocksdb.*;
import com.anyilanxin.toolkit.rocksdb.impl.rocksdb.Loggers;
import java.io.File;
import java.util.Collections;
import java.util.List;
import org.agrona.CloseHelper;
import org.rocksdb.Checkpoint;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.slf4j.Logger;

/**
 * @author zxuanhong
 */
public final class SnapshotOnlyDb<ColumnFamilyType extends Enum<ColumnFamilyType>>
    implements ZeebeDb<ColumnFamilyType> {
  private static final Logger LOG = Loggers.DB_LOGGER;

  private final RocksDB db;
  private final List<AutoCloseable> managedResources;

  public SnapshotOnlyDb(final RocksDB db, final List<AutoCloseable> managedResources) {
    this.db = db;
    this.managedResources = managedResources;
  }

  @Override
  public <KeyType extends DbKey, ValueType extends DbValue>
      ColumnFamily<KeyType, ValueType> createColumnFamily(
          final ColumnFamilyType columnFamily,
          final DbContext context,
          final KeyType keyInstance,
          final ValueType valueInstance) {
    throw unsupported("createColumnFamily");
  }

  @Override
  public DbContext createContext() {
    throw unsupported("createContext");
  }

  @Override
  public void close() throws Exception {
    Collections.reverse(managedResources);
    CloseHelper.closeAll(
        error ->
            LOG.error("Failed to close RockDB resource, which may lead to leaked resources", error),
        managedResources);
  }

  @Override
  public void createSnapshot(final File snapshotDir) {
    try (final var checkpoint = Checkpoint.create(db)) {
      checkpoint.createCheckpoint(snapshotDir.getAbsolutePath());
    } catch (final RocksDBException e) {
      throw new ZeebeDbException(
          "Failed to take a RocksDB snapshot at '%s'".formatted(snapshotDir), e);
    }
  }

  private UnsupportedOperationException unsupported(final String operation) {
    return new UnsupportedOperationException(
        "Failed to execute 'ZeebeDb#%s'; this operation is not supported on a snapshot-only DB"
            .formatted(operation));
  }
}
