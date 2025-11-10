import React, { useState } from "react";
import ChatSidebar from "../components/ChatSidebar";
import Chat from "../components/Chat";
import "../components/Chat.css";

export default function ChatPage() {
  const [activeSessionId, setActiveSessionId] = useState(null);

  return (
    <div className="chat-layout">
      <ChatSidebar onSelectSession={setActiveSessionId} />
      <Chat sessionId={activeSessionId} />
    </div>
  );
}
