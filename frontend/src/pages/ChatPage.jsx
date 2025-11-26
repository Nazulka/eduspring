import React, { useState } from "react";
import ChatSidebar from "../components/ChatSidebar";
import Chat from "../components/Chat";
import "../components/Chat.css";

export default function ChatPage() {
  const [activeSessionId, setActiveSessionId] = useState(null);

  const handleSessionUpdate = () => {
    setActiveSessionId((prev) => prev);
  };

  return (
    <div className="chat-layout">
      <ChatSidebar onSelectSession={setActiveSessionId} />
      <Chat sessionId={activeSessionId} onSessionUpdate={handleSessionUpdate} />
    </div>
  );
}
