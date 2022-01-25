package com.batch.demospringbatch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.batch.demospringbatch.listener.JobListener;
import com.batch.demospringbatch.model.Persona;
import com.batch.demospringbatch.processor.PersonaItemProcessor;


@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	/*
	 * Batch se compone de muchos steps
	 * un steps se compone de un 
	 * 	-itemReader
	 * 	-itemProcessor (este es el unico opcional)
	 * 	-itemWriter
	 * 
	 * */
	
	//READER
	@Bean
	public FlatFileItemReader<Persona> reader(){
		return new FlatFileItemReaderBuilder<Persona>()
				.name("personaItemReader")
				.resource(new ClassPathResource("sample-data.csv"))
				.delimited()
				.names(new String[] {"primerNombre", "segundoNombre", "telefono"})
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Persona>() {{
					setTargetType(Persona.class);
				}})
				.build();
	}
	
	//PROCESSOR
	@Bean
	public PersonaItemProcessor processor() {
		return new PersonaItemProcessor();
	}
	
	//WRITTER
	@Bean
	public JdbcBatchItemWriter<Persona> writer(DataSource dateSource){
		return new JdbcBatchItemWriterBuilder<Persona>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO persona (primer_nombre, segundo_nombre, telefono) VALUES (:primerNombre, :segundoNombre, :telefono)")
				.dataSource(dateSource)
				.build();
	}
	
	
	/*
	 * CONFIGURACION DEL JOB
	 * Utilizamos el listener para indicarle al job el listener que se ejecutara cuando el estado del job cambie, y tambien el step
	 * utilizamos la dependencia JobBuilderFactory
	 * se le asigna un incrementer ya que los jobs crean registro en memoria y cuando hay varios batchs cada uno tiene un id de ejecucion diferente,
	 * por lo que hay que indicar un incrementer para que la estructura interna de spring en los batch pueda funcionar correctamente
	 * el flow es el paso
	 * */
	
	@Bean
	public Job importPersonaJob(JobListener listener, Step step1) {
		return jobBuilderFactory.get("importPersonaJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end()
				.build();
	}
	
	//Chunk indica el tamaño del lote, depende del tamaño de la informacion, 
	@Bean
	public Step step1(JdbcBatchItemWriter<Persona> writer) {
		return stepBuilderFactory.get("step1")
				.<Persona,Persona> chunk(10)
				.reader(reader())
				.processor(processor())
				.writer(writer)
				.build();
	}
	
}
