package com.jhonatan.projetospringboot.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.jhonatan.projetospringboot.model.Pessoa;
import com.jhonatan.projetospringboot.model.Telefone;

@Repository
@Transactional
public interface TelefoneRepository extends CrudRepository<Telefone, Long> {
	@Query("select t from Telefone t where t.pessoa.id = ?1 ")
	public List<Telefone> listaTelefonesPorPessoa(Long pid);
}
