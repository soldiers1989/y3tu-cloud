package com.y3tu.cloud.gateway.config;

import com.y3tu.tool.web.redis.RedisRepository;
import com.y3tu.tool.web.redis.RedisUtils;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.serviceregistry.Registration;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.discovery.DiscoveryClientRouteLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuht
 * @date 2018/5/15
 * 动态路由配置类
 */
@Configuration
public class DynamicRouteConfiguration {
    private Registration registration;
    private DiscoveryClient discovery;
    private ZuulProperties zuulProperties;
    private ServerProperties server;
    private RedisUtils redisUtils;

    public DynamicRouteConfiguration(Registration registration, DiscoveryClient discovery,
                                     ZuulProperties zuulProperties, ServerProperties server, RedisUtils redisUtils) {
        this.registration = registration;
        this.discovery = discovery;
        this.zuulProperties = zuulProperties;
        this.server = server;
        this.redisUtils = redisUtils;
    }

    @Bean
    public DiscoveryClientRouteLocator dynamicRouteLocator() {
        return new DynamicRouteLocator(
                server.getServlet().getPath()
                , discovery
                , zuulProperties
                , registration
                , redisUtils);
    }
}
