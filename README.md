
# 稀稀拉拉的备注
###文件本地上传方式
    upload.local.dir 指定服务器路径，直接nginx转发访问静态文件 路径  /file/xxxx
### rabbitmq 移除 
    替代品ApplicationEvent
### redis移除
    concurrenthashmap ,本地缓存，可能存在redis功能上的一些问题，待修复！！！
### 数据库的轻量化【待做】 
    sqllite?
### mine
    blog.jonk.top   blog.jonk.top/admin/
#部署

## 本地编译

    mvn clean  install -P dev


### docker运行 

* docke部署

    ```mvn clean  install -P test ```
     
    ``` docker built -t aurora .```
    
    ``` docker run --name aurora -p 8090:8090 -d aurora ```
  
* docker-compose部署
  
  ``` mvn clean  install -P test ```
   
  ``` docker 更新   docker-compose build  ```

  ``` docker 部署   docker-compose up -d  ```
