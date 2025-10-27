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

import io.zeebe.msgpack.property.StringProperty;
import io.zeebe.msgpack.spec.MsgPackReader;
import io.zeebe.msgpack.spec.MsgPackWriter;
import io.zeebe.msgpack.value.ArrayValue;
import io.zeebe.msgpack.value.BaseValue;
import io.zeebe.msgpack.value.IntegerValue;
import io.zeebe.msgpack.value.StringValue;
import io.zeebe.util.buffer.BufferUtil;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;

public class ArrayValueTest {

  private final MsgPackWriter writer = new MsgPackWriter();
  private final MsgPackReader reader = new MsgPackReader();

  private final ArrayValue<IntegerValue> array = new ArrayValue<>(new IntegerValue());

  @Rule public final ExpectedException exception = ExpectedException.none();

  @Test
  public void shouldAppendValues() {
    // when
    addIntValues(array, 1, 2, 3);

    // then
    encodeAndDecode(array);
    assertIntValues(array, 1, 2, 3);
  }

  @Test
  public void shouldAddValueAtBeginning() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    // reset iterator to append at beginning
    array.iterator();
    addIntValues(array, 4, 5, 6);

    // then
    encodeAndDecode(array);
    assertIntValues(array, 4, 5, 6, 1, 2, 3);
  }

  @Test
  public void shouldAddValueInBetween() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    addIntValues(array, 4, 5, 6);

    // then
    encodeAndDecode(array);
    assertIntValues(array, 1, 4, 5, 6, 2, 3);
  }

  @Test
  public void shouldAddValuesAtEndAfterRead() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    iterator.next();
    iterator.next();
    addIntValues(array, 4, 5, 6);

    // then
    encodeAndDecode(array);
    assertIntValues(array, 1, 2, 3, 4, 5, 6);
  }

  @Test
  public void shouldUpdateValues() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next().setValue(4);
    iterator.next().setValue(5);
    iterator.next().setValue(6);

    // then
    encodeAndDecode(array);
    assertIntValues(array, 4, 5, 6);
  }

  @Test
  public void shouldSerializeValuesAfterPartialRead() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    iterator.next();

    // then
    encodeAndDecode(array);
    assertIntValues(array, 1, 2, 3);
  }

  @Test
  public void shouldRemoveValueAtBeginning() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    iterator.remove();

    // then
    encodeAndDecode(array);
    assertIntValues(array, 2, 3);
  }

  @Test
  public void shouldRemoveValueInBetween() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    iterator.next();
    iterator.remove();

    // then
    encodeAndDecode(array);
    assertIntValues(array, 1, 3);
  }

  @Test
  public void shouldRemoveValueAtEnd() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    iterator.next();
    iterator.next();
    iterator.remove();

    // then
    encodeAndDecode(array);
    assertIntValues(array, 1, 2);
  }

  @Test
  public void shouldRemoveAllValues() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.remove();
    iterator.next();
    iterator.remove();

    // then
    encodeAndDecode(array);
    assertIntValues(array);
  }

  @Test
  public void shouldNotInvalidElementOnRemove() {
    // given
    addIntValues(array, 1, 2, 3);

    // when
    final Iterator<IntegerValue> iterator = array.iterator();
    final IntegerValue element = iterator.next();
    iterator.remove();

    // then
    assertThat(element.getValue()).isEqualTo(1);
    encodeAndDecode(array);
    assertIntValues(array, 2, 3);
  }

  @Test
  public void shouldUpdateWithSmallerValue() {
    // given
    final ArrayValue<StringValue> array = new ArrayValue<>(new StringValue());
    addStringValues(array, "foo", "bar", "baz");

    // when
    final Iterator<StringValue> iterator = array.iterator();
    StringValue element = iterator.next();
    element.wrap(BufferUtil.wrapString("a"));
    element = iterator.next();
    element.wrap(BufferUtil.wrapString("b"));
    element = iterator.next();
    element.wrap(BufferUtil.wrapString("c"));

    // then
    encodeAndDecode(array);
    assertStringValues(array, "a", "b", "c");
  }

  @Test
  public void shouldUpdateWithBiggerValue() {
    // given
    final ArrayValue<StringValue> array = new ArrayValue<>(new StringValue());
    addStringValues(array, "foo", "bar", "baz");

    // when
    final Iterator<StringValue> iterator = array.iterator();
    StringValue element = iterator.next();
    element.wrap(BufferUtil.wrapString("hello"));
    element = iterator.next();
    element.wrap(BufferUtil.wrapString("world"));
    element = iterator.next();
    element.wrap(BufferUtil.wrapString("friend"));

    // then
    encodeAndDecode(array);
    assertStringValues(array, "hello", "world", "friend");
  }

  @Test
  public void shouldIncreaseInternalBufferWhenAddingToEnd() {
    // given
    final int valueCount = 10_000;

    final Integer[] values =
        IntStream.iterate(0, (i) -> ++i)
            .limit(valueCount)
            .boxed()
            .collect(Collectors.toList())
            .toArray(new Integer[valueCount]);

    // when
    addIntValues(array, values);

    // then
    encodeAndDecode(array);
    assertIntValues(array, values);
  }

  @Test
  public void shouldIncreaseInternalBufferWhenAddingToBeginning() {
    // given
    final int valueCount = 10_000;
    final List<Integer> generatedList =
        IntStream.iterate(0, (i) -> ++i).limit(valueCount).boxed().collect(Collectors.toList());
    final List<Integer> reverseList = new ArrayList<>(generatedList);
    Collections.reverse(generatedList);

    final Integer[] values = generatedList.toArray(new Integer[valueCount]);

    // when
    for (final Integer value : values) {
      // reset cursor to first position
      array.iterator();
      array.add().setValue(value);
    }

    // then
    encodeAndDecode(array);

    final Integer[] resultValues = reverseList.toArray(new Integer[valueCount]);
    assertIntValues(array, resultValues);
  }

  @Test
  public void shouldSerializeUndeclaredProperties() {
    // given
    final ArrayValue<Foo> fooArray = new ArrayValue<>(new Foo());
    fooArray.add().setFoo("foo").setBar("bar");

    final DirectBuffer buffer = encode(fooArray);

    final ArrayValue<Bar> barArray = new ArrayValue<>(new Bar());

    // when
    decode(barArray, buffer);
    barArray.iterator().next().setBar("barbar");

    // then
    encodeAndDecode(barArray);

    final Bar element = barArray.iterator().next();
    assertThat(element.getBar()).isEqualTo("barbar");
  }

  @Test
  public void shouldThrowExceptionIfNoNextElementExists() {
    // then
    exception.expect(NoSuchElementException.class);
    exception.expectMessage("No more elements left");

    // when
    array.iterator().next();
  }

  @Test
  public void shouldThrowExceptionIfRemoveWithoutNext() {
    // then
    exception.expect(IllegalStateException.class);
    exception.expectMessage("No element available to remove, call next() before");

    // when
    array.iterator().remove();
  }

  @Test
  public void shouldThrowExceptionIfRemoveIsCalledTwice() {
    // given
    array.add().setValue(1);
    final Iterator<IntegerValue> iterator = array.iterator();

    iterator.next();
    iterator.remove();

    // then
    exception.expect(IllegalStateException.class);
    exception.expectMessage("No element available to remove, call next() before");

    // when
    iterator.remove();
  }

  @Test
  public void shouldWriteJson() {
    // given
    final ArrayValue<MinimalPOJO> array = new ArrayValue<>(new MinimalPOJO());
    array.add().setLongProp(1);
    array.add().setLongProp(2);
    array.add().setLongProp(3);

    final StringBuilder builder = new StringBuilder();

    // when
    array.writeJSON(builder);

    // then
    assertThat(builder.toString())
        .isEqualTo("[{\"longProp\":1},{\"longProp\":2},{\"longProp\":3}]");
  }

  // Helpers

  protected void addIntValues(final ArrayValue<IntegerValue> array, final Integer... values) {
    for (final Integer value : values) {
      array.add().setValue(value);
    }
  }

  protected void assertIntValues(final ArrayValue<IntegerValue> array, final Integer... expected) {
    final List<Integer> values =
        StreamSupport.stream(array.spliterator(), false)
            .map(IntegerValue::getValue)
            .collect(Collectors.toList());
    assertThat(values).containsExactly(expected);
  }

  protected void addStringValues(final ArrayValue<StringValue> array, final String... values) {
    for (final String value : values) {
      array.add().wrap(BufferUtil.wrapString(value));
    }
  }

  protected void assertStringValues(final ArrayValue<StringValue> array, final String... expected) {
    final List<String> values =
        StreamSupport.stream(array.spliterator(), false)
            .map(StringValue::getValue)
            .map(BufferUtil::bufferAsString)
            .collect(Collectors.toList());

    assertThat(values).containsExactly(expected);
  }

  protected void encodeAndDecode(final BaseValue value) {
    final DirectBuffer buffer = encode(value);
    decode(value, buffer);
  }

  protected DirectBuffer encode(final BaseValue value) {
    final int encodedLength = value.getEncodedLength();
    final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[encodedLength]);

    writer.wrap(buffer, 0);
    value.write(writer);

    return buffer;
  }

  protected void decode(final BaseValue value, final DirectBuffer buffer) {
    value.reset();

    reader.wrap(buffer, 0, buffer.capacity());
    value.read(reader);
  }

  class Foo extends UnpackedObject {

    private final StringProperty fooProp = new StringProperty("foo");
    private final StringProperty barProp = new StringProperty("bar");

    Foo() {
        declareProperty(fooProp).declareProperty(barProp);
    }

    public String getFoo() {
      return BufferUtil.bufferAsString(fooProp.getValue());
    }

    public Foo setFoo(final String foo) {
      fooProp.setValue(foo);
      return this;
    }

    public String getBar() {
      return BufferUtil.bufferAsString(barProp.getValue());
    }

    public Foo setBar(final String bar) {
      barProp.setValue(bar);
      return this;
    }
  }

  class Bar extends UnpackedObject {

    private final StringProperty barProp = new StringProperty("bar");

    Bar() {
        declareProperty(barProp);
    }

    public String getBar() {
      return BufferUtil.bufferAsString(barProp.getValue());
    }

    public Bar setBar(final String bar) {
      barProp.setValue(bar);
      return this;
    }
  }
}
