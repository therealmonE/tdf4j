/*
 * Copyright (c) 2019 tdf4j
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

package org.tdf4j.utils;

import org.tdf4j.model.Stream;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class BufferedStreamTest {
    private final List<Integer> testData = List.of(1, 2, 3, 4);

    @Test
    public void test() {
        final List<Integer> list = new ArrayList<>(testData);
        final BufferedStream<Integer> bufferedStream = new BufferedStream<>(new Stream.Builder<Integer>().setGenerator(() -> {
            if(!list.isEmpty()) {
                final Integer value = list.get(0);
                list.remove(0);
                return value;
            } else {
                return null;
            }
        }).build());

        assertEquals(1, bufferedStream.peek().intValue());
        assertEquals(1, bufferedStream.peek().intValue());
        assertEquals(1, bufferedStream.next().intValue());
        assertEquals(2, bufferedStream.next().intValue());
        assertEquals(3, bufferedStream.peek().intValue());
        assertEquals(3, bufferedStream.next().intValue());
        assertEquals(4, bufferedStream.next().intValue());
        assertNull(bufferedStream.peek());
        assertNull(bufferedStream.next());
    }
}
