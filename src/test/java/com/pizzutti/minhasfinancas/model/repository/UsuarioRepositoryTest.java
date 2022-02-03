package com.pizzutti.minhasfinancas.model.repository;

import com.pizzutti.minhasfinancas.model.entity.Usuario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioRepositoryTest {

    @Autowired
    UsuarioRepository repository;

    @Autowired
    TestEntityManager entityManager;

    @Test
    public void deveVerificarAExistenciaDeUmEmail(){

        //Cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //Ação
        boolean result = repository.existsByEmail("usuario@email.com");

        //Verificação
        Assertions.assertThat(result).isTrue();

    }

    @Test
    public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComEmail(){

        //Cenário
            //banco de dados vazio.

        //Ação
        boolean result = repository.existsByEmail("usuario@email.com");

        //Verificação
        Assertions.assertThat(result).isFalse();

    }

    @Test
    public void devePersistirUmUsuarioNaBaseDeDados(){

        //Cenário
        Usuario usuario = criarUsuario();

        //Acao
        Usuario usuarioSalvo = repository.save(usuario);

        //Verificacao
        Assertions.assertThat(usuarioSalvo.getId()).isNotNull();

    }

    @Test
    public void deveBuscarUmUsuarioPorEmail(){

        //Cenário
        Usuario usuario = criarUsuario();
        entityManager.persist(usuario);

        //Acao
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        //Verificacao
        Assertions.assertThat(result.isPresent()).isTrue();

    }

    @Test
    public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase(){

        //Cenário
            //banco de dados vazio.

        //Acao
        Optional<Usuario> result = repository.findByEmail("usuario@email.com");

        //Verificacao
        Assertions.assertThat(result.isPresent()).isFalse();

    }

    public static Usuario criarUsuario(){

        return Usuario.builder()
                .nome("usuario")
                .email("usuario@email.com")
                .senha("senha")
                .build();

    }
}
