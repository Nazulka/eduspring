import React, { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import api from "../api/axiosInstance";
import "./SectionContent.css";

export default function SectionContent({ sectionId }) {
  const [section, setSection] = useState(null);
  const [loading, setLoading] = useState(false);

  // TEMP: using courseId = 1 until course selection UI is added
  const COURSE_ID = 1;

  useEffect(() => {
    if (!sectionId) {
      setSection(null);
      return;
    }

    setLoading(true);

    api
      .get(`/courses/${COURSE_ID}/sections`)
      .then((res) => {
        const found = res.data.find(
          (s) => String(s.id) === String(sectionId)
        );
        setSection(found || null);
      })
      .catch((err) => {
        console.error("Failed to load section", err);
        setSection(null);
      })
      .finally(() => {
        setLoading(false);
      });
  }, [sectionId]);

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

  return (
    <div className="section-content markdown">
      <ReactMarkdown>{section.content}</ReactMarkdown>
    </div>
  );
}
