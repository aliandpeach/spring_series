package com.yk.base.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 在spring中配置WebMvc时有两种方法，一种是继承WebMvcConfigurationSupport，重写里面相应的方法，还有一种是继承WebMvcConfigurer的子抽象类WebMvcConfigurerAdapter，也是重写里面相应的方法，但是需要在配置类上添加@EnableWebMvc注解。
 * WebMvcConfigurationSupport中那些子类可以重写的空方法在WebMvcConfigurer都有，这说明WebMvcConfigurer只是WebMvcConfigurationSupport的一个扩展类，它并没有扩展新功能，只是为让用户更方便安全的添加自定义配置，为什么说是安全呢？因为如果直接继承WebMvcConfigurationSupport，那么用户可以重写默认的配置，如果对原理不是很清楚地开发者不小心重写了默认的配置，springmvc可能相关功能就无法生效，是一种不安全的行为。但如果是继承WebMvcConfigurerAdapter，那么开发者是在默认配置的基础上添加自定义配置，相对来说更安全一些，只不过要多加一个@EnableWebMvc注解。从这个角度来说，最佳实践还是继承WebMvcConfigurerAdapter
 * EnableWebMvc注解类上导入了DelegatingWebMvcConfiguration类，该类是WebMvcConfigurationSupport的子类，该类除了实例化WebMvcConfigurationSupport实例以外，另一个作用就是收集BeanFactory中所有WebMvcConfigurer的实现，汇集到WebMvcConfigurerComposite中，在WebMvcConfigurationSupport实例化过程中会分别调用这些实现，将相应的实例传入这些实现中，供开发者在此基础上添加自定义的配置。这也就是在WebMvcConfigurerAdapter子类上要加@EnableWebMvc的原因，因为要先实例化WebMvcConfigurationSupport
 *
 *
 * 在spring boot的自定义配置类继承 WebMvcConfigurationSupport 后，
 * 发现自动配置的静态资源路径（classpath:/META/resources/，classpath:/resources/，classpath:/static/，classpath:/public/）不生效。
 * 这是因为在 springboot的web自动配置类 WebMvcAutoConfiguration 上有条件注解 @ConditionalOnMissingBean(WebMvcConfigurationSupport.class)
 * 如果想要使用自动配置生效，又要按自己的需要重写某些方法，比如增加 viewController ，则可以自己的配置类可以继承 WebMvcConfigurerAdapter (已过时) 但是可以实现WebMvcConfigurer并重写相关方法来达到类似的功能。
 *
 * SpringBoot做了这个限制，只有当WebMvcConfigurationSupport类不存在的时候才会生效WebMvc自动化配置，WebMvc自动配置类中不仅定义了classpath:/META-INF/resources/，classpath:/resources/，classpath:/static/，classpath:/public/等路径的映射，还定义了配置文件spring.mvc开头的配置信息等。
 *
 * 当 WebMvcAutoConfiguration 不生效时会导致以下几个问题：
 * 1.WebMvcProperties 和 ResourceProperties 失效 因为两个配置类中的属性都在 WebMvcAutoConfiguration 中使用
 * 当WebMvc自动配置失效(WebMvcAutoConfiguration自动化配置)时，会导致无法视图解析器无法解析并返回到对应的视图
 *
 * 2.类路径上的 HttpMessageConverter 失效
 *
 * https://blog.csdn.net/She_lock/article/details/86241685
 * https://blog.csdn.net/zhangpower1993/article/details/89016503
 * https://www.jianshu.com/p/bf9be49eb79b
 *
 * EnableWebMvc=WebMvcConfigurationSupport，使用了@EnableWebMvc注解等于扩展了WebMvcConfigurationSupport但是没有重写任何方法，而这里上面恰恰使用了@EnableWebMvc,因此重写的方法都失效了，注释之后就好了
 * 在使用2.0版本的springboot的时候 使用WebMvcConfigurationSupport类配置拦截器时一定要重写addResourceHandlers来实现静态资源的映射,不要使用application.properties中添加配置来实现映射，不然资源会映射不成功导致打开页面资源一直加载不到
 */
@Configuration
public class BaseWebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private StringHttpMessageConverter stringHttpMessageConverter;
    @Autowired
    private MappingJackson2HttpMessageConverter httpMessageConverter;

    /**
     * 添加转换器
     */
    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (int i = 0; i < converters.size(); i++) {
            if (converters.get(i) instanceof StringHttpMessageConverter) {
                converters.set(i, stringHttpMessageConverter);
            }
            if (converters.get(i) instanceof MappingJackson2HttpMessageConverter) {
                converters.set(i, httpMessageConverter);
            }
        }
    }
}
