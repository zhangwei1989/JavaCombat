## 实践 Part 1

- [x] 通过自研 Web MVC 框架实现（可以自己实现）一个用户注册，forward 到一个成功的页面（JSP 用法）/register
- [x] 通过 Controller -> Service -> Repository 实现（数据库实现）
- [x] （非必须）JNDI 的方式获取数据库源（DataSource），在获取 Connection

## 实践 Part 2
通过简易版依赖注入和依赖查找，实现用户注册功能：
- [x] 通过 UserService 实现用户注册
- [x] 注册用户需要校验
    - [x] Id：必须大于 0 的整数
    - [x] 密码：6-32 位
    - [x] 电话号码: 采用中国大陆方式（11 位校验）

## 实践 Part 3
### 一
- [ ] 整合 https://jolokia.org/。实现一个自定义 JMX MBean，通过 Jolokia 做 Servlet 代理

### 二
- 继续完成 Microprofile config API 中的实现
  - [x] 扩展 org.eclipse.microprofile.config.spi.ConfigSource 实现，包括 OS 环境变量，以及本地配置文件
  - [x] 扩展 org.eclipse.microprofile.config.spi.Converter 实现，提供 String 类型到简单类型
- 通过 org.eclipse.microprofile.config.Config 读取当前应用名称
  - [ ] 应用名称 property name = “application.name”


