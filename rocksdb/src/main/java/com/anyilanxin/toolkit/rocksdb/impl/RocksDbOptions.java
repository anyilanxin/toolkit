/*
 * Copyright © 2025 anyilanxin zxh(anyilanxin@aliyun.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.anyilanxin.toolkit.rocksdb.impl;

import org.rocksdb.ColumnFamilyOptions;
import org.rocksdb.DBOptions;

/**
 * RocksDB has separate options for the database and the column families. Zeebe configuration can
 * change these depending on its own configuration. As each of the parts must be individually
 * closed, this record allows easily passing both configurations around within Zeebe.
 *
 * <p>While each column family in RocksDB can be configured differently, Zeebe only uses a single
 * RocksDB column family. We therefore don't have to differentiate further than a single database
 * options and a single column family options.
 *
 * @param dbOptions The database options used to open the RocksDB database
 * @param cfOptions The column family options used to open the RocksDB database
 */
public record RocksDbOptions(DBOptions dbOptions, ColumnFamilyOptions cfOptions) {}
