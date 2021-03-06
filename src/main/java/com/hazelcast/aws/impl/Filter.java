/*
 * Copyright (c) 2008-2018, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hazelcast.aws.impl;

import java.util.HashMap;
import java.util.Map;

/**
 * Query filter to narrow down the scope of the queried EC2 instance set.
 */
public class Filter {

    private Map<String, String> filters = new HashMap<String, String>();

    /**
     * Filter index, each filter need to have a sequential index, starting from 1.
     */
    private int index = 1;

    /**
     * Add a new filter with the given name and value to the query.
     *
     * @param name  Filter name
     * @param value Filter value
     */
    public void addFilter(String name, String value) {
        filters.put("Filter." + index + ".Name", name);
        filters.put("Filter." + index + ".Value.1", value);
        ++index;
    }

    public Map<String, String> getFilters() {
        return filters;
    }
}
