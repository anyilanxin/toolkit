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
package io.zeebe.msgpack;

import io.zeebe.msgpack.property.BinaryProperty;
import io.zeebe.msgpack.property.EnumProperty;
import io.zeebe.msgpack.property.IntegerProperty;
import io.zeebe.msgpack.property.LongProperty;
import io.zeebe.msgpack.property.ObjectProperty;
import io.zeebe.msgpack.property.PackedProperty;
import io.zeebe.msgpack.property.StringProperty;
import org.agrona.DirectBuffer;

public class POJO extends UnpackedObject {

  private final EnumProperty<POJOEnum> enumProp = new EnumProperty<>("enumProp", POJOEnum.class);
  private final LongProperty longProp = new LongProperty("longProp");
  private final IntegerProperty intProp = new IntegerProperty("intProp");
  private final StringProperty stringProp = new StringProperty("stringProp");
  private final PackedProperty packedProp = new PackedProperty("packedProp");
  private final BinaryProperty binaryProp = new BinaryProperty("binaryProp");
  private final ObjectProperty<POJONested> objectProp =
      new ObjectProperty<>("objectProp", new POJONested());

  public POJO() {
    this.declareProperty(enumProp)
        .declareProperty(longProp)
        .declareProperty(intProp)
        .declareProperty(stringProp)
        .declareProperty(packedProp)
        .declareProperty(binaryProp)
        .declareProperty(objectProp);
  }

  public void setEnum(POJOEnum val) {
    this.enumProp.setValue(val);
  }

  public POJOEnum getEnum() {
    return this.enumProp.getValue();
  }

  public void setLong(long val) {
    this.longProp.setValue(val);
  }

  public long getLong() {
    return longProp.getValue();
  }

  public void setInt(int val) {
    this.intProp.setValue(val);
  }

  public int getInt() {
    return intProp.getValue();
  }

  public void setString(DirectBuffer buffer) {
    this.stringProp.setValue(buffer);
  }

  public DirectBuffer getString() {
    return stringProp.getValue();
  }

  public void setPacked(DirectBuffer buffer) {
    this.packedProp.setValue(buffer, 0, buffer.capacity());
  }

  public DirectBuffer getPacked() {
    return packedProp.getValue();
  }

  public void setBinary(DirectBuffer buffer) {
    this.binaryProp.setValue(buffer, 0, buffer.capacity());
  }

  public DirectBuffer getBinary() {
    return binaryProp.getValue();
  }

  public POJONested nestedObject() {
    return objectProp.getValue();
  }

  public enum POJOEnum {
    FOO,
    BAR;
  }
}
