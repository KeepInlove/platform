package com.gxy.generator;

import com.mybatisflex.codegen.Generator;
import com.mybatisflex.codegen.config.ColumnConfig;
import com.mybatisflex.codegen.config.GlobalConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * @author guo
 * @version 1.0
 * @description: 自动生产代码
 * @date 2023/10/15 20:52
 */
public class Codegen {

    public static void main(String[] args) {
        //配置数据源
        HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/test?characterEncoding=UTF8&autoReconnect=true&serverTimezone=Asia/Shanghai");
//        dataSource.setUsername("root");
//        dataSource.setPassword("root");
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setJdbcUrl("jdbc:postgresql://localhost:5432/talenthubDB");
        dataSource.setUsername("postgres");
        dataSource.setPassword("postgres");

        //创建配置内容，两种风格都可以。
//        GlobalConfig globalConfig = createGlobalConfigUseStyle1();
        GlobalConfig globalConfig = createGlobalConfigUseStyle2();

        //通过 datasource 和 globalConfig 创建代码生成器
        Generator generator = new Generator(dataSource, globalConfig);

        //生成代码
        generator.generate();
    }

    public static GlobalConfig createGlobalConfigUseStyle1() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();
        //设置根包
        globalConfig.setBasePackage("com.gxy");
        /**
         * 设置表前缀和只生成哪些表
         */
        globalConfig.setTablePrefix("tb_");
        globalConfig.setGenerateTable("tb_user");

        //设置生成 entity 并启用 Lombok
        globalConfig.setEntityGenerateEnable(true);
        globalConfig.setEntityWithLombok(true);

        //设置生成 mapper
        globalConfig.setMapperGenerateEnable(true);

        //可以单独配置某个列
        ColumnConfig columnConfig = new ColumnConfig();
        columnConfig.setColumnName("tenant_id");
        columnConfig.setLarge(true);
        columnConfig.setVersion(true);
        globalConfig.setColumnConfig("tb_test", columnConfig);

        return globalConfig;
    }

    public static GlobalConfig createGlobalConfigUseStyle2() {
        //创建配置内容
        GlobalConfig globalConfig = new GlobalConfig();
        /*
         注解
        */
        globalConfig.getJavadocConfig()
                .setAuthor("Gxy")
                .setSince("1.0.0");
        //设置根包
        globalConfig.getPackageConfig()
                .setSourceDir("D://work//code//platform//generator//src//main//java//com//gxy//generator")
                .setBasePackage("com.gxy")
                .setEntityPackage(globalConfig.getBasePackage() + ".entity")
                .setMapperPackage(globalConfig.getBasePackage() + ".mapper.postgresql")
                .setServicePackage(globalConfig.getBasePackage() + ".service")
                .setServiceImplPackage(globalConfig.getBasePackage() + ".service.impl")
//                .setControllerPackage(globalConfig.getBasePackage() + ".controller")
                .setMapperXmlPath("D://work//code//platform//generator//src//main/resources/mapper");

        //设置表前缀和只生成哪些表，setGenerateTable 未配置时，生成所有表
        globalConfig.getStrategyConfig()
                .setGenerateTable("candidate");

        //设置生成 entity 并启用 Lombok
        globalConfig.enableEntity()
                .setWithLombok(true);

        //设置生成 mapper
        globalConfig.enableMapper();
        globalConfig.enableMapperXml();

        //设置生成service
        globalConfig.enableService();
        globalConfig.enableServiceImpl();
        //设置生成controller
        globalConfig.enableController();
//        GeneratorFactory.registerGenerator("html",new HtmlGenerator());

        return globalConfig;
    }
}