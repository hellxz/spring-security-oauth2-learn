# spring-security-oauth2-learn
## 仓库说明
此项目用于学习Spring Security OAuth2，Demo使用的Api为4.x及以前的，5.x版本后提示已经废弃，不过为了学习这问题不是很大
> 后续有时间会写一写

另外，本仓库均使用授权服务与资源服务分离的方式来编写demo，由于此二者放在同一服务时默认省了很多配置（资源服务器访问授权服务器进行校验token部分），对于后续应用自认为不利，遂添加

## 基础演示demo功能说明
> 以下Demo均使用内存保存token，仅用于测试

- [x] authorization-code : 授权码模式

- [x] client-credentials : 客户端模式

- [x] implicit: 隐式模式（简化模式）

- [x] password: 密码模式

## client端对接demo
- [x] authorization-code/authorization-code-client-resttemplate-jdbc : 使用RestTemplate和数据库实现的授权码模式手动对接客户端

## JWT
- [x] jwt-authorization-server : 添加JWT实现token的授权服务器，这里开启了授权码与密码模式，支持refresh_token
- [x] jwt-resource-server：资源服务器，本地校验jwt token，解析出用户信息

## 使用Redis存储token
- [x] redis-token-saved-authorization-server : 使用password与授权码模式，支持刷新token的授权服务器，token保存到redis中
- [x] redis-token-saved-resource-server : 使用redis校验token的资源服务器



