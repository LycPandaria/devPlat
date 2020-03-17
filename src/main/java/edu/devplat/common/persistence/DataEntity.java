package edu.devplat.common.persistence;

import edu.devplat.common.utils.IdGen;
import edu.devplat.sys.model.User;

import java.util.Date;

/**
 * 数据 Entity 支持类
 * 封装了一些字段，实现基础类中的方法
 */
public abstract class DataEntity<T> extends BaseEntity<T> {

    // TODO currentUser 的内容还没加上

    private static final long serialVersionUDI = 1L;

    protected String remarks;	// 备注
    protected User createBy;	// 创建者
    protected Date createDate;	// 创建日期
    protected User updateBy;	// 更新者
    protected Date updateDate;	// 更新日期

    public DataEntity(){
        super();
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

        this.createDate = new Date();   // 设置新纪录的新建时间，并设置其修改时间和新建时间一样
        this.updateDate = this.createDate;
    }

    @Override
    public void preUpdate() {

        this.updateDate = new Date();
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public User getCreateBy() {
        return createBy;
    }

    public void setCreateBy(User createBy) {
        this.createBy = createBy;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public User getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(User updateBy) {
        this.updateBy = updateBy;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
