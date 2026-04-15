package br.com.nfq.backend.web;

import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.logging.Level;
import java.util.logging.Logger;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(JobInstanceAlreadyCompleteException.class)
    private ResponseEntity<?> handleFileAlreadyImported(JobInstanceAlreadyCompleteException ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body("O arquivo enviado já foi importado no sistema");
    }

}
