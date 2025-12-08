package co.edu.unicauca.user_microservice.service;

import co.edu.unicauca.user_microservice.entity.*;
import co.edu.unicauca.user_microservice.infra.dto.UsuarioDetalladoDto;
import co.edu.unicauca.user_microservice.repository.UsuarioRepository;
import co.edu.unicauca.user_microservice.utilities.exception.InvalidUserDataException;
import co.edu.unicauca.user_microservice.utilities.exception.UserAlreadyExistsException;
import co.edu.unicauca.user_microservice.utilities.security.PasswordValidator;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UsuarioService implements IUsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private Keycloak keycloakAdminClient; // Inyecta el bean configurado

    @Value("${keycloak.realm}")
    private String realm; // Nombre del realm

    // Opcional: Inyecta el repositorio local si almacenas datos específicos del dominio
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public Usuario registrarEstudiante(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        logger.info("Iniciando registro de estudiante: {}", usuario.getEmail());
        return registrarUsuario(usuario, "ESTUDIANTE");
    }

    @Override
    public Usuario registrarDocente(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        logger.info("Iniciando registro de docente: {}", usuario.getEmail());
        return registrarUsuario(usuario, "DOCENTE");
    }

    @Override
    public Usuario registrarCoordinador(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        logger.info("Iniciando registro de coordinador: {}", usuario.getEmail());
        return registrarUsuario(usuario, "COORDINADOR");
    }

    @Override
    public Usuario registrarJefeDepartamento(Usuario usuario) throws UserAlreadyExistsException, InvalidUserDataException {
        logger.info("Iniciando registro de jefe de departamento: {}", usuario.getEmail());
        return registrarUsuario(usuario, "JEFE_DEPARTAMENTO");
    }

   private Usuario registrarUsuario(Usuario usuario, String rolEsperado) throws UserAlreadyExistsException, InvalidUserDataException {
    logger.debug("Validando usuario: {}", usuario.getEmail());
    validarUsuario(usuario);

    logger.debug("Obteniendo recurso del realm: {}", realm);
    RealmResource realmResource = keycloakAdminClient.realm(realm);

    // Verificar si el usuario ya existe en Keycloak
    logger.debug("Buscando usuario existente: {}", usuario.getEmail());
    List<UserRepresentation> existingUsers = realmResource.users().search(usuario.getEmail(), null, null, null, 0, 1);
    if (!existingUsers.isEmpty()) {
        logger.warn("Usuario ya existe: {}", usuario.getEmail());
        throw new UserAlreadyExistsException("El usuario con email " + usuario.getEmail() + " ya existe en Keycloak.");
    }

    // Crear representación del usuario para Keycloak
    logger.debug("Creando representación de usuario para: {}", usuario.getEmail());
    UserRepresentation userRep = new UserRepresentation();
    userRep.setEnabled(true);
    userRep.setEmail(usuario.getEmail());
    userRep.setFirstName(usuario.getNombres());
    userRep.setLastName(usuario.getApellidos());
    userRep.setUsername(usuario.getEmail());
    
    // ⬅️ CORRECCIÓN: Siempre agregar atributos, incluyendo programa
    Map<String, List<String>> attributes = new HashMap<>();
    
    if (usuario.getCelular() != null && !usuario.getCelular().isEmpty()) {
        attributes.put("telefono", List.of(usuario.getCelular()));
    }
    
    // ⬅️ CRÍTICO: Agregar programa siempre que no sea null
    if (usuario.getPrograma() != null && !usuario.getPrograma().isEmpty()) {
        attributes.put("programa", List.of(usuario.getPrograma()));
        logger.debug("Agregando programa al usuario: {}", usuario.getPrograma());
    }
    
    // ⬅️ NUEVO: Agregar tipo de docente si aplica
    if (usuario instanceof Docente) {
        Docente docente = (Docente) usuario;
        if (docente.getTipoDocente() != null) {
            attributes.put("tipoDocente", List.of(docente.getTipoDocente().toString()));
        }
    }
    
    userRep.setAttributes(attributes);

    // Enviar solicitud de creación
    logger.debug("Enviando solicitud de creación para: {}", usuario.getEmail());
    Response response = realmResource.users().create(userRep);
    if (response.getStatus() != 201) {
        String responseBody = response.readEntity(String.class);
        logger.error("Error al crear usuario en Keycloak para {}: Status={}, Body={}", usuario.getEmail(), response.getStatus(), responseBody);
        throw new RuntimeException("Error al crear usuario en Keycloak: " + response.getStatus() + " - " + responseBody);
    }

    String userId = response.getLocation().getPath().replaceAll(".*/", "");
    logger.info("Usuario creado exitosamente en Keycloak con ID: {}", userId);

    // Establecer contraseña
    logger.debug("Estableciendo contraseña para usuario ID: {}", userId);
    CredentialRepresentation cred = new CredentialRepresentation();
    cred.setType(CredentialRepresentation.PASSWORD);
    cred.setValue(usuario.getPassword());
    cred.setTemporary(false);
    realmResource.users().get(userId).resetPassword(cred);

    // Asignar rol como Client Role
    logger.debug("Buscando cliente 'sistema-desktop' para asignar rol.");
    var clientResource = realmResource.clients().findByClientId("sistema-desktop");
    if (clientResource == null || clientResource.isEmpty()) {
        logger.error("Cliente 'sistema-desktop' no encontrado en Keycloak.");
        throw new RuntimeException("Cliente 'sistema-desktop' no encontrado en Keycloak.");
    }
    String clienteId = clientResource.get(0).getId();
    logger.debug("ID del cliente 'sistema-desktop': {}", clienteId);

    logger.debug("Buscando rol '{}' en el cliente 'sistema-desktop' para usuario ID: {}", rolEsperado, userId);
    var roleResource = realmResource.clients().get(clienteId).roles().get(rolEsperado).toRepresentation();
    if (roleResource == null) {
        logger.error("Rol '{}' no encontrado como Client Role en 'sistema-desktop'.", rolEsperado);
        throw new RuntimeException("Rol '" + rolEsperado + "' no encontrado como Client Role en 'sistema-desktop'.");
    }

    logger.debug("Asignando Client Role '{}' del cliente 'sistema-desktop' al usuario ID: {}", rolEsperado, userId);
    realmResource.users().get(userId).roles().clientLevel(clienteId).add(List.of(roleResource));
    logger.info("Client Role '{}' del cliente 'sistema-desktop' asignado exitosamente al usuario ID: {}", rolEsperado, userId);

    // ⬅️ NUEVO: Guardar también en base de datos local
    usuarioRepository.save(usuario);
    
    logger.info("Registro de usuario '{}' completado exitosamente.", usuario.getEmail());
    return usuario;
}
    // --- Resto del código (obtenerPorEmail, existeUsuario, obtenerRol) permanece igual ---
    // Asegúrate de que obtenerRol también busque en Client Roles si es necesario para otras operaciones.
    // Para la validación del token, lo importante es que el rol esté asignado como Client Role del cliente 'sistema-desktop'.

    @Override
public Usuario obtenerPorEmail(String email) throws InvalidUserDataException {
    try {
        logger.debug("Obteniendo usuario por email: {}", email);
        
        // ⬅️ NUEVO: Primero intentar desde BD local
        Optional<Usuario> usuarioLocal = usuarioRepository.findById(email);
        if (usuarioLocal.isPresent()) {
            logger.debug("Usuario encontrado en BD local: {}", email);
            return usuarioLocal.get();
        }
        
        // Si no está en BD local, buscar en Keycloak
        RealmResource realmResource = keycloakAdminClient.realm(realm);
        List<UserRepresentation> users = realmResource.users().search(email, null, null, null, 0, 1);
        if (users.isEmpty()) {
            logger.debug("Usuario no encontrado en Keycloak: {}", email);
            return null;
        }
        UserRepresentation userRep = users.get(0);

        // Mapear UserRepresentation a tu entidad Usuario
        String rol = obtenerRol(email);
        Usuario usuarioMapeado = null;

        switch (rol) {
            case "ESTUDIANTE":
                usuarioMapeado = new Estudiante();
                break;
            case "DOCENTE":
                Docente docente = new Docente();
                // ⬅️ CORRECCIÓN: Obtener tipo de docente de atributos
                if (userRep.getAttributes() != null && userRep.getAttributes().containsKey("tipoDocente")) {
                    String tipoStr = userRep.getAttributes().get("tipoDocente").get(0);
                    docente.setTipoDocente(co.edu.unicauca.user_microservice.entity.enums.TipoDocente.valueOf(tipoStr));
                }
                usuarioMapeado = docente;
                break;
            case "COORDINADOR":
                usuarioMapeado = new Coordinador();
                break;
            case "JEFE_DEPARTAMENTO":
                usuarioMapeado = new JefeDepartamento();
                break;
            default:
                usuarioMapeado = new Usuario(email) {};
                break;
        }

        usuarioMapeado.setEmail(userRep.getEmail());
        usuarioMapeado.setNombres(userRep.getFirstName());
        usuarioMapeado.setApellidos(userRep.getLastName());
        
        // ⬅️ CORRECCIÓN: Extraer atributos correctamente
        if (userRep.getAttributes() != null) {
            if (userRep.getAttributes().containsKey("telefono")) {
                usuarioMapeado.setCelular(userRep.getAttributes().get("telefono").get(0));
            }
            // ⬅️ CRÍTICO: Extraer programa
            if (userRep.getAttributes().containsKey("programa")) {
                String programa = userRep.getAttributes().get("programa").get(0);
                usuarioMapeado.setPrograma(programa);
                logger.debug("Programa recuperado de Keycloak: {}", programa);
            } else {
                logger.warn("No se encontró atributo 'programa' para el usuario: {}", email);
            }
        }

        logger.debug("Usuario obtenido exitosamente: {}", email);
        return usuarioMapeado;
    } catch (Exception e) {
        logger.error("Error obteniendo usuario de Keycloak: {}", e.getMessage(), e);
        return null;
    }
}

    @Override
    public boolean existeUsuario(String email) {
        try {
            logger.debug("Verificando existencia de usuario: {}", email);
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            List<UserRepresentation> users = realmResource.users().search(email, null, null, null, 0, 1);
            boolean existe = !users.isEmpty();
            logger.debug("Usuario '{}' existe: {}", email, existe);
            return existe;
        } catch (Exception e) {
            logger.error("Error verificando existencia de usuario en Keycloak: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String obtenerRol(String email) {
        try {
            logger.debug("Obteniendo rol para usuario: {}", email);
            RealmResource realmResource = keycloakAdminClient.realm(realm);
            List<UserRepresentation> users = realmResource.users().search(email, null, null, null, 0, 1);
            if (users.isEmpty()) {
                logger.debug("Usuario no encontrado para obtener rol: {}", email);
                return "DESCONOCIDO";
            }
            UserRepresentation userRep = users.get(0);

            // --- CORRECCIÓN: Buscar Client Roles del cliente 'sistema-desktop' ---
            var clientResource = realmResource.clients().findByClientId("sistema-desktop");
            if (clientResource == null || clientResource.isEmpty()) {
                logger.error("Cliente 'sistema-desktop' no encontrado para obtener roles de usuario.");
                return "DESCONOCIDO";
            }
            String clienteId = clientResource.get(0).getId();

            List<String> roles = realmResource.users().get(userRep.getId()).roles().clientLevel(clienteId).listAll().stream()
                    .map(RoleRepresentation::getName)
                    .toList();
            // --- FIN CORRECCIÓN ---

            logger.debug("Client Roles encontrados para '{}' en 'sistema-desktop': {}", email, roles);
            for (String role : roles) {
                if ("DOCENTE".equals(role)) return "DOCENTE";
                if ("ESTUDIANTE".equals(role)) return "ESTUDIANTE";
                if ("COORDINADOR".equals(role)) return "COORDINADOR";
                if ("JEFE_DEPARTAMENTO".equals(role)) return "JEFE_DEPARTAMENTO";
            }
            return "DESCONOCIDO";
        } catch (Exception e) {
            logger.error("Error obteniendo rol de usuario en Keycloak: {}", e.getMessage(), e);
            return "DESCONOCIDO";
        }
    }

    private void validarUsuario(Usuario usuario) throws InvalidUserDataException {
        if (usuario.getEmail() == null || !usuario.getEmail().endsWith("@unicauca.edu.co")) {
            throw new InvalidUserDataException("El email debe ser del dominio @unicauca.edu.co");
        }
        if (!PasswordValidator.isValid(usuario.getPassword())) {
            throw new InvalidUserDataException("La contraseña no cumple con los requisitos.");
        }
        // Puedes agregar más validaciones aquí
    }

    // UsuarioService.java
@Override
public List<UsuarioDetalladoDto> obtenerDocentesPorPrograma(String programa) {
    try {
        logger.debug("Obteniendo docentes por programa desde BD local: {}", programa);
        List<Docente> docentes = usuarioRepository.findDocentesByPrograma(programa);
        List<UsuarioDetalladoDto> dtos = new ArrayList<>();
        for (Docente docente : docentes) {
            UsuarioDetalladoDto dto = new UsuarioDetalladoDto();
            dto.setEmail(docente.getEmail());
            dto.setNombres(docente.getNombres());
            dto.setApellidos(docente.getApellidos());
            dto.setCelular(docente.getCelular());
            dto.setPrograma(docente.getPrograma());
            dto.setRol("DOCENTE");
            dtos.add(dto);
        }
        logger.debug("Docentes encontrados en BD local: {}", dtos.size());
        return dtos;
    } catch (Exception e) {
        logger.error("Error al obtener docentes por programa desde BD local", e);
        return Collections.emptyList();
    }
}
}
