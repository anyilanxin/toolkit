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

public class IntTuple<R> {
  private int left;
  private R right;

  public IntTuple(final int left, final R right) {
    this.left = left;
    this.right = right;
  }

  public int getInt() {
    return left;
  }

  public R getRight() {
    return right;
  }

  public void setInt(final int left) {
    this.left = left;
  }

  public void setRight(final R right) {
    this.right = right;
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();
    builder.append("<");
    builder.append(left);
    builder.append(", ");
    builder.append(right);
    builder.append(">");
    return builder.toString();
  }
}
