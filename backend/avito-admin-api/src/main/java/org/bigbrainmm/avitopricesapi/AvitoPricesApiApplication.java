package org.bigbrainmm.avitopricesapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;
import java.util.List;

@SpringBootApplication
public class AvitoPricesApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AvitoPricesApiApplication.class, args);
    }

    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        int count = jdbcTemplate.queryForObject("select COUNT(*) from source_baseline", Integer.class);
        if (count > 0) return null;
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        count = jdbcTemplate.queryForObject("select count(*) from pg_user where usename='replicator';", Integer.class);
        if (count == 0) populator.addScript(new ClassPathResource("sql_init/00_init.sql"));
        populator.addScript(new ClassPathResource("sql_init/baseline_matrix_1.sql"));
        populator.addScript(new ClassPathResource("sql_init/baseline_matrix_2.sql"));
        populator.addScript(new ClassPathResource("sql_init/baseline_matrix_3.sql"));
        populator.addScript(new ClassPathResource("sql_init/discount_matrix_1.sql"));
        populator.addScript(new ClassPathResource("sql_init/discount_matrix_2.sql"));
        populator.addScript(new ClassPathResource("sql_init/discount_matrix_3.sql"));
        populator.addScript(new ClassPathResource("sql_init/init_jpa.sql"));
        DataSourceInitializer initializer = new DataSourceInitializer();
        initializer.setDataSource(dataSource);
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

}
