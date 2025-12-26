import React, { useEffect, useState } from "react";
import ChatSidebar from "../components/ChatSidebar";
import Chat from "../components/Chat";
import SectionContent from "../components/SectionContent";
import "../components/Chat.css";

export default function ChatPage() {
  const [activeSessionId, setActiveSessionId] = useState(null);
  const [selectedSectionId, setSelectedSectionId] = useState(1);

  // 🔑 IMPORTANT: reset chat session when section changes
  useEffect(() => {
    setActiveSessionId(null);
  }, [selectedSectionId]);

  return (
    <div className="chat-layout">
      {/* Left: sections / chat history */}
      <ChatSidebar
        onSelectSession={setActiveSessionId}
        onSelectSection={setSelectedSectionId}
      />

      {/* Middle: section content */}
      <SectionContent sectionId={selectedSectionId} />

      {/* Right: chat */}
      <Chat
        sessionId={activeSessionId}
        sectionId={selectedSectionId}
      />
    </div>
  );
}
