package com.recipia.chat.exception

import com.recipia.chat.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.util.stream.Collectors

/**
 * 전역 에러처리
 */
@RestControllerAdvice
class GlobalControllerAdvice {

    val log = logger()

    @ExceptionHandler(ChatApplicationException::class)
    fun handleRecipeApplicationException(e: ChatApplicationException): ResponseEntity<*> {
        log.error("RecipeApplicationException occurred", e)
        return buildErrorResponse(e.errorCode, null) // 괄호를 제거함
    }


    /**
     * NullPointerException 처리
     */
    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointerException(e: NullPointerException): ResponseEntity<*> {
        log.error("NullPointerException occurred", e)
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.message)
    }

    /**
     * IllegalArgumentException 처리
     */
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<*> {
        log.error("IllegalArgumentException occurred", e)
        return buildErrorResponse(ErrorCode.BAD_REQUEST, e.message)
    }

    /**
     * @Valid 어노테이션을 사용하고 @NotNull, @NotBlank 등 어노테이션이 달려있는 필수값이 누락된 채로 들어올때 아래 에러를 발생.
     * MethodArgumentNotValidException 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ResponseEntity<*> {
        log.error("MethodArgumentNotValidException occurred", e)

        val missingFields = e.bindingResult.fieldErrors.stream()
                .map { obj: FieldError -> obj.field }
                .collect(Collectors.joining(", ")) // 필드를 쉼표와 공백으로 구분하여 하나의 문자열로 합친다.

        return buildErrorResponse(ErrorCode.BAD_REQUEST, missingFields)
    }

    /**
     * 모든 RuntimeException 처리
     */
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<*> {
        log.error("RuntimeException occurred", e)
        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.message)
    }

    /**
     * 공통 에러 응답 생성 메서드
     */
    fun buildErrorResponse(errorCode: ErrorCode, customMessage: String?)
    : ResponseEntity<Map<String, Any>> {
        return getErrorResponseEntity(errorCode, customMessage)
    }

//    /**
//     * Kafka 공통 에러 응답 생성 메서드
//     */
//    fun buildKafkaErrorResponse(errorCode: ErrorCode, customMessage: String, e: RuntimeException): ResponseEntity<Map<String, Any>> {
//        kafkaTemplate.send("error-messages", customMessage + " Occurred: " + e.message)
//        return getErrorResponseEntity(errorCode, customMessage)
//    }

    /**
     * [Extract] - 예외코드를 처리하는 메서드
     */
    fun getErrorResponseEntity(errorCode: ErrorCode, customMessage: String?)
    : ResponseEntity<Map<String, Any>> {

        val errorResponse: MutableMap<String, Any> = HashMap()
        errorResponse["status"] = errorCode.status
        errorResponse["code"] = errorCode.code

        if (customMessage != null) {
            errorResponse["message"] = customMessage
        }

        val status = HttpStatus.resolve(errorCode.status) ?: HttpStatus.INTERNAL_SERVER_ERROR

        return ResponseEntity.status(status)
                .body(errorResponse)
    }

}