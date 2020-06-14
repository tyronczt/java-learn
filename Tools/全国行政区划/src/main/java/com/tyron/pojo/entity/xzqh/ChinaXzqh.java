package com.tyron.pojo.entity.xzqh;

import lombok.Data;

import javax.persistence.*;

@Table(name = "china_xzqh")
@Data
public class ChinaXzqh {
    /**
     * id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 行政区划编码
     */
    private String code;

    /**
     * 行政区划名称
     */
    private String name;

    /**
     * 名称重命名-加政府，用于匹配经纬度
     */
    private String nameplus;

    /**
     * 行政区划全名
     */
    private String fullname;

    /**
     * 类型，1：省级，2：市级，3：县级
     */
    private String type;

    /**
     * 城市编码
     */
    private String citycode;

    /**
     * 城市名
     */
    private String cityname;

    /**
     * POI所在省份编码
     */
    private String pcode;

    /**
     * POI所在省份名称
     */
    private String pname;

    /**
     * 地址
     */
    private String address;

    /**
     * POI的电话
     */
    private String tel;

    /**
     * 高德经度
     */
    @Column(name = "gd_jd")
    private String gdJd;

    /**
     * 高德纬度
     */
    @Column(name = "gd_wd")
    private String gdWd;

    /**
     * 百度经度
     */
    @Column(name = "bd_jd")
    private String bdJd;

    /**
     * 百度纬度
     */
    @Column(name = "bd_wd")
    private String bdWd;

    /**
     * 备注
     */
    private String remark;

}