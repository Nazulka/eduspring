import React, { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import api from "../api/axiosInstance";
import "./SectionContent.css";

export default function SectionContent({ courseId, sectionId }) {
  const [section, setSection] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!courseId || !sectionId) {
      setSection(null);
      return;
    }

    setLoading(true);

    api
      .get(`/courses/${courseId}/sections`)
      .then((res) => {
        const sections = res.data || [];
        const found = sections.find((s) => s.id === sectionId);

        if (!found) {
          throw new Error("Section not found in course");
        }

        setSection(found);
      })
      .catch((err) => {
        console.error("Failed to load section", err);
        setSection(null);
      })
      .finally(() => setLoading(false));
  }, [courseId, sectionId]);

  /* ---------- Empty states ---------- */

  if (!courseId) {
    return (
      <div className="section-content empty">
        Select a course to begin
      </div>
    );
  }

  if (!sectionId) {
    return (
      <div className="section-content empty">
        Select a section to view its content
      </div>
    );
  }

  if (loading) {
    return (
      <div className="section-content loading">
        Loading section…
      </div>
    );
  }

  if (!section) {
    return (
      <div className="section-content empty">
        Section not found
      </div>
    );
  }

  /* ---------- Render ---------- */

  return (
    <div className="section-content markdown">
      <ReactMarkdown>{section.content}</ReactMarkdown>
    </div>
  );
}
