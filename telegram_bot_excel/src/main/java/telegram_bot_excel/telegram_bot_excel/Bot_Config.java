package telegram_bot_excel.telegram_bot_excel;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.sql.DataSource;

@Configuration
public class Bot_Config {

    @Bean
    public TelegramBotsApi telegramBotsApi(MainBot mainBot) throws TelegramApiException {

        var api = new TelegramBotsApi(DefaultBotSession.class);

        api.registerBot(mainBot);

        return api;
    }

    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();

        dataSource.setDriverClassName("org.postgresql.Driver");

        dataSource.setUrl("jdbc:postgresql://localhost:5432/excel");
        dataSource.setUsername("postgres");
        dataSource.setPassword("bestuser");

        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {

        return new JdbcTemplate(dataSource);
    }
}
