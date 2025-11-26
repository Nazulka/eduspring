const BASE_URL = "http://localhost:8081/api";

// Load all conversations for the logged-in user
export async function fetchChatSessions() {
  const res = await fetch(`${BASE_URL}/conversations`);
  if (!res.ok) throw new Error("Failed to load conversations");
  return await res.json();  // already an array of conversation DTOs
}

// Load all messages in a conversation
export async function fetchSessionMessages(conversationId) {
  const res = await fetch(`${BASE_URL}/conversations/${conversationId}`);
  if (!res.ok) throw new Error("Failed to load conversation messages");
  return await res.json(); // has { id, title, messages: [...] }
}
