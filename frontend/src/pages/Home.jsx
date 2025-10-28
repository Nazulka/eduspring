import React from "react";
import { Link, useNavigate } from "react-router-dom";
import "./Home.css";

export default function Home() {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <div className="home-container">
      <h1>Welcome to EduSpring 🎓</h1>
      <p>Your learning platform is ready to grow!</p>

      <nav className="home-nav">
        <Link to="/chat" className="nav-link">💬 Chat</Link>
        <Link to="/courses" className="nav-link disabled">📚 Courses (coming soon)</Link>
        <Link to="/profile" className="nav-link disabled">👤 Profile (coming soon)</Link>
      </nav>

      <button className="logout-btn" onClick={handleLogout}>
        🚪 Logout
      </button>
    </div>
  );
}
