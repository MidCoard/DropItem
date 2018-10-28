package com.focess.dropitem.util;

import java.util.ArrayList;
import java.util.List;

public class Util {
    
    public static List<String> toList(String[] list) {
        List<String> ret = new ArrayList<>();
        for (int i = 0;i<list.length;i++)
            ret.add(list[i]);
        return ret;
    }
}
