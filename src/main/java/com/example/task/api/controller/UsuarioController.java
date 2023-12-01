package com.example.task.api.controller;

import com.example.task.api.dto.TaskDto;
import com.example.task.api.dto.UsuarioDto;
import com.example.task.model.entity.Task;
import com.example.task.model.entity.Usuario;
import com.example.task.service.TaskService;
import com.example.task.service.UsuarioService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
@CrossOrigin
@Api(tags="User requests")
public class UsuarioController {

    private final UsuarioService service;
    private final TaskService taskService;

    @GetMapping()
    @ApiOperation("Get all users")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Users found"),
            @ApiResponse(code = 404, message = "Users not found")
    })
    public ResponseEntity get(){
        List<Usuario> usuarios = service.getUsuario();
        return ResponseEntity.ok(usuarios.stream().map(UsuarioDto::create).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @ApiOperation("Get user by id")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User found"),
            @ApiResponse(code = 404, message = "User not found")
    })
    public ResponseEntity get(@PathVariable("id") Long id) {
        Optional<Usuario> usuario = service.getUsuarioById(id);

        if(!usuario.isPresent()){
            return new ResponseEntity("Usuario not found!", HttpStatus.NOT_FOUND);
        }
        usuario.get().getName();
        return ResponseEntity.ok(usuario.map(UsuarioDto::create));
    }

    @GetMapping("/{id}/tasks")
    @ApiOperation("Get user tasks by userId")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User tasks found"),
            @ApiResponse(code = 404, message = "User tasks not found")
    })
    public ResponseEntity getUserTasks(@PathVariable("id") Long id){
        Optional<Usuario> usuario = service.getUsuarioById(id);
        if(!usuario.isPresent()){
            return new ResponseEntity("Usuário not found!", HttpStatus.NOT_FOUND);
        }

        List<Task> tasks = taskService.getTaskByUserId(usuario.get().getId());

        return ResponseEntity.ok(tasks.stream().map(TaskDto::create).collect(Collectors.toList()));
    }

    @PostMapping()
    @ApiOperation("Post user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User sucessfully saved"),
            @ApiResponse(code = 404, message = "Error saving user")
    })
    public ResponseEntity post(@RequestBody UsuarioDto dto){
        try {
            Usuario usuario = converter(dto);
            usuario = service.save(usuario);
            return new ResponseEntity(usuario, HttpStatus.CREATED);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @ApiOperation("Put user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User updated sucessfully"),
            @ApiResponse(code = 404, message = "Error updating user")
    })
    public ResponseEntity put(@PathVariable("id") Long id, @RequestBody UsuarioDto dto){
        if(!service.getUsuarioById(id).isPresent()){
            return new ResponseEntity("Usuario not found", HttpStatus.NOT_FOUND);
        }
        try {
            Usuario usuario = converter(dto);
            usuario.setId(id);
            service.save(usuario);
            return ResponseEntity.ok(usuario);
        } catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ApiOperation("Delete user")
    @ApiResponses({
            @ApiResponse(code = 200, message = "User deleted sucessfully"),
            @ApiResponse(code = 404, message = "Error deleting user")
    })
    public ResponseEntity delete(@PathVariable("id") Long id){
        Optional<Usuario> usuario = service.getUsuarioById(id);
        if(!usuario.isPresent()){
            return new ResponseEntity("Usuario not found", HttpStatus.NOT_FOUND);
        }
        try {
            service.delete(usuario.get());
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    public Usuario converter(UsuarioDto dto){
        ModelMapper modelMapper = new ModelMapper();
        Usuario usuario = modelMapper.map(dto, Usuario.class);
        return usuario;
    }
}
