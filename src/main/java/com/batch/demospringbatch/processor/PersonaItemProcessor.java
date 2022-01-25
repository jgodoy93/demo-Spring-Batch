package com.batch.demospringbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import com.batch.demospringbatch.model.Persona;

public class PersonaItemProcessor implements ItemProcessor<Persona,Persona> {

	private static final Logger LOG = LoggerFactory.getLogger(PersonaItemProcessor.class);

	@Override
	public Persona process(Persona item) throws Exception {
		
		var primerNombre = item.getPrimerNombre().toUpperCase();
		var segundoNombre = item.getSegundoNombre().toUpperCase();
		var telefono = item.getTelefono();
		
		var persona = Persona.builder()
				.primerNombre(primerNombre)
				.segundoNombre(segundoNombre)
				.telefono(telefono)
				.build();
		
		LOG.info("Convirtiendo ("+item+") a ("+persona+")");
		
		return persona;
	}
	


}
