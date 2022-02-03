package com.pizzutti.minhasfinancas.service;

import com.pizzutti.minhasfinancas.exception.ErroAutenticacao;
import com.pizzutti.minhasfinancas.exception.RegraNegocioException;
import com.pizzutti.minhasfinancas.model.entity.Usuario;
import com.pizzutti.minhasfinancas.model.repository.UsuarioRepository;
import com.pizzutti.minhasfinancas.service.impl.UsuarioServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

    @SpyBean
    UsuarioServiceImpl service;

    @MockBean
    UsuarioRepository repository;


    @Test
    public void deveSalvarUmUsuario(){

        //cenário
        Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
        Usuario usuario = Usuario.builder()
                .id(1l)
                .nome("nome")
                .email("email@email.com")
                .senha("senha")
                .build();

        Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);

        //ação
        Usuario usuarioSalvo = service.salvarUsuario(new Usuario());

        //verificação
        Assertions.assertEquals(usuarioSalvo, usuario);
    }

    @Test
    public void naoDeveSalvarUmUsuarioComEmailJaCadastrado(){

        //cenário
        String email = "email@email.com";
        Usuario usuario = Usuario.builder().email(email).build();
        Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);

        //ação
        service.salvarUsuario(usuario);

        //verificação
        Mockito.verify(repository, Mockito.never()).save(usuario);

    }

    @Test
    public void deveAutenticarUmUsuarioComSucesso(){

        //cenário
        String email = "email@email.com";
        String senha = "senha";

        Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();

        Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));

        //ação
        Usuario result = service.autenticar(email, senha);

        //verificação
        Assertions.assertEquals(result, usuario);

    }

    @Test
    public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {

        //cenário
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());

        //ação
        ErroAutenticacao thrown = Assertions.assertThrows(ErroAutenticacao.class,
                () -> {
                    service.autenticar("email@email.com", "senha");
                });
        //verificação
        Assertions.assertEquals("Usuário não encontrado para o email informado.", thrown.getMessage());

    }

    @Test
    public void deveLancarErroQuandoSenhaNaoBater() {

        //cenário
        String senha = "senha";
        Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
        Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));

        //ação
        ErroAutenticacao thrown = Assertions.assertThrows(ErroAutenticacao.class,
                () -> {
                    service.autenticar("email@email.com", "senhaErrada");
                });

        //verificação
        Assertions.assertEquals("Senha inválida.", thrown.getMessage());
    }

    @Test
    public void deveValidarEmail(){

        //cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

        //ação
        service.validarEmail("email@email.com");

    }

    @Test
    public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado(){

        //cenário
        Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

        //ação
        RegraNegocioException thrown = Assertions.assertThrows(RegraNegocioException.class,
                () -> {
                    service.validarEmail("email@email.com");
                });

        //verificação
        Assertions.assertEquals("Já existe um usuário cadastrado com esse email.", thrown.getMessage());
    }


}
