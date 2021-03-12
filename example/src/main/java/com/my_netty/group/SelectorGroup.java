package com.my_netty.group;

import com.my_netty.reactor.Reactor;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 选择器组
 *
 * @author huangfu
 * @date 2021年3月12日09:44:37
 */
public class SelectorGroup {
    private final List<Selector> SELECTOR_GROUP = new ArrayList<>(8);
    private static final int AVAILABLE_PROCESSORS = Runtime.getRuntime().availableProcessors();
    private final static AtomicInteger IDX = new AtomicInteger();


    public SelectorGroup(int count) throws IOException {

        for (int i = 0; i < count; i++) {
            Selector open = Selector.open();
            SELECTOR_GROUP.add(open);
        }
    }

    public SelectorGroup() throws IOException {
        this(AVAILABLE_PROCESSORS << 1);
    }


    public Selector next(){
        int andIncrement = IDX.getAndIncrement();
        int length = SELECTOR_GROUP.size();

        return SELECTOR_GROUP.get(Math.abs(andIncrement % length));
    }
}
