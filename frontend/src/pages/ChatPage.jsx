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
      {/* Left: Courses + Sections + History */}
      <aside className="learning-sidebar">
        <ChatSidebar
          selectedCourseId={selectedCourseId}
          onSelectCourse={(courseId) => {
            setSelectedCourseId(courseId);
            setSelectedSectionId(null);   // reset section
            setActiveSessionId(null);     // reset chat
          }}
          onSelectSection={(sectionId) => {
            setSelectedSectionId(sectionId);
            setActiveSessionId(null);     // new section = new chat
          }}
          onSelectSession={setActiveSessionId}
        />
      </aside>

      {/* Middle: Section content */}
      <main className="learning-content">
        <SectionContent
          courseId={selectedCourseId}
          sectionId={selectedSectionId}
        />
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
