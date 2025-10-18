import { BrowserRouter as Router, Routes, Route, Link } from "react-router-dom";
import Register from "./components/Register";
import Login from "./components/Login";

// backend java
function App() {
  return (
    <Router>
      <nav style={styles.navbar}>
        <Link to="/" style={styles.link}>Home</Link>
        <Link to="/register" style={styles.link}>Register</Link>
        <Link to="/login" style={styles.link}>Login</Link>
      </nav>

      <Routes>
        <Route path="/" element={<h2>Welcome to EduSpring</h2>} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
      </Routes>
    </Router>
  );
}

const styles = {
  navbar: {
    padding: "10px",
    backgroundColor: "#f2f2f2",
    marginBottom: "20px"
  },
  link: {
    marginRight: "15px",
    textDecoration: "none",
    color: "#007bff",
    fontWeight: "bold"
  }
};

export default App;
