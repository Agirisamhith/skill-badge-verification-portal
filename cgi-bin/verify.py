#!/usr/bin/env python3
import os
import cgi
import json
import hashlib
import pymysql

# Database Config
DB_HOST = os.environ.get('DB_HOST', '127.0.0.1')
DB_USER = os.environ.get('DB_USER', 'root')
DB_PASSWORD = os.environ.get('DB_PASSWORD', 'W7301@jqir#')
DB_NAME = os.environ.get('DB_NAME', 'skill_badge_portal')

def get_db_connection():
    return pymysql.connect(host=DB_HOST, user=DB_USER, password=DB_PASSWORD, database=DB_NAME, cursorclass=pymysql.cursors.DictCursor)

def main():
    print("Content-Type: application/json\n")
    
    form = cgi.FieldStorage()
    code = form.getvalue("code")
    
    if not code:
        print(json.dumps({"status": "ERROR", "message": "Verification code is required"}))
        return

    try:
        connection = get_db_connection()
        with connection.cursor() as cursor:
            sql = """
            SELECT b.verification_code, b.issued_at, b.score, b.tier, 
                   s.name as student_name, m.title as module_title
            FROM Badges b
            JOIN Students s ON b.student_id = s.student_id
            JOIN Modules m ON b.module_id = m.module_id
            WHERE b.verification_code = %s
            """
            cursor.execute(sql, (code,))
            result = cursor.fetchone()
            
            if result:
                # Convert datetime to string for JSON serialization
                result['issued_at'] = result['issued_at'].strftime('%Y-%m-%d %H:%M:%S')
                print(json.dumps({
                    "status": "VALID",
                    "data": result,
                    "engine": "CGI (Python)"
                }))
            else:
                print(json.dumps({"status": "INVALID", "message": "Badge not found or code tampered"}))
    except Exception as e:
        print(json.dumps({"status": "ERROR", "message": str(e)}))
    finally:
        if 'connection' in locals() and connection.open:
            connection.close()

if __name__ == "__main__":
    main()
