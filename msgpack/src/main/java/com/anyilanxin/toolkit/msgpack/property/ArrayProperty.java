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
package com.anyilanxin.toolkit.msgpack.property;

import com.anyilanxin.toolkit.msgpack.MsgpackPropertyException;
import com.anyilanxin.toolkit.msgpack.value.ArrayValue;
import com.anyilanxin.toolkit.msgpack.value.BaseValue;
import com.anyilanxin.toolkit.msgpack.value.ValueArray;

import java.util.Iterator;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public final class ArrayProperty<T extends BaseValue> extends BaseProperty<ArrayValue<T>>
        implements ValueArray<T> {
    public ArrayProperty(final String keyString, final Supplier<T> innerValueFactory) {
        super(keyString, new ArrayValue<>(innerValueFactory));
        isSet = true;
    }

    @Override
    public void reset() {
        super.reset();
        isSet = true;
    }

    @Override
    public Iterator<T> iterator() {
        return resolveValue().iterator();
    }

    @Override
    public T add() {
        try {
            return value.add();
        } catch (final Exception e) {
            throw new MsgpackPropertyException(getKey(), e);
        }
    }

    @Override
    public T add(final int index) {
        try {
            return value.add(index);
        } catch (final Exception e) {
            throw new MsgpackPropertyException(getKey(), e);
        }
    }

    @Override
    public Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }

    public boolean isEmpty() {
        return value.isEmpty();
    }

    public int size() {
        return value.size();
    }
}
