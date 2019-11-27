# 授权码模式Demo
## 1. 获取授权码
1. 用户（资源拥有者）浏览器访问  
`http://localhost:8080/oauth/authorize?client_id=client-a&redirect_uri=http://localhost:9001/callback&response_type=code&scope=read_user_info`  
    > 这里未使用state
2. 使用hellxz用户登录
3. 选择`Approve`，点`Authorize`提交，
重定向到`http://localhost:9001/callback?code=TJb4Pd`，这里的code后边的值就是授权码

    > 理论上这个重定向的url应该客户端服务器接收进行回调
## 2. 授权码换令牌
> 换取令牌操作为服务器进行操作的，这里登录用户需要是客户端的标识与secret

发送Post请求到`http://localhost:8080/oauth/token`

> 下面的请求头中的`Authorization:Basic Y2xpZW50LWE6Y2xpZW50LWEtc2VjcmV0`
> 使用的是Postman的`Authorization`中的Basic,username=client-a，password=client-a-secret

```
curl -X POST \
  http://localhost:8080/oauth/token \
  -H 'Authorization: Basic Y2xpZW50LWE6Y2xpZW50LWEtc2VjcmV0' \
  -d 'code=TJb4Pd&grant_type=authorization_code&redirect_uri=http%3A%2F%2Flocalhost%3A9001%2Fcallback&scope=read_user_info'
```

获得响应
```json
{
    "access_token": "3ed92b8a-86f0-4ee1-b309-7db0b513f554",
    "token_type": "bearer",
    "expires_in": 43199,
    "scope": "read_user_info"
}
```

## 3. 校验Token

通过登录后，我们可以先调用授权服务的`/oauth/check_token`是否可以正常使用  
发送Post请求
`http://localhost:8080/oauth/check_token?token=c8553de5-d5bc-4a68-a49e-c9e2bada11e4&client_id=client-a&client_secret=client-a-secret`

返回请求体：
```json
{
    "aud": [
        "resource1"
    ],
    "active": true,
    "exp": 1574724686,
    "user_name": "hellxz",
    "client_id": "client-a",
    "scope": [
        "read_user_info"
    ]
}
```
至此，我们确保了授权服务器的接口是正常的

## 4. 使用token访问受保护的资源
发起请求
```bash
curl -X GET http://localhost:8081/user/hellxz001 \
     -H 'Authorization: Bearer ca3c1d03-67b8-4e39-b2b3-0d634f094610'
```
> 这里的access_token使用 Authorization作Key，Bearer +{access_token} 注意Bearer后边有一个空格

返回结果
```json
{
    "username": "hellxz001",
    "email": "hellxz001@foxmail.com"
}
```

## 授权码流程梳理
1. 用户通过客户端(浏览器等)进行 访问资源服务的资源，客户端拼接请求授权服务器的url重定向给用户
2. 用户访问到授权服务器页面，用户登录，并进行授权
3. 授权通过，授权服务器返回code（授权码）到回调的url上，客户端收到授权码，带上授权码向授权服务器发起请求, 获取token
4. 授权服务器根据传递过来的客户端的client_id、client_secret、授权码进行校验，校验通过返回access_token
5. 客户端收到token，将token保存在本地，用户每次请求带上Authorization，通过token访问具体的资源

> 这里暂未写客户端（浏览器这部分），通过手动获取资源以了解授权码模式整体授权流程