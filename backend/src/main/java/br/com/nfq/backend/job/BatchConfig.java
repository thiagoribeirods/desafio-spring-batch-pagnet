package br.com.nfq.backend.job;

import br.com.nfq.backend.entity.Transacao;
import br.com.nfq.backend.entity.TransacaoCNAB;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.configuration.support.MapJobRegistry;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.launch.support.TaskExecutorJobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
public class BatchConfig {

    private PlatformTransactionManager transactionManager;
    private JobRepository jobRepository;

    public BatchConfig(PlatformTransactionManager transactionManager, JobRepository jobRepository) {
        this.transactionManager = transactionManager;
        this.jobRepository = jobRepository;
    }

    @Bean
    Job job(Step step) {
        return new JobBuilder("job", jobRepository)
                .start(step)
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    Step step(
            ItemReader<TransacaoCNAB> reader,
            ItemProcessor<TransacaoCNAB, Transacao> processor,
            ItemWriter<Transacao> writer) {
        return new StepBuilder("step", jobRepository)
                .<TransacaoCNAB, Transacao>chunk(1000)
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    /**
     * leitura de dados de tamanho fixo (Formato CNAB)
     * @return
     */
    @Bean
    @StepScope //para acessar o JobParameters
    FlatFileItemReader<TransacaoCNAB> reader(@Value("#{jobParameters['cnabFile']}") Resource file) {
        return new FlatFileItemReaderBuilder<TransacaoCNAB>()
                .name("reader")
                .resource(file)
                .fixedLength()
                .columns(
                    new Range(1,1), new Range(2,9),
                    new Range(10,19), new Range(20,30),
                    new Range(31,42), new Range(43,48),
                    new Range(49,62), new Range(63,80)
                )
                .names(
                    "tipo", "data", "valor", "cpf", "cartao", "hora", "donoLoja", "nomeLoja"
                )
                .targetType(TransacaoCNAB.class)
                .build();
    }

    @Bean
    ItemProcessor<TransacaoCNAB, Transacao> processor() {
        return  item -> {
            //Wither pattern
            var transacao = new Transacao(
                null,
                    item.tipo(),
                    null,
                    null,
                    item.cpf(),
                    item.cartao(),
                    null,
                    item.donoLoja(),
                    item.nomeLoja()
            )
            .withValor(item.valor().divide(BigDecimal.valueOf(100)))
            .withData(item.data())
            .withHora(item.hora())
            .withSinal(item.tipo());

            return transacao;
        };
    }

    @Bean
    JdbcBatchItemWriter<Transacao> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Transacao>()
                .dataSource(dataSource)
                .sql("""
                    INSERT INTO transacao (tipo, data, valor, cpf, cartao, hora, dono_loja, nome_loja)
                    VALUES (:tipo, :data, :valor, :cpf, :cartao, :hora, :donoLoja, :nomeLoja)
                    """)
                .beanMapped()
                .build();
    }

    @Bean
    JobOperator jobOperatorAsync(JobRepository jobRepository) throws Exception {
        var jobOperator = new TaskExecutorJobOperator();
        jobOperator.setJobRepository(jobRepository);
        jobOperator.setJobRegistry(jobRegistry());
        jobOperator.setTaskExecutor(new SimpleAsyncTaskExecutor());
        jobOperator.afterPropertiesSet();
        return jobOperator;
    }

    @Bean
    public JobRegistry jobRegistry() {
        return new MapJobRegistry();
    }
}
