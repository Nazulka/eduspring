const BASE_URL = "http://localhost:8081/api/chat";

export async function fetchChatSessions(userId) {
  const res = await fetch(`${BASE_URL}/sessions?userId=${userId}`);
  if (!res.ok) throw new Error("Failed to load chat sessions");
  const data = await res.json();
  return data.sessions;
}

export async function fetchSessionMessages(userId, sessionId) {
  const res = await fetch(`${BASE_URL}/sessions/${sessionId}?userId=${userId}`);
  if (!res.ok) throw new Error("Failed to load session messages");
  const data = await res.json();
  return data.messages;
}
