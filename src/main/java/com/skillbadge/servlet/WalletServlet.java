package com.skillbadge.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/wallet")
public class WalletServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/skill_badge_portal";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "W7301@jqir#";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String studentIdStr = request.getParameter("student_id");

        if (studentIdStr == null || studentIdStr.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Student ID is required");
            out.print(gson.toJson(error));
            return;
        }

        int studentId;
        try {
            studentId = Integer.parseInt(studentIdStr);
        } catch (NumberFormatException e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Invalid Student ID");
            out.print(gson.toJson(error));
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                
                // Get student name
                String studentName = "";
                try (PreparedStatement stmt = conn.prepareStatement("SELECT name FROM Students WHERE student_id = ?")) {
                    stmt.setInt(1, studentId);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        studentName = rs.getString("name");
                    } else {
                        Map<String, String> error = new HashMap<>();
                        error.put("status", "ERROR");
                        error.put("message", "Student not found");
                        out.print(gson.toJson(error));
                        return;
                    }
                }

                // Get badges
                List<Map<String, Object>> badges = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(
                        "SELECT b.verification_code, b.issued_at, b.score, b.tier, m.title as module_title " +
                                "FROM Badges b JOIN Modules m ON b.module_id = m.module_id " +
                                "WHERE b.student_id = ?")) {
                    stmt.setInt(1, studentId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Map<String, Object> badge = new HashMap<>();
                        badge.put("verification_code", rs.getString("verification_code"));
                        badge.put("issued_at", rs.getString("issued_at"));
                        badge.put("score", rs.getInt("score"));
                        badge.put("tier", rs.getString("tier"));
                        badge.put("module_title", rs.getString("module_title"));
                        badges.add(badge);
                    }
                }

                Map<String, Object> result = new HashMap<>();
                result.put("status", "SUCCESS");
                result.put("student_name", studentName);
                result.put("badges", badges);
                
                out.print(gson.toJson(result));
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", e.getMessage());
            out.print(gson.toJson(error));
        }
    }
}
