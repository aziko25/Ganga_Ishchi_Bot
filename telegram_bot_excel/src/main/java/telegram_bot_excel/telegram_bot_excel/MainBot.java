package telegram_bot_excel.telegram_bot_excel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MainBot extends TelegramLongPollingBot {

    private final JdbcTemplate jdbcTemplate;

    private final Map<Long, UserState> userStates = new HashMap<>();

    public static class UserState {
        public Boolean waitingForName = false;
        public Boolean waitingForAge = false;
        public Boolean waitingForNumber = false;
        public Boolean waitingForPosition = false;
        public String NAME;
        public int AGE;
        public String PHONE_NUMBER;
        public Integer GREETING = 0;
        public String USERNAME;
        public String POSITION;
    }

    @Override
    public void onUpdateReceived(Update update) {

        Long chatId = update.getMessage().getChatId();

        UserState state = userStates.getOrDefault(chatId, new UserState());

        state.USERNAME = update.getMessage().getFrom().getUserName();

        if (state.USERNAME == null) {

            state.USERNAME = "null";
        }

        String messageText = update.getMessage().getText();

        if ("/start".equals(messageText)) {

            start(chatId, state);
        }
        else if ("\uD83D\uDCCD Filiallar".equals(messageText)) {

            Filiallar(chatId);
        }
        else if ("\uD83D\uDCDD Ro'yhatdan Otish".equals(messageText)) {

            Register(chatId, state);
        }
        else if ("☎️ ALOQA MARKAZI".equals(messageText)) {

            ManagerNumber(chatId);
        }
        else if ("\uD83C\uDFE2 Biz Haqimizda".equals(messageText)) {

            AboutUs(chatId);
        }
        else if (update.hasMessage() && update.getMessage().hasContact()) {

            state.PHONE_NUMBER = update.getMessage().getContact().getPhoneNumber();
            InputPhoneNumber(chatId, state.PHONE_NUMBER, state);
        }
        else if (!messageText.isEmpty() && state.waitingForName) {

            InputName(chatId, messageText, state);
        }
        else if (!messageText.isEmpty() && state.waitingForAge) {

            try {

                int age = Integer.parseInt(messageText);

                InputAge(chatId, age, state);
            }
            catch (NumberFormatException e) {

                SendMessage message = new SendMessage();

                message.setChatId(chatId);
                message.setText("Iltimos, Yoshingizni Raqam Bilan Kiriting:");

                exception(message);
            }
        }
        else if (!messageText.isEmpty() && state.waitingForPosition) {

            InputPosition(chatId, messageText, state);
        }
        else if (!messageText.isEmpty() && state.waitingForNumber) {

            InputPhoneNumber(chatId, messageText, state);
        }
        else {

            start(chatId, state);
        }

        userStates.put(chatId, state);
    }

    public void start(Long chatId, UserState state) {

        SendMessage message = new SendMessage();

        keyBoardRemove(message);

        message.setChatId(chatId);

        if (state.GREETING == 0) {

            message.setText("Assalomu Alaykum");

            state.GREETING++;
        }
        else {

            message.setText("Tanlang:");
        }

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row1 = new KeyboardRow();
        KeyboardRow row2 = new KeyboardRow();
        KeyboardRow row3 = new KeyboardRow();
        KeyboardRow row4 = new KeyboardRow();

        row1.add("\uD83D\uDCCD Filiallar");
        row2.add("\uD83D\uDCDD Ro'yhatdan Otish");
        row3.add("☎️ ALOQA MARKAZI");
        row4.add("\uD83C\uDFE2 Biz Haqimizda");

        keyboard.add(row1);
        keyboard.add(row2);
        keyboard.add(row3);
        keyboard.add(row4);

        keyboardMarkup.setKeyboard(keyboard);

        message.setReplyMarkup(keyboardMarkup);

        exception(message);
    }

    public void Filiallar(Long chatId) {

        SendMessage message = new SendMessage();
        SendPhoto photo = new SendPhoto();

        InputFile photoFile1 = new InputFile(new File("/var/www/html/filial1.jpg"));
        InputFile photoFile2 = new InputFile(new File("/var/www/html/filial1_2.jpg"));
        InputFile photoFile3 = new InputFile(new File("/var/www/html/filial2.jpg"));
        InputFile photoFile4 = new InputFile(new File("/var/www/html/filial2_2.jpg"));

        message.setChatId(chatId);
        message.setText("""
                \uD83D\uDCCD1-filiali Amir Temur ko’chasida
                \uD83D\uDCCCMo’ljal: Qarshi davlat universiteti
                <a href="https://yandex.com/maps/org/115330481037">\uD83D\uDCCDYandex Lokatsiya</a>""");
        message.setParseMode(ParseMode.HTML);
        message.setDisableWebPagePreview(true);

        photo.setChatId(chatId);
        photo.setPhoto(photoFile1);

        // 1st Location

        exception(message);
        exceptionPhoto(photo);

        photo.setPhoto(photoFile2);
        photo.setCaption(null);
        exceptionPhoto(photo);

        // 2nd Location

        message.setText("""
                \uD83D\uDCCD2-filiali Qarluqobod ko’chasida
                \uD83D\uDCCCMo’ljal: Marjon supermarketining 2-chi qavatida
                <a href="https://yandex.com/maps/org/140732442930">\uD83D\uDCCDYandex Lokatsiya</a>""");
        message.setParseMode(ParseMode.HTML);
        message.setDisableWebPagePreview(true);

        photo.setPhoto(photoFile3);

        exception(message);
        exceptionPhoto(photo);

        photo.setPhoto(photoFile4);
        photo.setCaption(null);
        exceptionPhoto(photo);

        message.setChatId(chatId);
        message.setText("“Ganga” oz ish faoliyatini xar kuni soat ⏰10:00-00:00 gacha olib boradi.");
        exception(message);
    }

    public void Register(Long chatId, UserState state) {

        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("Ismingizni Kiriting:");

        keyBoardRemove(message);

        exception(message);

        state.waitingForName = true;
    }

    public void InputName(Long chatId, String name, UserState state) {

        state.waitingForName = false;

        state.NAME = name;

        SendMessage message = new SendMessage();

        message.setChatId(chatId);

        message.setText("Yoshingizni Kiriting:");

        state.waitingForAge = true;

        exception(message);
    }

    public void InputAge(Long chatId, int age, UserState state) {

        state.waitingForAge = false;
        state.AGE = age;

        SendMessage message = new SendMessage();

        if (age < 18 || age > 30) {

            message.setChatId(chatId);
            message.setText("18-30 Yosh Bolishiz Kerak!");

            exception(message);

            start(chatId, state);
        }
        else {

            state.waitingForNumber = true;

            KeyboardRow row1 = new KeyboardRow();
            KeyboardButton button = new KeyboardButton("Raqamni Yuborish");
            button.setRequestContact(true);

            row1.add(button);

            List<KeyboardRow> keyboard = new ArrayList<>();
            keyboard.add(row1);

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup(keyboard);
            keyboardMarkup.setResizeKeyboard(true);

            message.setChatId(chatId);
            message.setText("Iltimos, Raqamingizni Yuvoring:");

            message.setReplyMarkup(keyboardMarkup);

            exception(message);
        }
    }

    public void InputPhoneNumber(Long chatId, String phoneNumber, UserState state) {

        state.waitingForNumber = false;

        String regex = "^(\\+)?998[589]\\d{8}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);

        SendMessage message = new SendMessage();

        if (matcher.matches()) {

            keyBoardRemove(message);

            state.PHONE_NUMBER = phoneNumber;

            LocalDateTime timeNow = LocalDateTime.now();

            if (state.PHONE_NUMBER.startsWith("+")) {

                state.PHONE_NUMBER = state.PHONE_NUMBER.substring(1);
            }

            String checkIfNumberExists = "SELECT EXISTS (SELECT 1 FROM users.users WHERE phone = ?);";
            boolean exists = Boolean.TRUE.equals(jdbcTemplate.queryForObject(checkIfNumberExists, Boolean.class, state.PHONE_NUMBER));

            if (!exists) {

                String sql = "INSERT INTO users.users(name, age, phone, time, username) VALUES(?, ?, ?, ?, ?);";

                jdbcTemplate.update(sql, state.NAME, state.AGE, state.PHONE_NUMBER, timeNow, "@" + state.USERNAME);
            }

            message.setChatId(chatId);
            message.setText("Qaysi Positsiyaga Mo'ljal?\nBittasini tanlang:");

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row1 = new KeyboardRow();
            KeyboardRow row2 = new KeyboardRow();
            KeyboardRow row3 = new KeyboardRow();

            row1.add("SHAURMACHI");
            row1.add("XOT-DOGCHI");
            row2.add("OFITSANT");
            row2.add("SALATCHI");
            row3.add("FARROSH");

            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);

            keyboardMarkup.setKeyboard(keyboard);

            message.setReplyMarkup(keyboardMarkup);

            exception(message);

            state.waitingForPosition = true;
        }
        else {

            state.waitingForNumber = true;

            message.setChatId(chatId);
            message.setText("Notogri Raqam Terdiz. Iltimos, Yana Bir Bor Urining:");

            exception(message);
        }
    }

    public void InputPosition(Long chatId, String position, UserState state) {

        state.waitingForPosition = false;

        SendMessage message = new SendMessage();

        if (!position.equals("SHAURMACHI") && !position.equals("XOT-DOGCHI") && !position.equals("OFITSANT") &&
                !position.equals("SALATCHI") && !position.equals("FARROSH")) {

            message.setChatId(chatId);
            message.setText("Iltimos, Knopkani Bosing!");

            state.waitingForPosition = true;

            // Create keyboard markup
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setResizeKeyboard(true);
            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row1 = new KeyboardRow();
            KeyboardRow row2 = new KeyboardRow();
            KeyboardRow row3 = new KeyboardRow();

            row1.add("SHAURMACHI");
            row1.add("XOT-DOGCHI");
            row2.add("OFITSANT");
            row2.add("SALATCHI");
            row3.add("FARROSH");

            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);

            keyboardMarkup.setKeyboard(keyboard);

            // Set the keyboard markup to the message
            message.setReplyMarkup(keyboardMarkup);

            exception(message);
        }
        else {
            state.POSITION = position;

            System.out.println("Position: " + position);

            String sql = "UPDATE users.users SET position = ? WHERE phone = ?;";
            jdbcTemplate.update(sql, state.POSITION, state.PHONE_NUMBER);

            message.setChatId(chatId);
            message.setText("Rahmat! Siz Ro'yhatdan O'ttingiz!");

            exception(message);

            start(chatId, state);
        }
    }

    public void ManagerNumber(Long chatId) {

        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("""
                ISH BO’YICHA SAVOLLAR BILAN MENEJERIMIZGA BOG’LANING

                ☎️ +998 90 870 77 70
                        \uD83D\uDC68\uD83C\uDFFB\u200D\uD83D\uDCBBUlug’bek""");

        exception(message);
    }

    public void AboutUs(Long chatId) {

        SendMessage message = new SendMessage();

        message.setChatId(chatId);
        message.setText("""
                \uD83D\uDCABGanga brendi Qarshi shahrida Bobur Hakimov tomonidan asos solinib, 2008 yildan beri xalqimiz ko’nglidan joy olib xizmat qilib kelmoqda\uD83D\uDE0A

                Xozirda “Ganga”ning Qarshi shahrida 2-ta filiali mavjud.""");

        exception(message);
    }

    public void keyBoardRemove(SendMessage message) {

        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove();

        keyboardRemove.setRemoveKeyboard(true);
        message.setReplyMarkup(keyboardRemove);
    }

    public void exception(SendMessage message) {

        try {

            execute(message);
        }
        catch (TelegramApiException e) {

            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public void exceptionPhoto(SendPhoto message) {

        try {

            execute(message);
        }
        catch (TelegramApiException e) {

            e.printStackTrace();
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


    public MainBot(@Value("${bot.token}") String botToken, JdbcTemplate jdbcTemplate) {

        super(botToken);
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getBotUsername() {

        // Prod
        return "gangaishchiqarshibot";

        // Test
        //return "azizkhuja_test_bot";
    }
}