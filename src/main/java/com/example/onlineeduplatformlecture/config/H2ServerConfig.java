package com.example.onlineeduplatformlecture.config;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;

@Slf4j
@Configuration
@EnableR2dbcAuditing
public class H2ServerConfig {
    private Server webServer;
    Integer h2ConsolePort = 9093;

    @EventListener(ContextRefreshedEvent.class)
    public void start() throws SQLException {
//        log.info("starting h2 console at port {}", h2ConsolePort);
        this.webServer = Server.createWebServer("-webPort", h2ConsolePort.toString());
        this.webServer.start();
    }

    @EventListener(ContextClosedEvent.class)
    public void stop() {
//        log.info("stopping h2 console at port {}", h2ConsolePort);
        this.webServer.stop();
    }
}
