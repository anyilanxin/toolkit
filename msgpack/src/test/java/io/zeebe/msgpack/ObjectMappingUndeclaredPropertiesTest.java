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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Map;

import static io.zeebe.msgpack.MsgPackUtil.asMap;
import static io.zeebe.msgpack.MsgPackUtil.encodeMsgPack;
import static io.zeebe.util.buffer.BufferUtil.wrapString;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

public class ObjectMappingUndeclaredPropertiesTest {
  protected static final DirectBuffer MSG_PACK =
      encodeMsgPack(
          (w) -> {
            w.writeMapHeader(2);
            w.writeString(wrapString("longProp"));
            w.writeInteger(123L);
            w.writeString(wrapString("undeclaredProp"));
            w.writeInteger(456L);
          });

  @Rule public ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldDeserializePOJOWithUndeclaredProperties() {
    // given
    final MinimalPOJO pojo = new MinimalPOJO();

    // when
    pojo.wrap(MSG_PACK);

    // then
    assertThat(pojo.getLongProp()).isEqualTo(123L);
  }

  @Test
  public void shouldIncludeUndeclaredPropertiesInLengthEstimation() {
    // given
    final MinimalPOJO pojo = new MinimalPOJO();
    pojo.wrap(MSG_PACK);

    // when
    final long writeLength = pojo.getLength();

    // then
    assertThat(writeLength).isEqualTo(MSG_PACK.capacity());
  }

  @Test
  public void shouldSerializeUndeclaredProperties() {
    // given
    final MinimalPOJO pojo = new MinimalPOJO();
    pojo.wrap(MSG_PACK);

    final MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[pojo.getLength()]);

    // when
    pojo.write(writeBuffer, 0);

    // then
    final Map<String, Object> serialized = asMap(writeBuffer, 0, writeBuffer.capacity());

    assertThat(serialized).hasSize(2);
    assertThat(serialized).contains(entry("longProp", 123L), entry("undeclaredProp", 456L));
  }

  @Test
  public void shouldDropUndeclaredPropertiesOnReset() {
    // given
    final MinimalPOJO pojo = new MinimalPOJO();
    pojo.wrap(MSG_PACK);

    final MutableDirectBuffer writeBuffer = new UnsafeBuffer(new byte[pojo.getLength()]);

    // when
    pojo.reset();

    // then
    pojo.wrap(
        encodeMsgPack(
            (w) -> {
              w.writeMapHeader(1);
              w.writeString(wrapString("longProp"));
              w.writeInteger(123L);
            }));
    pojo.write(writeBuffer, 0);

    final Map<String, Object> serialized = asMap(writeBuffer, 0, writeBuffer.capacity());
    assertThat(serialized).containsExactly(entry("longProp", 123L));
  }

  @Test
  public void shouldFailReadingInvalidUndeclaredProperty() {
    // given
    final MinimalPOJO pojo = new MinimalPOJO();

    final MutableDirectBuffer msgPack =
        encodeMsgPack(
            (w) -> {
              w.writeMapHeader(2);
              w.writeString(wrapString("longProp"));
              w.writeInteger(123L);
              w.writeInteger(789L);
              w.writeInteger(123L);
            });

    // then
    exception.expect(RuntimeException.class);
    exception.expectMessage("Could not deserialize object");

    // when
    pojo.wrap(msgPack);
  }
}
