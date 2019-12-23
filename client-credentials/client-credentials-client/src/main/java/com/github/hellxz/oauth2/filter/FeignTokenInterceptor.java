package com.github.hellxz.oauth2.filter;

import com.github.hellxz.oauth2.dto.TokenDTO;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.codec.Charsets;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URI;
import java.util.*;

/**
 * Feign拦截器，为请求头上加上token
 */
@Configuration
public class FeignTokenInterceptor implements RequestInterceptor {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 请求头中没有Authorization的，调用资源服务器获取token置于请求头，使请求可被访问
     * 使用客户端模式
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        Map<String, Collection<String>> headers = requestTemplate.headers();
        if (CollectionUtils.isEmpty(headers) || !headers.containsKey("Authorization")) {
            String clientId = "user-center";
            String clientSecret = "12345";
            String s = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes(Charsets.UTF_8));
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setBasicAuth(s);
            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "client_credentials");
            RequestEntity<MultiValueMap<String, String>> requestEntity
                    = new RequestEntity<>(params, httpHeaders, HttpMethod.POST, URI.create("http://localhost:8080/oauth/token"));
            ResponseEntity<TokenDTO> exchange = restTemplate.exchange(requestEntity, TokenDTO.class);
            if (exchange.getStatusCode().is2xxSuccessful()) {
                requestTemplate.header("Authorization", "Bearer " + Objects.requireNonNull(exchange.getBody()).getAccessToken());
            }
        }
    }
}
