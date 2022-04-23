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