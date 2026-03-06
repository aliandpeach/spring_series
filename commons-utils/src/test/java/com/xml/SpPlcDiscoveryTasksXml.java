package com.xml;


import lombok.Data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement(name = "SvcObj")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
public class SpPlcDiscoveryTasksXml implements Serializable
{
    private static final long serialVersionUID = -5429655397617644082L;
    /**
     * 主键id
     */
    private String id;
    /**
     * 管理ID
     */
    private String mngId;
    /**
     * GUID，非空
     */
    private String guid;
    /**
     * 操作标识，修改一次加一。
     */
    private BigDecimal optlock = BigDecimal.valueOf(0);
    /**
     * 任务名称，非空
     */
    private String name;
    /**
     * 简单说明，非空
     * EndpointDiscoveryTask
     * FileSystemDiscoveryTask
     */
    private String disc;
    /**
     * 描述
     */
    private String description;
    /**
     * 发现任务类型，非空
     * 取值如下：
     * ENDPOINT_DISCOVERY_TASK
     * NETWORK_DISCOVERY_TASK
     */
    private String discoveryTaskType;
    /**
     * 元素状态，非空
     */
    private String elementStatus;
    /**
     * 完整扫描频率类型，非空
     * 共三种取值，对应UI的三个radio
     * ON_POLICY_AND_FP_VERSION_UPDATE
     * ON_POLICY_UPDATE
     * ALWAYS
     */
    private String fullScanFrequencyType;
    /**
     * 外键，参考PA_SELECTION，该表与[PA_SELECTION_ITEMS]有主从表关系，
     * 其中[PA_SELECTION_ITEMS]中[SELECTION_DATA]存储了具体各类资源的ID
     */
    private String scannedNetworkSelectionId;
    /**
     * 调度计划ID，外键，参考SP_SCHEDULING_DATA
     */
    private String schedulingId;
    /**
     * 文件过滤是否启用
     * 对应UI的File Filtering 中的Filter by type checkbox
     */
    private BigDecimal isFileNameEnabled;

    /**
     * 文件时间过滤是否启用
     */
    private BigDecimal isFileAgeEnabled;
    /**
     * 是否使用tcp协议，对应创建任务想到中的扫描方式combox
     * 1 —— tcp扫描
     * 0 —— icmp扫描
     */
    private BigDecimal isTcpProtocolUsed;
    /**
     * 对应文件时间过滤选项内的三个radio选项，指定文件修改时间的范围
     * 如：
     * WITHIN，多少个月之内修改的文件
     * MORE_THAN，多少个月之前修改的文件
     * BETWEEN ，某个时间区间内修改的文件
     */
    private String scanPeriodType;
    /**
     * 多少个月之内修改过的文件
     */
    private BigDecimal modifiedWithinMonths;
    /**
     * 多少个月之前修改的文件
     */
    private BigDecimal modifiedMonthsAgo;
    /**
     * 修改时间开始
     */
    private Date modifiedFromDate;
    /**
     * 修改日期结束
     */
    private Date modifiedToDate;
    /**
     * 是否启用文件大小的上下限，对应 checkbox
     */
    private BigDecimal isLargerThanEnabled;
    /**
     * 上限文件大小
     */
    private BigDecimal sizeLargerThan;
    /**
     * 是否启用文件下限，对应 checkbox
     */
    private BigDecimal isSmallerThanEnalbed;
    /**
     * 文件下限大小
     */
    private BigDecimal sizeSmallerThan;
    /**
     * 是否限制带宽
     */
    private BigDecimal isBandwithLimited;
    /**
     * 带宽限制大小
     */
    private BigDecimal bandwithLimit;
    /**
     * 发现模式
     */
    private String discoveryMode;
    /**
     * 最大内容体积
     */
    private String maxContentSize;
    /**
     * 扫描深度
     */
    private int depth;
    /**
     * 定义类型
     */
    private String definitionType;

    /**
     * 扫描引擎ID SP_SM_SITE_ELEMENTS中的ID
     */
    private String discoveryAgentId;
    /**
     * 域地址
     */
    private String domain;
    /**
     * 域用户密码
     */
    private String password;
    /**
     * 域用户名
     */
    private String username;
    /**
     * 数据库数据源名称
     * 与db扫描相关
     */
    private String dataSourceName;
    /**
     * 输入名称值
     */
    private String inputNameValue;
    /**
     * 输入密码值
     */
    private String inputPasswordValue;
    /**
     * 登录url
     */
    private String loginUrl;
    /**
     * 是否使用数据源的认证信息
     * 与db扫描相关
     */
    private BigDecimal isUseDatasourceCredentials;
    /**
     * 是否随机扫描
     * 与db扫描相关
     */
    private BigDecimal isRandomScan;
    /**
     * 随机扫描个数
     * 与db扫描相关
     */
    private BigDecimal numOfRandomScan;
    /**
     * 是否启用表过滤
     * 与db扫描相关
     */
    private BigDecimal isTableFilterEnabled;
    /**
     * 是否保留文件原始访问时间，扫描以后不改变文件的最后访问时间
     */
    private BigDecimal preserveOriginalAccessTime;
    /**
     * 网络扫描类型
     */
    private String networkScanType;
    /**
     * 共享文件夹类型 取ShareFolderType枚举值
     */
    private String sharedFolderType;
    /**
     * 指定文件夹列表
     */
    private String specificFoldersList;
    /**
     * 扫描端口 多个逗号分隔
     */
    private String scanPortsList;
    /**
     * pst文件夹路径
     * pst扫描相关
     * 或者
     * sharepoint扫描根目录
     * 与sharepoint扫描相关
     */
    private String rootSource;
    /**
     * 是否附加exchange服务器，默认false
     * exchange 扫描相关
     */
    private BigDecimal isAdditionalExchngSrv;
    /**
     * 是否启用邮箱扫描
     * exchange server相关
     */
    private BigDecimal isMailboxEnabled;
    /**
     * 是否启用主题扫描
     * exchange server相关
     */
    private BigDecimal isSubjectEnabled;
    /**
     * 运行状态
     */
    private String operationStatus;
    /**
     * 非空，是否执行所有策略,对应任务创建和管理的策略标签页面中的checkbox
     */
    private BigDecimal isAllPolicies;
    /**
     * 是否使用ssl
     * exchange 扫描相关
     */
    private BigDecimal useSsl;
    /**
     * 是否使用导入的服务
     * pst扫描相关内容
     * exchange 扫描相关
     */
    private BigDecimal useImportedServers;
    /**
     * 是否扫描邮箱
     * exchange 扫描相关
     */
    private BigDecimal isMailboxesSelected;
    /**
     * 是否扫描公共文件夹
     * exchange 扫描相关
     */
    private BigDecimal isPublicFoldersSelected;
    /**
     * 扫描任务是否启用，此字段对应 调度计划标签的 enable checkbox
     */
    private BigDecimal isEnabled;
    /**
     * exchange服务器名称
     */
    private String selectedExchangeServer;
    /**
     * PST文件密码
     */
    private String pstFilesPassword;
    /**
     * 是否扫描邮件正文
     * 与Domino扫描相关
     */
    private BigDecimal scanDocBody;
    /**
     * 是否扫描邮件附件
     * 与Domino扫描相关
     */
    private BigDecimal scanDocAttachments;
    /**
     * 是否扫描其他所有字段
     * 与Domino扫描相关
     */
    private BigDecimal scanAllOtherFields;
    /**
     * 主题字段名称，默认“Subject”
     * 与Domino扫描相关
     */
    private String subjectFieldName;
    /**
     * 正文字段名称，默认body
     * 与Domino扫描相关
     */
    private String bodyFieldName;
    /**
     * Domino文件过滤器ID，参考SP_PLC_DSC_DOC_FILTER
     * 注意与文件指纹分类器的区别
     */
    private BigDecimal dominoFilterId;
    /**
     * 从属哪个子系统任务
     */
    private int subsystem;

    /**
     * 创建者ID
     */
    private String createdBy;

    /**
     * 优先级
     */
    private String priorityLevel;

    /**
     * 任务状态：0：启用 1：停用
     */
    private String taskStatus;

    /**
     * 高级设置是否被选中：Y-选中，N-未选中
     */
    private String linkResponseSetting;
    /**
     * ftp响应
     */
    private String responseFtp;
    /**
     * ftp动作
     */
    private String responseFtpName;
    /**
     * SMTP响应
     */
    private String responseSMTP;

    /**
     * 增加或者删除的email字符串，多个邮箱以英文格式的逗号隔开
     */
    private String emailList;
    /**
     * SMTP动作
     */
    private String responseSMTPName;
    /**
     * SMTP响应选择“添加邮件标头”时，风险标记级别
     */
    private String mailHeader;
    /**
     * http响应
     */
    private String responseHttp;
    /**
     * http动作
     */
    private String responseHttpName;
    /**
     * 响应设置
     */
    private String responseSettings;
    /**
     * 响应设置动作
     */
    private String responseSettingsName;
    /**
     * http响应为“重定向”时，重定向的URL
     */
    private String redirectURL;

    /**
     * imap响应
     */
    private String responseImap;
    /**
     * imap动作
     */
    private String responseImapName;
    /**
     * pop3响应
     */
    private String responsePop3;
    /**
     * pop3动作
     */
    private String responsePop3Name;

    /**
     * 是否启用策略运行时间限制
     */
    private String isEnable;
    /**
     * 开始日期
     */
    private Date startDate;

    /**
     * 结束日期
     */
    private Date endDate;

    /**
     * 是否启用加密文件识别
     */
    private String ocrEnabled;
    /**
     * 操作用户
     */
    private String operateUser;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 操作时间
     */
    private String operateTime;
    /**
     * 阻断生效类型 0未设置生效时间 1永久生效 2临时生效 3自定义生效
     */
    private Integer blockAssertType = 0;
    /**
     * 临时生效时阻断时间单位 0分钟 1小时 2天
     */
    private Integer blockUnit = 0;
    /**
     * 自定义生效时阻断开始时间
     */
    private String blockStartValue;
    /**
     * 临时生效时阻断结束时间
     * 自定义生效时阻断结束时间
     */
    private String blockEndValue;
    private String envId;
}
