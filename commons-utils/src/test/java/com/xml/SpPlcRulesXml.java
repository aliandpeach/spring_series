package com.xml;

import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "FileContorlObj")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SpPlcRulesXml implements Serializable
{
    private static final long serialVersionUID = -3926298925203692586L;
    /**
     * 主键id
     */
    @XmlAttribute(name = "id")
    private String id;
    /**
     * 操作标识
     */
    private BigDecimal optlock = new BigDecimal(0);
    /**
     * 管理id
     */
    private String mngId;
    /**
     * 数据类型
     */
    @XmlAttribute(name = "dataType")
    private String dataType;
    /**
     * 定义类型
     */
    private String definitionType;
    /**
     * 规则描述信息
     */
    @XmlAttribute(name = "description")
    private String description;
    /**
     * 策略同步状态
     * SYNCHRONIZED部署后的状态
     * UNSYNCHRONIZED_EDIT 部署前的状态
     */
    private String elementStatus;
    /**
     * 是否使用，对应页面的checkbox
     */
    private BigDecimal isEnabled;
    /**
     * 是否显示
     */
    @XmlAttribute(name = "isHidden")
    private BigDecimal isHidden;
    /**
     * 位置,指规则在同一策略内的顺序。
     */
    private BigDecimal position;
    /**
     * 对应 规则编辑界面的严重性和响应标签下的高级部分
     */
    private BigDecimal isMaxMatches;
    /**
     * 逻辑条件关系
     */
    private String conditionRelationType;
    /**
     * 自定义表达式
     */
    private String customizedExpression;
    /**
     * 规则名称，与规则ID的组合 具有唯一性
     */
    @XmlAttribute(name = "name")
    private String name;
    /**
     * 128位guid，此选项对DLP策略有用，对扫描发现无用。因为扫描发现没有源、目的信息。
     * 此时只有PA_SELETION表中存在uuid， IS_ALL_SELECTED字段为1，PA_SELECTION_ITEM表中无数据
     */
    private String sourceSelectionId;
    /**
     * 策略ID
     */
    private String policyId;
    /**
     * 结果形式，取值如下：
     * num	RESULT_FORMAT
     * 261	IS_A
     * 1475	HAS_A
     * 从如下代码可以分析出，除了指纹和文件类型分类器，其他都为HAS_A
     * <pre>
     * public static ResultFormatType getDefaultResultFormatTypeByClassifierType(ContentClassifierType classifierType)
     * {
     * if ((ContentClassifierType.FILE_FINGERPRINT.equals(classifierType)) || (ContentClassifierType.FILE_TYPE.equals(classifierType))) {
     * return IS_A;
     * }
     * return HAS_A;
     * }
     * </pre>
     */
    private String resultFormat;
    /**
     * 是否分析所有的内容，标志位
     */
    private BigDecimal analyzeAnyContent;
    /**
     * 表达式ID，参考SP_PLC_CONDITION_EXPRESSION
     */
    private BigDecimal expressionId;
    /**
     * 预定义表达式ID
     */
    private BigDecimal predefinedExpressionId;
    /**
     * 预定义描述
     */
    private String predefineDesc;
    /**
     * 预定义名称
     */
    private String predefineName;
    /**
     * 预定义规则ID，从已有策略派生的规则会使用到该主键
     */
    private BigDecimal predefineId;
    /**
     * 预定义版本
     */
    private String predefineVersion;
    /**
     * 敏感类型
     */
    private String sensitivityType;
    /**
     * 终端连接类型
     */
    private String endpointConnectionType;
    /**
     * 是否监控笔记本
     */
    private BigDecimal enableLaptopMachineType;
    /**
     * 是否监控其他类型
     */
    private BigDecimal enableOtherMachineType;
    /**
     * 策略规则基础名称
     */
    private String baseName;
    /**
     * 策略规则子名称
     */
    private String subName;
    /**
     * 策略状态
     */
    private String policyEntityStatus;
    /**
     * 计数方式
     */
    private String partCountType;
    /**
     * 通道类型
     */
    private String channelType;
    /**
     * 规则类型
     */
    private String ruleType;
    /**
     * 状态规则的计数类型
     */
    private String statefulCountType;
    /**
     * 敏感
     */
    private BigDecimal sensitivity;
    /**
     * 0，发现。。。。。扫描。。。
     */
    private int subsystem;
    /**
     * 修改者
     */
    private String modifiedBy;

    /**
     * 分类器间的匹配次数
     */
    private String isMaxMatch;

    /**
     * 打分制默认给定分数
     */
    private String totalScore;
    /**
     * （时间属性）开始时间
     */
    private String startTime;

    /**
     * （时间属性）结束时间
     */
    private String endTime;

    /**
     * （时间属性）周
     */
    private String weeklyTime;

    /**
     * 生效时间：0：全天生效, 1：工作日，2：非工作日，3： 自定义
     */
    private String effictiveTime;


    private String envId;

    /**
     * 父节点id
     */
    private String parentId;

    /**
     * 标密扫描文件长度
     */
    private int scanLength;

    /**
     * 文件内容截取开始位置: 0. 从文件内容头开始截取 -1. 从文件内容尾开始截取 -2. 不截取, 全文件内容处理 (截取长度为scanLength)
     */
    private int startPos = -2;
}
