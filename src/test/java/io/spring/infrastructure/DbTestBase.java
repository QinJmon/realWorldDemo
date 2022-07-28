package io.spring.infrastructure;

import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

/**
 * 定义测试的基类，封装常用的配置信息
 */
@MybatisTest//用于指定当前测试用于测试DAO层,默认启用数据库操作相关内容
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)//不使用默认的嵌入式数据库驱动，而是从配置文件中读取数据库连接信息
public abstract  class DbTestBase {
}
