# 深度定制化的数据库  
## 实现 UserDetails 和 GrantedAuthority
- 把jdbc依赖改为jpa依赖
```xml
 <!--jdbc-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jdbc</artifactId>
</dependency>
```
```xml
 <!--jpa-->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
- application.yml
```yml
jpa:
    database! h2
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
  #自动建表和删除表
      ddl-auto: create-drop
```
schema.sql删除掉，不用自己写sql建表了

## UserDetailsService 和 UserDetailsPasswordService




