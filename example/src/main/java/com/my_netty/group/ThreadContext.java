package com.my_netty.group;

import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author huangfu
 * @date
 */
public class ThreadContext {
    public static final Set<Selector> runSelect = new HashSet<>();
}
