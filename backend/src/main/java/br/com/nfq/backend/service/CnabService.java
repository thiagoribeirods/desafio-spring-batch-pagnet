package br.com.nfq.backend.service;

import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.parameters.InvalidJobParametersException;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.core.launch.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.launch.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.JobRestartException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;

@Service
public class CnabService {

    private final Path fileStorageLocation;
    private final JobOperator jobOperator;
    private final Job job;

    public CnabService(@Value("${file.upload.dir}") String fileUploadDir,
                       @Qualifier("jobOperatorAsync") JobOperator jobOperator,
                       Job job) {
        this.fileStorageLocation = FileSystems.getDefault().getPath(fileUploadDir);
        this.jobOperator = jobOperator;
        this.job = job;
    }

    public void uploadCnabFile(MultipartFile file) throws IOException, JobInstanceAlreadyCompleteException, InvalidJobParametersException, JobExecutionAlreadyRunningException, JobRestartException {

        var originalName =  file.getOriginalFilename();

        var fileName = StringUtils.cleanPath(originalName != null ? originalName : "");
        Path targetLocation = this.fileStorageLocation.resolve(fileName);
        file.transferTo(targetLocation);

        var jobParameters = new JobParametersBuilder()
                .addJobParameter("cnab", file.getOriginalFilename(), String.class, true)
                .addJobParameter("cnabFile", "file:" + targetLocation, String.class, false) //identifying false olha o nome do arquivo e não apenas o caminho relativo completo
                .toJobParameters();

        jobOperator.run(job, jobParameters);
    }

}
