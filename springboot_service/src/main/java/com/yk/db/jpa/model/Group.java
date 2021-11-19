package com.yk.db.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/18 12:10:55
 */
@Entity
@Table(name = "t_group")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Group
{
    @Id
    @GeneratedValue(/*strategy = GenerationType.AUTO, */generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.yk.db.jpa.support.CustomIdGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "name", columnDefinition = "VARCHAR(255)")
    private String name;

    @OneToMany(mappedBy = "group")
    @JsonIgnore
    private Set<User> users = new HashSet<User>();
}
