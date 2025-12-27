import React, { useState } from "react";
import ChatSidebar from "../components/ChatSidebar";
import Chat from "../components/Chat";
import SectionContent from "../components/SectionContent";
import "./LearningLayout.css";

export default function ChatPage() {
  const [selectedCourseId, setSelectedCourseId] = useState(null);
  const [selectedSectionId, setSelectedSectionId] = useState(null);
  const [activeSessionId, setActiveSessionId] = useState(null);

  return (
    <div className="learning-layout">
      {/* Left: Courses + Sections */}
      <aside className="learning-sidebar">
        <ChatSidebar
          selectedCourseId={selectedCourseId}
          onSelectCourse={(courseId) => {
            setSelectedCourseId(courseId);
            setSelectedSectionId(null);
            setActiveSessionId(null);
          }}
          onSelectSection={(sectionId) => {
            setSelectedSectionId(sectionId);
            setActiveSessionId(null);
          }}
          onSelectSession={setActiveSessionId}
        />
      </aside>

      {/* Middle: Section content */}
      <main className="learning-content">
        <SectionContent sectionId={selectedSectionId} />
      </main>

      {/* Right: AI tutor */}
      <aside className="learning-assistant">
        <Chat
          sessionId={activeSessionId}
          sectionId={selectedSectionId}
        />
      </aside>
    </div>
  );
}
