package co.edu.unicauca.user_microservice.controller;

import co.edu.unicauca.user_microservice.entity.Estudiante;
import co.edu.unicauca.user_microservice.service.IUsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IUsuarioService usuarioService;

    @Test
    void debeRegistrarEstudianteYRetornar200() throws Exception {
        Estudiante estudiante = new Estudiante();
        estudiante.setEmail("estudiante@unicauca.edu.co");
        estudiante.setNombres("Ana");
        estudiante.setApellidos("López");

        when(usuarioService.registrarEstudiante(any())).thenReturn(estudiante);

        mockMvc.perform(post("/api/usuarios/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "estudiante@unicauca.edu.co",
                                  "password": "Stud123!",
                                  "nombres": "Ana",
                                  "apellidos": "López",
                                  "programa": "INGENIERIA_SISTEMAS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("estudiante@unicauca.edu.co"));
    }

    @Test
    void debeValidarUsuarioYRetornarRol() throws Exception {
        when(usuarioService.existeUsuario("docente@unicauca.edu.co")).thenReturn(true);
        when(usuarioService.obtenerRol("docente@unicauca.edu.co")).thenReturn("DOCENTE");

        mockMvc.perform(get("/api/usuarios/validar")
                        .param("email", "docente@unicauca.edu.co"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.existe").value(true))
                .andExpect(jsonPath("$.rol").value("DOCENTE"));
    }

    @Test
    void debeRetornar404SiUsuarioNoExiste() throws Exception {
        when(usuarioService.obtenerPorEmail("noexiste@unicauca.edu.co"))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/api/usuarios/noexiste@unicauca.edu.co"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Usuario no encontrado"));
    }
}