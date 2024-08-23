#### 使用 Maven Profiles 进行打包
开发环境打包：
``
mvn clean package -Pdev -DskipTests
``

生产环境打包：
``
mvn clean package -Pprod -DskipTests
``
