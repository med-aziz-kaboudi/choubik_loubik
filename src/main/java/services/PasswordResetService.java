package services;

import utils.Mailing;
import utils.MyDatabase;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PasswordResetService {
    private Connection connection;

    public PasswordResetService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public boolean resetPassword(String email) throws SQLException {
        String newPassword = generateNewPassword();
        // Update the password in all tables where the email is present
        updatePassword("client", email, newPassword);
        updatePassword("gerant", email, newPassword);
        updatePassword("livreur", email, newPassword);

        // Send email with the new password
        Mailing.sendEmail(email, "Password Reset", "Your new password is: " + newPassword);

        return true;
    }

    private void updatePassword(String tableName, String email, String newPassword) throws SQLException {
        String sql = "UPDATE " + tableName + " SET password = ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
        }
    }

    private String generateNewPassword() {
        SecureRandom random = new SecureRandom();
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            newPassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        return newPassword.toString();
    }
}
