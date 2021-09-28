package com.demo.lambda;

import java.util.List;

/**
 * @Description: 评价指标实体类
 * @Author: jiafei
 * @Date: 2021/1/5 8:53
 * @Version: 1.0
 */
public class QuotaVo implements Comparable<QuotaVo>
{

    private int id;
    private int pid;
    private String name;

    private String explain;

//    private List<OptionsVo> options;

    private List<QuotaVo> children;


    @Override
    public int compareTo(QuotaVo o)
    {//重写Comparable接口的compareTo方法，// 根据id升序排列，降序修改相减顺序即可
        return this.getId() - o.getId();
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public int getPid()
    {
        return pid;
    }

    public void setPid(int pid)
    {
        this.pid = pid;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getExplain()
    {
        return explain;
    }

    public void setExplain(String explain)
    {
        this.explain = explain;
    }

//    public List<OptionsVo> getOptions()
//    {
//        return options;
//    }
//
//    public void setOptions(List<OptionsVo> options)
//    {
//        this.options = options;
//    }

    public List<QuotaVo> getChildren()
    {
        return children;
    }

    public void setChildren(List<QuotaVo> children)
    {
        this.children = children;
    }
}

