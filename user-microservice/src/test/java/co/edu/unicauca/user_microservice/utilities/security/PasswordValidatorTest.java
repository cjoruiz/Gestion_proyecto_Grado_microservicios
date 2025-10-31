package co.edu.unicauca.user_microservice.utilities.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordValidatorTest {

    @Test
    void debeAceptarContrasenaValida() {
        assertTrue(PasswordValidator.isValid("Pass123!"));
        assertTrue(PasswordValidator.isValid("MyP@ssw0rd"));
    }

    @Test
    void debeRechazarContrasenaDebil() {
        assertFalse(PasswordValidator.isValid("pass")); // sin mayúscula, número o especial
        assertFalse(PasswordValidator.isValid("PASSWORD")); // sin minúscula, número o especial
        assertFalse(PasswordValidator.isValid("123456")); // sin letras o especial
        assertFalse(PasswordValidator.isValid("Password")); // sin número o especial
        assertFalse(PasswordValidator.isValid("Pass123")); // sin carácter especial
    }
}