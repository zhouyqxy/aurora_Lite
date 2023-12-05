#部署

## 本地运行

```mvn clean  install -P dev ```


### docker运行 
  * docke部署

    ```mvn clean  install -P test ```
 
    ``` docker built -t aurora .```

    ``` docker run --name aurora -p 8090:8090 -d aurora ```
  
  * docker-compose部署
    ``` mvn clean  install -P test ```
  * 
    ``` docker 更新   docker-compose build  ```
  * 
    ``` docker 部署   docker-compose up -d  ```
