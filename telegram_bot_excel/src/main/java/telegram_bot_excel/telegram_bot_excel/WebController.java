package telegram_bot_excel.telegram_bot_excel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

        return new ResponseEntity<>(jdbcTemplate.queryForList("SELECT id, name, age, phone FROM users.users WHERE blocked = false"), HttpStatus.OK);
    }

    @GetMapping("/blocked")
    public ResponseEntity<?> allBlockedUsers() {

        return new ResponseEntity<>(jdbcTemplate.queryForList("SELECT id, name, age, phone FROM users.users WHERE blocked = true"), HttpStatus.OK);
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