import React, { useEffect, useRef, useState } from "react";
import api from "../api/axiosInstance";
import { useAuth } from "../context/AuthContext";
import "./Chat.css";

export default function Chat({ sessionId }) {
  const { user } = useAuth();
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [activeSessionId, setActiveSessionId] = useState(sessionId || null);
  const chatEndRef = useRef(null);

  // Auto-scroll when messages change
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Load previous messages when a chat session is selected from sidebar
  useEffect(() => {
    if (!sessionId || !user) return;
    const loadMessages = async () => {
      try {
        const res = await api.get(`/chat/sessions/${sessionId}?userId=${user.id}`);
        setMessages(
          res.data.messages.map((m) => ({
            sender: m.role === "user" ? "user" : "bot",
            text: m.content,
            time: new Date(m.timestamp || Date.now()).toLocaleTimeString([], {
              hour: "2-digit",
              minute: "2-digit",
            }),
          }))
        );
        setActiveSessionId(sessionId);
      } catch (err) {
        console.error("Failed to load messages:", err);
      }
    };
    loadMessages();
  }, [sessionId, user]);

  // Send a message (and get AI reply)
  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || !user) {
      console.log("Stopped: missing input or user", { input, user });
      return;
    }

    const newMessage = {
      sender: "user",
      text: input,
      time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
    };

    setMessages((prev) => [...prev, newMessage]);
    setInput("");
    setLoading(true);

    try {
      const response = await api.post(
        `/chat/ask?userId=${user.id}&sessionId=${activeSessionId || ""}&message=${encodeURIComponent(input)}`
      );

      // Store sessionId returned by backend for reuse
      if (!activeSessionId && response.data.sessionId) {
        setActiveSessionId(response.data.sessionId);
        console.log("Session established:", response.data.sessionId);
      }

      const reply = response.data.aiReply || "AI did not respond.";

      const botMessage = {
        sender: "bot",
        text: reply,
        time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
      };

      setMessages((prev) => [...prev, botMessage]);
    } catch (error) {
      console.error("Chat error:", error);
      setMessages((prev) => [
        ...prev,
        {
          sender: "bot",
          text: "Could not reach the chat service.",
          time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" }),
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="chat-container">
      <div className="chat-box">
        {messages.length === 0 && <p className="placeholder">Start chatting below</p>}

        {messages.map((msg, index) => (
          <div
            key={index}
            className={`chat-message ${msg.sender === "user" ? "user" : "bot"}`}
          >
            <div className="chat-text">{msg.text}</div>
            <div className="chat-time">{msg.time}</div>
          </div>
        ))}

        {loading && (
          <div className="chat-message bot typing">
            <div className="typing-dots">
              <span></span>
              <span></span>
              <span></span>
            </div>
          </div>
        )}

        <div ref={chatEndRef} />
      </div>

      <div className="chat-input-area">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          onKeyDown={(e) => {
            if (e.key === "Enter" && !loading) handleSend(e);
          }}
          placeholder="Type your message..."
          className="chat-input"
          disabled={loading}
        />
        <button
          type="button"
          onClick={handleSend}
          className="chat-send"
          disabled={loading}
        >
          {loading ? "..." : "Send"}
        </button>
      </div>
    </div>
  );
}
