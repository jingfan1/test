package com.wangchao.config;


import javax.sql.DataSource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.druid.pool.DruidDataSource;
/**
 * druid配置类
 * @author wangchao
 *
 */
@Configuration
public class DruidConfig {
	
	@Bean
	@ConfigurationProperties(prefix="spring.dataSource")
	public DataSource druidDataSource() {
		DruidDataSource druidDataSource = new DruidDataSource();
		return druidDataSource;
	}
	
}
