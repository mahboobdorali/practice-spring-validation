package com.example.practivespring;


import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
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
import java.util.Locale;

@SpringBootApplication
@RestController

public class PracticeSpringApplication {
    //for read files.properties
    private final MessageSource messageSource;

    public PracticeSpringApplication(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public static void main(String[] args) {

        SpringApplication.run(PracticeSpringApplication.class, args);
    }

    private static List<UserDto> userDtoList = new ArrayList<>();

    //for give link to client after register
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

 /*   @GetMapping("/test")
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

    //for select language about message
    @GetMapping("/hello-world")
    public String helloWorld() {
        Locale local = LocaleContextHolder.getLocale();
        return messageSource.getMessage("good.morning.massage", null, ":/", local);
    }

    //control version
    @GetMapping("/v1/hello-word")
    public String helloWord() {
        return "hello";
    }

    @GetMapping("/v2/hello-word")
    public JsonMessage helloWord2() {
        return new JsonMessage("hello word");
    }

    @GetMapping(value = "/hello-word", params = "version1")
    public String helloWord3() {
        return "hello";
    }

    @GetMapping(value = "/hello-word", params = "version2")
    public JsonMessage helloWord4() {
        return new JsonMessage("hello word");
    }

    @GetMapping(value = "/hello-word", headers = "mahboob-version=1")
    public String helloWord5() {
        return "hello";
    }

    @GetMapping(value = "/hello-word", headers = "mahboob-version=2")
    public JsonMessage helloWord6() {
        return new JsonMessage("hello word");
    }

/*
    @GetMapping(value = "/hello-word", produces = "application/application/naser.app.v1+json")
    public String helloWord7() {
        return "hello";
    }
*/

    @GetMapping(value = "/hello-word", produces = "application/ali.app.v2+json")
    public JsonMessage helloWord8() {
        return new JsonMessage("hello word");
    }

    //HATEOAS
    @GetMapping(value = "/hello-hateoas-non-static")
    public EntityModel<JsonMessage> helloHateoas1() {
        JsonMessage jsonMessage=new JsonMessage("heeeeelloooooo:)");
        EntityModel<JsonMessage>jsonMessageEntityModel=EntityModel.of(jsonMessage);                                 //به خودش هم میتونه لینک بده
        WebMvcLinkBuilder webMvcLinkBuilder1=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).helloHateoas1());
        WebMvcLinkBuilder webMvcLinkBuilder2=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).helloHateoas2());
        //مینویسیم this چون کنترلر توی همین کلاس داریم حالا اگر کنترلر توی یه کلاس دیگ بود به صورت دیگ عمل میکنیم :
        WebMvcLinkBuilder webMvcLinkBuilder3=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(this.getClass()).helloHateoas3());
        WebMvcLinkBuilder webMvcLinkBuilderForOtherController=WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(Controller.class).hateoasInOtherClass());
        //withRel is link name where come before href of link
        jsonMessageEntityModel.add(webMvcLinkBuilder1.withRel("link hateoas1"));
        jsonMessageEntityModel.add(webMvcLinkBuilder2.withRel("link hateoas2"));
        jsonMessageEntityModel.add(webMvcLinkBuilder3.withRel("link hateoas3"));
        jsonMessageEntityModel.add(webMvcLinkBuilderForOtherController.withRel("link hateoas4"));
        return jsonMessageEntityModel;
    }
    @GetMapping(value = "/hello-hateoas2")
    public JsonMessage helloHateoas2() {
        return new JsonMessage("hello hateoas**");
    }
    @GetMapping(value = "/hello-hateoas3")
    public JsonMessage helloHateoas3() {
        return new JsonMessage("hello hateoas***");
    }

}

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
class UserDto {
    @Size(min = 2, message = "enter correct name :)")
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