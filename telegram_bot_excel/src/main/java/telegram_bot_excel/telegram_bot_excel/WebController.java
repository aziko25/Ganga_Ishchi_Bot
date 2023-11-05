package telegram_bot_excel.telegram_bot_excel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/allUsers")
@CrossOrigin(maxAge = 3600)
public class WebController {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public WebController(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping()
    public ResponseEntity<?> allUsers() {

        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM users.users WHERE blocked = false ORDER BY time");

        return getTime(users);
    }

    @GetMapping("/blocked")
    public ResponseEntity<?> allBlockedUsers() {

        List<Map<String, Object>> users = jdbcTemplate.queryForList("SELECT * FROM users.users WHERE blocked = true ORDER BY time");

        return getTime(users);
    }

    private ResponseEntity<?> getTime(List<Map<String, Object>> users) {

        for (Map<String, Object> user : users) {

            Timestamp timestamp = (Timestamp) user.get("time");
            String timestampStr = timestamp.toString();

            if (timestampStr.endsWith(".0")) {

                timestampStr = timestampStr.substring(0, timestampStr.length() - 2);
            }

            user.put("time", timestampStr);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/html")
    public String html() {

        return "index";
    }

    @GetMapping("/blocked/html")
    public String blockedHtml() {

        return "blocked-index";
    }

    @PutMapping("/block/html")
    public ResponseEntity<?> deleteUsers(@RequestBody List<Long> idsToDelete) {

        if (idsToDelete.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.OK);
        }

        String idsToDeleteString = idsToDelete.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        String sql = "UPDATE users.users SET blocked = true WHERE id IN (" + idsToDeleteString + ");";

        int rowsAffected = jdbcTemplate.update(sql);

        return new ResponseEntity<>(rowsAffected, HttpStatus.OK);
    }

    @PutMapping("/unblock/html")
    public ResponseEntity<?> unDeleteUsers(@RequestBody List<Long> idsToDelete) {

        if (idsToDelete.isEmpty()) {

            return new ResponseEntity<>(HttpStatus.OK);
        }

        String idsToDeleteString = idsToDelete.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        String sql = "UPDATE users.users SET blocked = false WHERE id IN (" + idsToDeleteString + ");";

        int rowsAffected = jdbcTemplate.update(sql);

        return new ResponseEntity<>(rowsAffected, HttpStatus.OK);
    }
}