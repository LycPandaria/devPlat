package edu.devplat.sys.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
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
     * 返回指定 type 的 dict list
     * @param type
     * @return
     */
    public static List<Dict> getDictList(String type){
        // TODO cache
        Map<String, List<Dict>> dictMap = Maps.newHashMap();

        return dictDao.findList(new Dict(type, ""));
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
