// infrastructure/config/BeanDefinitionsConfig.java
package co.edu.unicauca.project_microservice.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Configuración de beans de Spring
 * Separa la configuración del framework del código de dominio puro
 */
@Configuration
@ComponentScan(basePackages = {
    "co.edu.unicauca.project_microservice.domain",
    "co.edu.unicauca.project_microservice.application",
    "co.edu.unicauca.project_microservice.infrastructure"
})
public class BeanDefinitionsConfig {
    // La configuración está en las anotaciones de cada componente
    // Este archivo asegura que Spring escanee todos los paquetes necesarios
}