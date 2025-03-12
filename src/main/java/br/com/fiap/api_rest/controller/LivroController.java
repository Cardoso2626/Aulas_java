package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.LivroRequest;
import br.com.fiap.api_rest.dto.LivroResponse;
import br.com.fiap.api_rest.model.Livro;
import br.com.fiap.api_rest.repository.LivroRepository;
import br.com.fiap.api_rest.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/livros", consumes = {"application/json"})
@Tag(name = "api-livros")
public class LivroController {
    @Autowired
    private LivroRepository livroRepository;
    @Autowired
    private LivroService livroService;

    // CREATE, READ, UPDATE, DELETE
    // POST, GET, PUT, DELETE
    @Operation (summary = "Cria um livro")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Livro criado com sucesso",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Livro.class))}),
            @ApiResponse(responseCode = "400", description = "Parâmetros informados são inválidos ", content = {@Content(schema = @Schema())})
    }

    )
    @PostMapping
    public ResponseEntity<Livro> createLivro(@Valid @RequestBody LivroRequest livro) {
        Livro livroSalvo = livroRepository.save(livroService.requestToLivro(livro));
        return new ResponseEntity<>(livroSalvo,HttpStatus.CREATED);
    }


    @Operation (summary = "Lista todos os livros separados por página")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "20", description = "Livros encontrados com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Livro.class))}),
            @ApiResponse(responseCode = "400", description = "Parâmetros informados são inválidos ", content = {@Content(schema = @Schema())})
    }
    )
    @GetMapping
        public ResponseEntity<Page<LivroResponse>> readLivros(@RequestParam(defaultValue = "0") Integer pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, 2, Sort.by("titulo").ascending().and(Sort.by("titulo").ascending()));
        Page<LivroResponse> livros = livroService.findAll(pageable);
        for (LivroResponse livro: livros){
            livro.setLink(
                    linkTo(
                            methodOn(LivroController.class).readLivro(livro.getId())
                    ).withSelfRel()
            );
        }


        return new ResponseEntity<>(livroService.findAll(pageable),HttpStatus.OK);
    }


    // @Path localhost:8080/livros/1
    // @RequestParam LOCALHOST:8080/LIVROS/?id=1
    @Operation (summary = "Lista apenas um livro pelo id")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "20o", description = "Livro encontrado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = LivroResponseDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Lista de livros não encontrada", content = {@Content(schema = @Schema())})
    }
    )
    @GetMapping("/{id}")
    public ResponseEntity<LivroResponse> readLivro(@PathVariable Long id) {
        
        

        Optional<Livro> livro = livroRepository.findById(id);
        if (livro.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        LivroResponse livroResponse = livroService.livroToResponse(livro.get());
        livroResponse.setLink(
                linkTo(
                        methodOn(LivroController.class).readLivros(0)
                ).withRel("Lista de livros ")
        );
        return new ResponseEntity<>(livroResponse,HttpStatus.OK);
    }

    @Operation (summary = "Atualiza o livro de acordo com o id passado")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "201", description = "Livro atualizado com sucesso",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Livro.class))}),
            @ApiResponse(responseCode = "400", description = "Nenhum livro encontrado para o id fornecido ", content = {@Content(schema = @Schema())})
    }
    )

    @PutMapping("/{id}")
    public ResponseEntity<Livro> updateLivro( @PathVariable Long id, @RequestBody LivroRequest livro) {
        Optional<Livro> livroExistente = livroRepository.findById(id);
        if (livroExistente.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        Livro livroConvertido = livroService.requestToLivro(livro);

        livroConvertido.setId(livroExistente.get().getId());
        Livro livroSalvo = livroRepository.save(livroConvertido);
        return new ResponseEntity<>(livroSalvo,HttpStatus.CREATED);
    }

    @Operation (summary = "Exclui um livro por id")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "20o", description = "Livro excluido com sucesso",
                    content = {@Content(schema = @Schema())}),
            @ApiResponse(responseCode = "400", description = "Nenhum livro excluido de acordo com o id passado ",
                    content = {@Content(schema = @Schema())})
    }
    )
    @DeleteMapping("/{id}")

    public ResponseEntity<Void> deleteLivro(@PathVariable Long id) {
        Optional<Livro> livroExistente = livroRepository.findById(id);
        if (livroExistente.isEmpty()){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        livroRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }



}
