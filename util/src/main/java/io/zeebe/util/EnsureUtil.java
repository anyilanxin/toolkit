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
package io.zeebe.util;

import java.time.Duration;
import org.agrona.DirectBuffer;

public class EnsureUtil {

  public static void ensureNotNull(final String property, final Object o) {
    if (o == null) {
      throw new RuntimeException(property + " must not be null");
    }
  }

  public static void ensureNotEmpty(final String property, final String value) {
    if (value.isEmpty()) {
      throw new RuntimeException(property + " must not be empty");
    }
  }

  public static void ensureGreaterThan(
      final String property, final long testValue, final long comparisonValue) {
    if (testValue <= comparisonValue) {
      throw new RuntimeException(property + " must be greater than " + comparisonValue);
    }
  }

  public static void ensureGreaterThanOrEqual(
      final String property, final long testValue, final long comparisonValue) {
    if (testValue < comparisonValue) {
      throw new RuntimeException(property + " must be greater than or equal to " + comparisonValue);
    }
  }

  public static void ensureLessThan(
      final String property, final long testValue, final long comparisonValue) {
    if (testValue >= comparisonValue) {
      throw new RuntimeException(property + " must be less than " + comparisonValue);
    }
  }

  public static void ensureLessThanOrEqual(
      final String property, final long testValue, final long comparisonValue) {
    if (testValue > comparisonValue) {
      throw new RuntimeException(property + " must be less than or equal to " + comparisonValue);
    }
  }

  public static void ensureGreaterThan(
      final String property, final double testValue, final double comparisonValue) {
    if (testValue <= comparisonValue) {
      throw new RuntimeException(property + " must be greater than " + comparisonValue);
    }
  }

  public static void ensureGreaterThanOrEqual(
      final String property, final double testValue, final double comparisonValue) {
    if (testValue < comparisonValue) {
      throw new RuntimeException(property + " must be greater than or equal to " + comparisonValue);
    }
  }

  public static void ensureLessThan(
      final String property, final double testValue, final double comparisonValue) {
    if (testValue >= comparisonValue) {
      throw new RuntimeException(property + " must be less than " + comparisonValue);
    }
  }

  public static void ensureLessThanOrEqual(
      final String property, final double testValue, final double comparisonValue) {
    if (testValue > comparisonValue) {
      throw new RuntimeException(property + " must be less than or equal to " + comparisonValue);
    }
  }

  public static void ensureNotNullOrGreaterThan(
      final String property, final Duration testValue, final Duration comparisonValue) {
    ensureNotNull(property, testValue);

    if (testValue.compareTo(comparisonValue) <= 0) {
      throw new RuntimeException(property + " must be greater than " + comparisonValue);
    }
  }

  public static void ensureNotNullOrGreaterThanOrEqual(
      final String property, final Duration testValue, final Duration comparisonValue) {
    ensureNotNull(property, testValue);

    if (testValue.compareTo(comparisonValue) < 0) {
      throw new RuntimeException(property + " must be greater than or equal to " + comparisonValue);
    }
  }

  public static void ensureNotNullOrEmpty(final String property, final String testValue) {
    ensureNotNull(property, testValue);

    if (testValue.isEmpty()) {
      throw new RuntimeException(property + " must not be empty");
    }
  }

  public static void ensureNotNullOrEmpty(final String property, final byte[] testValue) {
    ensureNotNull(property, testValue);

    if (testValue.length == 0) {
      throw new RuntimeException(property + " must not be empty");
    }
  }

  public static void ensureNotNullOrEmpty(final String property, final DirectBuffer testValue) {
    ensureNotNull(property, testValue);

    if (testValue.capacity() == 0) {
      throw new RuntimeException(property + " must not be empty");
    }
  }

  public static void ensureAtLeastOneNotNull(final String property, final Object... values) {
    for (int i = 0; i < values.length; i++) {
      if (values[i] != null) {
        return;
      }
    }

    throw new RuntimeException(property + " must have at least one non-null value");
  }

  public static void ensureFalse(final String property, final boolean value) {
    if (value) {
      throw new RuntimeException(property + " must be false");
    }
  }

  public static void ensureArrayBacked(final DirectBuffer buffer) {
    ensureNotNull("bytes array", buffer.byteArray());
  }

  public static void ensureArrayBacked(final DirectBuffer... buffers) {
    for (final DirectBuffer buffer : buffers) {
      ensureArrayBacked(buffer);
    }
  }
}
