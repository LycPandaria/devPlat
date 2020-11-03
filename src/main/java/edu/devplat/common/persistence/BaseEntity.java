package edu.devplat.common.persistence;

import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

import edu.devplat.common.config.Global;
import edu.devplat.common.utils.StringUtils;
import edu.devplat.sys.model.User;
import edu.devplat.sys.utils.UserUtils;

/**
 * Entity 支持类
 * @author lyc
 * @version 2019.08.02
 */

public abstract class BaseEntity<T> implements Serializable {

    private static final long serialVersionUDI = 1L;

    /**
     * 删除标记（0：正常；1：删除；2：审核；）
     */
    public static final String DEL_FLAG_NORMAL = "0";
    public static final String DEL_FLAG_DELETE = "1";
    public static final String DEL_FLAG_AUDIT = "2";

    /**
     * 实体编号（唯一标识）
     */
    protected String id;

    /**
     * 当前用户
     */
    protected User currentUser;

    /**
     * 当前实体分页对象
     */
    protected Page<T> page;

    /**
     * 自定义 SQL
     */
    protected Map<String, String> sqlMap;

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    protected boolean isNewRecord = false;

    public BaseEntity(){

    }

    public BaseEntity(String id){
        this();
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JSONField(serialize = false)
    public User getCurrentUser(){
        if(currentUser == null)
            currentUser = UserUtils.getUser();
        return currentUser;
    }

    public void setCurrentUser(User currentUser){
        this.currentUser = currentUser;
    }

    @JSONField(serialize = false)
    public  Page<T> getPage(){
        if(page == null)
            page = new Page<T>();
        return page;
    }

    public Page<T> setPage(Page<T> page){
        this.page = page;
        return page;
    }

    @JSONField(serialize = false)
    public Map<String, String> getSqlMap() {
        if(sqlMap == null)
            sqlMap = Maps.newHashMap();
        return sqlMap;
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    /**
     * 插入之前执行的方法，子类实现
     */
    public abstract void preInsert();

    /**
     * 更新之前执行的方法，子类实现
     */
    public abstract void preUpdate();

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     * @return
     */
    public boolean getIsNewRecord() {
        return isNewRecord || StringUtils.isBlank(getId());
    }

    /**
     * 是否是新记录（默认：false），调用setIsNewRecord()设置新记录，使用自定义ID。
     * 设置为true后强制执行插入语句，ID不会自动生成，需从手动传入。
     */
    public void setIsNewRecord(boolean isNewRecord) {
        this.isNewRecord = isNewRecord;
    }

    /**
     * 获取 Global 类
     * @return
     */
    @JSONField(serialize = false)   // 不序列化该属性
    public Global getGlobal(){
        return Global.getInstance();
    }
}
