# JWT
这部分demo并非是OAuth2授权模式之一，是使用JWT作为token的演示代码

可以看到，变化的可以只有授权服务器

## JWT概念
JWT是一种自包含的加密token，整体分为三部分：
- Header : 标头通常由两部分组成：令牌的类型（即JWT）和所使用的签名算法，例如HMAC SHA256或RSA。标头通常由两部分组成：令牌的类型（即JWT）和所使用的签名算法，例如HMAC SHA256或RSA。
- Payload : 令牌的第二部分是有效负载，其中包含声明。声明是有关实体（通常是用户）和其他数据的声明。共有三种类型的claims：registered, public, and private claims.。
- Signature : 要创建签名部分，您必须获取编码的Header，编码的有效Payload，密钥，标头中指定的算法，并对其进行签名。
### JWT结构
> 每个json块使用Base64加密，然后使用`.`连接在一起  

其结构为`Header.Payload.Signature`

e.g.  
![](../../pictures/encoded-jwt3.png)
### Header
Header这部分包含令牌所用的算法（alg：algorithm, 常用的有HMAC SHA256或RSA）和 令牌的类型（typ: Token Type，即JWT）
未被加密前的样子就像
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```
Header部分使用Base64Url进行加密
### Payload
Payload这部分包含token的信息，这部分可以包含 已被注册信息、公有的信息、私有的信息，提供了很多可以自定义的部分  
官方给了一些推荐使用的已定义的参数名(已被注册信息)：
- "iss" (Issuer) Claim
- "sub" (Subject) Claim
- "aud" (Audience) Claim
- "exp" (Expiration Time) Claim
- "nbf" (Not Before) Claim
- "iat" (Issued At) Claim
- "jti" (JWT ID) Claim
公有的信息、私有的信息都是自定义的，注意不要与官方给的冲突即可，另外注意保持参数尽量少且不要暴露隐私数据

Payload部分使用Base64Url进行加密
### Signature
签名部分使用Header中定义的算法与密钥，把经过Base64Url加密的`Header.Payload`进行加密，得出签名。用来验证此token可信


## 使用方式
与普通的token使用一样，在请求头中添加`Authorization:Bearer 你的token`

## 使用JWT的好处
不使用session保持会话，实现无状态，从而容易扩展，对于分布式和微服务应用特别多