import React, { useEffect, useRef, useState } from "react";
import api from "../api/axiosInstance";
import "./Chat.css";

export default function Chat({ sessionId, onSessionUpdate }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [activeConversationId, setActiveConversationId] = useState(sessionId || null);
  const chatEndRef = useRef(null);

  // Auto-scroll
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Load messages when a conversation is selected
  useEffect(() => {
    if (!sessionId) return;

    const loadMessages = async () => {
      try {
        const res = await api.get(`/conversations/${sessionId}`);
        const data = res.data;

        const mapped = (data.messages || []).map((m) => ({
          sender: m.role === "user" ? "user" : "bot",
          text: m.content,
          time: new Date(m.createdAt).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          }),
        }));

        setMessages(mapped);
        setActiveConversationId(sessionId);
      } catch (err) {
        console.error("Failed to load messages:", err);
      }
    };

    loadMessages();
  }, [sessionId]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    const newMsg = {
      sender: "user",
      text: input,
      time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
    };

    setMessages(prev => [...prev, newMsg]);
    setInput("");
    setLoading(true);

    try {
      const res = await api.post("", {
        conversationId: activeConversationId,
        message: newMsg.text,
      });

      const returnedId = res.data.conversationId;

      if (!activeConversationId && returnedId) {
        setActiveConversationId(returnedId);
        onSessionUpdate?.();
      }

      const botMsg = {
        sender: "bot",
        text: res.data.reply,
        time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
      };

      setMessages(prev => [...prev, botMsg]);
    } catch (err) {
      console.error("Chat error:", err);
      setMessages(prev => [...prev, {
        sender: "bot",
        text: "Could not reach chat service.",
        time: new Date().toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
      }]);
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
