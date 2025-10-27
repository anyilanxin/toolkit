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
package com.anyilanxin.toolkit.msgpack.value;

import com.anyilanxin.toolkit.msgpack.spec.MsgPackReader;
import com.anyilanxin.toolkit.msgpack.spec.MsgPackWriter;
import org.agrona.collections.CollectionUtil;

import java.util.*;
import java.util.function.Supplier;

public final class ArrayValue<T extends BaseValue> extends BaseValue
        implements Iterable<T>, RandomAccess {
    private final List<T> items;
    private final Supplier<T> valueFactory;

    public ArrayValue(final Supplier<T> valueFactory) {
        this.valueFactory = valueFactory;

        items = new ArrayList<>();
    }

    @Override
    public void reset() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    @Override
    public void writeJSON(final StringBuilder builder) {
        builder.append("[");

        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                builder.append(",");
            }

            items.get(i).writeJSON(builder);
        }

        builder.append("]");
    }

    @Override
    public void write(final MsgPackWriter writer) {
        writer.writeArrayHeader(items.size());
        for (final T item : items) {
            item.write(writer);
        }
    }

    @Override
    public void read(final MsgPackReader reader) {
        reset();

        final var size = reader.readArrayHeader();
        for (int i = 0; i < size; i++) {
            final var value = valueFactory.get();
            value.read(reader);
            items.add(i, value);
        }
    }

    @Override
    public int getEncodedLength() {
        return MsgPackWriter.getEncodedArrayHeaderLenght(items.size())
                + CollectionUtil.sum(items, BaseValue::getEncodedLength);
    }

    @Override
    public Iterator<T> iterator() {
        return items.iterator();
    }

    @Override
    public int hashCode() {
        return Objects.hash(items);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof final ArrayValue<?> that)) {
            return false;
        }

        return items.equals(that.items);
    }

    public T add() {
        final var item = valueFactory.get();
        items.add(item);

        return item;
    }

    public T add(final int index) {
        final var item = valueFactory.get();
        items.add(index, item);
        return item;
    }

    public T get(final int index) {
        return items.get(index);
    }

    public T remove(final int index) {
        return items.remove(index);
    }

    public int size() {
        return items.size();
    }
}
