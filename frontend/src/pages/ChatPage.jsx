import React from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";

export default function ChatPage() {
  const navigate = useNavigate();

  const handleLogout = () => {
    // ✅ clear token and redirect
    localStorage.removeItem("token");
    toast.success("You have been logged out.");
    navigate("/login");
  };

  return (
    <div style={styles.container}>
      <h2>Welcome to the Chat Page 💬</h2>
      <p>This is a protected route — only logged-in users can see this.</p>
      <button onClick={handleLogout} style={styles.button}>
        Logout
      </button>
    </div>
  );
}

const styles = {
  container: {
    maxWidth: "600px",
    margin: "50px auto",
    textAlign: "center",
    padding: "20px",
    backgroundColor: "#f9f9f9",
    borderRadius: "8px",
    boxShadow: "0 2px 5px rgba(0,0,0,0.1)",
  },
  button: {
    backgroundColor: "#dc3545",
    color: "white",
    padding: "10px 15px",
    border: "none",
    borderRadius: "5px",
    cursor: "pointer",
    marginTop: "20px",
  },
};
