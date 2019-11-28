package keti.sgs.config;

import java.sql.Types;
import keti.sgs.model.Devices;
import keti.sgs.model.Server;
import keti.sgs.repository.DeviceJpaRepository;
import keti.sgs.repository.ServerRepository;
import keti.sgs.repository.TypeJpaRepository;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(
    basePackageClasses = {Jsr310JpaConverters.class, Devices.class, Types.class, Server.class})
@EnableJpaRepositories(basePackageClasses = {DeviceJpaRepository.class, TypeJpaRepository.class,
    ServerRepository.class})
@EnableJpaAuditing
public class JpaConfig {

}
