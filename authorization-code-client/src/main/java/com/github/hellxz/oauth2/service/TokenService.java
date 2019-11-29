package com.github.hellxz.oauth2.service;

import com.github.hellxz.oauth2.config.OAuth2ClientProperties;
import com.github.hellxz.oauth2.config.OAuth2ServerProperties;
import com.github.hellxz.oauth2.consts.GrantType;
import com.github.hellxz.oauth2.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

@Component
public class TokenService {

    @Autowired
    private OAuth2ServerProperties auth2ServerProperties;

    @Autowired
    private OAuth2ClientProperties auth2ClientProperties;

    @Autowired
    private RestTemplate restTemplate;

    /**
     * 请求授权码url
     */
    public String getAuthorizeUrl() {
        HashMap<String, String> params = new HashMap<>();
        params.put("client_id", auth2ClientProperties.getClientId());
        params.put("redirect_uri", URLEncoder.encode(auth2ClientProperties.getRedirectUri()));
        params.put("response_type", auth2ClientProperties.getResponseType());
        params.put("scope", URLEncoder.encode(auth2ClientProperties.getScope()));
        return buildUrlStr(auth2ServerProperties.getAuthorizeUrl(), params);
    }

    /**
     * 请求授权token的url
     */
    public String getToken(String code) throws UnsupportedEncodingException {
        System.err.println(getHttpBody(code).toString() +" == "+ getHttpHeaders());
        HttpEntity<MultiValueMap> httpEntity = new HttpEntity<>(getHttpBody(code), getHttpHeaders());
        ResponseEntity<TokenDTO> exchange = restTemplate.exchange(auth2ServerProperties.getTokenUrl(), HttpMethod.POST, httpEntity, TokenDTO.class);
        if (exchange.getStatusCode().is2xxSuccessful()) {
            return Objects.requireNonNull(exchange.getBody()).getAccessToken();
        }
        throw new RuntimeException("请求令牌失败！");
    }

    private HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        System.err.println(auth2ClientProperties.getClientId()+":"+auth2ClientProperties.getClientSecret());
        httpHeaders.setBasicAuth(auth2ClientProperties.getClientId(), auth2ClientProperties.getClientSecret());
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        return httpHeaders;
    }

    private MultiValueMap<String, String> getHttpBody(String code) throws UnsupportedEncodingException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("grant_type", GrantType.AUTHORIZATION_CODE.getCode());
        params.add("redirect_uri", urlEncode(auth2ClientProperties.getRedirectUri()));
        params.add("scope", urlEncode(auth2ClientProperties.getScope()));
        return params;
    }

    private String urlEncode(String url){
        String encode = null;
        try {
            encode = URLEncoder.encode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("url encode failed in " + this.getClass().getName());
        }
        return encode;
    }

    /**
     * 构建url
     *
     * @param endpoint 服务器端点url
     * @param params   参数列表
     * @return 带参数的url
     */
    private String buildUrlStr(String endpoint, Map<String, String> params) {
        List<String> keyValueParam = new ArrayList<>(params.size());
        params.forEach((key, value) -> keyValueParam.add(key + "=" + value));
        return endpoint + "?" + keyValueParam.stream().reduce((a, b) -> a + "&" + b).get();
    }
}
