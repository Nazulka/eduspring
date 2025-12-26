import React, { useEffect, useRef, useState } from "react";
import api from "../api/axiosInstance";
import "./Chat.css";

export default function Chat({ sectionId, sessionId, onSessionUpdate }) {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const [loading, setLoading] = useState(false);
  const [activeConversationId, setActiveConversationId] = useState(sessionId || null);
  const chatEndRef = useRef(null);

  // Keep session in sync when section changes
  useEffect(() => {
    setActiveConversationId(sessionId || null);
    setMessages([]);
  }, [sectionId, sessionId]);

  // Auto-scroll
  useEffect(() => {
    chatEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  // Load messages for existing session (per section)
  useEffect(() => {
    if (!sectionId || !activeConversationId) return;

    const loadMessages = async () => {
      try {
        const res = await api.get(`/chat/conversations/${activeConversationId}`);
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
      } catch (err) {
        console.error("Failed to load messages:", err);
      }
    };

    loadMessages();
  }, [sectionId, activeConversationId]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim() || !sectionId || loading) return;

    const userMessage = {
      sender: "user",
      text: input,
      time: new Date().toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      }),
    };

    setMessages((prev) => [...prev, userMessage]);
    setInput("");
    setLoading(true);

    try {
      const res = await api.post("/chat", {
        conversationId: activeConversationId,
        sectionId,
        message: userMessage.text,
      });

      const returnedConversationId = res.data.conversationId;

      // First message in this section → create session
      if (!activeConversationId && returnedConversationId) {
        setActiveConversationId(returnedConversationId);
        onSessionUpdate?.();
      }

      setMessages((prev) => [
        ...prev,
        {
          sender: "bot",
          text: res.data.reply,
          time: new Date().toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          }),
        },
      ]);
    } catch (err) {
      console.error("Chat error:", err);
      setMessages((prev) => [
        ...prev,
        {
          sender: "bot",
          text: "Sorry, something went wrong. Please try again.",
          time: new Date().toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          }),
        },
      ]);
    } finally {
      setLoading(false);
    }
  };

  // No section selected
  if (!sectionId) {
    return (
      <div className="chat-container empty">
        <p className="placeholder">Select a section to start chatting</p>
      </div>
    );
  }

  return (
    <div className="chat-container">
      <div className="chat-box">
        {messages.length === 0 && (
          <p className="placeholder">Ask a question about this section</p>
        )}

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
          placeholder="Ask about this section..."
          className="chat-input"
          disabled={loading}
          onKeyDown={(e) => {
            if (e.key === "Enter") handleSend(e);
          }}
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
