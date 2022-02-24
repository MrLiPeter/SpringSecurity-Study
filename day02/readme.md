# 复习http请求结构
- 请求行
- 请求头
- 请求体

如果以下请出现403错误
```http request
POST  http://localhost:8080/api/greeting?name="李寻欢" HTTP/1.1
Authorization: Basic user  82331631-20da-4e9d-be2a-114492065200
```
请禁用csrf防护
```java
http.csrf(csrf->csrf.disable()) //禁用csrf防护
```