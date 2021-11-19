package com.yk.db.jpa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/04 17:45:43
 */
@Entity
@Table(name = "t_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Role implements GrantedAuthority
{
    private static final long serialVersionUID = 797300138759261437L;
    @Id
    @GeneratedValue(/*strategy = GenerationType.AUTO, */generator = "custom-id")
    @GenericGenerator(name = "custom-id", strategy = "com.yk.db.jpa.support.CustomIdGenerator")
    @Column(name = "id", columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();

    @Override
    @JsonIgnore
    public String getAuthority()
    {
        return name;
    }
}
