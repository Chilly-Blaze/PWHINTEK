package com.pwhintek.backend.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ChillyBlaze
 * @version 1.0
 * @since 2022 May 30 21:58
 */
public class MiscUtils {
    public static <O> boolean isNotAllInList(List<String> l, Map<String, O> map) {
        // 检查传入内容是否符合要求
        Set<String> set = map.keySet();
        for (String s : set) {
            if (!l.contains(s)) {
                return true;
            }
        }
        return false;
    }
}
