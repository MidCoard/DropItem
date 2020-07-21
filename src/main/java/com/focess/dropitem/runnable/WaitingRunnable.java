package com.focess.dropitem.runnable;

import com.focess.dropitem.item.DropItemInfo;
import com.google.common.collect.Maps;

import java.util.Map;

public class WaitingRunnable implements Runnable {

    private static final Map<DropItemInfo,Integer> waitingItems = Maps.newConcurrentMap();

    public static void addWaitingItem(final DropItemInfo dropItemInfo, final int time){
        waitingItems.put(dropItemInfo,time);
    }

    public static boolean check(final DropItemInfo dropitemInfo) {
        return !waitingItems.containsKey(dropitemInfo);
    }

    public void run(){
        for (final DropItemInfo dropItemInfo:waitingItems.keySet()) {
            if (waitingItems.get(dropItemInfo) == 0)
                waitingItems.remove(dropItemInfo);
            else waitingItems.compute(dropItemInfo,(k,v)->v-1);
        }
    }
}
