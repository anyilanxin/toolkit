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

import static org.rocksdb.Status.Code.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.EnumSet;
import org.rocksdb.*;
import org.rocksdb.Status.Code;

public final class RocksDbInternal {

  static final EnumSet<Code> RECOVERABLE_ERROR_CODES =
      EnumSet.of(Ok, Aborted, Expired, IOError, Busy, TimedOut, TryAgain, MergeInProgress);

  static Field nativeHandle;

  static MethodHandle putWithHandle;
  static MethodHandle getWithHandle;
  static MethodHandle removeWithHandle;

  static {
    RocksDB.loadLibrary();

    try {
      resolveInternalMethods();
    } catch (final Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  private static void resolveInternalMethods() throws NoSuchFieldException, NoSuchMethodException {
    nativeHandles();

    putWithHandle();
    getWithHandle();
    removeWithHandle();
  }

  private static void nativeHandles() throws NoSuchFieldException {
    nativeHandle = RocksObject.class.getDeclaredField("nativeHandle_");
    nativeHandle.setAccessible(true);
  }

  /*
   private native void put(final long handle, final byte[] key, final int keyOffset,
        final int keyLength, final byte[] value, final int valueOffset, final int valueLength,
        final long columnFamilyHandle, final boolean assumeTracked) throws RocksDBException;
  */
  private static void putWithHandle() throws NoSuchMethodException {
    final var method =
        Transaction.class.getDeclaredMethod(
            "put",
            Long.TYPE,
            byte[].class,
            Integer.TYPE,
            Integer.TYPE,
            byte[].class,
            Integer.TYPE,
            Integer.TYPE,
            Long.TYPE,
            Boolean.TYPE);
    method.setAccessible(true);
    try {
      putWithHandle = MethodHandles.lookup().unreflect(method);
    } catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   private native byte[] get(final long handle, final long readOptionsHandle, final byte[] key,
      final int keyOffset, final int keyLength, final long columnFamilyHandle)
      throws RocksDBException;
  */
  private static void getWithHandle() throws NoSuchMethodException {
    final var method =
        Transaction.class.getDeclaredMethod(
            "get", Long.TYPE, Long.TYPE, byte[].class, Integer.TYPE, Integer.TYPE, Long.TYPE);
    method.setAccessible(true);
    try {
      getWithHandle = MethodHandles.lookup().unreflect(method);
    } catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  private static void removeWithHandle() throws NoSuchMethodException {
    final var method =
        Transaction.class.getDeclaredMethod(
            "delete", Long.TYPE, byte[].class, Integer.TYPE, Long.TYPE, Boolean.TYPE);
    method.setAccessible(true);
    try {
      removeWithHandle = MethodHandles.lookup().unreflect(method);
    } catch (final IllegalAccessException e) {
      throw new RuntimeException(e);
    }
  }

  static boolean isRocksDbExceptionRecoverable(final RocksDBException rdbex) {
    final Status status = rdbex.getStatus();
    return RECOVERABLE_ERROR_CODES.contains(status.getCode());
  }
}
