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
package com.anyilanxin.toolkit.util.collection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Supplier;

/** An expendable list of reusable objects. */
public class ReusableObjectList<T extends Reusable> implements Iterable<T> {
  private final ObjectIterator iterator = new ObjectIterator();

  private final List<ReusableElement> elements;
  private final Supplier<T> elementFactory;

  private int size = 0;

  public ReusableObjectList(Supplier<T> elementFactory) {
    this.elements = new ArrayList<>();
    this.elementFactory = elementFactory;
  }

  public T add() {
    for (int i = 0; i < elements.size(); i++) {
      final ReusableElement element = elements.get(i);
      if (!element.isSet()) {
        element.set(true);

        size += 1;

        return element.getElement();
      }
    }

    // expend list
    final T newElement = elementFactory.get();

    elements.add(new ReusableElement(newElement));

    size += 1;

    return newElement;
  }

  public void remove(T element) {
    for (int i = 0; i < elements.size(); i++) {
      final ReusableElement e = elements.get(i);
      if (e.getElement() == element) {
        e.getElement().reset();
        e.set(false);

        size -= 1;
      }
    }
  }

  public T poll() {
    for (int i = 0; i < elements.size(); i++) {
      final ReusableElement element = elements.get(i);

      if (element.isSet()) {
        element.set(false);
        size -= 1;

        return element.getElement();
      }
    }
    return null;
  }

  public int size() {
    return size;
  }

  public void clear() {
    for (ReusableElement element : elements) {
      element.getElement().reset();
      element.set(false);
    }

    size = 0;
  }

  @Override
  public Iterator<T> iterator() {
    iterator.reset();

    return iterator;
  }

  private class ObjectIterator implements Iterator<T> {
    private ReusableElement current = null;

    private int index = 0;

    public void reset() {
      index = 0;
      current = null;
    }

    @Override
    public boolean hasNext() {
      for (int i = index; i < elements.size(); i++) {
        final ReusableElement element = elements.get(i);

        if (element.isSet()) {
          index = i;
          return true;
        }
      }

      return false;
    }

    @Override
    public T next() {
      if (hasNext()) {
        current = elements.get(index);

        index += 1;

        return current.getElement();
      } else {
        throw new NoSuchElementException();
      }
    }

    @Override
    public void remove() {
      if (current == null) {
        throw new IllegalStateException();
      }

      current.getElement().reset();
      current.set(false);

      size -= 1;
    }
  }

  private class ReusableElement {
    private final T element;

    private boolean isSet = true;

    ReusableElement(T element) {
      this.element = element;
    }

    public boolean isSet() {
      return isSet;
    }

    public void set(boolean isSet) {
      this.isSet = isSet;
    }

    public T getElement() {
      return element;
    }
  }
}
