package com.vyntra.transactionmonitor.api;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesConstraintViolation() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        when(path.toString()).thenReturn("search.minAmount");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must be greater than or equal to 0");

        var response = handler.handleConstraintViolation(new ConstraintViolationException(Set.of(violation)));

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().details()).contains("search.minAmount must be greater than or equal to 0");
    }

    @Test
    void handlesTypeMismatch() throws NoSuchMethodException {
        Method method = getClass().getDeclaredMethod("sample", BigDecimal.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        var exception = new MethodArgumentTypeMismatchException("abc", BigDecimal.class, "minAmount", parameter, null);

        var response = handler.handleTypeMismatch(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().details()).contains("Invalid value for parameter 'minAmount'");
    }

    @Test
    void handlesMethodArgumentNotValid() throws Exception {
        Method method = getClass().getDeclaredMethod("sample", BigDecimal.class);
        MethodParameter parameter = new MethodParameter(method, 0);
        BeanPropertyBindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("target", "amount", "must be valid"));
        var exception = new MethodArgumentNotValidException(parameter, bindingResult);

        var response = handler.handleMethodArgumentNotValid(exception);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().details()).contains("amount must be valid");
    }

    @Test
    void handlesIllegalState() {
        var response = handler.handleIllegalState(new IllegalStateException("broken"));

        assertThat(response.getStatusCode().value()).isEqualTo(500);
        Assertions.assertNotNull(response.getBody());
        assertThat(response.getBody().details()).contains("broken");
    }

    private void sample(BigDecimal value) {
    }
}
