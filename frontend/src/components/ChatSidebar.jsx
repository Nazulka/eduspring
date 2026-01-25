import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/axiosInstance";
import "./Chat.css";

const ChatSidebar = ({
  selectedCourseId,
  selectedSectionId,
  onSelectCourse,
  onSelectSection,
  onSelectSession,
}) => {
  const { user } = useAuth();

  const [courses, setCourses] = useState([]);
  const [sections, setSections] = useState([]);
  const [sessions, setSessions] = useState([]);

  /* ------------------ Load courses ------------------ */
  useEffect(() => {
    api
      .get("/courses")
      .then((res) => setCourses(res.data || []))
      .catch((err) => console.warn("Failed to load courses", err));
  }, []);

  /* ------------------ Load sections for selected course ------------------ */
  useEffect(() => {
    if (!selectedCourseId) {
      setSections([]);
      return;
    }

    api
      .get(`/courses/${selectedCourseId}/sections`)
      .then((res) => setSections(res.data || []))
      .catch((err) => console.warn("Failed to load sections", err));
  }, [selectedCourseId]);

  /* ------------------ Load chat history (optional, user-based) ------------------ */
  useEffect(() => {
    if (!user) {
      setSessions([]);
      return;
    }

    api
      .get("/chat/conversations")
      .then((res) => setSessions(res.data || []))
      .catch(() => {});
  }, [user]);

  return (
    <div className="sidebar">
      {/* ================= Courses ================= */}
      <div className="sidebar-block">
        <h3 className="sidebar-title">Courses</h3>
        <ul className="sidebar-list">
          {courses.map((course) => (
            <li
              key={course.id}
              className={course.id === selectedCourseId ? "active" : ""}
              onClick={() => {
                onSelectCourse(course.id);
                onSelectSection(null); // reset section when course changes
              }}
            >
              {course.title}
            </li>
          ))}
        </ul>
      </div>

      {/* ================= Sections ================= */}
      {selectedCourseId && (
        <div className="sidebar-block">
          <h4 className="sidebar-subtitle">Sections</h4>
          <ul className="sidebar-list">
            {sections.length === 0 ? (
              <li className="empty">No sections</li>
            ) : (
              sections.map((section) => (
                <li
                  key={section.id}
                  className={section.id === selectedSectionId ? "active" : ""}
                  onClick={() => onSelectSection(section.id)}
                >
                  {section.title || `Section ${section.id}`}
                </li>
              ))
            )}
          </ul>
        </div>
      )}

      {/* ================= Previous Sessions ================= */}
      {sessions.length > 0 && (
        <div className="sidebar-block">
          <h4 className="sidebar-subtitle">Previous Chats</h4>
          <ul className="sidebar-list">
            {sessions.map((s) => (
              <li key={s.id} onClick={() => onSelectSession(s.id)}>
                {s.title || "Untitled Session"}
              </li>
            ))}
          </ul>
        </div>
      )}
    </div>
  );
};

export default ChatSidebar;
