package controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: landy
 * @date: 2019-04-11 21:33
 */
@RestController
public class TestController {
    @GetMapping("/test/index")
    public ResponseEntity index() {
        return ResponseEntity.ok("hello");
    }
    public void lala(){

    }
}
