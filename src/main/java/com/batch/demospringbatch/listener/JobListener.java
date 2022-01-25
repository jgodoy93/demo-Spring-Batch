package com.batch.demospringbatch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.batch.demospringbatch.model.Persona;


@Component
public class JobListener extends JobExecutionListenerSupport {

	private static final Logger LOG = LoggerFactory.getLogger(JobListener.class);
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public JobListener(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}
	
	
	/*
	 * Sobreescribo After job agregandole propia logica
	 * 
	 * puede agregar logica para todos los estados de batch ( abandonado, completado,
	 * cuando falla, cuando inicio, cuando esta iniciando, cuando se detuvo, etc)
	 * 
	 * */
	@Override
	public void afterJob(JobExecution jobExecution) {
		
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			
			LOG.info("Finalizo el Job! Verifica los resultados: ");
			
			jdbcTemplate.query("SELECT primer_nombre, segundo_nombre, telefono FROM persona",
					(rs, row) -> Persona.builder()
										.primerNombre(rs.getString(1))
										.segundoNombre(rs.getString(2))
										.telefono(rs.getString(3))
										.build())
					.forEach(persona -> LOG.info("Registro < "+ persona + ">"));
		}
		
	}
	
	
}
