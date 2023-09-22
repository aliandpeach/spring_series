package com.yk.db.jpa.support;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentityGenerator;

import java.io.Serializable;
import java.util.UUID;

/**
 * 描述
 *
 * @author yangk
 * @version 1.0
 * @since 2021/11/04 17:50:50
 */
public class CustomIdGenerator extends IdentityGenerator
{
    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object)
    {
        Object id = ReflectionUtils.getFieldValue("id", object);
        if (id != null)
        {
            return (Serializable) id;
        }
        Serializable iid = super.generate(session, object);
        return UUID.randomUUID().toString().replace("-", "");
    }
}
