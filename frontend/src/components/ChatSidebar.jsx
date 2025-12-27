import React, { useEffect, useState } from "react";
import { useAuth } from "../context/AuthContext";
import api from "../api/axiosInstance";
import "./Chat.css";

const ChatSidebar = ({
  selectedCourseId,
  onSelectCourse,
  onSelectSection,
  onSelectSession
}) => {
  const { user } = useAuth();

  const [courses, setCourses] = useState([]);
  const [sections, setSections] = useState([]);
  const [sessions, setSessions] = useState([]);

  /* Load all courses */
  useEffect(() => {
    api.get("/courses")
      .then(res => setCourses(res.data || []))
      .catch(err => console.warn("Failed to load courses", err));
  }, []);

  /* Load sections when course changes */
  useEffect(() => {
    if (!selectedCourseId) {
      setSections([]);
      return;
    }

    api.get(`/courses/${selectedCourseId}/sections`)
      .then(res => setSections(res.data || []))
      .catch(err => console.warn("Failed to load sections", err));
  }, [selectedCourseId]);

  /* Load chat history (optional) */
  useEffect(() => {
    if (!user) return;

    api.get("/chat/conversations")
      .then(res => setSessions(res.data || []))
      .catch(() => {});
  }, [user]);

  return (
    <div className="sidebar">
      {/* Courses */}
      <h3 className="sidebar-title">Courses</h3>
      <ul className="sidebar-list">
        {courses.map(course => (
          <li
            key={course.id}
            className={course.id === selectedCourseId ? "active" : ""}
            onClick={() => onSelectCourse(course.id)}
          >
            {course.title}
          </li>
        ))}
      </ul>

      {/* Sections */}
      {selectedCourseId && (
        <>
          <h4 className="sidebar-subtitle">Sections</h4>
          <ul className="sidebar-list">
            {sections.map(section => (
              <li
                key={section.id}
                onClick={() => onSelectSection(section.id)}
              >
                Section {section.id}
              </li>
            ))}
          </ul>
        </>
      )}

      {/* Optional: Previous sessions */}
      {sessions.length > 0 && (
        <>
          <h4 className="sidebar-subtitle">Previous Sessions</h4>
          <ul className="sidebar-list">
            {sessions.map(s => (
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
