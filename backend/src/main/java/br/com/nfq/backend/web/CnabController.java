package br.com.nfq.backend.web;

import br.com.nfq.backend.service.CnabService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/cnab")
@RequiredArgsConstructor
public class CnabController {

    private final CnabService cnabService;

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file) throws IOException, JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {
        this.cnabService.uploadCnabFile(file);
        return "Processamento iniciado";
    }

}
