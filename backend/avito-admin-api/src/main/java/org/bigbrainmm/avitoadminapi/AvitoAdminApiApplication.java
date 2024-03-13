package org.bigbrainmm.avitoadminapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Точка входа в приложение
 */
@SpringBootApplication
public class AvitoAdminApiApplication {

    /**
     * The entry point of application.
     *
     * @param args the input arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(AvitoAdminApiApplication.class, args);
    }

    /**
     * Подгрузка первых данных, что дали вначале хака
     *
     * @param dataSource   the data source
     * @param jdbcTemplate the jdbc template
     * @return the data source initializer
     */
    @Bean
    public DataSourceInitializer dataSourceInitializer(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        int count = jdbcTemplate.queryForObject("select COUNT(*) from source_baseline", Integer.class);
        if (count > 0) return null;
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        jdbcTemplate.execute("CREATE OR REPLACE FUNCTION set_matrix_id() RETURNS TRIGGER AS $$ BEGIN NEW.id := NEW.location_id + 4108 * (NEW.microcategory_id - 1); RETURN NEW; END; $$ LANGUAGE plpgsql;");
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
