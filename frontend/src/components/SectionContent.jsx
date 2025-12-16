import React, { useEffect, useState } from "react";
import ReactMarkdown from "react-markdown";
import api from "../api/axiosInstance";
import "./SectionContent.css";

export default function SectionContent({ sectionId }) {
  const [section, setSection] = useState(null);

  useEffect(() => {
    if (!sectionId) {
      setSection(null);
      return;
    }

    api.get(`/sections/${sectionId}`)
      .then(res => setSection(res.data))
      .catch(err => console.error("Failed to load section", err));
  }, [sectionId]);

  if (!sectionId) {
    return <div className="section-content empty">Select a section</div>;
  }

  if (!section) {
    return <div className="section-content loading">Loading section…</div>;
  }

  return (
    <div className="section-content markdown">
      <ReactMarkdown>
        {section.content}
      </ReactMarkdown>
    </div>
  );
}
