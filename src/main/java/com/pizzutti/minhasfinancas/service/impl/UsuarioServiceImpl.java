package com.pizzutti.minhasfinancas.service.impl;

import com.pizzutti.minhasfinancas.exception.ErroAutenticacao;
import com.pizzutti.minhasfinancas.exception.RegraNegocioException;
import com.pizzutti.minhasfinancas.model.entity.Usuario;
import com.pizzutti.minhasfinancas.model.repository.UsuarioRepository;
import com.pizzutti.minhasfinancas.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private UsuarioRepository repository;

    public UsuarioServiceImpl(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public Usuario autenticar(String email, String senha) {

        Optional<Usuario> usuario = repository.findByEmail(email);

        if(!usuario.isPresent()){

            throw new ErroAutenticacao("Usuário não encontrado para o email informado.");

        }

        if (!usuario.get().getSenha().equals(senha)){

            throw new ErroAutenticacao("Senha inválida.");

        }

        return usuario.get();

    }

    @Override
    @Transactional
    public Usuario salvarUsuario(Usuario usuario) {

        validarEmail(usuario.getEmail());

        return repository.save(usuario);

    }

    @Override
    public void validarEmail(String email) {

        boolean existe = repository.existsByEmail(email);

        if(existe){

            throw new RegraNegocioException("Já existe um usuário cadastrado com esse email.");

        }
    }

    @Override
    public Optional<Usuario> obterPorId(Long id) {

        return repository.findById(id);

    }
}
