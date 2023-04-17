package com.example.practivespring;

import jakarta.servlet.Servlet;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootApplication
@RestController

public class PracticeSpringApplication {

    public static void main(String[] args) {

        SpringApplication.run(PracticeSpringApplication.class, args);
    }

    private static List<UserDto> userDtoList = new ArrayList<>();

    @PostMapping("/user")
    public ResponseEntity<UserDto> testPost1(@Valid @RequestBody UserDto userDto) {
        userDtoList.add(userDto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{username}").buildAndExpand(userDto.getUsername()).toUri();
        //http://localhost:8080/users/{username}
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/user/{username}")
    public UserDto fetchUser(@PathVariable String username) {
        return userDtoList.stream().filter(user -> user.username.equals(username)).findFirst().get();
    }

    /* @GetMapping("/test")
     public JsonMessage test() {

         return new JsonMessage("ok");
     }*/
    //request param
    @GetMapping("/test")
    public JsonMessage test(@RequestParam(required = false) String username) {

        return new JsonMessage(username);
    }

    //path variable
    @GetMapping("/test1/{username}/pass/{password}")
    public JsonMessage test1(@PathVariable String username, @PathVariable String password) {

        return new JsonMessage(username + password);
    }


    @GetMapping("/testPost3")
    public String testPost(@RequestBody UserDto userDto) {
        return userDto.toString();
    }
}

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
class UserDto {
    @Size(min = 2,message = "enter correct name :)")
    String firstname;

    String lastname;
    @Email(message = "enter correct email :)")
    String username;

    String password;
}

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
class JsonMessage {
    String massage;
}

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
         //    @ExceptionHandler(UserNotFoundException.class)
         //    public ResponseEntity<ErrorDetails> userNotFoundExceptionHandler(UserNotFoundException e) {
         //        ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(), e.getMessage(), 404);
         //        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
      ErrorDetails errorDetails = new ErrorDetails(LocalDateTime.now(),
                "Total Errors:" + ex.getErrorCount() + " First Error:" + ex.getFieldError().getDefaultMessage(), 400);
    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

}
//برای اینکه یه بیغامی بتونیم بسازیم بفرستیم سمت بک اند یه کلاس میسازیم
@Setter
@AllArgsConstructor
@Getter
@NoArgsConstructor
class ErrorDetails {
    private LocalDateTime localDateTime;
    private String massage;
    private Integer statusCode;
}