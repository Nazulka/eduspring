import api from "./axiosInstance";

// Load all conversations for the logged-in user
export async function fetchChatSessions() {
  const res = await api.get("/chat/conversations");
  return res.data; // array of ConversationDto
}

// Load all messages in a conversation
export async function fetchSessionMessages(conversationId) {
  const res = await api.get(`/chat/conversations/${conversationId}`);
  return res.data; // ConversationDetailDto
}
