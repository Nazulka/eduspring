import React, { useState } from "react";
import ChatSidebar from "../components/ChatSidebar";
import Chat from "../components/Chat";
import SectionContent from "../components/SectionContent";
import "./LearningLayout.css";

export default function ChatPage() {
  const [activeSessionId, setActiveSessionId] = useState(null);
  const [selectedSectionId, setSelectedSectionId] = useState(1); // temp default

  return (
    <div className="learning-layout">
      {/* Left: course + section navigation */}
      <aside className="learning-sidebar">
        <ChatSidebar
          onSelectSession={setActiveSessionId}
          onSelectSection={setSelectedSectionId}
        />
      </aside>

      {/* Middle: section content */}
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
