package services;

import models.Subscription;
import utils.MyDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class SubscriptionService {
    private final Connection connection;

    public SubscriptionService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public void createSubscription(int restaurantId, int subscriptionTypeId) {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(1);

        String sql = "INSERT INTO abonnement (id_resto, status, date_debut, date_fin, abonnement_type_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, restaurantId);
            preparedStatement.setInt(2, 1);
            preparedStatement.setDate(3, java.sql.Date.valueOf(startDate));
            preparedStatement.setDate(4, java.sql.Date.valueOf(endDate));
            preparedStatement.setInt(5, subscriptionTypeId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating subscription failed, no rows affected.");
            }
            System.out.println("Subscription added successfully.");
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());

        }
    }
    // In SubscriptionService.java
    public void updateExpiredSubscriptions() {
        String sql = "UPDATE abonnement SET status = 0 WHERE date_fin <= CURDATE()";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Expired subscriptions updated successfully.");
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());
            // Handle exceptions
        }
    }
    public boolean isSubscriptionValid(int gerantId, LocalDate offerEndDate) throws SQLException {
        String sql = "SELECT date_fin FROM abonnement WHERE id_resto = ? AND status = 1 ORDER BY date_fin DESC LIMIT 1";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, gerantId);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                LocalDate subscriptionEndDate = resultSet.getDate("date_fin").toLocalDate();
                return !offerEndDate.isAfter(subscriptionEndDate);
            } else {
                throw new SQLException("No active subscription found for Gerant ID: " + gerantId);
            }
        }
    }
    public LocalDate getSubscriptionEndDate(int gerantId) throws SQLException {
        String sql = "SELECT date_fin FROM abonnement WHERE id_resto = ? AND status = 1 ORDER BY date_fin DESC LIMIT 1";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, gerantId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDate("date_fin").toLocalDate();
                } else {
                    return null; // No active subscription found for this gerant.
                }
            }
        }
    }

}
