package io.spring.billrun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.billrun.model.Bill;
import io.spring.billrun.model.Usage;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.JsonItemReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;

@Configuration
/* enables Spring Batch features and provides a base configuration for setting up batch jobs */
@EnableBatchProcessing
/* sets up a TaskRepository, which stores information about the task execution (such as the start and end times of the task and the exit code) */
@EnableTask
public class BillConfiguration {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Value("${usage.file.name:classpath:usageinfo.json}")
    private Resource usageResource;


    @Bean
    public Job job1(ItemReader reader, ItemProcessor<Usage, Bill> processor, ItemWriter<Bill> writer) {
        Step step = stepBuilderFactory.get("BillProcessing")
                .<Usage, Bill>chunk(1)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
        return jobBuilderFactory.get("BillJob")
                .incrementer(new RunIdIncrementer())
                .start(step)
                .build();
    }

    @Bean
    public JsonItemReader<Usage> jsonItemReader() {
        ObjectMapper objectMapper = new ObjectMapper();
        JacksonJsonObjectReader<Usage> usageJacksonJsonObjectReader = new JacksonJsonObjectReader<Usage>(Usage.class);
        usageJacksonJsonObjectReader.setMapper(objectMapper);

        return new JsonItemReaderBuilder<Usage>()
                .jsonObjectReader(usageJacksonJsonObjectReader)
                .resource(usageResource)
                .name("UsageJsonItemReader")
                .build();
    }

    @Bean
    public ItemWriter<Bill> jdbcBillWriter(DataSource dataSource) {
        JdbcBatchItemWriterBuilder<Bill> billJdbcBatchItemWriterBuilder = new JdbcBatchItemWriterBuilder<Bill>();
        return billJdbcBatchItemWriterBuilder
                .beanMapped()
                .dataSource(dataSource)
                .sql("INSERT INTO BILL_STATEMENTS (id, first_name, " +
                        "last_name, minutes, data_usage,bill_amount) VALUES " +
                        "(:id, :firstName, :lastName, :minutes, :dataUsage, " +
                        ":billAmount)")
                .build();

    }

    @Bean
    ItemProcessor<Usage, Bill> itemProcessor() {
        return new BillProcessor();
    }
}
