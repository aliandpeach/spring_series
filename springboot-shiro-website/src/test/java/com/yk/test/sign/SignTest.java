package com.yk.test.sign;

import com.yk.httprequest.JSONUtil;
import com.yk.user.model.User;
import com.yk.user.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SignTest
{
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserService userService;

    @Test
    public void testSignin() throws Exception
    {
        String tmp = System.getProperty("user.dir");
        User user = new User();
        user.setUsername("yk");
        user.setPasswd("123456");
        String _user = JSONUtil.toJson(user);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/signin")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(_user)).andReturn();
                /*.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());*/
        int status = mvcResult.getResponse().getStatus();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(mvcResult.getResponse().getHeader("Authorization"));
        System.out.println(result);
    }

    public void testSignup() throws Exception
    {
        User user = new User();
        user.setUsername("yk" + System.currentTimeMillis());
        user.setPasswd("123456");
        String _user = JSONUtil.toJson(user);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/signup")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(_user)).andReturn();
                /*.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());*/
        int status = mvcResult.getResponse().getStatus();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void testQueryUserList() throws Exception
    {
        String tmp = System.getProperty("user.dir");
        String _auth = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ5ayIsImF1dGgiOlsiUk9MRV9BRE1JTjpBREQsREVMRVRFLE1PRElGWSxRVUVSWSIsIlJPTEVfQ0xJRU5UOlFVRVJZIl0sImlhdCI6MTY2NTcyODE0MiwiZXhwIjoxNjY1NzMxNzQyfQ.kKlJmUPzOBszvljlWMw-wKX8MlVc6YDCXjOqTtLBZ8w";
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/user/list")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).header("Authorization", _auth))
                .andReturn();
                /*.andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());*/
        int status = mvcResult.getResponse().getStatus();
        String result = mvcResult.getResponse().getContentAsString();
        System.out.println(result);
    }

    @Test
    public void testCrypt() throws Exception
    {
        System.out.println(new BCryptPasswordEncoder(12).encode("123456"));
    }
}
