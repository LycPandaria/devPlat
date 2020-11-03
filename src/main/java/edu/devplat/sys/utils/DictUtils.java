package edu.devplat.sys.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import edu.devplat.common.utils.CacheUtils;
import edu.devplat.common.utils.SpringContextHolder;
import edu.devplat.common.utils.StringUtils;
import edu.devplat.sys.dao.DictDao;
import edu.devplat.sys.model.Dict;

import java.util.List;
import java.util.Map;

/**
 * 字典工具类
 * @author liyc
 * @version 20200326
 */
public class DictUtils {
    private static DictDao dictDao = SpringContextHolder.getBean(DictDao.class);

    public static final String CACHE_DICT_MAP = "dictMap";

    /**
     * 获取指定 type, value 的 字典 label，否则反正 defaultValue
     * @param value
     * @param type
     * @param defaultValue
     * @return
     */
    public static String getDictLabel(String value, String type, String defaultValue){
        if(StringUtils.isNotBlank(value) && StringUtils.isNotBlank(type)){
            for(Dict dict : getDictList(type)){
                if(type.equals(dict.getType()) && value.equals(dict.getValue()))
                    return dict.getLabel();
            }
        }
        return defaultValue;
    }

    /**
     * 获取指定 type 字典, 多个 values 的 labels，用","逗号分隔，否则反正 defaultValue
     * @param value
     * @param type
     * @param defaultValue
     * @return
     */
    public static String getDictLabels(String values, String type, String defaultValue){
        if(StringUtils.isNotBlank(type) && StringUtils.isNotBlank(values)){
            List<String> labels = Lists.newArrayList();
            List<String> valueList = Lists.newArrayList(StringUtils.split(values, ","));
            for(Dict dict : getDictList(type)){
                // for each dict of this type, add its label if its value in valueList
                if(type.equals(dict.getType()) && valueList.contains(dict.getValue()))
                    labels.add(dict.getLabel());
            }
            return StringUtils.join(labels, ",");
        }
        return defaultValue;
    }

    /**
     * 获取指定 type, label 的 value，否则反正 defaultValue
     * @param label
     * @param type
     * @param defaultValue
     * @return
     */
    public static String getDictValue(String label, String type, String defaultValue){
        if(StringUtils.isNotBlank(label) && StringUtils.isNotBlank(type)){
            for(Dict dict : getDictList(type)){
                if(type.equals(dict.getType()) && label.equals(dict.getLabel()))
                    return dict.getValue();
            }
        }
        return defaultValue;
    }

    /**
     * 返回指定 type 的 dict list
     * @param type
     * @return
     */
    public static List<Dict> getDictList(String type){
        // read dictMap from cache
        Map<String, List<Dict>> dictMap = (Map<String, List<Dict>>)CacheUtils.get(CACHE_DICT_MAP);
        if(dictMap == null){
            // if fail, save the whole dict map into cache
            dictMap = Maps.newHashMap();
            for(Dict dict : dictDao.findAllList(new Dict())){
                // for each dict, put into its type's list
                List<Dict> dictList = dictMap.get(dict.getType());
                if(dictList != null)
                    dictList.add(dict);
                else
                    dictMap.put(dict.getType(), Lists.newArrayList(dict));
            }
            // put into cache
            CacheUtils.put(CACHE_DICT_MAP, dictMap);
        }
        List<Dict> dictList = dictMap.get(type);
        if(dictList == null) dictList = Lists.newArrayList();
        return dictList;
    }

    /**
     * 返回字典列表（JSON）
     * @param type
     * @return
     */
    public static String getDictListJson(String type){
        return  JSON.toJSONString(getDictList(type));
    }
}
