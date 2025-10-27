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
package com.anyilanxin.toolkit.util;

import org.slf4j.Logger;

public class Loggers {

  public static final Logger CONFIG_LOGGER = new ZbLogger("com.anyilanxin.toolkit.util.config");
  public static final Logger ACTOR_LOGGER = new ZbLogger("com.anyilanxin.toolkit.util.actor");
  public static final Logger IO_LOGGER = new ZbLogger("com.anyilanxin.toolkit.util.buffer");
  public static final Logger FILE_LOGGER = new ZbLogger("com.anyilanxin.toolkit.util.fs");
}
