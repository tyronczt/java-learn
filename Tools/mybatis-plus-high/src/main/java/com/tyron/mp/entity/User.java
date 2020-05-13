package com.tyron.mp.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User extends Model<User> {

    /**
     * 主键
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 直属上级id
     */
    private Long managerId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime updateTime;

    /**
     * 版本
     */
    private Integer version;

    /**
     * 删除标识，0未删除，1已删除
     * @TableLogic 描述：表字段逻辑处理注解（逻辑删除）
     * @TableField 描述：字段注解(非主键) select 默认值：true 表示：是否进行 select 查询
     */
    @TableLogic
    @TableField(select = false)
    private Integer deleted;
}