/*
 * Copyright 2017, OpenSkywalking Organization All rights reserved.
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
 *
 * Project repository: https://github.com/OpenSkywalking/skywalking
 */

package org.skywalking.apm.agent.core.jvm.memory;

import org.skywalking.apm.network.proto.Memory;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.LinkedList;
import java.util.List;

/**
 * @author wusheng
 */
public enum MemoryProvider {
    INSTANCE;
    private final MemoryMXBean memoryMXBean;

    MemoryProvider() {
        this.memoryMXBean = ManagementFactory.getMemoryMXBean();
    }

    public List<Memory> getMemoryMetricList() {
        List<Memory> memoryList = new LinkedList<Memory>();

        // 堆内存
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        Memory.Builder heapMemoryBuilder = Memory.newBuilder();
        heapMemoryBuilder.setIsHeap(true);
        heapMemoryBuilder.setInit(heapMemoryUsage.getInit()); // java虚拟机在启动的时候向操作系统请求的初始内存容量，java虚拟机在运行的过程中可能向操作系统请求更多的内存或将内存释放给操作系统，所以init的值是不确定的。
        heapMemoryBuilder.setUsed(heapMemoryUsage.getUsed()); // 当前已经使用的内存量。
        heapMemoryBuilder.setCommitted(heapMemoryUsage.getCommitted()); // 表示保证 Java 虚拟机能使用的内存量，已提交的内存量可以随时间而变化
        heapMemoryBuilder.setMax(heapMemoryUsage.getMax()); // 最大内存容量
        memoryList.add(heapMemoryBuilder.build());

        // 非堆内存
        MemoryUsage nonHeapMemoryUsage = memoryMXBean.getNonHeapMemoryUsage();
        Memory.Builder nonHeapMemoryBuilder = Memory.newBuilder();
        nonHeapMemoryBuilder.setIsHeap(false);
        nonHeapMemoryBuilder.setInit(nonHeapMemoryUsage.getInit());
        nonHeapMemoryBuilder.setUsed(nonHeapMemoryUsage.getUsed());
        nonHeapMemoryBuilder.setCommitted(nonHeapMemoryUsage.getCommitted());
        nonHeapMemoryBuilder.setMax(nonHeapMemoryUsage.getMax());
        memoryList.add(nonHeapMemoryBuilder.build());

        return memoryList;
    }

}
