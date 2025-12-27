import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/axiosInstance";
import "./Chat.css";

const COURSE_ID = 1; // Week 11: single course is enough

const ChatSidebar = ({ onSelectSession, onSelectSection }) => {
  const { user } = useAuth();

  const [sections, setSections] = useState([]);
  const [sessions, setSessions] = useState([]);

  // Load sections for the course
  useEffect(() => {
    api
      .get(`/courses/${COURSE_ID}/sections`)
      .then((res) => {
        if (Array.isArray(res.data)) {
          setSections(res.data);
        }
      })
      .catch((err) => {
        console.warn("Failed to load sections (non-blocking)", err);
      });
  }, []);

  // Load previous chat sessions (optional)
  useEffect(() => {
    if (!user) return;

    api
      .get("/chat/conversations")
      .then((res) => {
        if (Array.isArray(res.data)) {
          setSessions(res.data);
        }
      })
      .catch((err) => {
        console.warn("Chat history unavailable (non-blocking)", err);
      });
  }, [user]);

  return (
    <div className="sidebar">
      {/* Course */}
      <h3 className="sidebar-title">Course</h3>
      <div className="sidebar-item active">
        Java Basics
      </div>

      {/* Sections */}
      <h4 className="sidebar-subtitle">Sections</h4>
      <ul className="sidebar-list">
        {sections.length === 0 && (
          <li className="muted">No sections available</li>
        )}

        {sections.map((section) => (
          <li
            key={section.id}
            onClick={() => onSelectSection(section.id)}
          >
            Section {section.id}
          </li>
        ))}
      </ul>

      {/* Previous Sessions (optional, non-blocking) */}
      {sessions.length > 0 && (
        <>
          <h4 className="sidebar-subtitle">Previous Sessions</h4>
          <ul className="sidebar-list">
            {sessions.map((s) => (
              <li
                key={s.id}
                onClick={() => onSelectSession?.(s.id)}
              >
                {s.title || "Untitled Session"}
              </li>
            ))}
          </ul>
        </>
      )}
    </div>
  );
};

export default ChatSidebar;
