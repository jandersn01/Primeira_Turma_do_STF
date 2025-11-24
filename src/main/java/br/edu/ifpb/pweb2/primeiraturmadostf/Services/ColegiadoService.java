package br.edu.ifpb.pweb2.primeiraturmadostf.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifpb.pweb2.primeiraturmadostf.repository.ColegiadoRepository;

@Service
@Transactional
public class ColegiadoService {

    private final ColegiadoRepository colegiadoRepository;

    @Autowired
    public ColegiadoService(ColegiadoRepository repository){
        this.colegiadoRepository = repository;
    }
}
