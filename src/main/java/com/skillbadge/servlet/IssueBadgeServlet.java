package com.skillbadge.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.MessageDigest;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/issue")
public class IssueBadgeServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/skill_badge_portal";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "W7301@jqir#";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String studentIdStr = request.getParameter("student_id");
        String moduleIdStr = request.getParameter("module_id");
        String scoreStr = request.getParameter("score");

        if (studentIdStr == null || moduleIdStr == null || scoreStr == null) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Missing parameters");
            out.print(gson.toJson(error));
            return;
        }

        try {
            int studentId = Integer.parseInt(studentIdStr);
            int moduleId = Integer.parseInt(moduleIdStr);
            int score = Integer.parseInt(scoreStr);
            
            String tier = "Bronze";
            if (score >= 90) {
                tier = "Gold";
            } else if (score >= 75) {
                tier = "Silver";
            }

            long timestamp = System.currentTimeMillis();
            String rawData = studentId + "-" + moduleId + "-" + timestamp;
            String verificationCode = generateHash(rawData).substring(0, 10).toUpperCase();

            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                
                // Verify student exists
                try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM Students WHERE student_id = ?")) {
                    stmt.setInt(1, studentId);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("status", "ERROR");
                        error.put("message", "Student ID does not exist");
                        out.print(gson.toJson(error));
                        return;
                    }
                }
                
                // Verify module exists
                try (PreparedStatement stmt = conn.prepareStatement("SELECT 1 FROM Modules WHERE module_id = ?")) {
                    stmt.setInt(1, moduleId);
                    ResultSet rs = stmt.executeQuery();
                    if (!rs.next()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("status", "ERROR");
                        error.put("message", "Module ID does not exist");
                        out.print(gson.toJson(error));
                        return;
                    }
                }

                // Insert Badge
                try (PreparedStatement stmt = conn.prepareStatement(
                        "INSERT INTO Badges (student_id, module_id, verification_code, score, tier) VALUES (?, ?, ?, ?, ?)")) {
                    stmt.setInt(1, studentId);
                    stmt.setInt(2, moduleId);
                    stmt.setString(3, verificationCode);
                    stmt.setInt(4, score);
                    stmt.setString(5, tier);
                    
                    stmt.executeUpdate();

                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "SUCCESS");
                    result.put("verification_code", verificationCode);
                    result.put("tier", tier);
                    out.print(gson.toJson(result));
                }
            }
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Invalid number format");
            out.print(gson.toJson(error));
        } catch (SQLIntegrityConstraintViolationException e) {
             Map<String, String> error = new HashMap<>();
             error.put("status", "ERROR");
             error.put("message", "Badge possibly already exists or DB constraint failed");
             out.print(gson.toJson(error));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", e.getMessage());
            out.print(gson.toJson(error));
        }
    }
    
    private String generateHash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate hash", e);
        }
    }
}
