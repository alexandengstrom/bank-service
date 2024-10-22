package hello;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AccountErrorController implements ErrorController {

    private static final String PATH = "/error";

    @RequestMapping(PATH)
    public ResponseEntity<String> handleError(HttpServletRequest request) {
        Object status = request.getAttribute("javax.servlet.error.status_code");

        if (status != null && status.equals(HttpStatus.BAD_REQUEST.value())) {
            return ResponseEntity.ok("FAILED");
        }

        return ResponseEntity.ok("");
    }
}
