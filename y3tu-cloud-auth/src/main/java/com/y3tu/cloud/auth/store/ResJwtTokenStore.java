package com.y3tu.cloud.auth.store;

import com.y3tu.tool.core.codec.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 资源服务器 TokenStore 配置类，使用 JWT RSA 非对称加密
 *
 * @author y3tu
 * @date 2018/9/29
 */
public class ResJwtTokenStore {
    private static final String PUBLIC_KEY = "pubkey.txt";
    @Autowired
    private ResourceServerProperties resource;

    /**
     * 获取非对称加密公钥 Key
     *
     * @return 公钥 Key
     */
    private String getPubKey() {
        Resource resource = new ClassPathResource(ResJwtTokenStore.PUBLIC_KEY);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            return br.lines().collect(Collectors.joining("\n"));
        } catch (IOException ioe) {
            return getKeyFromAuthorizationServer();
        }
    }

    /**
     * 通过访问授权服务器获取非对称加密公钥 Key
     *
     * @return 公钥 Key
     */
    private String getKeyFromAuthorizationServer() {
        final RestTemplate keyUriRestTemplate = new RestTemplate();
        final HttpHeaders headers = new HttpHeaders();
        final String username = this.resource.getClientId();
        final String password = this.resource.getClientSecret();
        if (username != null && password != null) {
            final byte[] token = Base64Util.encode((username + ":" + password).getBytes());
            headers.add("Authorization", "Basic " + new String(token));
        }
        final HttpEntity<Void> request = new HttpEntity<>(headers);
        final String url = this.resource.getJwt().getKeyUri();
        return (String) keyUriRestTemplate
                .exchange(url, HttpMethod.GET, request, Map.class).getBody()
                .get("value");
    }
}
