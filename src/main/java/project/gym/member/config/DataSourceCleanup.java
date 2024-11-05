package project.gym.member.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Component
public class DataSourceCleanup {

    private final HikariDataSource dataSource;

    public DataSourceCleanup(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @PreDestroy
    public void cleanup() {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}