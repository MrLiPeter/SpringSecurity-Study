# 核心组件练习
UserResource.java
```java
   @GetMapping("/principal")
    public Principal getPrincipal(Principal principal){
        return principal;
    }

    @GetMapping("/authentication")
    public Authentication getAuthentication(){
        return SecurityContextHolder.getContext().getAuthentication();
    }
```

# 熟悉一下定制数据库
核心代码
```java
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username,password,enabled from lxh_users where username=?")//如果想通过name来登录,可以写成select name as username 。。。。
                .usersByUsernameQuery("select username,password,enabled from lxh_authorities where username=?")
                .passwordEncoder(passwordEncoder());
    }
```