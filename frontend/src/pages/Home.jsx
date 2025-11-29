import React from "react";
import { Link } from "react-router-dom";
import "./Home.css";

export default function Home() {
  return (
    <div className="home-container">
      <h1>Welcome to EduSpring 🎓</h1>
      <p>Your personalised learning platform.</p>

      <div className="auth-section">
        <p>If you already have an account, please <Link to="/login" className="nav-link">log in</Link>.</p>
        <p>If you’re new here, you can <Link to="/register" className="nav-link">Sign Up</Link>.</p>
      </div>

      <nav className="home-nav">
{/*         <Link to="/chat" className="nav-link">💬 Chat</Link> */}
        <Link to="/courses" className="nav-link disabled">📚 Courses (coming soon)</Link>
        <Link to="/profile" className="nav-link disabled">👤 Profile (coming soon)</Link>
      </nav>
    </div>
  );
}
