package com.yk.db.jpa.service;

import com.yk.db.jpa.model.PageParam;
import com.yk.db.jpa.model.Role;
import com.yk.db.jpa.repository.RoleRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RoleService
{
    @Autowired
    private RoleRepository roleRepository;

    /**
     * JPA的分页查询4
     */
    public Page<Role> getRolesPage(PageParam pageParam, String roleName)
    {
        //规格定义
        Specification<Role> specification = new Specification<Role>()
        {
            private static final long serialVersionUID = 3816066239198641856L;

            /**
             * 构造断言
             *
             * @param root  实体对象引用
             * @param query 规则查询对象
             * @param cb    规则构建对象
             * @return 断言
             */
            @Override
            public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb)
            {
                List<Predicate> predicates = new ArrayList<>(); //所有的断言
                if (StringUtils.isNotBlank(roleName))
                { //添加断言
                    Predicate likeNickName = cb.like(root.get("name").as(String.class), roleName + "%");
                    predicates.add(likeNickName);
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };
        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        //分页信息
        Pageable pageable = PageRequest.of(pageParam.getCurrent(), pageParam.getPageSize(), sort); //页码：前端从1开始，jpa从0开始，做个转换
        //查询
        return this.roleRepository.findAll(specification, pageable);
    }

    /**
     * JPA的分页查询5
     */
    @PersistenceContext
    EntityManager entityManager;

    public Page<Role> getRolesPage2(PageParam pageParam, String roleName)
    {
        StringBuilder countSelectSql = new StringBuilder();
        countSelectSql.append("select count(*) from Role r where 1=1 ");

        StringBuilder selectSql = new StringBuilder();
        selectSql.append("from Role r where 1=1 ");

        Map<String, Object> params = new HashMap<>();
        StringBuilder whereSql = new StringBuilder();
        if (StringUtils.isNotBlank(roleName))
        {
            whereSql.append(" and name=:roleName ");
            params.put("roleName", roleName);
        }
        String countSql = String.valueOf(countSelectSql) + whereSql;
        Query countQuery = this.entityManager.createQuery(countSql, Long.class);
        this.setParameters(countQuery, params);
        Long count = (Long) countQuery.getSingleResult();

        String querySql = String.valueOf(selectSql) + whereSql;
        TypedQuery<Role> query = this.entityManager.createQuery(querySql, Role.class);
        this.setParameters(query, params);
        if (pageParam != null)
        {
            //分页
            query.setFirstResult(pageParam.getStart());
            query.setMaxResults(pageParam.getPageSize());
        }

        List<Role> roleList = query.getResultList();
        if (pageParam != null)
        {
            //分页
            Pageable pageable = PageRequest.of(pageParam.getCurrent(), pageParam.getPageSize());
            return new PageImpl<>(roleList, pageable, count);
        }
        else
        {
            //不分页
            return new PageImpl<>(roleList);
        }
    }

    /**
     * 给hql参数设置值
     *
     * @param query  查询
     * @param params 参数
     */
    private void setParameters(Query query, Map<String, Object> params)
    {
        for (Map.Entry<String, Object> entry : params.entrySet())
        {
            query.setParameter(entry.getKey(), entry.getValue());
        }
    }
}
