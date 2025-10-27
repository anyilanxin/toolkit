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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * Delegating Logger implementation which guards all invocations with static checks for the log
 * level. Allows the JVM to remove log statements which are not needed with the current log level.
 * Removes the possibility to dynamically change the log level.
 */
public class ZbLogger implements Logger {

  private final Logger logger;
  private final boolean isTraceEnabled;
  private final boolean isDebugEnabled;
  private final boolean isInfoEnabled;
  private final boolean isWarnEnabled;
  private final boolean isErrorEnabled;

  public ZbLogger(final Class<?> clazz) {
    this(LoggerFactory.getLogger(clazz));
  }

  public ZbLogger(final String name) {
    this(LoggerFactory.getLogger(name));
  }

  public ZbLogger(final Logger logger) {
    this.logger = logger;
    this.isTraceEnabled = logger.isTraceEnabled();
    this.isDebugEnabled = logger.isDebugEnabled();
    this.isInfoEnabled = logger.isInfoEnabled();
    this.isWarnEnabled = logger.isWarnEnabled();
    this.isErrorEnabled = logger.isErrorEnabled();
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public boolean isTraceEnabled() {
    return isTraceEnabled;
  }

  @Override
  public void trace(final String msg) {
    if (isTraceEnabled) {
      logger.trace(msg);
    }
  }

  @Override
  public void trace(final String format, final Object arg) {
    if (isTraceEnabled) {
      logger.trace(format, arg);
    }
  }

  public void trace(final String format, final int arg) {
    if (isTraceEnabled) {
      logger.trace(format, arg);
    }
  }

  @Override
  public void trace(final String format, final Object arg1, final Object arg2) {
    if (isTraceEnabled) {
      logger.trace(format, arg1, arg2);
    }
  }

  public void trace(final String format, final int arg1, final Object arg2) {
    if (isTraceEnabled) {
      logger.trace(format, arg1, arg2);
    }
  }

  public void trace(final String format, final int arg1, final int arg2) {
    if (isTraceEnabled) {
      logger.trace(format, arg1, arg2);
    }
  }

  @Override
  public void trace(final String format, final Object... arguments) {
    if (isTraceEnabled) {
      logger.trace(format, arguments);
    }
  }

  @Override
  public void trace(final String msg, final Throwable t) {
    if (isTraceEnabled) {
      logger.trace(msg, t);
    }
  }

  @Override
  public boolean isTraceEnabled(final Marker marker) {
    return isTraceEnabled;
  }

  @Override
  public void trace(final Marker marker, final String msg) {
    if (isTraceEnabled) {
      logger.trace(marker, msg);
    }
  }

  @Override
  public void trace(final Marker marker, final String format, final Object arg) {
    if (isTraceEnabled) {
      logger.trace(marker, format, arg);
    }
  }

  @Override
  public void trace(
      final Marker marker, final String format, final Object arg1, final Object arg2) {
    if (isTraceEnabled) {
      logger.trace(marker, format, arg1, arg2);
    }
  }

  @Override
  public void trace(final Marker marker, final String format, final Object... argArray) {
    if (isTraceEnabled) {
      logger.trace(marker, format, argArray);
    }
  }

  @Override
  public void trace(final Marker marker, final String msg, final Throwable t) {
    if (isTraceEnabled) {
      logger.trace(marker, msg, t);
    }
  }

  @Override
  public boolean isDebugEnabled() {
    return isDebugEnabled;
  }

  @Override
  public void debug(final String msg) {
    if (isDebugEnabled) {
      logger.debug(msg);
    }
  }

  @Override
  public void debug(final String format, final Object arg) {
    if (isDebugEnabled) {
      logger.debug(format, arg);
    }
  }

  @Override
  public void debug(final String format, final Object arg1, final Object arg2) {
    if (isDebugEnabled) {
      logger.debug(format, arg1, arg2);
    }
  }

  @Override
  public void debug(final String format, final Object... arguments) {
    if (isDebugEnabled) {
      logger.debug(format, arguments);
    }
  }

  @Override
  public void debug(final String msg, final Throwable t) {
    if (isDebugEnabled) {
      logger.debug(msg, t);
    }
  }

  @Override
  public boolean isDebugEnabled(final Marker marker) {
    return isDebugEnabled;
  }

  @Override
  public void debug(final Marker marker, final String msg) {
    if (isDebugEnabled) {
      logger.debug(marker, msg);
    }
  }

  @Override
  public void debug(final Marker marker, final String format, final Object arg) {
    if (isDebugEnabled) {
      logger.debug(marker, format, arg);
    }
  }

  @Override
  public void debug(
      final Marker marker, final String format, final Object arg1, final Object arg2) {
    if (isDebugEnabled) {
      logger.debug(marker, format, arg1, arg2);
    }
  }

  @Override
  public void debug(final Marker marker, final String format, final Object... arguments) {
    if (isDebugEnabled) {
      logger.debug(marker, format, arguments);
    }
  }

  @Override
  public void debug(final Marker marker, final String msg, final Throwable t) {
    if (isDebugEnabled) {
      logger.debug(marker, msg, t);
    }
  }

  @Override
  public boolean isInfoEnabled() {
    return isInfoEnabled;
  }

  @Override
  public void info(final String msg) {
    if (isInfoEnabled) {
      logger.info(msg);
    }
  }

  @Override
  public void info(final String format, final Object arg) {
    if (isInfoEnabled) {
      logger.info(format, arg);
    }
  }

  @Override
  public void info(final String format, final Object arg1, final Object arg2) {
    if (isInfoEnabled) {
      logger.info(format, arg1, arg2);
    }
  }

  @Override
  public void info(final String format, final Object... arguments) {
    if (isInfoEnabled) {
      logger.info(format, arguments);
    }
  }

  @Override
  public void info(final String msg, final Throwable t) {
    if (isInfoEnabled) {
      logger.info(msg, t);
    }
  }

  @Override
  public boolean isInfoEnabled(final Marker marker) {
    return isInfoEnabled;
  }

  @Override
  public void info(final Marker marker, final String msg) {
    if (isInfoEnabled) {
      logger.info(marker, msg);
    }
  }

  @Override
  public void info(final Marker marker, final String format, final Object arg) {
    if (isInfoEnabled) {
      logger.info(marker, format, arg);
    }
  }

  @Override
  public void info(final Marker marker, final String format, final Object arg1, final Object arg2) {
    if (isInfoEnabled) {
      logger.info(marker, format, arg1, arg2);
    }
  }

  @Override
  public void info(final Marker marker, final String format, final Object... arguments) {
    if (isInfoEnabled) {
      logger.info(marker, format, arguments);
    }
  }

  @Override
  public void info(final Marker marker, final String msg, final Throwable t) {
    if (isInfoEnabled) {
      logger.info(marker, msg, t);
    }
  }

  @Override
  public boolean isWarnEnabled() {
    return isWarnEnabled;
  }

  @Override
  public void warn(final String msg) {
    if (isWarnEnabled) {
      logger.warn(msg);
    }
  }

  @Override
  public void warn(final String format, final Object arg) {
    if (isWarnEnabled) {
      logger.warn(format, arg);
    }
  }

  @Override
  public void warn(final String format, final Object... arguments) {
    if (isWarnEnabled) {
      logger.warn(format, arguments);
    }
  }

  @Override
  public void warn(final String format, final Object arg1, final Object arg2) {
    if (isWarnEnabled) {
      logger.warn(format, arg1, arg2);
    }
  }

  @Override
  public void warn(final String msg, final Throwable t) {
    if (isWarnEnabled) {
      logger.warn(msg, t);
    }
  }

  @Override
  public boolean isWarnEnabled(final Marker marker) {
    return isWarnEnabled;
  }

  @Override
  public void warn(final Marker marker, final String msg) {
    if (isWarnEnabled) {
      logger.warn(marker, msg);
    }
  }

  @Override
  public void warn(final Marker marker, final String format, final Object arg) {
    if (isWarnEnabled) {
      logger.warn(marker, format, arg);
    }
  }

  @Override
  public void warn(final Marker marker, final String format, final Object arg1, final Object arg2) {
    if (isWarnEnabled) {
      logger.warn(marker, format, arg1, arg2);
    }
  }

  @Override
  public void warn(final Marker marker, final String format, final Object... arguments) {
    if (isWarnEnabled) {
      logger.warn(marker, format, arguments);
    }
  }

  @Override
  public void warn(final Marker marker, final String msg, final Throwable t) {
    if (isWarnEnabled) {
      logger.warn(marker, msg, t);
    }
  }

  @Override
  public boolean isErrorEnabled() {
    return isErrorEnabled;
  }

  @Override
  public void error(final String msg) {
    if (isErrorEnabled) {
      logger.error(msg);
    }
  }

  @Override
  public void error(final String format, final Object arg) {
    if (isErrorEnabled) {
      logger.error(format, arg);
    }
  }

  @Override
  public void error(final String format, final Object arg1, final Object arg2) {
    if (isErrorEnabled) {
      logger.error(format, arg1, arg2);
    }
  }

  @Override
  public void error(final String format, final Object... arguments) {
    if (isErrorEnabled) {
      logger.error(format, arguments);
    }
  }

  @Override
  public void error(final String msg, final Throwable t) {
    if (isErrorEnabled) {
      logger.error(msg, t);
    }
  }

  @Override
  public boolean isErrorEnabled(final Marker marker) {
    return isErrorEnabled;
  }

  @Override
  public void error(final Marker marker, final String msg) {
    if (isErrorEnabled) {
      logger.error(marker, msg);
    }
  }

  @Override
  public void error(final Marker marker, final String format, final Object arg) {
    if (isErrorEnabled) {
      logger.error(marker, format, arg);
    }
  }

  @Override
  public void error(
      final Marker marker, final String format, final Object arg1, final Object arg2) {
    if (isErrorEnabled) {
      logger.error(marker, format, arg1, arg2);
    }
  }

  @Override
  public void error(final Marker marker, final String format, final Object... arguments) {
    if (isErrorEnabled) {
      logger.error(marker, format, arguments);
    }
  }

  @Override
  public void error(final Marker marker, final String msg, final Throwable t) {
    if (isErrorEnabled) {
      logger.error(marker, msg, t);
    }
  }
}
