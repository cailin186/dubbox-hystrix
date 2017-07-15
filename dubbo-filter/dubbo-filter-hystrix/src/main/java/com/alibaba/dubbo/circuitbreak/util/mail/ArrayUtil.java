package com.alibaba.dubbo.circuitbreak.util.mail;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wangxing01 on 2017/1/23.
 */
public class ArrayUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArrayUtil.class);

    public static List<Object[]> splitArray(Object[] arr, int size) {

        if (null == arr)  return null;

        int count = arr.length % size == 0 ? arr.length / size: arr.length / size + 1;

        LOGGER.debug((count + " 取整" + (arr.length % size == 0)));
        List<List<Object>> subAryList = Lists.newArrayList();

        for (int i = 0; i < count; i++) {
            int index = i * size;
            List<Object> list = Lists.newArrayList();
            int j = 0;
            while (j < size && index < arr.length) {
                list.add(arr[index++]);
                j++;
            }
            subAryList.add(list);
        }

        List<Object[]> subArrs = Lists.newArrayList();

        for(int i = 0; i < subAryList.size(); i++){
            List<Object> subList = subAryList.get(i);
            Object[] subAryItem = new Object[subList.size()];
            for(int j = 0; j < subList.size(); j++){
                subAryItem[j] = subList.get(j);
            }

            subArrs.add(subAryItem);
            LOGGER.debug("切分的数组大小：{}", subAryItem.length);
        }

        return subArrs;
    }



}
