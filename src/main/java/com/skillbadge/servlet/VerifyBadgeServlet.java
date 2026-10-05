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
import java.util.HashMap;
import java.util.Map;

@WebServlet("/verify")
public class VerifyBadgeServlet extends HttpServlet {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/skill_badge_portal";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "W7301@jqir#";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        Gson gson = new Gson();

        String code = request.getParameter("code");

        if (code == null || code.trim().isEmpty()) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", "Verification code is required");
            out.print(gson.toJson(error));
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                    PreparedStatement stmt = conn.prepareStatement(
                            "SELECT b.verification_code, b.issued_at, b.score, b.tier, " +
                                    "s.name as student_name, m.title as module_title " +
                                    "FROM Badges b " +
                                    "JOIN Students s ON b.student_id = s.student_id " +
                                    "JOIN Modules m ON b.module_id = m.module_id " +
                                    "WHERE b.verification_code = ?")) {

                stmt.setString(1, code);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("verification_code", rs.getString("verification_code"));
                    data.put("issued_at", rs.getString("issued_at"));
                    data.put("score", rs.getInt("score"));
                    data.put("tier", rs.getString("tier"));
                    data.put("student_name", rs.getString("student_name"));
                    data.put("module_title", rs.getString("module_title"));

                    Map<String, Object> result = new HashMap<>();
                    result.put("status", "VALID");
                    result.put("data", data);
                    result.put("engine", "Servlet (Java)");

                    out.print(gson.toJson(result));
                } else {
                    Map<String, String> invalid = new HashMap<>();
                    invalid.put("status", "INVALID");
                    invalid.put("message", "Badge not found or code tampered");
                    out.print(gson.toJson(invalid));
                }
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("status", "ERROR");
            error.put("message", e.getMessage());
            out.print(gson.toJson(error));
        }
    }
}
