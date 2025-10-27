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
package com.anyilanxin.toolkit.msgpack;

import com.anyilanxin.toolkit.msgpack.POJO.POJOEnum;
import com.anyilanxin.toolkit.msgpack.property.*;
import org.agrona.DirectBuffer;

public class AllTypesDefaultValuesPOJO extends UnpackedObject {

  private final EnumProperty<POJOEnum> enumProp;
  private final LongProperty longProp;
  private final IntegerProperty intProp;
  private final StringProperty stringProp;
  private final PackedProperty packedProp;
  private final BinaryProperty binaryProp;
  private final ObjectProperty<POJONested> objectProp;

  public AllTypesDefaultValuesPOJO(
          final POJOEnum enumDefault,
          final long longDefault,
          final int intDefault,
          final String stringDefault,
          final DirectBuffer packedDefault,
          final DirectBuffer binaryDefault,
          final POJONested objectDefault) {
    enumProp = new EnumProperty<>("enumProp", POJOEnum.class, enumDefault);
    longProp = new LongProperty("longProp", longDefault);
    intProp = new IntegerProperty("intProp", intDefault);
    stringProp = new StringProperty("stringProp", stringDefault);
    packedProp = new PackedProperty("packedProp", packedDefault);
    binaryProp = new BinaryProperty("binaryProp", binaryDefault);
    objectProp = new ObjectProperty<>("objectProp", objectDefault);

      declareProperty(enumProp)
        .declareProperty(longProp)
        .declareProperty(intProp)
        .declareProperty(stringProp)
        .declareProperty(packedProp)
        .declareProperty(binaryProp)
        .declareProperty(objectProp);
  }

  public POJOEnum getEnum() {
    return enumProp.getValue();
  }

  public long getLong() {
    return longProp.getValue();
  }

  public int getInt() {
    return intProp.getValue();
  }

  public DirectBuffer getString() {
    return stringProp.getValue();
  }

  public DirectBuffer getPacked() {
    return packedProp.getValue();
  }

  public DirectBuffer getBinary() {
    return binaryProp.getValue();
  }

  public POJONested getNestedObject() {
    return objectProp.getValue();
  }
}
