package com.lucassellis.gestao_estoque_inteligente.Infrastructure.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/*
 Essa classe representa um erro de conflito.
 Normalmente usamos quando alguma regra de negócio é violada,
 por exemplo: tentar cadastrar um email que já existe.
*/
@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends RuntimeException {

    /*
     Construtor simples.
     Recebe apenas a mensagem do erro.
     Ex: "Email já cadastrado"
    */
    public ConflictException(String mensagem) {
        super(mensagem);
    }

    /*
     Construtor mais completo.
     Recebe a mensagem + a causa do erro (outro erro que gerou esse).
     Isso é útil quando quero encadear exceções.
    */
    public ConflictException(String mensagem, Throwable throwable) {
        super(mensagem, throwable); // aqui estava o erro: faltava passar o throwable
    }
}
