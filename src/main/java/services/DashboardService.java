package services;

import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DashboardService {
    private final Connection connection;

    public DashboardService() {
        this.connection = MyDatabase.getInstance().getConnection();
    }
    public void initialize() {
        scheduleMonthlyDonationUpdates();
    }


    public double fetchTotalIncomeForCurrentMonth() throws SQLException {
        String sql = "SELECT SUM(abonnement_type.price) AS total_income " +
                "FROM gerant " +
                "JOIN abonnement ON abonnement.abonnement_type_id = abonnement.id " +
                "JOIN abonnement_type ON abonnement_type.id_abonnement = abonnement.abonnement_type_id " +
                "WHERE abonnement.status =  1 AND MONTH(abonnement.date_debut) = MONTH(CURDATE()) AND YEAR(abonnement.date_debut) = YEAR(CURDATE())";

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_income");
            }
            return  0;
        }
    }


    public void updateMonthlyDonations() throws SQLException {
        double totalIncome = fetchTotalIncomeForCurrentMonth();
        // Let's assume that 5% of the total income is the donation amount.
        double donationAmount = totalIncome * 0.05;
        double netIncome = totalIncome - donationAmount; // If you need to calculate net income after donation.

        LocalDate now = LocalDate.now();
        String checkSql = "SELECT id FROM donations WHERE YEAR(date) = ? AND MONTH(date) = ?";
        try (PreparedStatement checkStmt = connection.prepareStatement(checkSql)) {
            checkStmt.setInt(1, now.getYear());
            checkStmt.setInt(2, now.getMonthValue());
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                int donationId = rs.getInt("id");
                String updateSql = "UPDATE donations SET total = total + ? WHERE id = ?";
                try (PreparedStatement updateStmt = connection.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, donationAmount);
                    updateStmt.setInt(2, donationId);
                    updateStmt.executeUpdate();
                }
            } else {
                String insertSql = "INSERT INTO donations (date, total) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertSql)) {
                    insertStmt.setDate(1, Date.valueOf(now));
                    insertStmt.setDouble(2, donationAmount);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    public void scheduleMonthlyDonationUpdates() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        // Set the initial delay to the start of next month
        LocalDate firstOfNextMonth = LocalDate.now().withDayOfMonth(1).plusMonths(1);
        long initialDelay = ChronoUnit.SECONDS.between(LocalDateTime.now(), firstOfNextMonth.atStartOfDay());
        long period = TimeUnit.DAYS.toSeconds(30); // This should be adjusted to account for different month lengths

        scheduler.scheduleAtFixedRate(() -> {
            try {
                updateMonthlyDonations();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }, initialDelay, period, TimeUnit.SECONDS);
    }



    public int fetchTotalUsers() throws SQLException {
        int totalUsers = 0;

        String clientCountSql = "SELECT COUNT(*) AS user_count FROM client";
        String gerantCountSql = "SELECT COUNT(*) AS user_count FROM gerant";
        String livreurCountSql = "SELECT COUNT(*) AS user_count FROM livreur";

        // Count clients
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(clientCountSql)) {
            if (rs.next()) {
                totalUsers += rs.getInt("user_count");
            }
        }

        // Count gerants
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(gerantCountSql)) {
            if (rs.next()) {
                totalUsers += rs.getInt("user_count");
            }
        }

        // Count livreurs
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(livreurCountSql)) {
            if (rs.next()) {
                totalUsers += rs.getInt("user_count");
            }
        }

        return totalUsers;
    }

}
