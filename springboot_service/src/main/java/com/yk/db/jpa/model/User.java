package com.yk.db.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/04 17:43:28
 */
@Entity
@Table(name = "t_user")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User implements Serializable
{
    private static final long serialVersionUID = -7378584157032232356L;
    @Id
    @GeneratedValue(/*strategy = GenerationType.AUTO, */generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.yk.db.jpa.support.CustomIdGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "name")
    private String name;

    @Size(min = 8, message = "Minimum password length: 8 characters")
    @JsonIgnore
    private String passwd;

    /**
     * CascadeType.MERGE
     *
     * 当CascadeType.PERSIST和CascadeType.MERGE都配上时，cascade = {CascadeType.PERSIST, CascadeType.MERGE}才会出现网上所说的那种，如果角色存在，才会报角色重复的异常
	 *   java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'admin' for key 'UK_iubw515ff0ugtm28p8g3myt0h'
	 * 在ManyToMany中轻易不要使用cascade = CascadeType.REMOVE 这种级联关系
	 * 在ManyToMany中不要cascade = {CascadeType.PERSIST, CascadeType.MERGE}，让这两个同时出现
	 * 在ManyToMany绝对不要使用 cascade = CascadeType.ALL
	 * 推荐单独使用cascade = {CascadeType.MERGE}即可
     *
     * FetchType 管读取，CascadeType 管增删改级联
     */
    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "t_user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"), inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "group_id")
    private Group group;
}
