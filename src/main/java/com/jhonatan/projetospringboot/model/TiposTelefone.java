package com.jhonatan.projetospringboot.model;

public enum TiposTelefone {
	CELULAR("Celular"),
	TELEFONE("Telefone"),
	RECADO("Recado");

	private String nome;
	
	private TiposTelefone(String nome) {
		
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	
}
