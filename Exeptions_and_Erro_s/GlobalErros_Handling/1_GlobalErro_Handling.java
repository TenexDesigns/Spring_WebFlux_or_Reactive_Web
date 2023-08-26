In Spring WebFlux, you still have access to the `HttpStatus` enum and other classes for handling HTTP status codes and error responses. However, the way you handle errors might be slightly different due to the reactive nature of WebFlux.

Instead of using the `WebRequest` object to retrieve request information, you can directly use the `ServerRequest` object provided by WebFlux to access request-related information within your global exception handler. Additionally, you can use the `ServerResponse` class to construct error responses.

Here's how you can adapt the `GlobalExceptionHandler` class to work with Spring WebFlux:

```java
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Component
public class GlobalExceptionHandler {

    public Mono<ServerResponse> handleException(ServerRequest request, Throwable ex) {
        if (ex instanceof ResourceNotFoundException) {
            ErrorResponse errorResponse = new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), request.path());
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(errorResponse);
        }

        // Handle other exceptions
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue("An internal server error occurred");
    }
}
```

In this example, the `handleException` method takes a `ServerRequest` object and a throwable (exception) as parameters. It checks the type of the exception and constructs an appropriate error response using the `ServerResponse` class. You can access the request path using `request.path()`.

To use this global exception handler, you need to register it in your application configuration:

```java
import org.springframework.boot.autoconfigure.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    @Bean
    public ErrorWebExceptionHandler errorWebExceptionHandler(ErrorAttributes errorAttributes,
                                                             ServerCodecConfigurer codecConfigurer) {
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();
        exceptionHandler.setMessageWriters(codecConfigurer.getWriters());
        exceptionHandler.setMessageReaders(codecConfigurer.getReaders());
        exceptionHandler.setErrorAttributes(errorAttributes);
        return exceptionHandler;
    }
}
```

This configuration sets up the `GlobalExceptionHandler` to be used as the global error handler for your WebFlux application.

Remember to adapt this code to your specific use case and application structure.
  
