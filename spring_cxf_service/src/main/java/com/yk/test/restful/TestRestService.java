package com.yk.test.restful;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

@Path("/rest")
@Service
public class TestRestService implements InitializingBean
{

    @Path("/test")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response test()
    {
        Response.ResponseBuilder builder = Response.ok();
        Map<String, String> result = new HashMap<String, String>();
        result.put("test", "test");
        builder.entity(result);
        return builder.build();
    }

    @Path("/test")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> test(String string)
    {
        Map<String, String> result = new HashMap<String, String>();
        result.put("test", "map");
        return result;
    }

    @Path("/test/{val}")
    @GET
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String test1(@PathParam("val") String val)

    {
        return "test" + val;
    }

    @Override
    public void afterPropertiesSet() throws Exception
    {
        RestfulPublish.getInstance().addObjects(this);
        RestfulPublish.getInstance().addClazzs(TestRestService.class);
    }
}
