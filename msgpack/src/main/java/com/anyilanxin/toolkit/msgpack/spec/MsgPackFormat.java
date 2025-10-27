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
package com.anyilanxin.toolkit.msgpack.spec;

import static com.anyilanxin.toolkit.msgpack.spec.MsgPackCodes.*;
import static com.anyilanxin.toolkit.msgpack.spec.MsgPackType.*;

/** Describes the list of the message format types defined in the MessagePack specification. */
public enum MsgPackFormat {
  // INT7
  POSFIXINT(INTEGER),
  // MAP4
  FIXMAP(MAP),
  // ARRAY4
  FIXARRAY(ARRAY),
  // STR5
  FIXSTR(STRING),
  NIL(MsgPackType.NIL),
  NEVER_USED(MsgPackType.NEVER_USED),
  BOOLEAN(MsgPackType.BOOLEAN),
  BIN8(BINARY),
  BIN16(BINARY),
  BIN32(BINARY),
  EXT8(EXTENSION),
  EXT16(EXTENSION),
  EXT32(EXTENSION),
  FLOAT32(FLOAT),
  FLOAT64(FLOAT),
  UINT8(INTEGER),
  UINT16(INTEGER),
  UINT32(INTEGER),
  UINT64(INTEGER),

  INT8(INTEGER),
  INT16(INTEGER),
  INT32(INTEGER),
  INT64(INTEGER),
  FIXEXT1(EXTENSION),
  FIXEXT2(EXTENSION),
  FIXEXT4(EXTENSION),
  FIXEXT8(EXTENSION),
  FIXEXT16(EXTENSION),
  STR8(STRING),
  STR16(STRING),
  STR32(STRING),
  ARRAY16(ARRAY),
  ARRAY32(ARRAY),
  MAP16(MAP),
  MAP32(MAP),
  NEGFIXINT(INTEGER);

  final MsgPackType type;

  MsgPackFormat(final MsgPackType type) {
    this.type = type;
  }

  public MsgPackType getType() {
    return type;
  }

  private static final MsgPackFormat[] FORMAT_TABLE = new MsgPackFormat[256];

  static {
    // Preparing a look up table for converting byte values into MessageFormat types
    for (int b = 0; b <= 0xFF; ++b) {
      FORMAT_TABLE[b] = toMessageFormat((byte) b);
    }
  }

  /**
   * Returns a MessageFormat type of the specified byte value
   *
   * @param b MessageFormat of the given byte
   * @return
   */
  public static MsgPackFormat valueOf(final byte b) {
    return FORMAT_TABLE[b & 0xFF];
  }

  /**
   * Converting a byte value into MessageFormat. For faster performance, use {@link #valueOf}
   *
   * @param b MessageFormat of the given byte
   * @return
   */
  static MsgPackFormat toMessageFormat(final byte b) {
    if (isPosFixInt(b)) {
      return POSFIXINT;
    }
    if (isNegFixInt(b)) {
      return NEGFIXINT;
    }
    if (isFixStr(b)) {
      return FIXSTR;
    }
    if (isFixedArray(b)) {
      return FIXARRAY;
    }
    if (isFixedMap(b)) {
      return FIXMAP;
    }
    return switch (b) {
      case MsgPackCodes.NIL -> NIL;
      case MsgPackCodes.FALSE, MsgPackCodes.TRUE -> BOOLEAN;
      case MsgPackCodes.BIN8 -> BIN8;
      case MsgPackCodes.BIN16 -> BIN16;
      case MsgPackCodes.BIN32 -> BIN32;
      case MsgPackCodes.EXT8 -> EXT8;
      case MsgPackCodes.EXT16 -> EXT16;
      case MsgPackCodes.EXT32 -> EXT32;
      case MsgPackCodes.FLOAT32 -> FLOAT32;
      case MsgPackCodes.FLOAT64 -> FLOAT64;
      case MsgPackCodes.UINT8 -> UINT8;
      case MsgPackCodes.UINT16 -> UINT16;
      case MsgPackCodes.UINT32 -> UINT32;
      case MsgPackCodes.UINT64 -> UINT64;
      case MsgPackCodes.INT8 -> INT8;
      case MsgPackCodes.INT16 -> INT16;
      case MsgPackCodes.INT32 -> INT32;
      case MsgPackCodes.INT64 -> INT64;
      case MsgPackCodes.FIXEXT1 -> FIXEXT1;
      case MsgPackCodes.FIXEXT2 -> FIXEXT2;
      case MsgPackCodes.FIXEXT4 -> FIXEXT4;
      case MsgPackCodes.FIXEXT8 -> FIXEXT8;
      case MsgPackCodes.FIXEXT16 -> FIXEXT16;
      case MsgPackCodes.STR8 -> STR8;
      case MsgPackCodes.STR16 -> STR16;
      case MsgPackCodes.STR32 -> STR32;
      case MsgPackCodes.ARRAY16 -> ARRAY16;
      case MsgPackCodes.ARRAY32 -> ARRAY32;
      case MsgPackCodes.MAP16 -> MAP16;
      case MsgPackCodes.MAP32 -> MAP32;
      default -> NEVER_USED;
    };
  }
}
