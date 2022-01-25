package com.batch.demospringbatch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {
	
	String primerNombre;
	String segundoNombre;
	String telefono;
	
	@Override
	public String toString() {
		return "Persona [primerNombre=" + primerNombre + ", segundoNombre=" + segundoNombre + ", telefono=" + telefono
				+ "]";
	}
	
}
