package com.datasource;

import cn.hutool.core.util.HexUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.datasource.transaction.DataSourceTransactionManager;
import com.yk.crypto.BinHexSHAUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static cn.hutool.core.thread.ThreadUtil.sleep;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2022/06/16 11:45:44
 */
public class DatasourceTest
{
    private static final String QUERY_INDEX_SQL = "select a.owner, a.index_name, a.uniqueness, b.column_name,b.table_name from" +
            "  dba_indexes a, dba_ind_columns b where a.index_name = b.index_name and a.owner in (?) and  a.table_name = ? and a.owner = b.index_owner  " +
//			"and not exists ( select 1 from dba_constraints t where t.constraint_type = 'P' and t.owner = ?  and t.constraint_name = a.index_name) "+
            " order by b.column_position";
    private static final String QUERY_TABLE_COLUMN = "SELECT A.TABLE_NAME,A.COLUMN_NAME,A.DATA_TYPE,A.DATA_LENGTH,B.COMMENTS,A.COLUMN_ID,'COMMON_A' AS OWNER FROM " +
            "USER_TAB_COLUMNS A, USER_COL_COMMENTS B, USER_TABLES C  WHERE A.TABLE_NAME = B.TABLE_NAME AND A.COLUMN_NAME = B.COLUMN_NAME AND A.TABLE_NAME = C.TABLE_NAME  AND NOT EXISTS (select 1 from user_tab_cols c  where c.virtual_column = 'YES' and a.table_name = c.table_name and a.column_name = c.column_name) " +
            "and A.TABLE_NAME in ('DATA_1000_1_YangKai')";

    private static final String QUERY_DBA_USERS = "select * from dba_users where ACCOUNT_STATUS = 'OPEN' and username not in ('SYSDBA', 'SYS', 'SYSTEM') order by username";

    private static final Map<String, String> results = new HashMap<>();

    public static void main(String[] args) throws Exception
    {
        queryMysqlDate();
    }

    public static void testGeometry() throws Exception
    {
        byte[] bytes2 = Base64.getDecoder().decode("AAAAAAEDAAAAAQAAAAUAAAAEAADAc2JeQA8AAGAMOD1A+///Tx9jXKAEAAAYZC89QP///9P1YI5A8///72DNPUAeAABmRmJeQAkAAPjBZT1ABAAAWHNiXkAPAABgDNA9QA==");
        byte[] bytes3 = Base64.getDecoder().decode(new byte[]{65, 65, 65, 65, 65, 65, 69, 68, 65, 65, 65, 65, 65, 81, 65, 65, 65, 65, 81, 65, 65, 65, 68, 56, 97, 85, 90, 100, 106, 78, 53, 100, 81, 80, 101, 84, 47, 48, 56, 117, 100, 122, 120, 65, 56, 87, 110, 71, 122, 89, 122, 101, 88, 85, 68, 47, 107, 47, 47, 49, 76, 88, 99, 56, 81, 79, 57, 112, 82, 117, 83, 77, 51, 108, 49, 65, 68, 112, 84, 47, 116, 121, 57, 51, 80, 69, 68, 56, 97, 85, 90, 100, 106, 78, 53, 100, 81, 80, 101, 84, 47, 48, 56, 117, 100, 122, 120, 65});

        byte [] error = Base64.getDecoder().decode("AAAAAAEGAAAAAgAAAAEDAAAAAQAAAAEAAADaAGxAhBZeQKYpApzedT5AAQMAAAABAAAABQAAAO7/24JsFl5ADgAg3eZ1PkD2/9vpfRZeQAQAILXwdT5A4//bl4QWXkABACD9e3U+QAIA3JtxFl5ACgAgoW51PkDu/9uCbBZeQA4AIN3mdT5A");
        System.out.println(HexUtil.encodeHexStr(error));
        // select ST_AsText(X'');可将该几何对象转为文本, 但是无法被ST_GeomFromWKB解析

        String polygox = "POLYGON ((119.75 30.2906, 119.754 30.2919, 119.758 30.295, 119.763 30.303, 119.766 30.3112, 119.767 30.3165, 119.766 30.3205, 119.767 30.323, 119.767 30.3261, 119.766 30.3324, 119.766 30.3347, 119.767 30.3363, 119.773 30.3371, 119.78 30.3365, 119.783 30.3374, 119.787 30.3394, 119.792 30.34, 119.796 30.3386, 119.798 30.3366, 119.8 30.3336, 119.801 30.3299, 119.803 30.3268, 119.803 30.3254, 119.801 30.3218, 119.801 30.3211, 119.802 30.3204, 119.803 30.319, 119.803 30.3171, 119.803 30.3146, 119.801 30.3107, 119.8 30.307, 119.798 30.3028, 119.798 30.3007, 119.8 30.2982, 119.803 30.2968, 119.805 30.2964, 119.809 30.2971, 119.814 30.3012, 119.819 30.3033, 119.823 30.3062, 119.825 30.3074, 119.829 30.3074, 119.834 30.3073, 119.838 30.306, 119.84 30.3068, 119.844 30.3104, 119.857 30.3055, 119.86 30.3054, 119.861 30.3062, 119.86 30.3087, 119.861 30.3099, 119.861 30.3099, 119.864 30.308, 119.865 30.3129, 119.878 30.3135, 119.875 30.3095, 119.88 30.3103, 119.885 30.309, 119.887 30.3116, 119.894 30.3093, 119.895 30.3081, 119.896 30.306, 119.907 30.306, 119.903 30.3115, 119.894 30.3117, 119.891 30.3129, 119.886 30.3149, 119.889 30.3179, 119.89 30.3214, 119.89 30.3265, 119.891 30.3274, 119.894 30.3274, 119.895 30.3265, 119.896 30.3263, 119.898 30.3278, 119.896 30.3299, 119.895 30.3327, 119.899 30.3316, 119.9 30.336, 119.905 30.3384, 119.906 30.3423, 119.908 30.3437, 119.91 30.3419, 119.916 30.344, 119.923 30.3397, 119.929 30.338, 119.933 30.3382, 119.935 30.3403, 119.934 30.3425, 119.931 30.3432, 119.928 30.3454, 119.942 30.3527, 119.946 30.3529, 119.946 30.3537, 119.946 30.3556, 119.948 30.3572, 119.95 30.3548, 119.95 30.3531, 119.948 30.3502, 119.948 30.348, 119.95 30.3464, 119.961 30.3468, 119.967 30.3442, 119.97 30.3447, 119.981 30.3398, 119.989 30.3381, 120 30.3375, 120.021 30.3391, 120.022 30.3337, 120.022 30.3318, 120.024 30.3284, 120.023 30.3249, 120.02 30.3223, 120.016 30.3188, 120.017 30.3146, 120.018 30.3134, 120.017 30.3114, 120.018 30.3094, 120.022 30.3058, 120.026 30.3003, 120.034 30.3032, 120.039 30.3013, 120.041 30.3041, 120.046 30.3023, 120.047 30.3004, 120.051 30.2984, 120.045 30.292, 120.043 30.2876, 120.041 30.2771, 120.038 30.261, 120.038 30.2541, 120.039 30.2443, 120.024 30.2416, 119.997 30.2386, 119.988 30.2365, 119.981 30.2358, 119.976 30.2357, 119.973 30.2367, 119.967 30.2421, 119.963 30.2464, 119.951 30.2507, 119.942 30.253, 119.938 30.252, 119.926 30.2459, 119.921 30.2437, 119.914 30.2435, 119.907 30.2443, 119.899 30.247, 119.894 30.2487, 119.889 30.2493, 119.881 30.2499, 119.872 30.2514, 119.863 30.2512, 119.861 30.2479, 119.848 30.2475, 119.839 30.2464, 119.83 30.2432, 119.824 30.2393, 119.817 30.2324, 119.809 30.2272, 119.803 30.2243, 119.795 30.2225, 119.787 30.2174, 119.782 30.2075, 119.778 30.2022, 119.774 30.1998, 119.767 30.1988, 119.763 30.2001, 119.761 30.2039, 119.758 30.2134, 119.755 30.2178, 119.753 30.2196, 119.745 30.2222, 119.733 30.2249, 119.729 30.2274, 119.728 30.2384, 119.724 30.2488, 119.721 30.2555, 119.717 30.2613, 119.716 30.2654, 119.716 30.2681, 119.726 30.2781, 119.738 30.2864, 119.745 30.2894, 119.75 30.2906))";
        String polygon = "MULTIPOLYGON(((120.351822 30.460428)),((120.350372996181 30.4605539515615,120.351435150951 30.4607041552663,120.351842846721 30.4589231684804,120.350684132427 30.4587193205953,120.350372996181 30.4605539515615,)))";
        insertFromText(polygox);
        String queryAsText = queryAsText();
        System.out.println("queryAsText compare to original, result = " + polygon.equals(queryAsText));

        byte[] queryST_GeomFromWKB = queryST_GeomFromWKB();
        byte[] queryST_AsBinary = queryST_AsBinary();
        byte[] queryAsBytes = queryAsBytes();

        System.out.println("queryST_GeomFromWKB compare to queryAsBytes, result = " + Arrays.equals(queryST_GeomFromWKB, queryAsBytes));

        byte[] temp = new byte[queryST_AsBinary.length + 4];
        byte[] zero = new byte[]{0, 0, 0, 0};
        System.arraycopy(queryST_AsBinary, 0, temp, 4, queryST_AsBinary.length);
        System.arraycopy(zero, 0, temp, 0, 4);
        System.out.println("queryST_GeomFromWKB compare to queryST_AsBinary, result = " + Arrays.equals(queryST_GeomFromWKB, temp));

        // ST_AsBinary 查询的结果通过 ST_GeomFromWKB 写入数据库
        insertFromWKBBytes(queryST_AsBinary);
        System.out.println(HexUtil.encodeHexStr(queryST_AsBinary));
        insertFromBytes(queryST_GeomFromWKB);
        System.out.println(HexUtil.encodeHexStr(queryST_GeomFromWKB));
    }

    public static void insertFromBytes(byte[] bytes) throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("update one.data_100w_1_YangKai_target set addr_geometry = ?");

        statement.setBytes(1, bytes);
        statement.addBatch();

        statement.executeBatch();
        statement.close();
        conn.close();
    }

    public static void insertFromText(String polygon) throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("update one.data_100w_1_YangKai_target set addr_geometry = ST_GeomFromText(?)");

        statement.setString(1, polygon);
        statement.addBatch();

        statement.executeBatch();
        statement.close();
        conn.close();
    }

    public static void insertFromWKBBytes(byte[] queryST_AsBinary) throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("update one.data_100w_1_YangKai_target set addr_geometry = ST_GeomFromWKB(?)");

        String polygon = "00000000010300000001000000B90000000000000000F05D40B537F8C2644A3E40C74B378941F05D40D0B359F5B94A3E408D976E1283F05D40EC51B81E854B3E4046B6F3FDD4F05D4021B07268914D3E401B2FDD2406F15D40E4839ECDAA4F3E400C022B8716F15D401B2FDD2406513E401B2FDD2406F15D40355EBA490C523E400C022B8716F15D40A69BC420B0523E400C022B8716F15D40C139234A7B533E401B2FDD2406F15D40BE30992A18553E401B2FDD2406F15D40A1F831E6AE553E400C022B8716F15D4012A5BDC117563E40B6F3FDD478F15D404A7B832F4C563E4052B81E85EBF15D40A01A2FDD24563E40273108AC1CF25D409FABADD85F563E40EE7C3F355EF25D402D431CEBE2563E40A69BC420B0F25D40D7A3703D0A573E406DE7FBA9F1F25D40F46C567DAE563E40508D976E12F35D4067D5E76A2B563E403333333333F35D4013F241CF66553E402506819543F35D404DF38E5374543E4008AC1C5A64F35D403255302AA9533E4008AC1C5A64F35D404F1E166A4D533E402506819543F35D4051DA1B7C61523E402506819543F35D40E0BE0E9C33523E4017D9CEF753F35D406EA301BC05523E4008AC1C5A64F35D408B6CE7FBA9513E4008AC1C5A64F35D40C58F31772D513E4008AC1C5A64F35D40545227A089503E402506819543F35D4001DE02098A4F3E403333333333F35D403BDF4F8D974E3E40508D976E12F35D40933A014D844D3E40508D976E12F35D403EE8D9ACFA4C3E403333333333F35D40CEAACFD5564C3E4008AC1C5A64F35D40EB73B515FB4B3E40EC51B81E85F35D40CE88D2DEE04B3E40B29DEFA7C6F35D4040A4DFBE0E4C3E406ABC749318F45D40228E75711B4D3E4023DBF97E6AF45D4076E09C11A54D3E40E9263108ACF45D4003098A1F634E3E40CDCCCCCCCCF45D4058CA32C4B14E3E40931804560EF55D4058CA32C4B14E3E404C37894160F55D40910F7A36AB4E3E401283C0CAA1F55D4075931804564E3E40F6285C8FC2F55D40AD69DE718A4E3E40BC74931804F65D40ACADD85F764F3E40022B8716D9F65D4091ED7C3F354E3E40D7A3703D0AF75D40CA32C4B12E4E3E40C976BE9F1AF75D4003098A1F634E3E40D7A3703D0AF75D40744694F6064F3E40C976BE9F1AF75D40C8073D9B554F3E40C976BE9F1AF75D40C8073D9B554F3E409EEFA7C64BF75D40022B8716D94E3E408FC2F5285CF75D401CEBE2361A503E40D578E92631F85D40C74B378941503E400000000000F85D40AC1C5A643B4F3E40B81E85EB51F85D40E5F21FD26F4F3E40713D0AD7A3F85D40C976BE9F1A4F3E4054E3A59BC4F85D40006F8104C54F3E40F0A7C64B37F95D401EA7E8482E4F3E40E17A14AE47F95D40C9E53FA4DF4E3E40D34D621058F95D4075931804564E3E40355EBA490CFA5D4075931804564E3E406F1283C0CAF95D4039B4C876BE4F3E40F0A7C64B37F95D40C7293A92CB4F3E401B2FDD2406F95D401CEBE2361A503E4062105839B4F85D40AA8251499D503E4037894160E5F85D40FE65F7E461513E40295C8FC2F5F85D4035EF384547523E40295C8FC2F5F85D40DD24068195533E401B2FDD2406F95D40DDB5847CD0533E40F0A7C64B37F95D40DDB5847CD0533E40E17A14AE47F95D40DD24068195533E40D34D621058F95D404FAF946588533E40B6F3FDD478F95D40F9A067B3EA533E40D34D621058F95D404DF38E5374543E40E17A14AE47F95D401361C3D32B553E40A8C64B3789F95D40865AD3BCE3543E409A99999999F95D40BC74931804563E4052B81E85EBF95D4066F7E461A1563E40448B6CE7FBF95D40BA6B09F9A0573E40273108AC1CFA5D409CA223B9FC573E400AD7A3703DFA5D409D8026C286573E40B4C876BE9FFA5D40F2D24D6210583E40508D976E12FB5D4082734694F6563E40FA7E6ABC74FB5D404A0C022B87563E40C1CAA145B6FB5D40D881734694563E40A4703D0AD7FB5D402CD49AE61D573E40B29DEFA7C6FB5D4048E17A14AE573E40DD24068195FB5D40B9FC87F4DB573E4008AC1C5A64FB5D40D50968226C583E403F355EBA49FC5D40984C158C4A5A3E40068195438BFC5D4027C286A7575A3E40068195438BFC5D405F984C158C5A3E40068195438BFC5D402575029A085B3E40E9263108ACFC5D4096218E75715B3E40CDCCCCCCCCFC5D40ED9E3C2CD45A3E40CDCCCCCCCCFC5D40B537F8C2645A3E40E9263108ACFC5D40280F0BB5A6593E40E9263108ACFC5D400C022B8716593E40CDCCCCCCCCFC5D409B559FABAD583E402FDD240681FD5D40B84082E2C7583E40D9CEF753E3FD5D408048BF7D1D583E40AE47E17A14FE5D4063EE5A423E583E40105839B4C8FE5D40492EFF21FD563E409EEFA7C64BFF5D4011C7BAB88D563E400000000000005E406666666666563E40D34D621058015E40D712F241CF563E40C520B07268015E40DAACFA5C6D553E40C520B07268015E4014D044D8F0543E40A8C64B3789015E40A301BC0512543E40B6F3FDD478015E406C787AA52C533E40E17A14AE47015E403480B74082523E401B2FDD2406015E40FDF675E09C513E400C022B8716015E40545227A089503E40FED478E926015E4000917EFB3A503E400C022B8716015E4072F90FE9B74F3E40FED478E926015E40E561A1D6344F3E40C520B07268015E40E71DA7E8484E3E408B6CE7FBA9015E4022FDF675E04C3E401904560E2D025E40AF25E4839E4D3E40D122DBF97E025E40E9482EFF214D3E40B4C876BE9F025E40AEB6627FD94D3E406DE7FBA9F1025E40AF946588634D3E405EBA490C02035E40E9B7AF03E74C3E402506819543035E405C2041F1634C3E407B14AE47E1025E40986E1283C04A3E40986E1283C0025E4061545227A0493E40B4C876BE9F025E40BBB88D06F0463E40DF4F8D976E025E40894160E5D0423E40DF4F8D976E025E40E2E995B20C413E40D122DBF97E025E40AD69DE718A3E3E40A8C64B3789015E40AEB6627FD93D3E402B8716D9CEFF5D405BD3BCE3143D3E40AC1C5A643BFF5D40068195438B3C3E40105839B4C8FE5D40956588635D3C3E405839B4C876FE5D40CEAACFD5563C3E4083C0CAA145FE5D4094F6065F983C3E40D9CEF753E3FD5D40925CFE43FA3D3E401283C0CAA1FD5D4002BC0512143F3E40BE9F1A2FDDFC5D40711B0DE02D403E403F355EBA49FC5D4054E3A59BC4403E4079E9263108FC5D408D976E1283403E402506819543FB5D401E166A4DF33E3E406DE7FBA9F1FA5D4003098A1F633E3E40D122DBF97EFA5D4075931804563E3E40355EBA490CFA5D40AD69DE718A3E3E40A8C64B3789F95D40AC1C5A643B3F3E40F0A7C64B37F95D40E4839ECDAA3F3E4037894160E5F85D408FE4F21FD23F3E40AAF1D24D62F85D4039454772F93F3E402B8716D9CEF75D40E3361AC05B403E40AC1C5A643BF75D4055C1A8A44E403E40C976BE9F1AF75D40ACADD85F763F3E4083C0CAA145F65D408FC2F5285C3F3E4004560E2DB2F55D4002BC0512143F3E4085EB51B81EF55D402063EE5A423E3E40DBF97E6ABCF45D40CCEEC9C3423D3E403F355EBA49F45D402497FF907E3B3E40B29DEFA7C6F35D40B5A679C7293A3E4008AC1C5A64F35D40287E8CB96B393E407B14AE47E1F25D40295C8FC2F5383E40EE7C3F355EF25D408126C286A7373E40355EBA490CF25D4085EB51B81E353E406F1283C0CAF15D404F401361C3333E40A8C64B3789F15D40A5BDC11726333E400C022B8716F15D40DE718A8EE4323E4046B6F3FDD4F05D40FAEDEBC039333E4062105839B4F05D4087A757CA32343E408D976E1283F05D4066F7E461A1363E40B81E85EB51F05D409D11A5BDC1373E40D578E92631F05D409C33A2B437383E4048E17A14AEEF5D40D42B6519E2383E40F4FDD478E9EE5D40D3DEE00B93393E402DB29DEFA7EE5D40431CEBE2363A3E403BDF4F8D97EE5D40CC5D4BC8073D3E407593180456EE5D40AB3E575BB13F3E40A01A2FDD24EE5D40C520B07268413E40D9CEF753E3ED5D40DE718A8EE4423E40E7FBA9F1D2ED5D40C05B2041F1433E40E7FBA9F1D2ED5D40BF0E9C33A2443E405839B4C876EE5D408104C58F31473E40AC1C5A643BEF5D400C93A98251493E4048E17A14AEEF5D4060764F1E164A3E400000000000F05D40B537F8C2644A3E40";
        statement.setBytes(1, queryST_AsBinary);
        statement.addBatch();

        statement.executeBatch();
        statement.close();
        conn.close();
    }

    public static String queryAsText() throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("select ST_AsText(addr_geometry) from one.data_100w_1_YangKai_target  limit 1");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
        {
            Object out = resultSet.getObject(1);// String
            return String.valueOf(out);
        }
        statement.close();
        conn.close();
        return null;
    }

    /**
     * queryST_GeomFromWKB == queryAsBytes
     */
    public static byte[] queryST_GeomFromWKB() throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("select ST_GeomFromWKB(addr_geometry) from one.data_100w_1_YangKai_target limit 1");
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next())
        {
            Object out = resultSet.getObject(1); // byte[]
            return (byte[]) out;
        }
        statement.close();
        conn.close();
        return null;
    }

    /**
     * 查询出来的byte[] 比queryAsBytes和queryST_GeomFromWKB的结果数组少首位的4个0
     */
    public static byte[] queryST_AsBinary() throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("select ST_AsBinary(addr_geometry) from one.data_100w_1_YangKai_target limit 1");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
        {
            Object out = resultSet.getObject(1); // byte[]
            return (byte[]) out;
        }
        statement.close();
        conn.close();
        return null;
    }

    /**
     * queryST_GeomFromWKB == queryAsBytes
     */
    public static byte[] queryAsBytes() throws Exception
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("root");

        Connection conn = dataSourceMysql.getConnection();
        PreparedStatement statement = conn.prepareStatement("select addr_geometry from one.data_100w_1_YangKai_target limit 1");
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next())
        {
            Object out = resultSet.getObject(1); // byte[]
            return (byte[]) out;
        }
        statement.close();
        conn.close();
        return null;
    }

    public static void queryAllMysql() throws Exception
    {
        String aaa = "select * from SYS_CODE_LIST where code_type = 'class_path'";
        String bbb = "select * from SYS_CODE_LIST where code_type = 'rule_type'";
        String ccc = "select * from T_MATCH_RULE";
        String ddd = "select * from T_RULE_REGULAR";
        String eee = "select * from T_DESENSITIZE_RULE";
        String fff = "select * from T_DESENSITIZE_CONFIG";

        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.168.20.252:3306/datatest?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("Admin@0123");
        dataSourceMysql.setDbType("mysql");

        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("DATATEST");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");

        Connection connection = dataSourceMysql.getConnection();
        PreparedStatement statement = connection.prepareStatement(aaa);
        ResultSet _rs = statement.executeQuery();
        while (_rs.next())
        {

        }
    }

    private static void queryCancelPrivs() throws SQLException
    {
        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("SYS AS SYSDBA");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");
        List<String> privileges = new ArrayList<>();
        try (Connection connection = dataSourceOracle.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement("SELECT privilege from dba_sys_privs where grantee = 'COMMON_A' ORDER BY PRIVILEGE ASC");
            ResultSet _r = statement.executeQuery();
            while (_r.next())
            {
                String privilege = _r.getString(1);
                privileges.add(privilege);
            }
        }
        List<String> results = privileges.stream().map(t -> "REVOKE " + t + " FROM \"COMMON_A\"").collect(Collectors.toList());
//        System.out.println(result);
        try (Connection connection = dataSourceOracle.getConnection())
        {
            Statement statement = connection.createStatement();
            for (String sql : results)
            {
                if (sql.contains("REVOKE CREATE SESSION FROM"))
                {
                    continue;
                }
                statement.addBatch(sql);
            }
            statement.executeBatch();
            connection.commit();
        }
    }

    private static void queryPrivilegesByRoleName() throws SQLException
    {
        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("SYS AS SYSDBA");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");
        List<String> privileges = new ArrayList<>();
        try (Connection connection = dataSourceOracle.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement("SELECT privilege from dba_sys_privs where grantee = 'RESOURCE' ORDER BY PRIVILEGE ASC");
            ResultSet _r = statement.executeQuery();
            while (_r.next())
            {
                String privilege = _r.getString(1);
                privileges.add(privilege);
            }
        }
        System.out.println(privileges);
    }
    private static void queryTableColumn() throws SQLException
    {
        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("COMMON_A");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");
        List<String> privileges = new ArrayList<>();
        try (Connection connection = dataSourceOracle.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(QUERY_TABLE_COLUMN);
            ResultSet _r = statement.executeQuery();
            while (_r.next())
            {
                String a = _r.getString(1);
                String b = _r.getString(2);
                String c = _r.getString(3);
                privileges.add(b);
            }
        }
        System.out.println(privileges);
    }
    private static void queryDbaUsers() throws SQLException
    {
        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.170.23.163:1521:orcl");
        dataSourceOracle.setUsername("SYS AS SYSDBA");
        dataSourceOracle.setPassword("Spinfo0123");
        dataSourceOracle.setDbType("oracle");
        List<String> privileges = new ArrayList<>();
        try (Connection connection = dataSourceOracle.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(QUERY_DBA_USERS);
            ResultSet _r = statement.executeQuery();
            while (_r.next())
            {
                String a = _r.getString(1);
                privileges.add(a);
            }
        }
        System.out.println(privileges);
    }

    private static void queryUsers() throws SQLException
    {
        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("COMMON_A");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");
        List<String> privileges = new ArrayList<>();
        try (Connection connection = dataSourceOracle.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement("select username from dba_users where account_status = 'OPEN' and username not in ( 'SYSDBA', 'SYS' , 'SYSTEM')  order by userName");
            ResultSet _r = statement.executeQuery();
            while (_r.next())
            {
                String privilege = _r.getString(1);
                privileges.add(privilege);
            }
        }
        System.out.println(privileges);
    }

    private static void queryIndex() throws SQLException
    {
        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("TEST_ONE");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");
        try (Connection connection = dataSourceOracle.getConnection())
        {
            PreparedStatement statement = connection.prepareStatement(QUERY_INDEX_SQL);
            statement.setObject(1, "TEST_FIVE,TEST_THREE,TEST_TWO".split(","));
            statement.setString(1, "FIVE_DATA_1000_1");
            ResultSet _r = statement.executeQuery();
            while (_r.next())
            {
                String a = _r.getString(1);
                String b = _r.getString(2);
                String c = _r.getString(3);
                String d = _r.getString(4);
            }
        }
    }

    public void moveMysqlData2Oracle()
    {
        DruidDataSource dataSourceMysql = new DruidDataSource();
        dataSourceMysql.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceMysql.setUrl("jdbc:mysql://192.168.31.15:3306/datatest?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSourceMysql.setUsername("root");
        dataSourceMysql.setPassword("Admin@0123");
        dataSourceMysql.setDbType("mysql");

        DruidDataSource dataSourceOracle = new DruidDataSource();
        dataSourceOracle.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSourceOracle.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
        dataSourceOracle.setUsername("DATATEST");
        dataSourceOracle.setPassword("Admin#0123");
        dataSourceOracle.setDbType("oracle");

        BlockingQueue<SysBaseValue> queue = new LinkedBlockingQueue<>(100000);
        AtomicInteger count = new AtomicInteger(0);
        AtomicBoolean finished = new AtomicBoolean(false);

        new Thread(() ->
        {
            try (Connection connection = dataSourceMysql.getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT ID, CODE, CONVERT(AES_DECRYPT(UNHEX(VALUE),'salt') USING utf8)  as VALUE, BTYPE, REMARK from SYS_BASE_VALUE", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY))
            {
                statement.setFetchSize(Integer.MIN_VALUE);
                ResultSet _r = statement.executeQuery();
                while (_r.next())
                {
                    String id = _r.getString("ID");
                    int code = _r.getInt("CODE");
                    String value = _r.getString("VALUE");
                    String btype = _r.getString("BTYPE");
                    String remark = _r.getString("REMARK");
                    SysBaseValue sysBaseValue = new SysBaseValue(id, code, value, btype, remark);
                    queue.put(sysBaseValue);
                    count.incrementAndGet();
                }
            }
            catch (SQLException | InterruptedException e)
            {
                e.printStackTrace();
            }
            finished.set(true);
        }).start();

        new Thread(() ->
        {
            List<SysBaseValue> list = new ArrayList<>();
            while (true)
            {
                SysBaseValue sysBaseValue = queue.poll();
                if (null != sysBaseValue)
                    list.add(sysBaseValue);
                if (list.size() == 100000)
                {
                    try (Connection connection = dataSourceOracle.getConnection();
                         PreparedStatement statement = connection.prepareStatement("INSERT INTO SYS_BASE_VALUE_2 (ID, CODE, VALUE, BTYPE, REMARK) values (?, ?, ?, ?, ?)"))
                    {
                        for (SysBaseValue _value : list)
                        {
                            statement.setString(1, _value.getId());
                            statement.setInt(2, _value.getCode());
                            statement.setString(3, _value.getValue());
                            statement.setString(4, _value.getBytpe());
                            statement.setString(5, _value.getRemark());
                            statement.addBatch();
                        }
                        statement.executeBatch();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    list.clear();
                }
                if (list.size() < 100000 && finished.get() && list.size() > 0 && queue.size() == 0)
                {
                    try (Connection connection = dataSourceOracle.getConnection();
                         PreparedStatement statement = connection.prepareStatement("INSERT INTO SYS_BASE_VALUE_2 (ID, CODE, VALUE, BTYPE, REMARK) values (?, ?, ?, ?, ?)"))
                    {
                        for (SysBaseValue _value : list)
                        {
                            statement.setString(1, _value.getId());
                            statement.setInt(2, _value.getCode());
                            statement.setString(3, _value.getValue());
                            statement.setString(4, _value.getBytpe());
                            statement.setString(5, _value.getRemark());
                            statement.addBatch();
                        }
                        statement.executeBatch();
                    }
                    catch (SQLException e)
                    {
                        e.printStackTrace();
                    }
                    list.clear();
                    break;
                }
            }
        }).start();
    }

    @Data
    @AllArgsConstructor
    public static class SysBaseValue
    {
        private String id;
        private int code;
        private String value;
        private String bytpe;
        private String remark;
    }


    public DataSource create() throws Exception
    {
        Properties props = new Properties();
        props.put("useSSL", "false");
        props.put("useUnicode", "true");
        props.put("characterEncoding", "UTF-8");
        props.put("characterSetResults", "UTF-8");

        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://192.190.20.252:13307/one?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&serverTimezone=GMT%2B8&useSSL=false&allowMultiQueries=true");
        properties.setProperty("username", "root");
        properties.setProperty("password", "root");
        properties.setProperty("dbType", "mysql");

        DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        druidDataSource.setBreakAfterAcquireFailure(true);
        druidDataSource.setConnectionErrorRetryAttempts(0);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setConnectProperties(props);
        return druidDataSource;
    }

    public DataSource dataSource() throws SQLException
    {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://192.168.20.251:3306/test?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setDbType("mysql");

//        dataSource.setDriverClassName("com.pivotal.jdbc.GreenplumDriver");
//        dataSource.setUrl("jdbc:pivotal:greenplum://192.170.24.41:55432;DatabaseName=sdm_source");
//        dataSource.setUsername("gpadmin");
//        dataSource.setPassword("gpadmin");
//        dataSource.setDbType("mysql");

//        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//        dataSource.setUrl("jdbc:oracle:thin:@192.168.31.19:1521:orcl");
//        dataSource.setUsername("TEST_TWO");
//        dataSource.setPassword("Admin#0123");
//        dataSource.setDbType("oracle");
        //最大连接池数量
        dataSource.setMaxActive(100);
        // 初始化时建立物理连接的个数
        dataSource.setInitialSize(5);
        // 最小连接池数量
        dataSource.setMinIdle(5);
        // 获取连接时最大等待时间，单位毫秒
        dataSource.setMaxWait(60000);
        // 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        // 连接保持空闲而不被驱逐的最小时间
        dataSource.setMinEvictableIdleTimeMillis(300000);
        // 用来检测连接是否有效的sql，要求是一个查询语句
//        dataSource.setValidationQuery("SELECT version()");
        // 建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。
        dataSource.setTestWhileIdle(true);
        // 申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnBorrow(false);
        // 归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。
        dataSource.setTestOnReturn(false);
        // 是否缓存preparedStatement，也就是PSCache。PSCache对支持游标的数据库性能提升巨大，比如说oracle。在mysql下建议关闭。
        dataSource.setPoolPreparedStatements(false);
        // 要启用PSCache，必须配置大于0，当大于0时，poolPreparedStatements自动触发修改为true。
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(0);
        // 配置监控统计拦截的filters，去掉后监控界面sql无法统计
        dataSource.setFilters("stat,wall");
        // 通过connectProperties属性来打开mergeSql功能；慢SQL记录
        dataSource.setConnectionProperties("druid.stat.mergeSql=true;druid.stat.slowSqlMillis=500");
        // 合并多个DruidDataSource的监控数据
        dataSource.setUseGlobalDataSourceStat(true);

//        dataSource.setRemoveAbandoned(true);
//        dataSource.setRemoveAbandonedTimeout(1800);
        dataSource.setLogAbandoned(true);
        dataSource.init();
        return dataSource;
    }

    public void insert(DataSource dataSource, int index) throws SQLException
    {
        DataSourceTransactionManager dataSourceTransactionManager = null;

        System.out.println("===========index===========：" + index);
        System.out.println("最大连接数：" + ((DruidDataSource) dataSource).getMaxActive());
        System.out.println("最小连接数：" + ((DruidDataSource) dataSource).getMinIdle());
        System.out.println("当前总连接数：" + ((DruidDataSource) dataSource).getActiveCount() + ((DruidDataSource) dataSource).getPoolingCount());
        System.out.println("当前活跃连接数：" + ((DruidDataSource) dataSource).getActiveCount());
        System.out.println("当前闲置连接数：" + ((DruidDataSource) dataSource).getPoolingCount());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO \"data_test_insert\" (\"id\", \"name\", \"index\") VALUES (?,?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                /*PreparedStatement statement2 = connection.prepareStatement("INSERT INTO \"data_test_insert\" (\"id\", \"name\", \"index\") VALUES (?,?,?)", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);*/)
        {
            dataSourceTransactionManager = new DataSourceTransactionManager(connection);
            connection.setAutoCommit(false);

            long start = System.currentTimeMillis();
            dataSourceTransactionManager.beginTransaction();
            for (int i = 0; i < 10000; i++)
            {
                statement.setString(1, UUID.randomUUID().toString().replace("-", ""));
                statement.setString(2, "name" + i);
                statement.setInt(3, index);
                statement.addBatch();
            }
            statement.executeBatch();
            System.out.println("time = " + (System.currentTimeMillis() - start));

//            if (index == 5)
//            {
//                statement2.setString(1, UUID.randomUUID().toString());
//                statement2.setString(2, "NAME");
//                statement2.setInt(3, index);
//                statement2.execute();
//            }

            dataSourceTransactionManager.commit();
        }
        catch (Exception e)
        {
            if (null != dataSourceTransactionManager)
            {
                dataSourceTransactionManager.rollback();
            }
            e.printStackTrace();
        }
//        finally
//        {
//            closeStatement(statement2);
//            closeStatement(statement);
//            doCloseConnection(connection, dataSource);
//        }
    }

    /**
     * 测试批量更新（表名不一样的场景, 由于PreparedStatement需要预编译, 不能把表名作为参数传入, 所以不行）
     * 改用createStatement的addBatch
     */
    public static void testBatchInsert2() throws SQLException
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = null;
        try
        {
            dataSource = datasourceTest.dataSource();
        }
        catch (Exception throwables)
        {
            throwables.printStackTrace();
        }

        List<Map<String, Object>> list = new ArrayList<>();

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("SELECT * FROM test_1");
             PreparedStatement ps2 = connection.prepareStatement("SELECT * FROM test_2");
        )
        {
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getString("id"));
                map.put("name", rs.getString("name"));
                map.put("tableName", "test_1");
                list.add(map);
            }
            ResultSet rs2 = ps2.executeQuery();
            while (rs2.next())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs2.getString("id"));
                map.put("name", rs2.getString("name"));
                map.put("tableName", "test_2");
                list.add(map);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
        )
        {
            for (Map<String, Object> e : list)
            {
                String _sql = "UPDATE " + e.get("tableName") + " set name = '" + e.get("name").toString() + "_" + e.get("id") + "' where id = " + e.get("id").toString();
                System.out.println(_sql);
                statement.addBatch(_sql);
            }
            statement.executeBatch();
        }
        System.out.println();
    }

    public static void testOracleSelect(String[] args) throws SQLException
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = null;
        try
        {
            dataSource = datasourceTest.dataSource();
        }
        catch (Exception throwables)
        {
            throwables.printStackTrace();
        }
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("select a.owner, a.index_name, a.uniqueness, b.column_name,b.table_name from  dba_indexes a, dba_ind_columns b where a.index_name = b.index_name and a.owner in (?) and  a.table_name = ? and a.owner = b.index_owner   order by b.column_position", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        )
        {
            System.out.println(Arrays.stream("TEST_TWO".trim().split(",")).collect(Collectors.joining("','", "'", "'")));
            ps.setString(1, Arrays.stream("TEST_TWO".trim().split(",")).collect(Collectors.joining(",")));
            ps.setString(2, "DATA_1000W_1");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
            {
                Map<String, Object> map = new HashMap<>();
                map.put("1", rs.getString("index_name"));
                map.put("2", rs.getString("column_name"));
                map.put("3", rs.getString("table_name"));
                map.put("4", rs.getString("uniqueness").equals("UNIQUE"));
                map.put("5", rs.getString("owner"));
                System.out.println(map);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static void testBatchInsert() throws SQLException
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        final DataSource dataSource = datasourceTest.dataSource();
        ExecutorService service = Executors.newFixedThreadPool(5);
//        datasourceTest.insert(dataSource, 999999);
        for (int index = 1; index <= 1000; index++)
        {
            final int i = index;
            service.execute(() ->
            {
                try
                {
                    datasourceTest.insert(dataSource, i);
                }
                catch (SQLException throwables)
                {
                    throwables.printStackTrace();
                }
            });
        }
    }

    public static void testQueryDFetchSize()
    {
        /*try
        {
            DatasourceTest datasourceTest = new DatasourceTest();
            DataSource dataSource = datasourceTest.dataSource();

            Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("insert into test (`id`) values (?)");
            ps.setString(1, System.currentTimeMillis() + "_A");
            ps.execute();
            ps.close();
//            connection.close();
            sleep(120 * 1000);

//            connection = dataSource.getConnection();
            PreparedStatement _ps = connection.prepareStatement("insert into test (`id`) values (?)");
            _ps.setString(1, System.currentTimeMillis() + "_B");
            _ps.execute();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }*/
        final Runtime r = Runtime.getRuntime();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    sleep(15000);
                    System.out.println("maxMemory = " + (r.maxMemory() / (1024 * 1024))
                            + ", totalMemory = " + (r.totalMemory() / (1024 * 1024))
                            + ", freeMemory = " + (r.freeMemory() / (1024 * 1024)));
                }
            }
        });
        executorService.execute(new Runnable()
        {
            @Override
            public void run()
            {
                DatasourceTest datasourceTest = new DatasourceTest();
                DataSource dataSource = null;
                try
                {
                    dataSource = datasourceTest.create();
                }
                catch (Exception throwables)
                {
                    throwables.printStackTrace();
                }
                try (Connection connection = dataSource.getConnection();
                     PreparedStatement ps = connection.prepareStatement(
                             "SELECT * FROM `data_100w_1_YangKai`",
                             ResultSet.TYPE_FORWARD_ONLY,
                             ResultSet.CONCUR_READ_ONLY);
                )
                {
                    long start = System.currentTimeMillis();
//                    Statement ps = connection.createStatement();
                    ps.setFetchSize(Integer.MIN_VALUE);
                    ResultSet set = ps.executeQuery();
//                    set.setFetchSize(100);
                    System.out.println((System.currentTimeMillis() - start));

                    AtomicLong lo = new AtomicLong();
                    ResultSetMetaData _meta = set.getMetaData();
                    int _columnCount = _meta.getColumnCount();
                    while (set.next())
                    {
                        Map<String, Object> map = new HashMap<>();
                        for (int i = 1; i <= _columnCount; i++)
                        {
                            String a = set.getString(i);
                            map.put(_meta.getColumnLabel(i), a);
                        }
                        ((DruidPooledConnection) set.getStatement().getConnection()).setConnectedTimeNano(System.nanoTime());
                        lo.incrementAndGet();
                        Thread.sleep(500 * 1000);
                    }
                    System.out.println("  =============    " + lo.get());
                }
                catch (Throwable e)
                {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void testTiDBQueryDFetchSize()
    {
        final Runtime r = Runtime.getRuntime();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        executorService.execute(() ->
        {
            while (true)
            {
                sleep(15000);
                System.out.println("maxMemory = " + (r.maxMemory() / (1024 * 1024))
                        + ", totalMemory = " + (r.totalMemory() / (1024 * 1024))
                        + ", freeMemory = " + (r.freeMemory() / (1024 * 1024)));
            }
        });
        executorService.execute(() ->
        {
            DatasourceTest datasourceTest = new DatasourceTest();
            DataSource dataSource = null;
            try
            {
                dataSource = datasourceTest.createTiDB();
            }
            catch (Exception throwables)
            {
                throwables.printStackTrace();
            }
            try (Connection connection = dataSource.getConnection();
                 PreparedStatement ps = connection.prepareStatement(
                         "SELECT * FROM `data_100w_1_YangKai`",
                         ResultSet.TYPE_FORWARD_ONLY,
                         ResultSet.CONCUR_READ_ONLY);
            )
            {
                long start = System.currentTimeMillis();
//                    Statement ps = connection.createStatement();
                ps.setFetchSize(Integer.MIN_VALUE);
                ResultSet set = ps.executeQuery();
//                    set.setFetchSize(100);
                System.out.println((System.currentTimeMillis() - start));

                AtomicLong lo = new AtomicLong();
                ResultSetMetaData _meta = set.getMetaData();
                int _columnCount = _meta.getColumnCount();
                while (set.next())
                {
                    Map<String, Object> map = new HashMap<>();
                    for (int i = 1; i <= _columnCount; i++)
                    {
                        String a = set.getString(i);
                        map.put(_meta.getColumnLabel(i), a);
                    }
                    ((DruidPooledConnection) set.getStatement().getConnection()).setConnectedTimeNano(System.nanoTime());
                    lo.incrementAndGet();
                    Thread.sleep(500 * 1000);
                }
                System.out.println("  =============    " + lo.get());
            }
            catch (Throwable e)
            {
                e.printStackTrace();
            }
        });
    }

    public DataSource createTiDB() throws Exception
    {
        Properties props = new Properties();
        props.put("useSSL", "false");
        props.put("useUnicode", "true");
        props.put("characterEncoding", "UTF-8");
        props.put("characterSetResults", "UTF-8");

        Properties properties = new Properties();
        properties.setProperty("driverClassName", "com.mysql.cj.jdbc.Driver");
        properties.setProperty("url", "jdbc:mysql://192.170.23.166:4000/YangKai?autoReconnect=true&useUnicode=true&characterEncoding=UTF-8&useSSL=false&allowMultiQueries=true");
        properties.setProperty("username", "root");
        properties.setProperty("password", "root");
        properties.setProperty("dbType", "mysql");

        DruidDataSource druidDataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);
        druidDataSource.setBreakAfterAcquireFailure(true);
        druidDataSource.setConnectionErrorRetryAttempts(0);
        druidDataSource.setValidationQuery("select 1");
        druidDataSource.setConnectProperties(props);
        return druidDataSource;
    }

    public static void closeStatement(Statement stmt)
    {
        if (stmt != null)
        {
            try
            {
                stmt.close();
            }
            catch (SQLException ex)
            {
            }
            catch (Throwable ex)
            {
            }
        }
    }

    public static void doCloseConnection(Connection con, DataSource dataSource) throws SQLException
    {
        if (null != con)
        {
            con.close();
        }
    }

    public static void importMysql() throws Exception
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = datasourceTest.create();
        String sql = "LOAD DATA LOCAL INFILE 'F:\\\\SDM\\\\data\\\\one.data_100w_1_YangKai_target.csv' INTO TABLE `one`.`data_100w_1_YangKai_target` FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n' IGNORE 1 LINES (`id`, `NAME`, `sex`, `age`, `email`, `tel`, `address`, `id_Card`, `birthday`, `photo_dir`, `face_photo_`, `view_photo_`, @addr_geometry) SET addr_geometry=UNHEX(@addr_geometry);";
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.execute();
        System.out.println();
    }

    public static void queryMysqlBlob() throws Exception
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = datasourceTest.create();
        String sql1 = "update `one`.`data_100w_1_backup` set logo_file = ?";
        String sql2 = "update `one`.`data_100w_1_backup` set txt_file = ?";
        String sql3 = "update `one`.`data_100w_1_backup` set image_file = ?";
        String sql4 = "update `one`.`data_100w_1_backup` set video_file = ?";
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql1);
        byte[] param = new byte[10];
        new Random().nextBytes(param);
        statement.setBytes(1, param);
        statement.execute();
        System.out.println();

        statement = connection.prepareStatement(sql2);
        param = new byte[10];
        new Random().nextBytes(param);
        statement.setBytes(1, param);
        statement.execute();
        System.out.println();

        statement = connection.prepareStatement(sql3);
        param = new byte[10];
        new Random().nextBytes(param);
        statement.setBytes(1, param);
        statement.execute();
        System.out.println();

        statement = connection.prepareStatement(sql4);
        param = new byte[10];
        new Random().nextBytes(param);
        statement.setBytes(1, param);
        statement.execute();
        System.out.println();

        for (String _sql : new String[]{"`logo_file`", "`txt_file`", "`image_file`", "`video_file`"})
        {
            String sqlQuery = "select " + _sql + " from `one`.`data_100w_1_backup` limit 1";
            statement = connection.prepareStatement(sqlQuery);
            ResultSet _result = statement.executeQuery();
            if (_result.next())
            {
                System.out.println(Arrays.toString(_result.getBytes(1)));
            }
        }
    }


    public static void insertMysqlYearDate() throws Exception
    {
        Date year = new SimpleDateFormat("yyyy").parse("2021");
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = datasourceTest.create();
        Connection connection = dataSource.getConnection();
        String sql1 = "update `one`.`data_100w_1_YangKai` set `year_date` = ?";
        PreparedStatement statement = connection.prepareStatement(sql1);
        LocalDate localDate = year.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        statement.setObject(1, localDate.getYear());
        statement.execute();
    }

    public static void queryMysqlDate() throws Exception
    {
        DatasourceTest datasourceTest = new DatasourceTest();
        DataSource dataSource = datasourceTest.create();
        String sql1 = "select * from `one`.`data_100w_1_YangKai` limit 1";
        Connection connection = dataSource.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql1);
        ResultSet resultSet = statement.executeQuery();
        ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
        int count = resultSetMetaData.getColumnCount();
        if (resultSet.next())
        {
            Map<String, Object> map = new HashMap<>();
            for (int i = 1; i <= count; i++)
            {
                Object obj = resultSet.getObject(i);
                String name = resultSetMetaData.getColumnName(i);
                if (obj instanceof java.util.Date)
                {
                    System.out.println("java.util.Date : " + name);
                }
                if (obj instanceof java.sql.Date)
                {
                    System.out.println("java.sql.Date : " + name);
                }
                if (obj instanceof java.sql.Time)
                {
                    System.out.println("java.sql.Time : " + name);
                }
                if (obj instanceof LocalDateTime)
                {
                    System.out.println("LocalDateTime : " + name);
                }
                map.put(name, obj);
            }
            System.out.println(map);
        }
    }
}
