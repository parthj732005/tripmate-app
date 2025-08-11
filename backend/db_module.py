# backend/db_module.py

import sqlite3

DB_PATH = 'database/tripmate.db'

def init_db():
    """Create search_logs table if not exists."""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        CREATE TABLE IF NOT EXISTS search_logs (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            city TEXT NOT NULL,
            timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
        )
    ''')
    conn.commit()
    conn.close()

def log_search(city):
    """Insert a search log."""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('INSERT INTO search_logs (city) VALUES (?)', (city,))
    conn.commit()
    conn.close()

def get_search_statistics():
    """Return most searched places as (city, count)."""
    conn = sqlite3.connect(DB_PATH)
    cursor = conn.cursor()
    cursor.execute('''
        SELECT city, COUNT(*) as count FROM search_logs
        GROUP BY city ORDER BY count DESC LIMIT 10
    ''')
    result = cursor.fetchall()
    conn.close()
    return result
