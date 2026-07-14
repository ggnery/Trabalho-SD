package br.com.projeto.filamensagem.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_ATENDIMENTO = "clinica.atendimento";
    public static final String FILA_CONSULTA = "fila.consulta";
    public static final String FILA_EXAME = "fila.exame";

    @Bean
    public DirectExchange atendimentoExchange() {
        return new DirectExchange(EXCHANGE_ATENDIMENTO);
    }

    @Bean
    public Queue filaConsulta() {
        return QueueBuilder.durable(FILA_CONSULTA)
                .withArgument("x-max-priority", 10)
                .build();
    }

    @Bean
    public Queue filaExame() {
        return QueueBuilder.durable(FILA_EXAME)
                .withArgument("x-max-priority", 10)
                .build();
    }

    @Bean
    public Binding bindingConsulta(Queue filaConsulta, DirectExchange atendimentoExchange) {
        return BindingBuilder.bind(filaConsulta).to(atendimentoExchange).with("Consulta");
    }

    @Bean
    public Binding bindingExame(Queue filaExame, DirectExchange atendimentoExchange) {
        return BindingBuilder.bind(filaExame).to(atendimentoExchange).with("Exame");
    }
}
