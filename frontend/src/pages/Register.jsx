import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";
import api from "../api"; // use shared Axios instance

export default function Register() {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    firstName: "",
    lastName: "",
    email: "",
  });

  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();

  // Ensure user is logged out when accessing register page
  useEffect(() => {
    localStorage.removeItem("token");
  }, []);

  // Redirect authenticated users trying to access /register manually
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token && isAuthenticated) {
      navigate("/chat");
    }
  }, [isAuthenticated, navigate]);

  // ✏️ Handle form field changes
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  // Handle form submission
  const handleSubmit = async (e) => {
    e.preventDefault();
    setMessage("");
    setError("");

    const { username, password, email } = formData;

    if (!username || !password || !email) {
      setError("Username, password, and email are required.");
      return;
    }

    try {
      await api.post("/auth/register", formData);

      setMessage("Registration successful! Redirecting to login...");
      setFormData({
        username: "",
        password: "",
        firstName: "",
        lastName: "",
        email: "",
      });

      setTimeout(() => navigate("/login"), 1500);
    } catch (err) {
      // Better error handling
      if (err.response?.data) {
        setError(err.response.data);
      } else {
        setError("Network error: could not reach the server.");
      }
    }
  };

  return (
    <div style={styles.container}>
      <h2 style={styles.header}>Create Your EduSpring Account</h2>

      <form onSubmit={handleSubmit} style={styles.form}>
        <input
          name="username"
          placeholder="Username"
          value={formData.username}
          onChange={handleChange}
          style={styles.input}
        />
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={formData.password}
          onChange={handleChange}
          style={styles.input}
        />
        <input
          name="firstName"
          placeholder="First Name"
          value={formData.firstName}
          onChange={handleChange}
          style={styles.input}
        />
        <input
