I apologize for the confusion. The behavior you're observing might be related to how error handling is propagated in reactive streams. In Spring WebFlux, when you use `Mono.error` or `Flux.error` to propagate an error, it triggers the creation of an error signal in the reactive stream, which eventually results in an error being propagated downstream.

However, when using the `Mono.error` or `Flux.error` approach, the error is handled within the reactive pipeline, and by default, it won't trigger custom error handling configured for traditional Spring MVC (like the `@ExceptionHandler` or `ResponseStatusException` you mentioned).

To ensure that the custom error handling and error response formatting is applied, you should consider using a global error handling mechanism in combination with `ResponseStatusException`. Here's how you can achieve this:

1. Create a global exception handler that transforms exceptions into consistent error responses. You can use the `@ControllerAdvice` annotation for this purpose.

```java
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(404, ex.getMessage(), null); // Set path to null
        return ResponseEntity.status(404).body(errorResponse);
    }

    // Add more exception handlers as needed
}
```

2. In your service layer, throw a `ResponseStatusException` instead of using `Mono.error` or `Flux.error` to trigger global error handling.

```java
@Service
@Slf4j
@RequiredArgsConstructor
public class AnimeService {

    private final AnimeRepository animeRepository;

    public Flux<Anime> listAll() {
        return animeRepository.findAll();
    }

    public Mono<Anime> findById(int id) {
        return animeRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("Anime with ID " + id + " not found")));
    }
}
```

By throwing a `ResponseStatusException`, you're leveraging the global error handling mechanism, and the custom error handling defined in the `GlobalExceptionHandler` class will be applied. This should help you achieve the consistent error response format you're looking for.

