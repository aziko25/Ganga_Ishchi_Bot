package telegram_bot_excel.telegram_bot_excel;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TelegramBotExcelApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramBotExcelApplication.class, args);
	}

	@PostConstruct
	public void init() {

		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Tashkent"));
	}
}
