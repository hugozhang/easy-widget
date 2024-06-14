package me.about.widget.otel.zipkin.spring;


import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.io.Serializable;

@EnableConfigurationProperties
@Configuration
@ConfigurationProperties("zipkin.storage.mysql")
public class ZipkinMySQLStorageProperties implements Serializable {
    private static final long serialVersionUID = 0L;

    private String driverClassName;
    private String jdbcUrl;
    private String host = "localhost";
    private int port = 3306;
    private String username;
    private String password;
    private String db = "zipkin";
    private int maxActive = 10;
    private boolean useSsl;

    ZipkinMySQLStorageProperties() {
    }

    public String getDriverClassName() {
        return driverClassName;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDb() {
        return db;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public boolean isUseSsl() {
        return useSsl;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = "".equals(username) ? null : username;
    }

    public void setPassword(String password) {
        this.password = "".equals(password) ? null : password;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    public void setUseSsl(boolean useSsl) {
        this.useSsl = useSsl;
    }

    public DataSource toDataSource() {
        DruidDataSource ds = new DruidDataSource();
        ds.setDriverClassName(this.getDriverClassName());
        ds.setUrl(this.determineJdbcUrl());
        ds.setUsername(this.getUsername());
        ds.setPassword(this.getPassword());
        ds.setMaxActive(this.getMaxActive());
        return ds;
    }

    private String determineJdbcUrl() {
        if (StringUtils.hasText(this.getJdbcUrl())) {
            return this.getJdbcUrl();
        } else {
            StringBuilder url = new StringBuilder();
            url.append("jdbc:mysql://");
            url.append(this.getHost()).append(":").append(this.getPort());
            url.append("/").append(this.getDb());
            url.append("?autoReconnect=true");
            url.append("&useSSL=").append(this.isUseSsl());
            url.append("&useUnicode=yes&characterEncoding=UTF-8");
            return url.toString();
        }
    }
}

