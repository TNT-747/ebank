package com.ebank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration;

@SpringBootApplication(exclude = { GrpcServerSecurityAutoConfiguration.class })
public class EbankApplication {
    public static void main(String[] args) {
        SpringApplication.run(EbankApplication.class, args);
    }
}
