import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/axiosInstance";
import "./Chat.css";

const ChatSidebar = ({ onSelectSession }) => {
  const { user } = useAuth();
  const [sessions, setSessions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (!user) {
      setLoading(false);
      return;
    }

    const fetchSessions = async () => {
      try {
        const res = await api.get("/conversations");
        const data = Array.isArray(res.data) ? res.data : [];
        setSessions(data);
      } catch (err) {
        console.error("Failed to load chat sessions:", err);
        setError("Failed to load chat sessions");
      } finally {
        setLoading(false);
      }
    };

    fetchSessions();
  }, [user]);

  if (loading) return <div className="sidebar">Loading chats...</div>;
  if (error) return <div className="sidebar error">{error}</div>;

  return (
    <div className="sidebar">
      <h3>Chat History</h3>
      {sessions.length === 0 ? (
        <p className="empty">No previous chats</p>
      ) : (
        <ul>
          {sessions.map((s) => (
            <li key={s.id} onClick={() => onSelectSession(s.id)}>
              {s.title || "Untitled Chat"}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default ChatSidebar;
