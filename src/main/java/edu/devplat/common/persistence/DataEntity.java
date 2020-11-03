package edu.devplat.common.persistence;

import com.alibaba.fastjson.annotation.JSONField;
import edu.devplat.common.utils.IdGen;
import edu.devplat.common.utils.StringUtils;
import edu.devplat.sys.model.User;
import edu.devplat.sys.utils.UserUtils;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

/**
 * 数据 Entity 支持类
 * 封装了一些字段，实现基础类中的方法
 */
public abstract class DataEntity<T> extends BaseEntity<T> {

    private static final long serialVersionUDI = 1L;

    protected String remarks;	// 备注
    protected User createBy;	// 创建者
    protected Date createDate;	// 创建日期
    protected User updateBy;	// 更新者
    protected Date updateDate;	// 更新日期
    protected String delflag;   // 删除标记

    public DataEntity(){
        super();
        this.delflag = DEL_FLAG_NORMAL;
    }

    public DataEntity(String id){
        super(id);
    }

    /**
     * 插入前执行方法，需要手动调用
     */
    @Override
    public void preInsert() {
        if(!this.isNewRecord)
            setId(IdGen.uuid());    // 如果是新记录，为其设置一个 ID

        // set updateBy and createBy
        User user = UserUtils.getUser();
        if(StringUtils.isNotBlank(user.getId())){
            this.updateBy = user;
            this.createBy = user;
        }

        this.createDate = new Date();   // 设置新纪录的新建时间，并设置其修改时间和新建时间一样
        this.updateDate = this.createDate;
    }

    @Override
    public void preUpdate() {
        User user = UserUtils.getUser();
        if(StringUtils.isNotBlank(user.getId())){
            this.updateBy = user;
        }
        this.updateDate = new Date();
    }

    @Length(min = 0, max = 255)
    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    @JSONField(serialize = false)
    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss") // FastJson包使用注解
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @JSONField(serialize = false)
    public User getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    @JSONField(format = "yyyy-MM-dd HH:mm:ss") // FastJson包使用注解
    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
    
    @JSONField(serialize = false)
    @Length(min = 1, max = 0)
    public String getDelflag(){
        return delflag;
    }
    
    public void setDelflag(String delflag){
        this.delflag = delflag;
    }
}
