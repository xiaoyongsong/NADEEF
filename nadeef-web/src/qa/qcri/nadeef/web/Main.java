/*
 * QCRI, NADEEF LICENSE
 * NADEEF is an extensible, generalized and easy-to-deploy data cleaning platform built at QCRI.
 * NADEEF means "Clean" in Arabic
 *
 * Copyright (c) 2011-2013, Qatar Foundation for Education, Science and Community Development (on
 * behalf of Qatar Computing Research Institute) having its principle place of business in Doha,
 * Qatar with the registered address P.O box 5825 Doha, Qatar (hereinafter referred to as "QCRI")
 *
 * NADEEF has patent pending nevertheless the following is granted.
 * NADEEF is released under the terms of the MIT License, (http://opensource.org/licenses/MIT).
 */

package qa.qcri.nadeef.web;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import qa.qcri.nadeef.web.rest.model.JdbcRule;
import qa.qcri.nadeef.web.rest.model.RuleDao;

import javax.sql.DataSource;


@Configuration
class ApplicationContext {
    @Value("${database.url}") private String databaseUrl;
    @Value("${database.type}") private String databaseType;
    @Value("${database.username}") private String databaseUserName;
    @Value("${database.password}") private String databasePassword;
    @Value("${database.driverClass}") private String databaseDriverClass;

    @Bean(name = "dataSource")
    public DataSource getBasicDataSource() {
        BasicDataSource result = new BasicDataSource();
        result.setUrl(databaseUrl);
        result.setDriverClassName(databaseDriverClass);
        result.setUsername(databaseUserName);
        result.setPassword(databasePassword);
        return result;
    }

    @Bean
    @Autowired
    public RuleDao getRuleDao(DataSource dataSource) {
        return new JdbcRule(dataSource);
    }

}

@ComponentScan
@EnableAutoConfiguration
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}